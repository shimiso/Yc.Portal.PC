package com.yuecheng.workportal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.events.DisposeEvent;
import com.teamdev.jxbrowser.chromium.events.DisposeListener;
import com.teamdev.jxbrowser.chromium.events.DownloadEvent;
import com.teamdev.jxbrowser.chromium.events.DownloadListener;
import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.yuecheng.workportal.ui.Main;

public class BrowserManager {
	Browser browser;
	Main main;
	static BrowserManager browserManager = null;
	
	private BrowserManager() {
		System.setProperty("jxbrowser.chromium.sandbox", "true");
		// Specifies remote debugging port for remote Chrome Developer Tools.
		BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");
		BrowserPreferences.setUserAgent("jxbrowser");
		browser = new Browser();
		setPopupHandler();
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public static BrowserManager getInstance() {
		if (browserManager == null) {
			browserManager = new BrowserManager();
		}
		return browserManager;
	}

	public Browser getBrowser() {
		return browser;
	}

	/**
	 * Handling Pop-ups Swing
	 */
	public void setPopupHandler() {
		// 网页跳出拦截
		// browser.setPopupHandler(new PopupHandler() {
		// @Override
		// public PopupContainer handlePopup(PopupParams popupParams) {
		//// browser.loadURL(popupParams.getURL());
		// //原理: 通过执行CMD命令,来实现
		// String str = "cmd /c start iexplore "+popupParams.getURL();
		// try {
		// Runtime.getRuntime().exec(str);
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// return null;
		// }
		// });
		
		browser.setDownloadHandler(new DownloadHandler() {
			public boolean allowDownload(DownloadItem download) {
				showProgressDialog(main,main);
				download.addDownloadListener(new DownloadListener() {
					public void onDownloadUpdated(DownloadEvent event) {
						DownloadItem download = event.getDownloadItem();
						currentProgress = (int) (((float) download.getReceivedBytes() / download.getTotalBytes()) * 100);
						System.out.println("progress: " + currentProgress);
						System.out.println("ReceivedBytes: " + download.getReceivedBytes() + " TotalBytes: "
								+ download.getTotalBytes() + " progress: " + currentProgress);
						if (download.isCompleted()) {
//							System.out.println("Download is completed!");
//							Runtime runtime = Runtime.getRuntime();  
//							try {
//								String url = download.getDestinationFile().getAbsolutePath();
//								String osName = System.getProperty("os.name", "");
//								if (osName.startsWith("Mac OS")) {
//									// 苹果的打开方式 
//									Class fileMgr = Class.forName("com.apple.eio.FileManager");
//									Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
//									openURL.invoke(null, new Object[] { url });
//								} else if (osName.startsWith("Windows")) {
//									// windows的打开方式。 
//									Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler "+url);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}  
						}
					}
				});
				System.out.println("Dest file: " + download.getDestinationFile().getAbsolutePath());
				return true;
			}
			
		});
		browser.setPopupHandler(new PopupHandler() {
			public PopupContainer handlePopup(PopupParams params) {
				return new PopupContainer() {
					public void insertBrowser(final Browser browser, final Rectangle initialBounds) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								BrowserView browserView = new BrowserView(browser);
								browserView.setPreferredSize(initialBounds.getSize());

								final JFrame frame = new JFrame();
								frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
								Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
								Integer minScreenWidth = (int) (dimension.width * 0.66);
								Integer minScreenHeight = (int) (dimension.height * 0.7);
								Integer screenWidth = (int) (dimension.width * 0.76);
								Integer screenHeight = (int) (dimension.height * 0.8);
								frame.setMinimumSize(new Dimension(minScreenWidth, minScreenHeight));
								frame.setSize(screenWidth, screenHeight);
								Image image = frame.getToolkit().getImage(getRes("res/tray.png"));
								frame.setIconImage(image);
								frame.add(browserView, BorderLayout.CENTER);
								frame.setLocation(initialBounds.getLocation());
								browser.addTitleListener(new TitleListener() {
									public void onTitleChange(TitleEvent event) {
										frame.setTitle(event.getTitle());
									}
								});
								frame.addWindowListener(new WindowAdapter() {
									@Override
									public void windowClosing(WindowEvent e) {
										browser.dispose();
									}
								});

								browser.addDisposeListener(new DisposeListener<Browser>() {
									public void onDisposed(DisposeEvent<Browser> event) {
										frame.setVisible(false);
									}
								});
								browser.loadURL(params.getURL());
								frame.setVisible(true);
							}
						});
					}
				};
			}
		});
	}
	
	 // 创建一个标签显示消息内容
    JProgressBar progressBar;
    JDialog dialog;
    int currentProgress = 0;
	/**
     * 显示一个自定义的对话框
     *
     * @param owner 对话框的拥有者
     * @param parentComponent 对话框的父级组件
     */
    private void showProgressDialog(Frame owner, Component parentComponent) {
    	// 创建一个模态对话框
        dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(250, 150);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);
        progressBar = new JProgressBar();
        // 设置进度的 最小值 和 最大值
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        // 设置当前进度值
        progressBar.setValue(0);
        // 绘制百分比文本（进度条中间显示的百分数）
        progressBar.setStringPainted(true);
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
        // 模拟延时操作进度, 每隔 0.5 秒更新进度
        new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setValue(currentProgress);
            }
        }).start();
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }

	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}
}
