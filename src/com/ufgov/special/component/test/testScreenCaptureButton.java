package com.ufgov.special.component.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.FlowLayout;

import com.ufgov.special.component.screenCapture.CaptureScreenButton;

import javax.swing.JTextField;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class testScreenCaptureButton extends JFrame {

  private static final long serialVersionUID = 1L;

  private JPanel contentPane;

  private JTextField textField;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          testScreenCaptureButton frame = new testScreenCaptureButton();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public testScreenCaptureButton() {
    setTitle("截屏演示");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 500, 300);
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    setContentPane(contentPane);
    contentPane.setLayout(new GridLayout(2, 1));

    //盛放按钮的面板
    JPanel buttonPanel = new JPanel();
    contentPane.add(buttonPanel);
    buttonPanel.setBorder(new EmptyBorder(50, 0, 0, 0));
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    //正常截屏按钮
    CaptureScreenButton screenCaptureButton = new CaptureScreenButton("截屏");
    doButtonAction(screenCaptureButton);
    buttonPanel.add(screenCaptureButton);

    //截屏时隐藏窗体
    CaptureScreenButton screenCaptureButton1 = new CaptureScreenButton("截屏时隐藏窗体");
    screenCaptureButton1.setWindowHideWhenCapture(true);
    doButtonAction(screenCaptureButton1);
    buttonPanel.add(screenCaptureButton1);

    //放置 textField
    JPanel textFieldPanel = new JPanel();
    contentPane.add(textFieldPanel);
    textFieldPanel.setBorder(new EmptyBorder(20, 40, 0, 40));
    textFieldPanel.setLayout(new BorderLayout(0, 0));

    textField = new JTextField(200);
    textFieldPanel.add(textField, BorderLayout.NORTH);
    textField.setColumns(10);
  }

  /**
   * @describe 实际使用时方法
   * @param button 
   * @author suyanga
   * @date 2015年5月10日 下午5:43:08
   */
  private void doButtonAction(final CaptureScreenButton button) {
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        button.actionPerformed(e);
        //以下代码为正常使用时截屏后的操作
        textField.setText(button.getFilePath());
      }
    });
  }

}
