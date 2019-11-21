package com.yuecheng.workportal.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.listener.WindowListenerDecorator;
import com.yuecheng.workportal.tools.Constant;
import com.yuecheng.workportal.tools.StringUtils;

public class DonwLoadDialog {
	private final static Log logger = LogFactory.getLog(DonwLoadDialog.class);
	public static DonwLoadDialog ProgressDialog;
	public JFrame dialog;
	public JButton closeBtn;
	public JProgressBar progressBar;
	public JFrame main;
	public boolean isShow = false;
    ProgressBarRealized progressBarRealized;
    String url;
    String fileName;
	public DonwLoadDialog(JFrame main) {
		this.main = main;
		dialog = new JFrame();
		ImageIcon icon = new ImageIcon(getRes("images/download.png"));// 图标
		dialog.setIconImage(icon.getImage());
		// dialog.setUndecorated(true);
		dialog.addWindowListener(new WindowListenerDecorator() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 关闭对话框
				cancelDownLoad();
			}
		});
//		dialog.setModal(false);
		// 设置对话框的宽高
		dialog.setSize(280, 140);
		// 设置对话框大小不可改变
		dialog.setResizable(false);
		// 设置对话框相对显示的位置
		dialog.setLocationRelativeTo(main);
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(250, 30));
		// 设置进度的 最小值 和 最大值
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		// 设置当前进度值
		progressBar.setValue(0);
		// 绘制百分比文本（进度条中间显示的百分数）
		progressBar.setStringPainted(true);

		// 创建一个按钮用于关闭对话框
		closeBtn = new JButton();
		closeBtn.setPreferredSize(new Dimension(120, 30));
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 关闭对话框
				cancelDownLoad();
			}
		});
		// 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
		JPanel panel = new JPanel();// 默认为0，0；水平间距10，垂直间距5
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalStrut(14)); // 采用y布局时，添加固定高度组件隔开
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));// 默认为居中;水平间距10，垂直间距5
		panel1.add(progressBar);
		// 添加组件到面板
		panel.add(panel1);
		JPanel panel2 = new JPanel();
		panel2.add(closeBtn);
		panel.add(Box.createVerticalStrut(16)); // 采用y布局时，添加固定高度组件隔开
		panel.add(panel2);
		// 设置对话框的内容面板
		dialog.setContentPane(panel);
	}

	/**
	 * 创建一个进度条对话框
	 * 
	 * @param main
	 * @param title
	 * @return
	 */
	public static DonwLoadDialog createProgressDialog(JFrame main) {
		if (ProgressDialog == null) {
			ProgressDialog = new DonwLoadDialog(main);
		}
		return ProgressDialog;
	}

	/**
	 * 显示
	 */
	public void startDownLoad(String url,String fileName) {
		if(isShow) return;
		this.url = url;
		this.fileName = fileName;
		dialog.setTitle(Main.RES_BUNDLE.getString(Constant.LOADING));
		closeBtn.setText(Main.RES_BUNDLE.getString(Constant.CLOSE));
		isShow = true;
		progressBar.setValue(0);
		progressBarRealized = new ProgressBarRealized();
		progressBarRealized.execute();
		// 显示对话框
		dialog.setVisible(true);
	}
	
	/**
	 * 关闭
	 */
	public void cancelDownLoad() {
		isShow = false;
		if (progressBarRealized != null) {
			progressBarRealized.cancel(true);
			progressBarRealized = null;
		}
		dialog.dispose();
	}
	class ProgressBarRealized extends SwingWorker<File, Integer> {
        @Override
        //后台任务在此方法中实现
        protected File doInBackground() throws Exception {
        	File file = null;
        	long receiveFileSize = 0;
        	try {
    			HttpClient client = HttpClientBuilder.create().build();//获取DefaultHttpClient请求
    			HttpGet httpget = new HttpGet(url);
    			HttpResponse response = client.execute(httpget);
     
    			HttpEntity entity = response.getEntity();
    			InputStream is = entity.getContent();
    			long fileSize = entity.getContentLength();
    			file =StringUtils.getUserDownloads(fileName);;
    			//如果文件夹不存在就创建
    			if (!file.getParentFile().exists()) {
    			  file.getParentFile().mkdirs();
    			}
    			if(file.exists()) {
    				file.delete();
            	}
    			FileOutputStream fileout = new FileOutputStream(file);
    			/**
    			 * 根据实际运行效果 设置缓冲区大小
    			 */
    			byte[] buffer=new byte[10 * 1024];
    			int ch = 0;
    			while ((ch = is.read(buffer)) != -1) {
    				fileout.write(buffer,0,ch);
    				receiveFileSize += ch;
    				publish((int)(receiveFileSize * 100 / fileSize));
    				if(isCancelled()) {
    					break;
    				}
    			}
    			is.close();
    			fileout.flush();
    			fileout.close();
     
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	
            return file;
        }
        @Override
        //每次更新进度条的信息
        protected void process(List<Integer> chunks) {
        	int currentProgress = chunks.get(chunks.size() - 1);
        	progressBar.setValue(currentProgress);
        }
        @Override
        //任务完成后返回一个信息
        protected void done() {
        	logger.info("完成");
        	if(!isCancelled()) {
	    		try {
	    			File file = get();
	    			dialog.dispose();
	    			if(url.endsWith(".exe")) {
	    				BrowserManager.getInstance().openFile(file.getParent());
	    			}
	    			BrowserManager.getInstance().openFile(file.getAbsolutePath());
	    			
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	cancelDownLoad();
        }
    }
	
	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}
}
