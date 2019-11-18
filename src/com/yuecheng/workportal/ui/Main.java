package com.yuecheng.workportal.ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.bridge.BrowserBridge;
import com.yuecheng.workportal.tools.Constant;
/**
 * 
 * 创建闪动的托盘图像
 * 
 * @author Everest
 *
 */
public class Main extends JFrame{
	private static final long serialVersionUID = -3115128552716619277L;
	private static final String SERVER_URL = "http://yctestportalweb.yuechenggroup.com/";
	private SystemTray sysTray;// 当前操作系统的托盘对象
	private TrayIcon trayIcon;// 当前对象的托盘
	private ImageIcon icon = null;
	private TrayShakeUI trayThread;
	public static ResourceBundle RES_BUNDLE = ResourceBundle.getBundle("msg_zh_CN",Locale.getDefault());
	public static boolean isOpen = false;
	/**
	 * 初始化窗体的方法
	 */
	public void init() {
//        this.setUndecorated(true);
		this.setTitle(RES_BUNDLE.getString(Constant.MainFrame_Title));
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Integer minScreenWidth = (int) (dimension.width*0.66);
		Integer minScreenHeight = (int)(dimension.height*0.7);
		Integer screenWidth = (int) (dimension.width*0.76);
		Integer screenHeight = (int)(dimension.height*0.8);
		this.setMinimumSize(new Dimension(minScreenWidth, minScreenHeight)); 
		this.setSize(screenWidth, screenHeight);
		Browser browser = BrowserManager.getInstance().getBrowser();
		BrowserManager.getInstance().setMain(this);
		BrowserView view = new BrowserView(browser);
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				stopShake();
				isOpen = true;
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				isOpen = false;
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				isOpen = false;
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				isOpen = false;
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			
		});
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
//		this.setLocationByPlatform(true); 
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.add(view, BorderLayout.CENTER);
		
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLocationRelativeTo(null);
	

		browser.addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				if (event.isMainFrame()) {
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					BrowserBridge browserBridge =new BrowserBridge(Main.this);
					window.asObject().setProperty("BrowserBridge", browserBridge);
				}
			}
		});
		
