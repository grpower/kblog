package com.baidu.ueditor.upload;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StorageManager {
  public static final int BUFFER_SIZE = 8192;

  public StorageManager() {
  }

  public static State saveBinaryFile(final byte[] data, final String path) {
    final File file = new File(path);

    State state = valid(file);

    if (!state.isSuccess()) {
      return state;
    }

    try {
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      bos.write(data);
      bos.flush();
      bos.close();
    } catch (final IOException ioe) {
      return new BaseState(false, AppInfo.IO_ERROR);
    }

    state = new BaseState(true, file.getAbsolutePath());
    state.putInfo("size", data.length);
    state.putInfo("title", file.getName());
    return state;
  }

  public static State saveFileByInputStream(final InputStream is, final String path,
      final long maxSize) {
    State state = null;

    final File tmpFile = getTmpFile();

    final byte[] dataBuf = new byte[2048];
    final BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

    try {
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile),
          StorageManager.BUFFER_SIZE);

      int count = 0;
      while ((count = bis.read(dataBuf)) != -1) {
        bos.write(dataBuf, 0, count);
      }
      bos.flush();
      bos.close();

      if (tmpFile.length() > maxSize) {
        tmpFile.delete();
        return new BaseState(false, AppInfo.MAX_SIZE);
      }

      state = saveTmpFile(tmpFile, path);

      if (!state.isSuccess()) {
        tmpFile.delete();
      }

      return state;

    } catch (final IOException e) {
    }
    return new BaseState(false, AppInfo.IO_ERROR);
  }

  public static State saveFileByInputStream(final InputStream is, final String path) {
    State state = null;

    final File tmpFile = getTmpFile();

    final byte[] dataBuf = new byte[2048];
    final BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

    try {
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile),
          StorageManager.BUFFER_SIZE);

      int count = 0;
      while ((count = bis.read(dataBuf)) != -1) {
        bos.write(dataBuf, 0, count);
      }
      bos.flush();
      bos.close();

      state = saveTmpFile(tmpFile, path);

      if (!state.isSuccess()) {
        tmpFile.delete();
      }

      return state;
    } catch (final IOException e) {
    }
    return new BaseState(false, AppInfo.IO_ERROR);
  }

  private static File getTmpFile() {
    final File tmpDir = FileUtils.getTempDirectory();
    final String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
    return new File(tmpDir, tmpFileName);
  }

  private static State saveTmpFile(final File tmpFile, final String path) {
    State state = null;
    final File targetFile = new File(path);

    if (targetFile.canWrite()) {
      return new BaseState(false, AppInfo.PERMISSION_DENIED);
    }
    try {
      FileUtils.moveFile(tmpFile, targetFile);
    } catch (final IOException e) {
      return new BaseState(false, AppInfo.IO_ERROR);
    }

    state = new BaseState(true);
    state.putInfo("size", targetFile.length());
    state.putInfo("title", targetFile.getName());

    return state;
  }

  private static State valid(final File file) {
    final File parentPath = file.getParentFile();

    if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
      return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
    }

    if (!parentPath.canWrite()) {
      return new BaseState(false, AppInfo.PERMISSION_DENIED);
    }

    return new BaseState(true);
  }
}
