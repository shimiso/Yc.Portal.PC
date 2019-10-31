package com.ufgov.special.component.util;

import java.awt.Color;

public class ColorUtil {

  /**
   * @describe 设置颜色透明度
   * @param originColor
   * @param alpha
   * @return 
   * @author suyanga
   * @date 2015年5月9日 下午10:12:43
   */
  public static Color colorToAlphaColor(Color originColor, int alpha) {
    return new Color(originColor.getRed(), originColor.getGreen(), originColor.getBlue(), alpha);
  }
}
