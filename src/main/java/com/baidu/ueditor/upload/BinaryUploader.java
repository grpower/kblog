package com.baidu.ueditor.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class BinaryUploader {

  public static final State save(final HttpServletRequest request, final Map<String, Object> conf) {
    FileItemStream fileStream = null;
    final boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;

    if (!ServletFileUpload.isMultipartContent(request)) {
      return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
    }

    final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

    if (isAjaxUpload) {
      upload.setHeaderEncoding("UTF-8");
    }

    try {
      final FileItemIterator iterator = upload.getItemIterator(request);

      while (iterator.hasNext()) {
        fileStream = iterator.next();

        if (!fileStream.isFormField()) {
          break;
        }
        fileStream = null;
      }

      if (fileStream == null) {
        return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
      }

      String savePath = (String) conf.get("savePath");
      String originFileName = fileStream.getName();
      final String suffix = FileType.getSuffixByFilename(originFileName);

      originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
      savePath = savePath + suffix;

      final long maxSize = ((Long) conf.get("maxSize")).longValue();

      if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
        return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
      }

      savePath = PathFormat.parse(savePath, originFileName);

      // String physicalPath = (String) conf.get("rootPath") + savePath;
      final String physicalPath = PathFormat
          .format((String) conf.get("upfilesPhysicalRootPath") + savePath);

      final InputStream is = fileStream.openStream();
      final State storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
      is.close();

      if (storageState.isSuccess()) {
        storageState.putInfo("url", PathFormat.format(savePath));
        storageState.putInfo("type", suffix);
        storageState.putInfo("original", originFileName + suffix);
      }

      return storageState;
    } catch (final FileUploadException e) {
      return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
    } catch (final IOException e) {
    }
    return new BaseState(false, AppInfo.IO_ERROR);
  }

  private static boolean validType(final String type, final String[] allowTypes) {
    final List<String> list = Arrays.asList(allowTypes);

    return list.contains(type);
  }
}
