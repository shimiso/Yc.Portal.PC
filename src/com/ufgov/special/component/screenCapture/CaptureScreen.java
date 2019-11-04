package com.ufgov.special.component.screenCapture;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import com.ufgov.special.component.screenCapture.subUIS.MagnifierWindow;
import com.ufgov.special.component.screenCapture.subUIS.RectInfoWindow;
import com.ufgov.special.component.screenCapture.subUIS.ToolBarWindow;
import com.ufgov.special.component.util.ImageUtil;
import com.yuecheng.workportal.tools.GraphicsUtils;
import com.yuecheng.workportal.tools.ImageToBase64;

/**
 * @describe 截屏功能
 * @author suyanga
 * @date 2015年5月10日 上午12:12:14
 */
public class CaptureScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	/** 选区周围的敏感触点宽 */
	public final static int SENSITIVE_REC_WIDTH = 5;

	/** 选区周围的敏感触点高 */
	public final static int SENSITIVE_REC_HEIGHT = 5;

	/** 左上角敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_LEFT_TOP = 1;

	/** 左中敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_LEFT_CENTER = 2;

	/** 左下敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_LEFT_BOTTOM = 3;

	/** 上中敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_TOP_CENTER = 4;

	/** 下中敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_BOTTOM_CENTER = 5;

	/** 右上角敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_RIGHT_TOP = 6;

	/** 右中敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_RIGHT_CENTER = 7;

	/** 右下角敏感区标识符 */
	public final static int SENSITIVE_REC_IDENT_RIGHT_BOTTOM = 8;

	/** 在选择区内滚动滚轴(x1,y1)坐标X轴方向的位移量(绝对值) */
	public final static int DELTA_X_FOR_SCALE_SELECTED_AREA = 10;

	/** 存放敏感区标识符 */
	private int sensitive_rec_ident = 0;

	/** 选择区坐标原点X */
	public static int x = -1;

	/** 选择区坐标原点Y */
	public static int y = -1;

	/** 选择区右下角坐标X */
	public static int x1 = -1;

	/** 选择区右下角坐标Y */
	public static int y1 = -1;

	/** 当mouse在选择区内按下左键瞬间时距左上点x距离 */
	private static int x_rela = -1;

	/** 当mouse在选择区内按下左键瞬间时距左上点y距离 */
	private static int y_rela = -1;

	/** 当mouse在选择区内按下左键瞬间时距右下点x1距离 */
	private static int x1_rela = -1;

	/** 当mouse在选择区内按下左键瞬间时距右下点y1距离 */
	private static int y1_rela = -1;

	/** 当mouse到达敏感区内按下左键瞬间时的坐标X */
	private static int x_in_sensitive = -1;

	/** 当mouse到达敏感区内按下左键瞬间时的坐标Y */
	private static int y_in_sensitive = -1;

	/** 面板的背景图像(缓存用于每次重绘) */
	public static Image background = null;

	public static BufferedImage bufferedImage = null;

	/** 宿主窗口 */
	private static JDialog parentDialog = null;

	/** 是否重新确定选择区 */
	private boolean isDraw = false;

	/** 是否是选择区移动 */
	private boolean isMove = false;

	/** 是否是改变大小 */
	private boolean isZoom = false;

	public static CaptureScreen csStamdalone;

	private static Robot rbt;

	private static Toolkit toolKit = null;

	/**
	 * 彩色鼠标指针
	 */
	private static Cursor colorfulCursor;

	/**
	 * 选择区上的弹出框
	 */
	private static JPopupMenu popupMenu;

	/**
	 * 文件路径
	 */
	private static String filePath;

	static {
		try {
			rbt = new Robot();
			toolKit = Toolkit.getDefaultToolkit();
		} catch (Exception e) {
			System.out.println("无法实例化Robot!");
		}
	}

	private CaptureScreen() throws Exception {
		this.initGUI();
	}

	private void initGUI() {
		this.setDoubleBuffered(true);
		this.setSize(getScreenWidth(), getScreenHeight());
		this.doAddMouseListener();
		this.doAddMouseMotionListener();
	}

	/**
	 * 设置默认坐标(还原).
	 */
	public static void initLocation() {
		x = -1;
		y = -1;
		x1 = -1;
		y1 = -1;

		x_rela = -1;
		y_rela = -1;
		x1_rela = -1;
		y1_rela = -1;

		x_in_sensitive = -1;
		y_in_sensitive = -1;
	}

	public static void refreshBackgroud() {
		if (rbt != null) {
			// 利用机器人捕获整个屏幕快照
			background = rbt.createScreenCapture(
					new Rectangle(0, 0, (int) CaptureScreen.getScreenWidth(), (int) CaptureScreen.getScreenHeight()));
			bufferedImage = ImageUtil.toBufferedImage(CaptureScreen.background);
		} else {
			System.out.println("Robot引用为空，可能是无法实例化Robot对象！");
		}
	}

	/**
	 * 添加mouse监听事件.
	 * 
	 * @param com
	 *            JComponent
	 */
	private void doAddMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// 如果是左键
				if (e.getButton() == MouseEvent.BUTTON1) {
					// 出于Java底层API对mouse事件的捕获粒度影响,在诸如敏感区等如此小的区
					// 域内进行移动事件捕获要想准确是相当困难的(以下代码是本程序的解决方法:一旦
					// 确定在哪个区进行伸缩,直到释放前,无需再进行其它处理)
					if (isInsideSensitiveRec(e.getX(), e.getY())) {
						// 伸缩操作
						isZoom = true;

						// 确定伸缩操作的开始位置(以决定伸缩方式)
						if (isInsideRecLeftTop(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_LEFT_TOP;
						} else if (isInsideRecLeftCenter(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_LEFT_CENTER;
						} else if (isInsideRecLeftBottom(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_LEFT_BOTTOM;
						} else if (isInsideRecTopCenter(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_TOP_CENTER;
						} else if (isInsideRecBottomCenter(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_BOTTOM_CENTER;
						} else if (isInsideRecRightTop(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_RIGHT_TOP;
						} else if (isInsideRecRightCenter(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_RIGHT_CENTER;
						} else if (isInsideRecRightBottom(e.getX(), e.getY())) {
							sensitive_rec_ident = SENSITIVE_REC_IDENT_RIGHT_BOTTOM;
						}

						// 当mouse在敏感区内时设定首次参考坐标，以便进行选择区伸缩
						x_in_sensitive = e.getX();
						y_in_sensitive = e.getY();
					}
					// 当mouse在选择区内时设定首次参考坐标，以便进行选择区移动
					else if (isInSelectedArea(e.getX(), e.getY())) {
						isMove = true;

						x_rela = e.getX() - x;
						y_rela = e.getY() - y;

						x1_rela = x1 - e.getX();
						y1_rela = y1 - e.getY();
					}
					// 区域外时不更新选择区坐标原点,但更新右下角坐标(以便重绘)
					else if (!hasRectSelected()) {
						isDraw = true;

						x = e.getX();
						y = e.getY();
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				// mouse左键释放时选区矩形以确定(包括位置、大小),那么绘制它的左上角坐标
				// 和右下角坐标我们需根据不同情况进行调整(如从右上往左下画的情况,x1与x之
				// 间的差是负,这在本程序逻辑中是不能利用Graphics.drawRec()方法(本方法
				// 只能绘制x1-x>=0)的情况)进行绘制的:比如在选区确定后进行移动或进行快照
				// 时是不能对这些特殊情况下的从标用标准的API方法进行重绘或选区捕获的
				if (e.getButton() == MouseEvent.BUTTON1) {
					// 还原默认值
					isDraw = false;
					isMove = false;
					isZoom = false;
					sensitive_rec_ident = 0;

					// mouse左键释放时(x,y)与(x1,y1)的偏移量(矢量)
					int deltaX = x1 - x;
					int deltaY = y1 - y;

					// 从右上角往左下拖动mouse
					if (deltaX < 0 && deltaY > 0) {
						// 修正选区矩形的原点坐标(x,y)
						x = x - Math.abs(deltaX);
						// y = y;
					}
					// 从左下角往右上拖动
					else if (deltaX > 0 && deltaY < 0) {
						// x = x;
						y = y - Math.abs(deltaY);
					}
					// 从右下角往左上拖动
					else if (deltaX < 0 && deltaY < 0) {
						x = x - Math.abs(deltaX);
						y = y - Math.abs(deltaY);
					}
					// 正常情况(从左上角往右下拖动)
					else if (deltaX > 0 && deltaY > 0) {
						// x = x;
						// y = y;
					}

					// 修正选区矩形的(x1,y1)坐标
					x1 = x + Math.abs(deltaX);
					y1 = y + Math.abs(deltaY);

					if (hasRectSelected()) {
						setToolBarWindowVisible(true);
						setRectInfoWindowVisible(true);
					} else {
						setToolBarWindowVisible(false);
						setRectInfoWindowVisible(false);
					}
				}
			}

			public void mouseClicked(MouseEvent e) {
				// 如果是左键双击(在所选区内双击进行快照抓取)
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2
						&& isInSelectedArea(e.getX(), e.getY())) {
					doSaveToClipboard();
				}
				// 右键在选区外单击取消选区
				else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1 && hasRectSelected()
						&& !isInSelectedArea(e.getX(), e.getY())) {
					// 并还原mouses样式
					CaptureScreen.getParentDialog(null).setCursor(getColorfulCursor());
					// 坐标还原
					initLocation();
					// 工具按钮界面不可见
					setToolBarWindowVisible(false);
					// 放大镜可见
					setMagnifierWindowVisible(true, 0, 0);
					// 显示截图区域不可见
					setRectInfoWindowVisible(false);
					// 重绘屏幕
					repaint();
				}
				// 没有选取，右键单击- 退出截图
				else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1 && !hasRectSelected()) {
					doQuit();
				}
				// 选区内可弹出右键菜单
				else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1
						&& isInSelectedArea(e.getX(), e.getY())) {
					// 指定位置处显示菜单
					getPopupMenuOnSelectedRect().show(CaptureScreen.this, e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * 添加mouse移动监听事件.
	 * 
	 * @param com
	 *            JComponent
	 */
	private void doAddMouseMotionListener() {
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
					if (isZoom) {
						setMagnifierWindowVisible(true, 5, 0);
						// 位移(矢量)
						int deltaX = e.getX() - x_in_sensitive;
						int deltaY = e.getY() - y_in_sensitive;

						// 保留坐标以便下次参考
						x_in_sensitive = e.getX();
						y_in_sensitive = e.getY();

						// 进行拖放(伸缩)
						if (sensitive_rec_ident == SENSITIVE_REC_IDENT_LEFT_TOP) {
							x += deltaX;
							y += deltaY;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_LEFT_CENTER) {
							x += deltaX;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_LEFT_BOTTOM) {
							x += deltaX;
							y1 += deltaY;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_TOP_CENTER) {
							y += deltaY;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_BOTTOM_CENTER) {
							y1 += deltaY;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_RIGHT_TOP) {
							y += deltaY;
							x1 += deltaX;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_RIGHT_CENTER) {
							x1 += deltaX;
						} else if (sensitive_rec_ident == SENSITIVE_REC_IDENT_RIGHT_BOTTOM) {
							x1 += deltaX;
							y1 += deltaY;
						}

						// 边界处理
						boundleBalance();

						// 隐藏功能按钮
						setToolBarWindowVisible(false);
						// 截图区域大小信息显示
						int delTAX = Math.abs(x - x1);
						int delTAY = Math.abs(y - y1);
						if (delTAX == 0)
							delTAX = 1;
						if (delTAY == 0)
							delTAY = 1;
						RectInfoWindow.setText(delTAX + " x " + delTAY);
						setRectInfoWindowVisible(true);

						// 重绘选区
						repaint();
					}
					// 选择区内拖动mouse时进行选择区整体移动
					else if (isMove) {
						// 获得mouse在选区内相对上一次拖动的位移量(注意第一次情况)
						x = e.getX() - x_rela;
						y = e.getY() - y_rela;

						x1 = e.getX() + x1_rela;
						y1 = e.getY() + y1_rela;
						// 边界处理(如果使用以下代码得处理边界，可使选区移动到边界时不动(不会变小))
						if (x < 0) {
							x = 0;
							x1 = x_rela + x1_rela + 1;
						}
						if (x1 >= getScreenWidth()) {
							x = getScreenWidth() - x_rela - x1_rela - 1;
							x1 = getScreenWidth() - 1;
						}
						if (y < 0) {
							y = 0;
							y1 = y_rela + y1_rela + 1;
						}
						if (y1 >= getScreenHeight()) {
							y = getScreenHeight() - y_rela - y1_rela - 1;
							y1 = getScreenHeight() - 1;
						}
						setToolBarWindowVisible(true);
						// 截图区域大小信息显示
						int delTAX = Math.abs(x - x1);
						int delTAY = Math.abs(y - y1);
						RectInfoWindow.setText(delTAX + " x " + delTAY);
						setRectInfoWindowVisible(true);
						// 重绘选区
						repaint();
					}
					// 正常拖动(绘制矩形选区)
					else if (CaptureScreen.this.isDraw) {
						setToolBarWindowVisible(false);
						// 选区外拖时仅更新右下角坐标
						CaptureScreen.x1 = (int) e.getX();
						CaptureScreen.y1 = (int) e.getY();
						// 修正，如果x1 = x，或者y1 = y，保证1个像素的间距
						if (x1 == x)
							x1 = x + 1;
						if (y1 == y)
							y1 = y + 1;
						// 截图区域大小信息显示
						int delTAX = Math.abs(x - x1);
						int delTAY = Math.abs(y - y1);
						RectInfoWindow.setText(delTAX + " x " + delTAY);
						setRectInfoWindowVisible(true);
						// 放大镜显示
						setMagnifierWindowVisible(true, 5, 0);
						// 并重绘
						repaint();
					}
				} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
					CaptureScreen.getParentDialog(null).setCursor(getColorfulCursor());
					// 如果此时还没有选区
					if (!hasRectSelected()) {
						setMagnifierWindowVisible(true, 0, 0);
					} else {
						setMagnifierWindowVisible(false, 0, 0);
						setToolBarWindowVisible(true);
					}
				}
			}

			public void mouseMoved(MouseEvent e) {
				if (CaptureScreen.this.isInsideSensitiveRec(e.getX(), e.getY())) {
					setMagnifierWindowVisible(false, 0, 0);
					// 改变mouse样式
					CaptureScreen.this.setParentDialogCursorWhenInsideSensitiveRec(e);
				}
				// 如果mouse到达选区内时
				else if (CaptureScreen.this.isInSelectedArea(e.getX(), e.getY())) {
					// 改变mouse样式
					CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.MOVE_CURSOR));
					setMagnifierWindowVisible(false, 0, 0);
				}
				// 选区外
				else {
					CaptureScreen.getParentDialog(null).setCursor(getColorfulCursor());
					// 如果此时还没有选区
					if (!hasRectSelected()) {
						setMagnifierWindowVisible(true, 0, 0);
					} else {
						setMagnifierWindowVisible(false, 0, 0);
					}
				}
			}
		});
	}

	/**
	 * @describe 选择区域上右键弹出框
	 * @return
	 * @author suyanga
	 * @date 2015年5月9日 下午11:55:19
	 */
	private static JPopupMenu getPopupMenuOnSelectedRect() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.setCursor(getColorfulCursor());

			// 复制图片
			JMenuItem imgCopyAction = new JMenuItem("复制图片");
			imgCopyAction.setIcon(new ImageIcon(CaptureScreen.class.getResource("/images/ok.png")));
			imgCopyAction.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					doSaveToClipboard();
				}
			});

			// 图片另存为
			JMenuItem imgSaveAsAction = new JMenuItem("图片另存为");
			imgSaveAsAction.setIcon(new ImageIcon(CaptureScreen.class.getResource("/images/save.png")));
			imgSaveAsAction.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					doSaveAs();
				}
			});

			// 重新截图
			JMenuItem redoAction = new JMenuItem("重新截图");
			redoAction.setIcon(new ImageIcon(CaptureScreen.class.getResource("/images/back.png")));
			redoAction.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					doBack();
					setMagnifierWindowVisible(true, 0, 0);
					CaptureScreen.getParentDialog(null).setCursor(getColorfulCursor());
				}
			});

			// 复制图片
			JMenuItem quitAction = new JMenuItem("退出截图");
			quitAction.setIcon(new ImageIcon(CaptureScreen.class.getResource("/images/cancle.png")));
			quitAction.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					doQuit();
				}
			});

			// 将菜单条目加入菜单
			popupMenu.add(imgCopyAction);
			popupMenu.add(imgSaveAsAction);
			popupMenu.addSeparator();
			popupMenu.add(redoAction);
			popupMenu.add(quitAction);
		}
		return popupMenu;
	}

	/**
	 * @describe 返回
	 * @author suyanga
	 * @date 2015年5月10日 上午4:08:18
	 */
	public static void doBack() {
		initLocation();
		setToolBarWindowVisible(false);
		setRectInfoWindowVisible(false);
		CaptureScreen.getParentDialog(null).repaint();
	}

	/**
	 * @describe 另存为操作
	 * @author suyanga
	 * @date 2015年5月10日 上午12:23:11
	 */
	public static void doSaveAs() {
		CaptureScreen.getParentDialog(null).setAlwaysOnTop(false);

		// 最终保存文件名
		String fname = "";
		// 默认目录c:\
		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		// 默认保存文件名
		fileChooser.setSelectedFile(new File(CaptureScreen.getTimestampAsStr() + ".png"));
		// 显示保存对话框
		int result = fileChooser.showSaveDialog(null);
		CaptureScreen.getParentDialog(null).setAlwaysOnTop(true);

		if (result == JFileChooser.APPROVE_OPTION) {
			fname = fileChooser.getSelectedFile().getPath();
			File f = new File(fname);
			try {
				BufferedImage imgSelectedArea = ImageUtil.toBufferedImage(snapShot());
				// 将screenshot对象写入图像文件
				ImageIO.write(imgSelectedArea, "png", f);
				filePath = fname;
				getParentDialog(null).setVisible(false);
			} catch (Exception ee) {
			}
		}
	}

	public static String getBase64Image() {
		String imageBase64 = null;
		Image image = snapShot();
		try {
			GraphicsUtils.setClipboardImage(image);
			BufferedImage imgSelectedArea = ImageUtil.toBufferedImage(image);
			imageBase64 = ImageToBase64.ImageToBase64(imgSelectedArea);
			getParentDialog(null).setVisible(false);
		} catch (Exception e) {
		}
		return imageBase64;
	}

	/**
	 * @describe 保存到黏贴板
	 * @author suyanga
	 * @date 2015年5月10日 上午12:25:11
	 */
	public static void doSaveToClipboard() {
		GraphicsUtils.setClipboardImage(snapShot());
		getParentDialog(null).setVisible(false);
	}

	/**
	 * @describe 退出截图
	 * @author suyanga
	 * @date 2015年5月10日 上午12:28:49
	 */
	public static void doQuit() {
		filePath = "";
		getParentDialog(null).setVisible(false);
	}

	/**
	 * @describe 获取文件路径
	 * @return
	 * @author suyanga
	 * @date 2015年5月10日 下午4:30:05
	 */
	public static String getFilePath() {
		return filePath;
	}

	/**
	 * @describe 初始化文件路径
	 * @author suyanga
	 * @date 2015年5月10日 下午5:00:02
	 */
	public static void initFilePath() {
		filePath = "";
	}

	/**
	 * @describe 是否有区域被选择
	 * @return
	 * @author suyanga
	 * @date 2015年5月9日 下午11:26:19
	 */
	private static boolean hasRectSelected() {
		if (x < 0 || y < 0 || x1 < 0 || y1 < 0)
			return false;
		return true;
	}

	/**
	 * 当前坐标是否在选择区内.
	 * 
	 * @param px
	 *            int
	 * @param py
	 *            int
	 * @return boolean
	 */
	private boolean isInSelectedArea(int px, int py) {
		return (px > x && px < x1 && py > y && py <= y1);
	}

	/**
	 * @describe 设置放大镜window状态
	 * @param window
	 * @param visible
	 * @param e
	 * @author suyanga
	 * @date 2015年5月9日 上午1:58:20
	 */
	private static void setMagnifierWindowVisible(boolean visible, int xOffSet, int yOffSet) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		MagnifierWindow.setWindowVisible(MagnifierWindow.getMagnifierWindow(getParentDialog(null)), visible, p, xOffSet,
				yOffSet);
	}

	/**
	 * @describe 设置工具按钮界面是否可见
	 * @param b
	 * @author suyanga
	 * @date 2015年5月9日 下午11:20:35
	 */
	private static void setToolBarWindowVisible(boolean b) {
		ToolBarWindow.setWindowVisible(ToolBarWindow.getToolBarWindow(getParentDialog(null)), b,
				new Rectangle(x, y, x1 - x, y1 - y));
	}

	/**
	 * @describe 设置截图大小界面是否可见
	 * @param b
	 * @author suyanga
	 * @date 2015年5月9日 下午11:20:35
	 */
	private static void setRectInfoWindowVisible(boolean b) {
		int px = Math.min(x, x1);
		int py = Math.min(y, y1);
		// 工具条显示，并且在截图区域上面，并且跟 RectInfoWindow 有重叠
		if (ToolBarWindow.getToolBarWindow(getParentDialog(null)).isVisible()
				&& ToolBarWindow.getToolBarWindow(getParentDialog(null)).getY() < py
				&& ToolBarWindow.getToolBarWindow(getParentDialog(null)).getX() < px
						+ RectInfoWindow.getRectInfoWindow(getParentDialog(null)).getWidth() + 1) {
			px = px - RectInfoWindow.getRectInfoWindow(getParentDialog(null)).getWidth() - 1;
			py = py + RectInfoWindow.getRectInfoWindow(getParentDialog(null)).getHeight();
			// 超过左边界，在截图区域右侧显示
			if (px < 0)
				px = Math.max(x, x1);
			// 超过右边界，在截图区域做上角内侧显示
			if (px + RectInfoWindow.getRectInfoWindow(getParentDialog(null)).getWidth() > getScreenWidth())
				px = Math.min(x, x1);
		}
		RectInfoWindow.setWindowVisible(RectInfoWindow.getRectInfoWindow(getParentDialog(null)), b, new Point(px, py));
	}

	/**
	 * 分别设置到达各敏感区范围内的mouse样式.
	 * 
	 * @param e
	 *            MouseEvent
	 */
	private void setParentDialogCursorWhenInsideSensitiveRec(MouseEvent e) {
		if (CaptureScreen.this.isInsideRecLeftTop(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecLeftCenter(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecLeftBottom(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecTopCenter(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecBottomCenter(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecRightTop(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecRightCenter(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		else if (CaptureScreen.this.isInsideRecRightBottom(e.getX(), e.getY()))
			CaptureScreen.getParentDialog(null).setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
	}

	/**
	 * 设置宿主窗口内的mouse样式的实施方法(使用自定义图片).
	 * 
	 * @param img
	 *            String
	 */
	public static Cursor getColorfulCursor() {
		if (colorfulCursor == null)
			colorfulCursor = toolKit.createCustomCursor(
					new ImageIcon(CaptureScreen.class.getResource("/images/cursor.png")).getImage(), new Point(9, 6),
					"cscur");
		return colorfulCursor;
	}

	/**
	 * 测试给定的点是否在敏感触点任何一个矩形区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideSensitiveRec(int _x, int _y) {
		boolean isInside = false;
		Point p = new Point(_x, _y);

		// 左上角触点矩形区
		Rectangle recLeftTop = this.getRecLeftTop();
		// 左中
		Rectangle recLeftCenter = this.getRecLeftCenter();
		// 左下
		Rectangle recLeftBottom = this.getRecLeftBottom();
		// 上中
		Rectangle recTopCenter = this.getRecTopCenter();
		// 下中
		Rectangle recBottomCenter = this.getRecBottomCenter();
		// 右上
		Rectangle recRightTop = this.getRecRightTop();
		// 右中
		Rectangle recRightCenter = this.getRecRightCenter();
		// 右下
		Rectangle recRightBottom = this.getRecRightBottom();

		// 如果在这些矩形区内则返回true
		if (recLeftTop.contains(p) || recLeftCenter.contains(p) || recLeftBottom.contains(p) || recTopCenter.contains(p)
				|| recBottomCenter.contains(p) || recRightTop.contains(p) || recRightCenter.contains(p)
				|| recRightBottom.contains(p)) {
			isInside = true;
		}

		return isInside;
	}

	/**
	 * 在敏感触点左上角区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecLeftTop(int _x, int _y) {
		return this.getRecLeftTop().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点左中区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecLeftCenter(int _x, int _y) {
		return this.getRecLeftCenter().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点左下角区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecLeftBottom(int _x, int _y) {
		return this.getRecLeftBottom().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点上中区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecTopCenter(int _x, int _y) {
		return this.getRecTopCenter().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点下中区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecBottomCenter(int _x, int _y) {
		return this.getRecBottomCenter().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点右上角区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecRightTop(int _x, int _y) {
		return this.getRecRightTop().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点右中区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecRightCenter(int _x, int _y) {
		return this.getRecRightCenter().contains(new Point(_x, _y));
	}

	/**
	 * 在敏感触点右下角区内.
	 * 
	 * @param _x
	 *            int
	 * @param _y
	 *            int
	 * @return boolean
	 */
	private boolean isInsideRecRightBottom(int _x, int _y) {
		return this.getRecRightBottom().contains(new Point(_x, _y));
	}

	/**
	 * 画虚线边框的矩形.
	 * 
	 * @param x1
	 *            int
	 * @param y1
	 *            int
	 * @param x2
	 *            int
	 * @param y2
	 *            int
	 * @param width
	 *            int 线宽
	 * @param g
	 *            Graphics
	 */
	@SuppressWarnings("unused")
	private static void drawDashedRec(int x1, int y1, int x2, int y2, int width, Graphics g) {
		((Graphics2D) g).setStroke(new BasicStroke(width // 线宽
				, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 5.0f, 5.0f } // 虚实线的长度([0]为实,[1]为虚
				, 5)); // 开始画虚线的offset
		g.drawRect(x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * 获得敏感触点左上角矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecLeftTop() {
		return getSensitiveRec(x, y);
	}

	/**
	 * 获得敏感触点左中矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecLeftCenter() {
		return getSensitiveRec(x, y + (y1 - y) / 2);
	}

	/**
	 * 获得敏感触点左下矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecLeftBottom() {
		return getSensitiveRec(x, y1);
	}

	/**
	 * 获得敏感触点上中矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecTopCenter() {
		return getSensitiveRec(x + (x1 - x) / 2, y);
	}

	/**
	 * 获得敏感触点下中矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecBottomCenter() {
		return getSensitiveRec(x + (x1 - x) / 2, y1);
	}

	/**
	 * 获得敏感触点右上角矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecRightTop() {
		return getSensitiveRec(x1, y);
	}

	/**
	 * 获得敏感触点右中矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecRightCenter() {
		return getSensitiveRec(x1, y + (y1 - y) / 2);
	}

	/**
	 * 获得敏感触点右下角矩形区.
	 * 
	 * @return Rectangle
	 */
	private Rectangle getRecRightBottom() {
		return getSensitiveRec(x1, y1);
	}

	/**
	 * 获得各敏感触点矩形的实现方法.1
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @return Rectangle
	 */
	private static Rectangle getSensitiveRec(int x, int y) {
		Rectangle rec = new Rectangle(x - CaptureScreen.SENSITIVE_REC_WIDTH / 2,
				y - CaptureScreen.SENSITIVE_REC_HEIGHT / 2, CaptureScreen.SENSITIVE_REC_WIDTH,
				CaptureScreen.SENSITIVE_REC_HEIGHT);

		return rec;
	}

	/**
	 * 边界调整.
	 */
	private void boundleBalance() {
		if (x1 >= CaptureScreen.getScreenWidth()) {
			CaptureScreen.x1 = CaptureScreen.getScreenWidth();
		}
		if (x1 <= 0) {
			CaptureScreen.x1 = 0;
		}
		if (y1 >= CaptureScreen.getScreenHeight()) {
			CaptureScreen.y1 = CaptureScreen.getScreenHeight();
		}
		if (y1 <= 0) {
			CaptureScreen.y1 = 0;
		}
		if (x >= CaptureScreen.getScreenWidth()) {
			CaptureScreen.x = CaptureScreen.getScreenWidth();
		}
		if (x <= 0) {
			CaptureScreen.x = 0;
		}
		if (y >= CaptureScreen.getScreenHeight()) {
			CaptureScreen.y = CaptureScreen.getScreenHeight();
		}
		if (y <= 0) {
			CaptureScreen.y = 0;
		}
	}

	public static JDialog getParentDialog(Window window) {
		if (parentDialog == null) {
			parentDialog = new JDialog() {
				private static final long serialVersionUID = 1L;

				public void setVisible(boolean b) {
					if (!b) {
						CaptureScreen.initLocation();
						setToolBarWindowVisible(false);
						setRectInfoWindowVisible(false);
						setMagnifierWindowVisible(false, 0, 0);
						super.dispose();
					}
					if (b) {
						Point p = MouseInfo.getPointerInfo().getLocation();
						MagnifierWindow.getMagnifierWindow(parentDialog).setLocation(p.x, p.y + 30);
						MagnifierWindow.getMagnifierWindow(parentDialog).setVisible(b);
						super.setVisible(b);
					}
				}
			};
			if (window != null)
				parentDialog.setModal(true);
			parentDialog.setUndecorated(true);
			parentDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			parentDialog.setSize(CaptureScreen.getScreenWidth(), CaptureScreen.getScreenHeight());
			parentDialog.getContentPane().add(getCaptureScreenInstance());

			parentDialog.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					// 按ESC键退出
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						parentDialog.dispose();
					}
				}
			});

			parentDialog.setAlwaysOnTop(true);
			// 启用全屏排斥模式
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if (gd.isFullScreenSupported()) {
				try {
					// 以下代码暂时不用，用了会导致第一次使用的时候弹出两次
					// gd.setFullScreenWindow(parentDialog);
				} catch (Exception e) {
				} finally {
				}
			}
		}

		return parentDialog;
	}

	/**
	 * Override panit()方法用于绘制选区.
	 * 
	 * @param g
	 *            Graphics
	 */
	public void paint(Graphics g) {
		// 清屏
		super.paint(g);

		if (x < 0 || y < 0 || x1 < 0 || y1 < 0) {
			return;
		}
		// 绘制选区
		else {
			// 矩形选区
			this.drawRecEx(g);
			// 8个敏感触点
			this.draw8SensitiveRecForSelectedArea(g);
		}
	}

	/**
	 * 可从全4个方向绘制矩形的方法(API只提供一个方向).
	 * 
	 * @param g
	 *            Graphics
	 */
	private void drawRecEx(Graphics g) {
		g.setColor(new Color(0, 174, 255));
		int deltaX = x1 - x;
		int deltaY = y1 - y;

		if (deltaX == 0)
			deltaX = 1;
		if (deltaY == 0)
			deltaY = 1;

		if (deltaX < 0 && deltaY > 0) {
			g.drawRect(x - Math.abs(deltaX), y, Math.abs(deltaX), Math.abs(deltaY));
		} else if (deltaX > 0 && deltaY < 0) {
			g.drawRect(x, y - Math.abs(deltaY), Math.abs(deltaX), Math.abs(deltaY));
		} else if (deltaX < 0 && deltaY < 0) {
			g.drawRect(x - Math.abs(deltaX), y - Math.abs(deltaY), Math.abs(deltaX), Math.abs(deltaY));
		} else if (deltaX > 0 && deltaY > 0) {
			g.drawRect(x, y, deltaX, deltaY);
		}
	}

	/**
	 * 绘制选区边上8个感觉触点.
	 * 
	 * @param g
	 *            Graphics
	 */
	private void draw8SensitiveRecForSelectedArea(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 174, 255));

		// 左上角
		Rectangle recLeftTop = this.getRecLeftTop();
		// 左中
		Rectangle recLeftCenter = this.getRecLeftCenter();
		// 左下
		Rectangle recLeftBottom = this.getRecLeftBottom();
		// 上中
		Rectangle recTopCenter = this.getRecTopCenter();
		// 下中
		Rectangle recBottomCenter = this.getRecBottomCenter();
		// 右上
		Rectangle recRightTop = this.getRecRightTop();
		// 右中
		Rectangle recRightCenter = this.getRecRightCenter();
		// 右下
		Rectangle recRightBottom = this.getRecRightBottom();

		// 填充
		g2.fill(recLeftTop);
		g2.fill(recLeftCenter);
		g2.fill(recLeftBottom);
		g2.fill(recTopCenter);
		g2.fill(recBottomCenter);
		g2.fill(recRightTop);
		g2.fill(recRightCenter);
		g2.fill(recRightBottom);
	}

	/**
	 * 绘制整个面板的背景.
	 * 
	 * @param g
	 *            Graphics
	 */
	public void paintComponent(Graphics g) {
		if (background != null) {

			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(background, 0, 0, this);

			g2d.setColor(new Color(0, 0, 0, 90));
			// 说明有选中区域
			if (hasRectSelected()) {
				Point upLeft = new Point(Math.min(x, x1), Math.min(y, y1));
				Point upRight = new Point(Math.max(x, x1), Math.min(y, y1));
				Point lowLeftP = new Point(Math.min(x, x1), Math.max(y, y1));
				Point lowRight = new Point(Math.max(x, x1), Math.max(y, y1));
				// 绘制左边
				g.fillRect(0, 0, upLeft.x, (int) CaptureScreen.getScreenHeight());
				// 绘制右边
				g.fillRect(upRight.x + 1, 0, (int) CaptureScreen.getScreenWidth() - upRight.x,
						(int) CaptureScreen.getScreenHeight());
				// 绘制中间上部
				g.fillRect(upLeft.x, 0, upRight.x - upLeft.x + 1, upLeft.y);
				// 绘制中间下部
				g.fillRect(lowLeftP.x, lowLeftP.y + 1, lowRight.x - lowLeftP.x + 1,
						(int) CaptureScreen.getScreenHeight() - lowLeftP.y);
			}
			// g.fillRect(0, 0, (int) CaptureScreen.getScreenWidth(), (int)
			// CaptureScreen.getScreenHeight());
		} else {
			super.paintComponent(g);
		}
	}

	/**
	 * 获得屏幕快照.
	 * 
	 * @param rec
	 *            Rectangle
	 */
	private static Image snapShot() {
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x1 > getScreenWidth())
			x1 = getScreenWidth();
		if (y1 > getScreenHeight())
			y1 = getScreenHeight();
		Rectangle rec = new Rectangle(x, y, x1 - x, y1 - y);
		try {
			// Image screenshot = (new Robot()).createScreenCapture(rec);
			// 将剪切下来的图像保存到系统剪贴板
			Image screenshot = bufferedImage.getSubimage(rec.x, rec.y, rec.width, rec.height);
			Clipboard cb = toolKit.getSystemClipboard();
			ImageUtil it = new ImageUtil(screenshot);
			cb.setContents(it, it);
			// SnapShotTray.displayMsg("友情提示!",
			// "\n截图已保存到剪贴板,大小是: " + screenshot.getWidth(this) + "*" +
			// screenshot.getHeight(this) + "\n", 0);
			initLocation();
			return screenshot;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得日期时间的无符号字符串形式(精确到毫秒).
	 * 
	 * @return String
	 */
	private static String getTimestampAsStr() {
		Calendar calendar = null;
		calendar = Calendar.getInstance();
		int y, M, d, h, m, s;
		y = calendar.get(Calendar.YEAR);
		M = calendar.get(Calendar.MONTH) + 1;
		d = calendar.get(Calendar.DAY_OF_MONTH);
		h = calendar.get(Calendar.HOUR_OF_DAY);
		m = calendar.get(Calendar.MINUTE);
		s = calendar.get(Calendar.SECOND);
		String str = "" + y + ((M >= 10) ? ("" + M) : ("0" + M)) + ((d >= 10) ? ("" + d) : ("0" + d))
				+ ((h >= 10) ? ("" + h) : ("0" + h)) + ((m >= 10) ? ("" + m) : ("0" + m))
				+ (s < 10 ? "00" + s : (s < 100 ? "0" + s : "" + s));

		return "cap_" + str;
	}

	/**
	 * 获得屏幕宽.
	 * 
	 * @return int
	 */
	public static int getScreenWidth() {
		return (int) toolKit.getScreenSize().getWidth();
	}

	/**
	 * 获得屏幕高.
	 * 
	 * @return int
	 */
	public static int getScreenHeight() {
		return (int) toolKit.getScreenSize().getHeight();
	}

	private static CaptureScreen getCaptureScreenInstance() {
		if (csStamdalone == null) {
			try {
				csStamdalone = new CaptureScreen();
			} catch (Exception e) {
				System.out.println("CaptureScreen无法实例化，" + e.getMessage());
			}
		}
		return csStamdalone;
	}

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		CaptureScreen.refreshBackgroud();
		JDialog f = CaptureScreen.getParentDialog(null);

		f.setVisible(true);
	}
}
