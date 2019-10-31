package com.ufgov.special.component.screenCapture.subUIS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.ufgov.special.component.screenCapture.CaptureScreen;
import com.ufgov.special.component.util.ColorUtil;
import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.bridge.TrayBridge;
import com.yuecheng.workportal.tools.ImageToBase64;

public class ToolBarWindow {

  private static JWindow toolBarWindow = null;

  private static Color backColor = new Color(231, 235, 243);

  @SuppressWarnings("restriction")
  public static JWindow getToolBarWindow(Window window) {
    if (toolBarWindow == null) {
      toolBarWindow = new JWindow(window);
      toolBarWindow.setContentPane(getToolBarPanel());
      toolBarWindow.pack();
      com.sun.awt.AWTUtilities.setWindowShape(toolBarWindow, new RoundRectangle2D.Double(0, 0,
        toolBarWindow.getWidth(), toolBarWindow.getHeight(), 3, 3));
    }

    return toolBarWindow;
  }

  private static JPanel getToolBarPanel() {
    GridBagLayout gbaglayout = new GridBagLayout();
    JPanel panel = new JPanel(gbaglayout);
    panel.setCursor(CaptureScreen.getColorfulCursor());
    panel.setBackground(backColor);
    Insets inset = new Insets(3, 2, 3, 2);
    //manager panel
    int index = 0;
    GridBagConstraints constraints = new GridBagConstraints(index++, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
      GridBagConstraints.BOTH, inset, 0, 0);
    //重做
    JButton backBtn = new ToolButton(new ImageIcon(CaptureScreen.class.getResource("/images/back.png")));
    backBtn.setActionCommand("BACK");
    doButtonAction(backBtn);
    gbaglayout.setConstraints(backBtn, constraints);
    panel.add(backBtn);
    //另存为按钮
    JButton saveBtn = new ToolButton(new ImageIcon(CaptureScreen.class.getResource("/images/save.png")));
    saveBtn.setActionCommand("SAVE");
    doButtonAction(saveBtn);
    saveBtn.setToolTipText("另存为...");
    constraints = new GridBagConstraints(index++, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
      inset, 0, 0);
    gbaglayout.setConstraints(saveBtn, constraints);
    panel.add(saveBtn);
    //分隔线
    JLabel vertical = new JLabel(new ImageIcon(CaptureScreen.class.getResource("/images/vertical.png")));
    constraints = new GridBagConstraints(index++, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
      inset, 0, 0);
    gbaglayout.setConstraints(vertical, constraints);
    panel.add(vertical);
    //取消按钮
    JButton cancleBtn = new ToolButton(new ImageIcon(CaptureScreen.class.getResource("/images/cancle.png")));
    cancleBtn.setActionCommand("QUIT");
    doButtonAction(cancleBtn);
    cancleBtn.setToolTipText("退出截图");
    constraints = new GridBagConstraints(index++, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
      inset, 0, 0);
    gbaglayout.setConstraints(cancleBtn, constraints);
    panel.add(cancleBtn);

    //完成按钮
    JButton okBtn = new ToolButton("完成");
    okBtn.setActionCommand("OK");
    doButtonAction(okBtn);
    okBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    okBtn.setIcon(new ImageIcon(CaptureScreen.class.getResource("/images/ok.png")));
    constraints = new GridBagConstraints(index++, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
      inset, 0, 0);
    gbaglayout.setConstraints(okBtn, constraints);
    panel.add(okBtn);

    return panel;
  }

  private static void doButtonAction(JButton btn) {
    btn.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if ("BACK".equals(actionCommand))
          CaptureScreen.doBack();
        else if ("SAVE".equals(actionCommand))
          CaptureScreen.doSaveAs();
        else if ("OK".equals(actionCommand)) {
        	String path=CaptureScreen.doAutoSave();
        	String imageBase64 = ImageToBase64.ImageToBase64(path);
        	System.out.println(imageBase64);
        	String method = String.format(TrayBridge.shotPhotoCallback,imageBase64);
        	BrowserManager.getInstance().getBrowser().executeJavaScript(method);
        }else if ("QUIT".equals(actionCommand))
            CaptureScreen.doQuit();
        }
//          CaptureScreen.doSaveToClipboard();
//        	CaptureScreen.doAutoSave();
        
    });
  }

  /**
   * @describe 面板上的按钮
   * @author suyanga
   * @date 2015年5月9日 下午11:04:54
   */
  private static class ToolButton extends JButton {

    private static final long serialVersionUID = 1L;

    public ToolButton(String string) {
      super(string);
      init();
    }

    public ToolButton(ImageIcon imageIcon) {
      super(imageIcon);
      init();
    }

    private void init() {
      setContentAreaFilled(false);
      setFocusPainted(false);
      setBorder(new EmptyBorder(1, 5, 1, 5));
      setMargin(new Insets(0, 0, 0, 0));
    }

    @Override
    public void paintComponent(Graphics g) {
      ButtonModel bm = getModel();
      //鼠标经过按钮时，绘制灰色边框
      if (bm.isRollover()) {
        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 2, 2);
      }
      //按钮按下时用灰色填充，透明度100。
      if (bm.isPressed()) {
        g.setColor(ColorUtil.colorToAlphaColor(Color.LIGHT_GRAY, 100));
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 2, 2);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 2, 2);
      }
      super.paintComponent(g);
    }
  }

  /**
   * @describe 设置工具面板window显示情况
   * @param window 就是getToolBarWindow()，不过是当参数传进来
   * @param visible
   * @param rec 截取的图片的区域
   * @author suyanga
   * @date 2015年5月9日 下午11:10:03
   */
  public static void setWindowVisible(JWindow window, boolean visible, Rectangle rec) {
    if (!visible) {
      window.setVisible(visible);
    } else {
      int px = rec.x + rec.width - window.getWidth();
      int py = rec.y + rec.height + 5;
      if (px < 0)
        px = 0;
      if (py + window.getHeight() > CaptureScreen.getScreenHeight())
        py = rec.y - window.getHeight() - 5;
      if (py < 0)
        py = 0;
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
    getToolBarWindow(null).setLocationRelativeTo(null);
    getToolBarWindow(null).setVisible(true);
  }
}
