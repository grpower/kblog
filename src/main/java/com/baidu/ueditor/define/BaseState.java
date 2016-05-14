package com.baidu.ueditor.define;

import com.baidu.ueditor.Encoder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseState implements State {

  private boolean state = false;
  private String info = null;

  private final Map<String, String> infoMap = new HashMap<String, String>();

  public BaseState() {
    this.state = true;
  }

  public BaseState(final boolean state) {
    this.setState(state);
  }

  public BaseState(final boolean state, final String info) {
    this.setState(state);
    this.info = info;
  }

  public BaseState(final boolean state, final int infoCode) {
    this.setState(state);
    this.info = AppInfo.getStateInfo(infoCode);
  }

  @Override
  public boolean isSuccess() {
    return this.state;
  }

  public void setState(final boolean state) {
    this.state = state;
  }

  public void setInfo(final String info) {
    this.info = info;
  }

  public void setInfo(final int infoCode) {
    this.info = AppInfo.getStateInfo(infoCode);
  }

  @Override
  public String toJSONString() {
    return this.toString();
  }

  @Override
  public String toString() {

    String key = null;
    final String stateVal = this.isSuccess() ? AppInfo.getStateInfo(AppInfo.SUCCESS) : this.info;

    final StringBuilder builder = new StringBuilder();

    builder.append("{\"state\": \"" + stateVal + "\"");

    final Iterator<String> iterator = this.infoMap.keySet().iterator();

    while (iterator.hasNext()) {

      key = iterator.next();

      builder.append(",\"" + key + "\": \"" + this.infoMap.get(key) + "\"");

    }

    builder.append("}");

    return Encoder.toUnicode(builder.toString());

  }

  @Override
  public void putInfo(final String name, final String val) {
    this.infoMap.put(name, val);
  }

  @Override
  public void putInfo(final String name, final long val) {
    this.putInfo(name, val + "");
  }

}
