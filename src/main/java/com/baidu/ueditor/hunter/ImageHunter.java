package com.baidu.ueditor.hunter;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MIMEType;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 图片抓取器
 * 
 * @author hancong03@baidu.com
 *
 */
public class ImageHunter {

  private String filename = null;
  private String savePath = null;
  private String rootPath = null;
  private List<String> allowTypes = null;
  private long maxSize = -1;

  private List<String> filters = null;

  public ImageHunter(final Map<String, Object> conf) {

    this.filename = (String) conf.get("filename");
    this.savePath = (String) conf.get("savePath");
    this.rootPath = (String) conf.get("rootPath");
    this.maxSize = (Long) conf.get("maxSize");
    this.allowTypes = Arrays.asList((String[]) conf.get("allowFiles"));
    this.filters = Arrays.asList((String[]) conf.get("filter"));

  }

  public State capture(final String[] list) {

    final MultiState state = new MultiState(true);

    for (final String source : list) {
      state.addState(captureRemoteData(source));
    }

    return state;

  }

  public State captureRemoteData(final String urlStr) {

    HttpURLConnection connection = null;
    URL url = null;
    String suffix = null;

    try {
      url = new URL(urlStr);

      if (!validHost(url.getHost())) {
        return new BaseState(false, AppInfo.PREVENT_HOST);
      }

      connection = (HttpURLConnection) url.openConnection();

      connection.setInstanceFollowRedirects(true);
      connection.setUseCaches(true);

      if (!validContentState(connection.getResponseCode())) {
        return new BaseState(false, AppInfo.CONNECTION_ERROR);
      }

      suffix = MIMEType.getSuffix(connection.getContentType());

      if (!validFileType(suffix)) {
        return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
      }

      if (!validFileSize(connection.getContentLength())) {
        return new BaseState(false, AppInfo.MAX_SIZE);
      }

      final String savePath = this.getPath(this.savePath, this.filename, suffix);
      final String physicalPath = this.rootPath + savePath;

      final State state = StorageManager.saveFileByInputStream(connection.getInputStream(),
          physicalPath);

      if (state.isSuccess()) {
        state.putInfo("url", PathFormat.format(savePath));
        state.putInfo("source", urlStr);
      }

      return state;

    } catch (final Exception e) {
      return new BaseState(false, AppInfo.REMOTE_FAIL);
    }

  }

  private String getPath(final String savePath, final String filename, final String suffix) {

    return PathFormat.parse(savePath + suffix, filename);

  }

  private boolean validHost(final String hostname) {
    try {
      final InetAddress ip = InetAddress.getByName(hostname);

      if (ip.isSiteLocalAddress()) {
        return false;
      }
    } catch (final UnknownHostException e) {
      return false;
    }

    return !filters.contains(hostname);

  }

  private boolean validContentState(final int code) {

    return HttpURLConnection.HTTP_OK == code;

  }

  private boolean validFileType(final String type) {

    return this.allowTypes.contains(type);

  }

  private boolean validFileSize(final int size) {
    return size < this.maxSize;
  }

}
