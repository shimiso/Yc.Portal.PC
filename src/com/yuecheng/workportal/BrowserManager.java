package com.yuecheng.workportal;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.events.DisposeEvent;
import com.teamdev.jxbrowser.chromium.events.DisposeListener;
import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class BrowserManager {
	Browser browser;
	static BrowserManager browserManager = null;

	private BrowserManager() {
		System.setProperty("jxbrowser.chromium.sandbox", "true");
		browser = new Browser();
		setPopupHandler();
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
								frame.setSize(1200, 768);
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

	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}
}
