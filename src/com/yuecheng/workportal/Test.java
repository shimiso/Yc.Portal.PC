package com.yuecheng.workportal;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test {
	 // 创建一个标签显示消息内容
        static JProgressBar progressBar = new JProgressBar();
	 	private static final int MIN_PROGRESS = 0;
	    private static final int MAX_PROGRESS = 100;
	    static JDialog dialog;
	    private static int currentProgress = MIN_PROGRESS;
	    private static void showCustomDialog(Frame owner, Component parentComponent) {
	        // 创建一个模态对话框
	        dialog = new JDialog(owner, "提示", true);
	        // 设置对话框的宽高
	        dialog.setSize(250, 150);
	        // 设置对话框大小不可改变
	        dialog.setResizable(false);
	        // 设置对话框相对显示的位置
	        dialog.setLocationRelativeTo(parentComponent);
	        // 设置进度的 最小值 和 最大值
	        progressBar.setMinimum(0);
	        progressBar.setMaximum(100);
	        // 设置当前进度值
	        progressBar.setValue(0);
	        // 绘制百分比文本（进度条中间显示的百分数）
	        progressBar.setStringPainted(true);
	        // 模拟延时操作进度, 每隔 0.5 秒更新进度
	        new Timer(500, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                currentProgress++;
	                if (currentProgress > MAX_PROGRESS) {
	                    currentProgress = MIN_PROGRESS;
	                }
	                progressBar.setValue(currentProgress);
	            }
	        }).start();
	        // 创建一个按钮用于关闭对话框
	        JButton okBtn = new JButton("确定");
	        okBtn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // 关闭对话框
	                dialog.dispose();
	            }
	        });
	        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
	        JPanel panel = new JPanel();
	        // 添加组件到面板
	        panel.add(progressBar);
	        panel.add(okBtn);
	        // 设置对话框的内容面板
	        dialog.setContentPane(panel);
	        // 显示对话框
	        dialog.setVisible(true);
	    }
	    
	    public static void main(String[] args) {
//	        JFrame jf = new JFrame("测试窗口");
//	        jf.setSize(250, 250);
//	        jf.setLocationRelativeTo(null);
//	        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//	        jf.setVisible(true);
//	        showCustomDialog(jf,jf);

//	    	Key                     Meaning
//	    	-------------------     ------------------------------
//	    	"file.separator"        File separator (e.g., "/")
//	    	"java.class.path"       Java classpath
//	    	"java.class.version"    Java class version number
//	    	"java.home"             Java installation directory
//	    	"java.vendor"           Java vendor-specific string
//
//	    	"java.vendor.url"       Java vendor URL
//	    	"java.version"          Java version number
//	    	"line.separator"        Line separator
//	    	"os.arch"               Operating system architecture
//	    	"os.name"               Operating system name
//
//	    	"path.separator"        Path separator (e.g., ":")
//	    	"user.dir"              User's current working directory
//	    	"user.home"             User home directory
//	    	"user.name"             User account name
	    	
	    	System.out.println("user.name: "+ System.getProperty("user.name"));
	    	System.out.println("user.home: "+ System.getProperty("user.home"));
	    	System.out.println("user.dir: "+ System.getProperty("user.dir"));
	    }
}

