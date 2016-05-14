package com.baidu.ueditor;

public class Encoder {

  public static String toUnicode(final String input) {

    final StringBuilder builder = new StringBuilder();
    final char[] chars = input.toCharArray();

    for (final char ch : chars) {

      if (ch < 256) {
        builder.append(ch);
      } else {
        builder.append("\\u" + Integer.toHexString(ch & 0xffff));
      }

    }

    return builder.toString();

  }

}
