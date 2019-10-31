package com.yuecheng.workportal;

import java.applet.Applet;
import java.applet.AudioClip;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.yuecheng.workportal.bridge.CaptureScreenBridge;
import com.yuecheng.workportal.bridge.LowerRightPromptBridge;
import com.yuecheng.workportal.bridge.TrayBridge;

/**
 * 
 * 创建闪动的托盘图像
 * 
 * @author Everest
 *
 */
public class Main extends JFrame implements Runnable {
	private static final long serialVersionUID = -3115128552716619277L;
	private SystemTray sysTray;// 当前操作系统的托盘对象
	private TrayIcon trayIcon;// 当前对象的托盘
	private ImageIcon icon = null;
	private static int count = 1; // 记录消息闪动的次数
	private boolean flag = false; // 是否有新消息
	private static int times = 1; // 接收消息次数
	
	/**
	 * 初始化窗体的方法
	 */
	public void init() {
		this.setTitle("乐成工作台");
		Browser browser = BrowserManager.getInstance().getBrowser();
		BrowserView view = new BrowserView(browser);

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.add(view, BorderLayout.CENTER);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLocationRelativeTo(null);

		browser.addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				if (event.isMainFrame()) {
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					// 给jswindows对象添加一个扩展的属性
					CaptureScreenBridge captureScreenBridge = new CaptureScreenBridge(Main.this);
					window.asObject().setProperty("CaptureScreenBridge", captureScreenBridge);
					
					TrayBridge trayBridge =new TrayBridge(Main.this);
					window.asObject().setProperty("TrayBridge", trayBridge);
					
					LowerRightPromptBridge lowerRightPromptBridge =new LowerRightPromptBridge();
					window.asObject().setProperty("LowerRightPromptBridge", lowerRightPromptBridge);
					// 调用前端页面js
					if (event.isMainFrame()) {
						// browser.executeJavaScript("alert('java調用了js')");
					}
				}
			}
		});
//		 browser.loadURL("E:\\eclipse-workspace\\JxBrowserTest\\src\\res\\test.html");
		browser.loadURL("http://yctestportalweb.yuechenggroup.com/");
		this.setSize(1280, 800);
		Font font = new Font("微软雅黑", Font.PLAIN, 12);
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key.toString().toLowerCase().contains(".font")) {
				UIManager.put(key, font);
			}
		}

		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 添加窗口最小化事件,将托盘添加到操作系统的托盘
		// this.addWindowListener(new WindowAdapter() {
		// public void windowIconified(WindowEvent e) {
		// addTrayIcon();
		// }
		// });
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
			new Thread(this).start();
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
		JMenuItem exit = new JMenuItem("退出");
		popupMenu.add(mi);
		popupMenu.add(exit);
		// 为弹出菜单项添加事件
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.this.setExtendedState(JFrame.NORMAL);
				Main.this.setVisible(true); // 显示窗口
				Main.this.toFront(); // 显示窗口到最前端
				flag = false; // 消息打开了
				count = 0;
				times++;
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayIcon = new TrayIcon(icon.getImage(), "乐成工作平台");
		trayIcon.setImageAutoSize(true);
		
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
		        }
		    }
		});
		
		/** 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口 */
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // 鼠标双击
					Main.this.setExtendedState(JFrame.NORMAL);
					Main.this.setVisible(true); // 显示窗口
					Main.this.toFront();
					flag = false; // 消息打开了
					count = 0;
					times++;
				}
			}
		});
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Main() {
		Image image = this.getToolkit().getImage(getRes("res/tray.png"));
		this.setIconImage(image);
		init();
	}

	public URL getRes(String str) {
		return this.getClass().getClassLoader().getResource(str);
	}

	/**
	 * 线程控制闪动
	 */
	public void run() {
		while (true) {
			if (flag) { // 有新消息
				try {
					if (count == 1) {
						// 播放消息提示音
						// AudioPlayer p = new AudioPlayer(getRes("file:com/sound/Msg.wav"));
						// p.play(); p.stop();
						try {
							AudioClip p = Applet.newAudioClip(new URL("file:sound/msg.wav"));
							p.play();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
					// 闪动消息的空白时间
					this.setTitle("");
					this.trayIcon.setImage(new ImageIcon("").getImage());
					Thread.sleep(500);
					// 闪动消息的提示图片
					this.trayIcon.setImage(icon.getImage());
					this.setTitle("乐成工作台");
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
				count++;
			} else { // 无消息或是消息已经打开过
				this.trayIcon.setImage(icon.getImage());
				// 每隔5秒闪动一次，测试时使用
				// try {
				// Thread.sleep(5000);
				// flag = true;
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}
		}
	}
}
