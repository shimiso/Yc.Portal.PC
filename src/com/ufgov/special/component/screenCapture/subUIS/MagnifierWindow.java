package com.ufgov.special.component.screenCapture.subUIS;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ufgov.special.component.screenCapture.CaptureScreen;

/**
 * @describe 放大镜
 * @author suyanga 
 * @date 2015年5月10日 上午12:56:37
 */
public class MagnifierWindow {

  private static JWindow magnifierWindow = null;

  /**
   * 放大镜内容面板
   */
  private static MagnifierPanel magnifierPanel = null;

  /**
   * 放大镜的大小
   */
  private static int magnifierSize = 120;

  @SuppressWarnings("restriction")
  public static JWindow getMagnifierWindow(Window window) {
    if (magnifierWindow == null) {
      magnifierWindow = new JWindow(window);
      magnifierWindow.setContentPane(getMagnifierPanel());
      magnifierWindow.setSize(magnifierSize, magnifierSize);
      //圆角window
      com.sun.awt.AWTUtilities.setWindowShape(magnifierWindow,
        new RoundRectangle2D.Double(0, 0, magnifierWindow.getWidth(), magnifierWindow.getHeight(), 3, 3));
    }
    return magnifierWindow;
  }

  /**
   * @describe 放大镜内容面板
   * @return 
   * @author suyanga
   * @date 2015年5月10日 上午3:44:22
   */
  private static MagnifierPanel getMagnifierPanel() {
    if (magnifierPanel == null)
      magnifierPanel = new MagnifierPanel(magnifierSize);
    return magnifierPanel;
  }

  /**
   * @describe 设置工具面板window显示情况
   * @param window 就是getToolBarWindow()，不过是当参数传进来
   * @param visible
   * @param rec 截取的图片的区域
   * @author suyanga
   * @date 2015年5月9日 下午11:10:03
   */
  public static void setWindowVisible(JWindow window, boolean visible, Point point, int xOffSet, int yOffSet) {
    if (!visible) {
      window.setVisible(visible);
    } else {
      getMagnifierPanel().setMagnifierLocation(point.x, point.y);
      int px = point.x + xOffSet;
      int py = point.y + 30 + yOffSet;
      if (px <= 0)
        px = 1;
      if (py <= 0)
        py = 1;
      if (px + window.getWidth() > CaptureScreen.getScreenWidth())
        px = point.x - 130;
      if (py + window.getHeight() > CaptureScreen.getScreenHeight())
        py = point.y - 140;
      window.setLocation(px, py);
      if (CaptureScreen.getParentDialog(null).isVisible())
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
    getMagnifierWindow(null).setLocationRelativeTo(null);
    getMagnifierWindow(null).setVisible(true);
  }
}

class MagnifierPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  /**
   * 放大镜的尺寸
   */
  private int magnifierSize;

  private int locationX;

  private int locationY;

  private String rgb = "";

  /**
   * 带参数的构造函数
   * @param magnifierSize
   *         放大尺寸
   */
  public MagnifierPanel(int magnifierSize) {
    // 截屏幕
    this.magnifierSize = magnifierSize;
  }

  /**
   * 设置放大镜的位置
   * @param locationX
   *         x坐标
   * @param locationY
   *         y坐标
   */
  public void setMagnifierLocation(int locationX, int locationY) {
    this.locationX = locationX - magnifierSize / 2;
    this.locationY = locationY - (magnifierSize - 35) / 2;
    try {
      int irgb = CaptureScreen.bufferedImage.getRGB(locationX, locationY);
      int R = (irgb & 0xff0000) >> 16;
      int G = (irgb & 0xff00) >> 8;
      int B = (irgb & 0xff);
      rgb = "RGB:(" + R + "," + G + "," + B + ")";
    } catch (Exception e) {
      rgb = "RGB:(0,0,0)";
      e.printStackTrace();
    }
    repaint(); // 注意重画控件
  }

  /**
   * 设置放大镜的尺寸
   * @param magnifierSize
   *         放大镜尺寸
   */
  public void setMagnifierSize(int magnifierSize) {
    this.magnifierSize = magnifierSize;
  }

  public void paintComponent(Graphics g) {
    super.paintComponent((Graphics2D) g);
    // 关键处理代码
    g.drawImage(CaptureScreen.background, // 要画的图片
      0, // 目标矩形的第一个角的x坐标     
      0, // 目标矩形的第一个角的y坐标
      magnifierSize, // 目标矩形的第二个角的x坐标
      magnifierSize - 35, // 目标矩形的第二个角的y坐标
      locationX + magnifierSize / 8 * 3 + 1, // 源矩形的第一个角的x坐标
      locationY + ((magnifierSize - 35)) / 8 * 3 + 2, // 源矩形的第一个角的y坐标
      locationX + magnifierSize / 8 * 5 + 1, // 源矩形的第二个角的x坐标
      locationY + ((magnifierSize - 35)) / 8 * 5 + 2, // 源矩形的第二个角的y坐标
      this);
    g.setColor(Color.BLACK);
    g.drawRoundRect(0, 0, magnifierSize - 1, magnifierSize - 1, 2, 2);
    //画十字线
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(new Color(39, 141, 230, 200));
    g2d.setStroke(new BasicStroke(4));
    g2d.drawLine(4, 44, 116, 44);
    g2d.drawLine(60, 4, 60, 82);
    //填充下面的黑色
    g2d.setColor(Color.GRAY);
    g2d.fillRoundRect(1, magnifierSize - 35, magnifierSize - 2, 34, 1, 1);
    g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
    g2d.setColor(new Color(255, 255, 255, 255));
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int delTAX = Math.abs(CaptureScreen.x - CaptureScreen.x1);
    int delTAY = Math.abs(CaptureScreen.y - CaptureScreen.y1);
    g2d.drawString(delTAX + " x " + delTAY, 5, 100);
    g2d.drawString(rgb, 1, 115);
  }
}
