package com.yuecheng.workportal.ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.bridge.BrowserBridge;

/**
 * 
 * 创建闪动的托盘图像
 * 
 * @author Everest
 *
 */
public class Main extends JFrame{
	private static final long serialVersionUID = -3115128552716619277L;
	private SystemTray sysTray;// 当前操作系统的托盘对象
	private TrayIcon trayIcon;// 当前对象的托盘
	private ImageIcon icon = null;
	private TrayShakeUI trayThread;
	
	/**
	 * 初始化窗体的方法
	 */
	public void init() {
		 // Specifies remote debugging port for remote Chrome Developer Tools.
        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");
//        this.setUndecorated(true);
		this.setTitle("乐成工作台");
		this.setSize(1280, 800);
		Browser browser = BrowserManager.getInstance().getBrowser();
		BrowserView view = new BrowserView(browser);
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				stopShake();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			
		});
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
//		this.setLocationByPlatform(true); 
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
		
		//网页跳出拦截
		browser.setPopupHandler(new PopupHandler() {
			@Override
			public PopupContainer handlePopup(PopupParams popupParams) {
//				browser.loadURL(popupParams.getURL());
				//原理: 通过执行CMD命令,来实现
	            String str = "cmd /c start iexplore "+popupParams.getURL();
	            try {
	                Runtime.getRuntime().exec(str);
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
				return null;
			}
		});
		browser.loadURL(getRes("res/test.html").toString().replace("file:/", ""));
//		browser.loadURL("http://office.yuechenggroup.com/");
		Font font = new Font("微软雅黑", Font.PLAIN, 12);
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
	public void createTrayIcon() {
		sysTray = SystemTray.getSystemTray();// 获得当前操作系统的托盘对象
		icon = new ImageIcon(getRes("res/tray.png"));// 托盘图标
		JPopupMenu popupMenu = new JPopupMenu();// 弹出菜单
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		JMenuItem mi = new JMenuItem("打开");
		JMenuItem debug = new JMenuItem("调试模式");
		JMenuItem server = new JMenuItem("切换服务");
		JMenuItem exit = new JMenuItem("退出");
		popupMenu.add(mi);
		popupMenu.add(debug);
		popupMenu.add(server);
		popupMenu.add(exit);
		// 为弹出菜单项添加事件
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.this.setExtendedState(JFrame.NORMAL);
				Main.this.setVisible(true); // 显示窗口
				Main.this.toFront(); // 显示窗口到最前端
				stopShake(); // 消息打开了
			}
		});
		debug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugBrowser(true);
			}
		});
		server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 消息对话框无返回, 仅做通知作用
				String inputContent = JOptionPane.showInputDialog(Main.this,"输入URL地址","http://yctestportalweb.yuechenggroup.com/");
				if(inputContent!=null&&!inputContent.trim().equals("")) {
					BrowserManager.getInstance().getBrowser().loadURL(inputContent);
				}else {
					JOptionPane.showMessageDialog(null, "地址不能为空", "提示", JOptionPane.WARNING_MESSAGE );
				}
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayIcon = new TrayIcon(icon.getImage(), "乐成工作平台");
		trayIcon.setImageAutoSize(true);
		/** 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口 */
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON1) { // 鼠标单机
					Main.this.setExtendedState(JFrame.NORMAL);
					Main.this.setVisible(true); // 显示窗口
					Main.this.toFront();
					stopShake(); // 消息打开了
				}  
			}

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
				}
			}
		});
	}

	/**
	 * 主窗口不是最前置活跃就开始闪动托盘和任务栏
	 */
	public void startShake() {
		if(!this.isActive()&&SystemTray.isSupported()){
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
}
