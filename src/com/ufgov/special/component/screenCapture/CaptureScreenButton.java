package com.ufgov.special.component.screenCapture;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @describe 截屏按钮
 * @author suyanga
 * @date 2015年5月10日 下午2:06:34
 */
public class CaptureScreenButton extends JButton implements ActionListener {

  private static final long serialVersionUID = 1L;

  /**
   * 截屏时窗口是否隐藏，默认不隐藏。
   */
  private boolean isWindowHideWhenCapture = false;

  /**
   * 截图文件路径
   */
  private String filePath = "";

  /**
   * 按钮所在的父级窗口容器
   */
  private Window ancestorWindow;

  public CaptureScreenButton() {
  }

  public CaptureScreenButton(String text) {
    super(text);
  }

  public CaptureScreenButton(Icon icon) {
    super(icon);
  }

  public CaptureScreenButton(String text, Icon icon) {
    super(text, icon);
  }

  /**
   * @describe
   * @param isHideWindowWhenCapture 
   * @author suyanga
   * @date 2015年5月10日 下午2:51:49
   */
  public void setWindowHideWhenCapture(boolean isWindowHideWhenCapture) {
    this.isWindowHideWhenCapture = isWindowHideWhenCapture;
  }

  /**
   * @describe 找component的祖先Container
   * @param component
   * @return 
   * @author suyanga
   * @date 2015年4月13日 上午12:34:16
   */
  public static Window getWindowAncestor(final Component component) {
    if (component == null) {
      return null;
    }
    if (component instanceof Window) {
      return (Window) component;
    }
    for (Container p = component.getParent(); p != null; p = p.getParent()) {
      if (p instanceof Window) {
        return (Window) p;
      }
    }
    return null;
  }

  public String getFilePath() {
    return filePath;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (ancestorWindow == null)
      ancestorWindow = getWindowAncestor(CaptureScreenButton.this);
    if (isWindowHideWhenCapture) {
      if (ancestorWindow != null) {
        if (ancestorWindow instanceof Frame) {
          ((Frame) ancestorWindow).setExtendedState(Frame.ICONIFIED);
        } else if (ancestorWindow instanceof Dialog)
          ((Dialog) ancestorWindow).dispose();
      }
      //窗口隐藏后，休眠300毫秒，确认窗口隐藏了才去截图
      try {
        Thread.sleep(300);
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    CaptureScreen.initFilePath();
    CaptureScreen.refreshBackgroud();
    CaptureScreen.getParentDialog(ancestorWindow).setVisible(true);
    filePath = CaptureScreen.getFilePath();
    //重置窗口
    if (isWindowHideWhenCapture) {
      if (ancestorWindow != null) {
        if (ancestorWindow instanceof Frame)
          ((Frame) ancestorWindow).setState(Frame.NORMAL);
        else if (ancestorWindow instanceof Dialog)
          ((Dialog) ancestorWindow).setVisible(true);
      }
    }
  }
}