//		browser.loadURL(getRes("res/test.html").toString());
		browser.loadURL(SERVER_URL);
		Font font = new Font("微软雅黑", Font.PLAIN, 14);
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key.toString().toLowerCase().contains(".font")) {
				UIManager.put(key, font);
			}
		}

		if (SystemTray.isSupported()) {
			this.createTrayIcon();// 创建托盘对象
			addTrayIcon();
		}
		this.setVisible(true);
	}

	/**
	 * 添加托盘的方法
	 */
	public void addTrayIcon() {
		try {
			sysTray.add(trayIcon);// 将托盘添加到操作系统的托盘
			setVisible(false); // 使得当前的窗口隐藏
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 创建系统托盘的对象 步骤: 1,获得当前操作系统的托盘对象 2,创建弹出菜单popupMenu 3,创建托盘图标icon
	 * 4,创建系统的托盘对象trayIcon
	 */
	JMenuItem openItem = null;
	JMenuItem debugItem  = null;
	JMenuItem serverItem  = null;
	JMenuItem exitItem  = null;
	JMenuItem clearCacheItem = null;
	JMenuItem refreshItem = null;
	public void createTrayIcon() {
		sysTray = SystemTray.getSystemTray();// 获得当前操作系统的托盘对象
		icon = new ImageIcon(getRes("res/tray.png"));// 托盘图标
		JPopupMenu popupMenu = new JPopupMenu();// 弹出菜单
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		openItem = new JMenuItem(RES_BUNDLE.getString(Constant.Open_Item));
		debugItem = new JMenuItem(RES_BUNDLE.getString(Constant.Debug_Item));
		serverItem = new JMenuItem(RES_BUNDLE.getString(Constant.Server_Item));
		clearCacheItem = new JMenuItem(RES_BUNDLE.getString(Constant.ClearCache_Item));
		refreshItem = new JMenuItem(RES_BUNDLE.getString(Constant.Refresh_Item));
		exitItem = new JMenuItem(RES_BUNDLE.getString(Constant.Exit_Item));
		popupMenu.add(openItem);
		popupMenu.add(debugItem);
		popupMenu.add(serverItem);
		popupMenu.add(clearCacheItem);
		popupMenu.add(refreshItem);
		popupMenu.add(exitItem);
		// 为弹出菜单项添加事件
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.this.setState(JFrame.NORMAL);
				Main.this.setVisible(true); // 显示窗口
				Main.this.toFront(); // 显示窗口到最前端
				stopShake(); // 消息打开了
			}
		});
		//清空缓存
		clearCacheItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BrowserManager.getInstance().getBrowser().getCacheStorage().clearCache();
				BrowserManager.getInstance().getBrowser().reload();
			}
		});
		//刷新
		refreshItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BrowserManager.getInstance().getBrowser().reload();
			}
		});
		debugItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugBrowser(true);
			}
		});
		serverItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 消息对话框无返回, 仅做通知作用
				String inputContent = JOptionPane.showInputDialog(Main.this,"输入URL地址",SERVER_URL);
				if(inputContent!=null&&!inputContent.trim().equals("")) {
					BrowserManager.getInstance().getBrowser().loadURL(inputContent);
				}
			}
		});
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayIcon = new TrayIcon(icon.getImage(), RES_BUNDLE.getString(Constant.MainFrame_Title));
		trayIcon.setImageAutoSize(true);
		/** 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口 */
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.setLocation(e.getX(), e.getY());
					popupMenu.setInvoker(popupMenu);
					popupMenu.setVisible(true);
				}else if(SwingUtilities.isLeftMouseButton(e)) {
					Main.this.setVisible(true); // 显示窗口
					Main.this.setState(JFrame.NORMAL);
					Main.this.toFront();
					stopShake(); // 消息打开了
				}
			}
		});
	}

	/**
	 * 主窗口不是最前置活跃就开始闪动托盘和任务栏
	 */
	public void startShake() {
		if(SystemTray.isSupported()&&!isOpen){
			stopShake();
			trayThread = new TrayShakeUI(this,trayIcon,icon);
			trayThread.start();
		}
	}
	
	/**
	 * 
	 */
	public void stopShake() {
		if(trayThread!=null) {
			trayThread.stopShake();
			trayThread.stop();
			trayThread = null;
		}
	}
	
	public Main() {
		Image image = this.getToolkit().getImage(getRes("res/tray.png"));
		this.setIconImage(image);
		init();
	}

	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}

	JFrame debugFrame;
	public void debugBrowser(boolean isDebug) {
		if(isDebug) {
			if(debugFrame!=null) {
				debugFrame.setVisible(true);
			}else {
				Browser debugBrowser = new Browser();
		        BrowserView view2 = new BrowserView(debugBrowser);
		     // Gets URL of the remote Developer Tools web page for browser1 instance.
		        String remoteDebuggingURL = BrowserManager.getInstance().getBrowser().getRemoteDebuggingURL();

		        debugFrame = new JFrame();
		        debugFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		        debugFrame.add(view2, BorderLayout.CENTER);
		        debugFrame.setSize(700, 500);
		        debugFrame.setLocationRelativeTo(null);
		        debugFrame.setVisible(true);
		        debugBrowser.loadURL(remoteDebuggingURL);
			}
		}else {
			if(debugFrame!=null)
				debugFrame.dispose();
		}
	}

	public void switchLanguage(ResourceBundle bundle) {
		Main.RES_BUNDLE = bundle;
		this.setTitle(bundle.getString(Constant.MainFrame_Title));
		this.openItem.setText(bundle.getString(Constant.Open_Item));
		this.debugItem.setText(bundle.getString(Constant.Debug_Item));
		this.serverItem.setText(bundle.getString(Constant.Server_Item));
		this.clearCacheItem.setText(bundle.getString(Constant.ClearCache_Item));
		this.refreshItem.setText(bundle.getString(Constant.Refresh_Item));
		this.exitItem.setText(bundle.getString(Constant.Exit_Item));
		this.trayIcon.setToolTip(bundle.getString(Constant.MainFrame_Title));
	}
}
