package com.yuecheng.workportal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
import com.yuecheng.workportal.listener.DownloadCancelListener;
import com.yuecheng.workportal.tools.CMDUtil;
import com.yuecheng.workportal.tools.Constant;
import com.yuecheng.workportal.tools.StringUtils;
import com.yuecheng.workportal.ui.Main;
import com.yuecheng.workportal.ui.ProgressDialog;

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
	 * 打开文件或者应用
	 * @param fileUrl
	 */
	public void openFile(String fileUrl) {
		try {
			
			if (StringUtils.isMac()) {
				// 苹果的打开方式 
				String result = CMDUtil.excuteCMDCommand("/usr/bin/open "+fileUrl);
				if(result.contains("No application knows how to open")) {
					JOptionPane.showMessageDialog(main, Main.RES_BUNDLE.getString(Constant.No_APP_KNOWS), "",JOptionPane.WARNING_MESSAGE);  
				}
			} else if (StringUtils.isWindows()) {
				if(fileUrl.endsWith(".exe")) {
					// windows的打开方式。 
					CMDUtil.excuteCMDCommand(fileUrl);
				}else {
					// windows的打开方式。 
					CMDUtil.excuteCMDCommand("rundll32 url.dll FileProtocolHandler "+fileUrl);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(main, Main.RES_BUNDLE.getString(Constant.No_APP_KNOWS), "",JOptionPane.WARNING_MESSAGE);
		}  
	}
	
	/**
	 * Handling Pop-ups Swing
	 */
	public void setPopupHandler() {
		// 网页跳出拦截
		browser.setDownloadHandler(new DownloadHandler() {
			public boolean allowDownload(DownloadItem download) {
				System.out.println("开始下载: ");
				File destinationFile = StringUtils.getUserDownloads(download.getDestinationFile());
				download.setDestinationFile(destinationFile);
				
				ProgressDialog  progressDialog = ProgressDialog.createProgressDialog(main);
				System.out.println("isShow: "+progressDialog.isShow);
				if(progressDialog.isShow) {
					return true;
				}else {
					progressDialog.setDownloadCancelListener(new DownloadCancelListener() {
						@Override
						public void downLoadCancel() {
							download.cancel();
						}
					});
					progressDialog.show();
					
					download.addDownloadListener(new DownloadListener() {
						public void onDownloadUpdated(DownloadEvent event) {
							DownloadItem download = event.getDownloadItem();
							int currentProgress = (int) (((float) download.getReceivedBytes() / download.getTotalBytes())
									* 100);
							progressDialog.progressUpdated(currentProgress);
							System.out.println("ReceivedBytes: " + download.getReceivedBytes() + " TotalBytes: "
									+ download.getTotalBytes() + " currentProgress: " + currentProgress);
							if (download.isCompleted()&&!download.isCanceled()) {
								String fileUrl = download.getDestinationFile().getAbsolutePath();
								openFile(fileUrl);
							}
						}
					});
					
					System.out.println("Dest file: " + download.getDestinationFile().getAbsolutePath());
					return true;
				}
			}

		});
		
		browser.setPopupHandler(new PopupHandler() {
			public PopupContainer handlePopup(PopupParams popupParams) {
				String url = popupParams.getURL();
				if(StringUtils.isOARequestUrl(url)) {
					try {
						if(StringUtils.isWindows()) {
							//*******下面这样处理是为了避免在打开IE浏览器的时候带&号后面的参数被截掉的问题
							url = url.replaceAll("\"","\'");
							url = "\"" + url + "\"";
							CMDUtil.excuteCMDCommand("cmd /c start iexplore " + url);
						}else if(StringUtils.isMac()) {
							CMDUtil.excuteCMDCommand("/usr/bin/open -a safari "+url);
						}
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
									browser.loadURL(popupParams.getURL());
									frame.setVisible(true);
								}
							});
						}
					};
				}
			}
		});
	}

	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}
}
