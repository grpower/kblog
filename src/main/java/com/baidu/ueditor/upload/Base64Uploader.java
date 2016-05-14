package com.baidu.ueditor.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;

import org.apache.commons.codec.binary.Base64;

import java.util.Map;

public final class Base64Uploader {

  public static State save(final String content, final Map<String, Object> conf) {

    final byte[] data = decode(content);

    final long maxSize = ((Long) conf.get("maxSize")).longValue();

    if (!validSize(data, maxSize)) {
      return new BaseState(false, AppInfo.MAX_SIZE);
    }

    final String suffix = FileType.getSuffix("JPG");

    String savePath = PathFormat.parse((String) conf.get("savePath"),
        (String) conf.get("filename"));

    savePath = savePath + suffix;
    // String physicalPath = (String) conf.get("rootPath") + savePath;
    final String physicalPath = PathFormat
        .format((String) conf.get("upfilesPhysicalRootPath") + savePath);

    final State storageState = StorageManager.saveBinaryFile(data, physicalPath);

    if (storageState.isSuccess()) {
      storageState.putInfo("url", PathFormat.format(savePath));
      storageState.putInfo("type", suffix);
      storageState.putInfo("original", "");
    }

    return storageState;
  }

  private static byte[] decode(final String content) {
    return Base64.decodeBase64(content.getBytes());
  }

  private static boolean validSize(final byte[] data, final long length) {
    return data.length <= length;
  }

}
