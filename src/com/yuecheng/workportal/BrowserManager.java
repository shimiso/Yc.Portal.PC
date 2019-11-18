package com.yuecheng.workportal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
import com.yuecheng.workportal.tools.CMDUtil;
import com.yuecheng.workportal.tools.Constant;
import com.yuecheng.workportal.tools.StringUtils;
import com.yuecheng.workportal.ui.Main;

public class BrowserManager {
	Browser browser;
	Main main;
	static BrowserManager browserManager = null;
	 // 创建一个标签显示消息内容
	JDialog progressDialog;
    JProgressBar progressBar;
    int currentProgress = 0;
    String url;
	
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
		browser.setDownloadHandler(new DownloadHandler() {
			public boolean allowDownload(DownloadItem download) {
				if (StringUtils.isWindows()) {
					String fileName = download.getDestinationFile().getName();
					download.setDestinationFile(StringUtils.getUserDownloads(fileName));
				}
				
				download.addDownloadListener(new DownloadListener() {
					public void onDownloadUpdated(DownloadEvent event) {
						DownloadItem download = event.getDownloadItem();
						currentProgress = (int) (((float) download.getReceivedBytes() / download.getTotalBytes()) * 100);
						System.out.println("ReceivedBytes: " + download.getReceivedBytes() + " TotalBytes: "
								+ download.getTotalBytes() + " currentProgress: " + currentProgress);
						if (download.isCompleted()) {
							try {
								String url = download.getDestinationFile().getAbsolutePath();
								if (StringUtils.isMac()) {
									// 苹果的打开方式 
									String result = CMDUtil.excuteCMDCommand("/usr/bin/open "+url);
									if(result.contains("No application knows how to open")) {
										JOptionPane.showMessageDialog(main, Main.RES_BUNDLE.getString(Constant.No_APP_KNOWS), "",JOptionPane.WARNING_MESSAGE);  
									}
								} else if (StringUtils.isWindows()) {
									// windows的打开方式。 
									CMDUtil.excuteCMDCommand("rundll32 url.dll FileProtocolHandler "+url);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}  
						}
					}
				});
				
				showProgressDialog(Main.RES_BUNDLE.getString(Constant.LOADING));
				System.out.println("Dest file: " + download.getDestinationFile().getAbsolutePath());
				return true;
			}
			
		});
		browser.setPopupHandler(new PopupHandler() {
			public PopupContainer handlePopup(PopupParams popupParams) {
			    url = popupParams.getURL();
				//*******下面这样处理是为了避免在打开IE浏览器的时候带&号后面的参数被截掉的问题
				url = url.replaceAll("\"","\'");
				url = "\"" + url + "\"";
				if(StringUtils.isWindows()&&StringUtils.isOARequestUrl(url)) {
					// 原理: 通过执行CMD命令,来实现
					String str = "cmd /c start iexplore " + url;
					try {
						Runtime.getRuntime().exec(str);
					} catch (Exception ex) {
						ex.printStackTrace();
					} 
					return null;
				}else {
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
									browser.loadURL(url);
									frame.setVisible(true);
								}
							});
						}
					};
				}
			}
		});
	}

	/**
     * 显示一个自定义的对话框
     *
     * @param owner 对话框的拥有者
     * @param parentComponent 对话框的父级组件
     */
    public void showProgressDialog(String title) {
    	if(progressDialog!=null) {
			progressDialog.dispose();
			progressDialog = null;
		}
    	// 创建一个模态对话框
    	progressDialog = new JDialog(main, title, true);
//    	dialog.setUndecorated(true);
    	progressDialog.setModal(false);
        // 设置对话框的宽高
        progressDialog.setSize(250, 150);
        // 设置对话框大小不可改变
        progressDialog.setResizable(false);
        // 设置对话框相对显示的位置
        progressDialog.setLocationRelativeTo(main);
        progressBar = new JProgressBar();
        // 设置进度的 最小值 和 最大值
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        // 设置当前进度值
        progressBar.setValue(0);
        // 绘制百分比文本（进度条中间显示的百分数）
        progressBar.setStringPainted(true);
        // 模拟延时操作进度, 每隔 0.5 秒更新进度
        new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(currentProgress>=100) {
            		currentProgress =100;
            		 progressDialog.dispose();
            	}
	            progressBar.setValue(currentProgress);
            }
        }).start();
        // 创建一个按钮用于关闭对话框
        JButton closeBtn = new JButton(Main.RES_BUNDLE.getString(Constant.CLOSE));
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                progressDialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        // 添加组件到面板
        panel.add(progressBar);
        panel.add(closeBtn);
        // 设置对话框的内容面板
        progressDialog.setContentPane(panel);
        // 显示对话框
        progressDialog.setVisible(true);
    }

	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}
}
