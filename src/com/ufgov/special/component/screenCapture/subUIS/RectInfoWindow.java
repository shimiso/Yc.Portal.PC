package com.ufgov.special.component.screenCapture.subUIS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class RectInfoWindow {

  private static JWindow rectBarWindow = null;

  /**
   * 显示尺寸
   */
  private static JLabel label;

  @SuppressWarnings("restriction")
  public static JWindow getRectInfoWindow(Window window) {
    if (rectBarWindow == null) {
      rectBarWindow = new JWindow(window);
      rectBarWindow.setContentPane(getRectInfoPanel());
      rectBarWindow.setSize(80, 20);
      com.sun.awt.AWTUtilities.setWindowOpacity(rectBarWindow, 0.7f);
    }

    return rectBarWindow;
  }

  /**
   * @describe 截图尺寸信息面板
   * @return 
   * @author suyanga
   * @date 2015年5月10日 上午2:20:00
   */
  private static JPanel getRectInfoPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.BLACK);
    label = new JLabel();
    label.setFont(new Font("Consolas", Font.PLAIN, 12));
    label.setForeground(Color.WHITE);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setVerticalAlignment(SwingConstants.CENTER);
    panel.add(label);
    return panel;
  }

  /**
   * @describe 设置要显示的字
   * @param text 
   * @author suyanga
   * @date 2015年5月10日 上午2:19:36
   */
  public static void setText(String text) {
    if (label != null)
      label.setText(text);
  }

  /**
   * @describe 设置工具面板window显示情况
   * @param window 就是getRectInfoWindow()，不过是当参数传进来
   * @param visible
   * @param point 截取区域的左上顶点
   * @author suyanga
   * @date 2015年5月9日 下午11:10:03
   */
  public static void setWindowVisible(JWindow window, boolean visible, Point point) {
    if (!visible) {
      window.setVisible(visible);
    } else {
      int px = point.x + 1;
      int py = point.y - window.getHeight();
      if (py < 0)
        py = point.y + 1;
      window.setLocation(px, py);
      window.setVisible(true);
    }
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    getRectInfoWindow(null).setLocationRelativeTo(null);
    getRectInfoWindow(null).setVisible(true);
    setText("158 x 256");
  }
}
