package com.yuecheng.workportal.screen;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.yuecheng.workportal.ui.Main;


/**
 * 
 * @author  zdyang
 */
public class Capturer extends JLabel {
	Main mainFrame;
	private static final long serialVersionUID = -3260450507959149932L;

	private static final Toolkit kit = Toolkit.getDefaultToolkit();
	private static final Dimension screenSize = kit.getScreenSize();

	/**
	 * 整体截图的window
	 */
	private JWindow captureWindow = new JWindow();
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice gd = ge.getDefaultScreenDevice();

	/**
	 * 截得的图形的边框颜色
	 */
	private Color selectBorderColor = new Color(0, 174, 255);
	//private Color selectBorderColor = Color.GREEN;
	private Color selectFillColor = new Color(200, 200, 200, 60);
	private Color textColor = Color.BLUE;
	private Stroke selectStroke = new BasicStroke(1.0f);
	private Stroke resizeStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10, 5 }, 0.2f);
	/**
	 * 工具栏
	 */
	private JPanel controlPanel = getControlPanel();
	/**
	 * 信息栏
	 */
	private JPanel infoPanel = getInfoPanel();
	/**
	 * 显示鼠标经过区域放大后的图像
	 */
	private ToolImagePanel pickImgPanel;
	/**
	 * 描述 x、y坐标及宽、高、rgb信息的文本框
	 */
	private JTextArea infoArea;
	/**
	 * 鼠标经过点的Color值
	 */
	private Color point_color;

	private Robot robot = null;

	private List<BorderStructure> borderList = new ArrayList<BorderStructure>();
	private int borderInflexionSize = 6;
	private Color borderInflexionColor = selectBorderColor;

	private Rectangle rectangle;

	private BorderStructure editStructure = null;
	/**
	 * 截图完成时的Rectangle
	 */
	private Rectangle selectedRectangle = null;
	private Rectangle resizeRectangle;
	/**
	 * 拖动过程中的Rectangle 一直在变
	 * 拖动结束时为null
	 */
	private Rectangle dragRectangle = null;

	private BufferedImage screenImage;
	/**
	 * 截图时  最初的点的Point
	 */
	private Point startPoint;
	private Point pressPoint;
	private Point dragPoint;
	private Point mousePoint;
	/**
	 * 是否在编辑的标志位
	 * 截图完成且在八个可拖动位置拖动时 此值为true
	 */
	private boolean isEditing = false;
	/**
	 * 是否在移动的标志位
	 * 截图完成且在所选区域拖动移动时 此值为true
	 */
	private boolean isMoving = false;
	private int xMoveOffset;
	private int yMoveOffset;

	private Cursor predefinedCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	private Cursor dragMoveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

	private static final Rectangle leftRectangle = new Rectangle(10, 10, 234, 210);
	private static final Rectangle rightRectangle = new Rectangle(screenSize.width - 244, 10, 234, 210);
	
	private static Capturer singleton;
	private static final Object LOCK = new Object();
	

	public void setMousePoint(Point mousePoint) {
		this.mousePoint = mousePoint;
		this.repaint();
	}
	
	public static Capturer getInstance(Main mainFrame) {
		synchronized (LOCK) {
			if (null == singleton) {
				Capturer controller = new Capturer(mainFrame);
				singleton = controller;
				return controller;
			}
		}
		return singleton;
	}
	
	private Capturer() {
		
	}
	private Capturer(Main mainFrame) {
		this.mainFrame= mainFrame;
		installMouseListener();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		captureWindow.getLayeredPane().add(controlPanel, JLayeredPane.DEFAULT_LAYER.intValue() + 10);
		captureWindow.getLayeredPane().add(infoPanel, JLayeredPane.DEFAULT_LAYER.intValue() + 10);

		captureWindow.setContentPane(this);
		captureWindow.setAlwaysOnTop(true) ;

	}

	private JPanel getControlPanel() {
		JPanel panel = new ControlPanel(this);
		return panel;
	}
	
	/**
	 * @return
	 */
	private JPanel getInfoPanel() {
		final JPanel panel = new JPanel();
		//panel.setOpaque(false);
		panel.setBounds(leftRectangle);
		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				if (panel.getBounds().equals(leftRectangle)) {
					panel.setBounds(rightRectangle);
				} else {
					panel.setBounds(leftRectangle);
				}

			}

		});

		panel.setLayout(null);
		pickImgPanel = new ToolImagePanel();
		Border border1 = BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(114, 114, 114));
		Border border2 = BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(114, 114, 114));
		pickImgPanel.setBorder(border1);
		infoArea = new JTextArea();
		infoArea.setOpaque(true);
		infoArea.setEditable(false);
		infoArea.setForeground(Color.RED);
		infoArea.setFont(new Font("宋体", Font.PLAIN, 12));
		infoArea.setText("");
		infoArea.setBackground(new Color(105, 110, 105));
		pickImgPanel.setBounds(5, 5, 224, 168);
		infoArea.setBounds(5, 173, 224, 30);
		infoArea.setBorder(border2);
		panel.add(pickImgPanel);/* 用于跟踪鼠标的移动的 */
		panel.add(infoArea);
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, new Color(114, 114, 114), Color.WHITE));
		return panel;
	}

	/**
	 * 刷新信息栏
	 */
	public void refreshInfoText() {

		String infoString = "";
		int point_x = mousePoint.x;
		int point_y = mousePoint.y;

		if (rectangle != null) {
			int w = rectangle.width;
			int h = rectangle.height;
			/*infoString = "X,Y : " + point_x + "," + point_y + "    W*H : " + w + " x " + h + "\nRBG:(" + this.point_color.getRed() + "," + this.point_color.getGreen() + "," + this.point_color.getBlue()
					+ ")\n";*/
			infoString = "X,Y : " + point_x + "," + point_y + "     " + w + " x " + h + "\nRBG:(" + this.point_color.getRed() + "," + this.point_color.getGreen() + "," + this.point_color.getBlue()
					+ ")\n";
		} else {
			infoString = "X,Y : " + point_x + "," + point_y + "     0 x 0 \nRBG:(" + this.point_color.getRed() + "," + this.point_color.getGreen() + "," + this.point_color.getBlue() + ")\n";
			//infoString = "X,Y : " + point_x + "," + point_y + "    W*H : 0*0" + "\nRBG:(" + this.point_color.getRed() + "," + this.point_color.getGreen() + "," + this.point_color.getBlue() + ")\n";
		}

		this.infoArea.setText(infoString);
		this.infoArea.setForeground(new Color(237, 237, 234));
		int pickImg_x;
		int pick_x1;
		int pick_x2;
		int dis1 = 28;
		int dis2 = 21;

		if (point_x - dis1 < 0) {
			pick_x1 = 0;
			pick_x2 = point_x + dis1;
			pickImg_x = dis1 - point_x;
		} else {

			if (point_x + dis1 > getWidth()) {
				pick_x1 = point_x - dis1;
				pick_x2 = getWidth();
				pickImg_x = 0;
			} else {
				pick_x1 = point_x - dis1;
				pick_x2 = point_x + dis1;
				pickImg_x = 0;
			}
		}
		int pickImg_y;
		int pick_y1;
		int pick_y2;

		if (point_y - dis2 < 0) {
			pick_y1 = 0;
			pick_y2 = point_y + dis2;
			pickImg_y = dis2 - point_y;
		} else {

			if (point_y + dis2 > getHeight()) {
				pick_y1 = point_y - dis2;
				pick_y2 = getHeight();
				pickImg_y = 0;
			} else {
				pick_y1 = point_y - dis2;
				pick_y2 = point_y + dis2;
				pickImg_y = 0;
			}
		}
		BufferedImage pickImg = new BufferedImage(56, 42, BufferedImage.TYPE_INT_BGR);
		Graphics pickGraphics = pickImg.getGraphics();
		pickGraphics.drawImage(screenImage.getSubimage(pick_x1, pick_y1, pick_x2 - pick_x1, pick_y2 - pick_y1), pickImg_x, pickImg_y, Color.black, null);
		this.pickImgPanel.refreshImg(pickImg.getScaledInstance(224, 168, Image.SCALE_SMOOTH));
		infoPanel.validate();
	}

	private void installMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			//鼠标按下 调用顺序   mousePressed--->mouseReleased--->mouseClicked
			public void mousePressed(MouseEvent e) {
				pressPoint = e.getPoint();

				if (isMoving) {
					xMoveOffset = pressPoint.x - selectedRectangle.x;
					yMoveOffset = pressPoint.y - selectedRectangle.y;
					return;
				}
				if (isEditing) {

				} else {
					setStartPoint(e.getPoint());
					setEndPoint(null);
					setDragPoint(null);
					controlPanel.setVisible(false);
				}

			}

			public void mouseClicked(MouseEvent e) {
				if (e.isMetaDown()) {
					System.out.println("右键了");
					if (selectedRectangle != null && selectedRectangle.contains(e.getPoint())) {
						//什么也不做
					} else {
						exitCapturer();
					}
				}

			}

			public void mouseReleased(MouseEvent e) {
				if (getCursor() == dragMoveCursor && selectedRectangle != null) {
					return;
				}
				if (!isEditing) {
					setEndPoint(e.getPoint());
					if (getDragPoint() != null) {
						setDragPoint(null);
					}
				} else {
					if (resizeRectangle != null) {
						selectedRectangle = resizeRectangle;
						repaint();
					}
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			//鼠标按下并拖动时调用 调用前会先调用mousePressed
			public void mouseDragged(MouseEvent e) {
				Point point = e.getPoint();

				if (isMoving) {
					selectedRectangle.x = point.x - xMoveOffset;
					selectedRectangle.y = point.y - yMoveOffset;
					if (selectedRectangle.x < 0) {
						selectedRectangle.x = 0;
					}
					if (selectedRectangle.y < 0) {
						selectedRectangle.y = 0;
					}
					if (selectedRectangle.x + selectedRectangle.width > screenSize.width) {
						selectedRectangle.x = screenSize.width - selectedRectangle.width;
					}
					if (selectedRectangle.y + selectedRectangle.height > screenSize.height) {
						selectedRectangle.y = screenSize.height - selectedRectangle.height;
					}
					repaint();
					return;
				}
				if (isEditing) {
					double x = selectedRectangle.x;
					double y = selectedRectangle.y;
					double w = selectedRectangle.width;
					double h = selectedRectangle.height;
					Point p1 = null;
					Point p2 = null;
					switch (editStructure.getPosition()) {
					case BorderStructure.POSITION_TOP:
						if (point.y >= (y + h)) {
							p1 = new Point((int) x, (int) (y + h));
							p2 = new Point((int) (x + w), point.y);
						} else {
							p1 = new Point((int) x, point.y);
							p2 = new Point((int) (x + w), (int) (y + h));
						}
						break;
					case BorderStructure.POSITION_BOTTOM:
						if (point.y < y) {
							p1 = new Point((int) x, point.y);
							p2 = new Point((int) (x + w), (int) y);
						} else {
							p1 = new Point((int) x, (int) y);
							p2 = new Point((int) (x + w), point.y);
						}
						break;
					case BorderStructure.POSITION_LEFT:
						if (point.x > (x + w)) {
							p1 = new Point((int) (x + w), (int) y);
							p2 = new Point(point.x, (int) (y + h));
						} else {
							p1 = new Point(point.x, (int) y);
							p2 = new Point((int) (x + w), (int) (y + h));
						}
						break;
					case BorderStructure.POSITION_RIGHT:
						if (point.x < x) {
							p1 = new Point(point.x, (int) y);
							p2 = new Point((int) x, (int) (y + h));
						} else {
							p1 = new Point((int) x, (int) y);
							p2 = new Point(point.x, (int) (y + h));
						}
						break;
					case BorderStructure.POSITION_TOPLEFT:

						p1 = new Point(point.x, point.y);
						p2 = new Point((int) (x + w), (int) (y + h));
						break;
					case BorderStructure.POSITION_TOPRIGHT:
						p1 = new Point((int) x, point.y);
						p2 = new Point(point.x, (int) (y + h));
						break;
					case BorderStructure.POSITION_BOTTOMLEFT:
						p1 = new Point(point.x, (int) y);
						p2 = new Point((int) (x + w), point.y);
						break;
					case BorderStructure.POSITION_BOTTOMRIGHT:
						p1 = new Point((int) x, (int) y);
						p2 = new Point(point.x, point.y);
						break;
					}
					resizeRectangle = getRectangle(p1, p2);
					repaint();
				} else {
					setDragPoint(point);
					setEndPoint(null);
					setMousePoint(point);

				}

			}

			//鼠标移动时会调用  此时鼠标没有按下
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint();
				setMousePoint(point);
				int size = borderList.size();
				setCursor(predefinedCursor);
				isMoving = false;
				isEditing = false;
				editStructure = null;
				resizeRectangle = null;
				if (selectedRectangle != null) {
					if (selectedRectangle.contains(point)) {
						isMoving = true;
						setCursor(dragMoveCursor);
						return;
					}
				}
				for (int i = 0; i < size; i++) {
					BorderStructure structure = (BorderStructure) borderList.get(i);
					if (structure.getBorder().contains(point)) {
						setCursor(structure.getCursor());
						isEditing = true;
						editStructure = structure;
						break;
					}
				}
			}
		});

	}

	public void setScreenImage() {
		this.setCursor(predefinedCursor);
		screenImage = robot.createScreenCapture(new Rectangle(0, 0, screenSize.width, screenSize.height));
	}

	public void beginCapture(boolean hideFrameBox) {
		if(hideFrameBox) {
			mainFrame.dispose();
			mainFrame.setVisible(false);
		}
		try {
			Thread.sleep(300) ;
		} catch (Exception e) {
		}
		if (gd.isFullScreenSupported()) {
			setScreenImage();
			this.repaint();
			captureWindow.setSize(screenSize);
			captureWindow.setVisible(true);
			//captureWindow.toFront() ;
			captureWindow.setAlwaysOnTop(true);
			//gd.setFullScreenWindow(captureWindow);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(screenImage, 0, 0, this);
		g2d.setStroke(resizeStroke);
		if (dragRectangle != null) {
			g2d.setColor(selectBorderColor.darker());
			rectangle = dragRectangle;
			g2d.draw(rectangle);
			g2d.setColor(selectFillColor);
			g2d.fill(rectangle);
		}

		if (selectedRectangle != null) {
			g2d.setStroke(selectStroke);
			g2d.setColor(selectBorderColor.darker());
			rectangle = selectedRectangle;
			g2d.draw(rectangle);
			g2d.setColor(selectFillColor);
			g2d.fill(rectangle);

			setBorder(g2d, rectangle);

			controlPanel.setVisible(true);
			controlPanel.setLocation(getControlPanelLocation(rectangle));
		}
		/*if (rectangle != null) {
			g2d.setColor(textColor);
			g2d.drawString(rectangle.width + " x " + rectangle.height, rectangle.x, rectangle.y);
		}
		
		if (mousePoint != null && !controlPanel.isVisible()) {
			g2d.setColor(textColor);
			g2d.drawString("x=" + mousePoint.x + " y=" + mousePoint.y, mousePoint.x, mousePoint.y);
			
		}*/
		//绘制信息面板
		if (mousePoint != null) {
			try {
				if (screenImage != null) {
					this.point_color = new Color(this.screenImage.getRGB(mousePoint.x, mousePoint.y));
					refreshInfoText();
					infoPanel.setVisible(true);
					//infoPanel.setLocation(getInfoPanelLocation(rectangle));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (resizeRectangle != null) {
			g2d.setStroke(resizeStroke);
			g2d.setXORMode(Color.WHITE);
			g2d.setColor(selectBorderColor.darker());
			g2d.draw(resizeRectangle);
		}

	}

	private void setBorder(Graphics2D g, Rectangle rectangle) {
		borderList.clear();
		double x = rectangle.getX();
		double y = rectangle.getY();
		double w = rectangle.getWidth();
		double h = rectangle.getHeight();
		double centerX = rectangle.getCenterX();
		double centerY = rectangle.getCenterY();
		Rectangle2D.Double top = new Rectangle2D.Double(centerX - borderInflexionSize / 2, y - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(top, BorderStructure.POSITION_TOP));
		Rectangle2D.Double bottom = new Rectangle2D.Double(centerX - borderInflexionSize / 2, y + h - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(bottom, BorderStructure.POSITION_BOTTOM));
		Rectangle2D.Double left = new Rectangle2D.Double(x - borderInflexionSize / 2, centerY - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(left, BorderStructure.POSITION_LEFT));
		Rectangle2D.Double right = new Rectangle2D.Double(x + w - borderInflexionSize / 2, centerY - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(right, BorderStructure.POSITION_RIGHT));
		Rectangle2D.Double topLeft = new Rectangle2D.Double(x - borderInflexionSize / 2, y - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(topLeft, BorderStructure.POSITION_TOPLEFT));
		Rectangle2D.Double topRight = new Rectangle2D.Double(x + w - borderInflexionSize / 2, y - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(topRight, BorderStructure.POSITION_TOPRIGHT));
		Rectangle2D.Double bottomLeft = new Rectangle2D.Double(x - borderInflexionSize / 2, y + h - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(bottomLeft, BorderStructure.POSITION_BOTTOMLEFT));
		Rectangle2D.Double bottomRight = new Rectangle2D.Double(x + w - borderInflexionSize / 2, y + h - borderInflexionSize / 2, borderInflexionSize, borderInflexionSize);
		borderList.add(new BorderStructure(bottomRight, BorderStructure.POSITION_BOTTOMRIGHT));
		int size = borderList.size();
		g.setColor(borderInflexionColor);
		for (int i = 0; i < size; i++) {
			BorderStructure structure = (BorderStructure) borderList.get(i);
			g.fill(structure.getBorder());
		}

	}

	private Point getControlPanelLocation(Rectangle rectengle) {
		Point point = new Point(rectengle.x + rectengle.width - controlPanel.getWidth(), rectengle.y + rectengle.height);
		if ((point.x) < 0) {
			point.x = 0;
		}
		point.y += 5;
		if (((point.y + controlPanel.getHeight()) > screenSize.height)) {
			point.y = screenSize.height - controlPanel.getHeight();
		}
		return point;
	}

	/*private Point getInfoPanelLocation(Rectangle rectengle) {
		Point point = new Point(rectengle.x + rectengle.width - controlPanel.getWidth(), rectengle.y + rectengle.height);
		System.out.println("infoPanel.getWidth():" + infoPanel.getWidth());
		if (point.x + infoPanel.getWidth() < 0) {
			point.x = 0;
		}
		point.y += 5 ;
		if (((point.y + controlPanel.getHeight()) > screenSize.height)) {
			point.y = screenSize.height - controlPanel.getHeight();
		}
		return point;
		return new Point(300, 200);
	}*/

	private Rectangle getRectangle(Point p1, Point p2) {
		int width = Math.abs(p2.x - p1.x);
		int height = Math.abs(p2.y - p1.y);
		if ((p1.x <= p2.x)) {
			if (p1.y < p2.y) {
				return new Rectangle(p1.x, p1.y, width, height);
			} else {
				return new Rectangle(p1.x, p2.y, width, height);
			}
		} else {
			if (p1.y < p2.y) {
				return new Rectangle(p2.x, p1.y, width, height);
			} else {
				return new Rectangle(p2.x, p2.y, width, height);
			}
		}
	}

	private void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
		this.repaint();
	}

	private void setEndPoint(Point endPoint) {
		if (endPoint != null) {
			selectedRectangle = getRectangle(startPoint, endPoint);
			if (selectedRectangle.getWidth() == 0 || selectedRectangle.getHeight() == 0) {
				selectedRectangle = null;
			}
		} else {
			selectedRectangle = null;
		}
	}

	private Point getDragPoint() {
		return dragPoint;
	}

	private void setDragPoint(Point dragPoint) {
		this.dragPoint = dragPoint;
		if (dragPoint == null) {
			dragRectangle = null;
		} else {
			dragRectangle = getRectangle(startPoint, dragPoint);
			if (dragRectangle.getWidth() == 0 || dragRectangle.getHeight() == 0) {
				dragRectangle = null;
			}
		}
		this.repaint();
	}

	public void exitCapturer() {
		setStartPoint(null);
		setEndPoint(null);
		setDragPoint(null);
		rectangle = null;
		controlPanel.setVisible(false);
		infoPanel.setVisible(false);
		captureWindow.setVisible(false);
		captureWindow.setAlwaysOnTop(false) ;
		setDefaultValue();
		mainFrame.setVisible(true);
	}

	public Color getFillColor() {
		return selectFillColor;
	}

	public void setFillColor(Color fillColor) {
		this.selectFillColor = fillColor;
		this.repaint();
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
		this.repaint();
	}

	public Stroke getBorderStroke() {
		return resizeStroke;
	}

	public void setBorderStroke(Stroke borderStroke) {
		this.resizeStroke = borderStroke;
		this.repaint();
	}

	public Stroke getResizeStroke() {
		return resizeStroke;
	}

	public void setResizeStroke(Stroke resizeStroke) {
		this.resizeStroke = resizeStroke;
		this.repaint();
	}

	public Cursor getPredefinedCursor() {
		return predefinedCursor;
	}

	public void setPredefinedCursor(Cursor predefinedCursor) {
		this.predefinedCursor = predefinedCursor;
		this.repaint();
	}

	public Color getSelectBorderColor() {
		return selectBorderColor;
	}

	public void setSelectBorderColor(Color selectBorderColor) {
		this.selectBorderColor = selectBorderColor;
		this.repaint();
	}

	public Color getSelectFillColor() {
		return selectFillColor;
	}

	public void setSelectFillColor(Color selectFillColor) {
		this.selectFillColor = selectFillColor;
		this.repaint();
	}

	public Stroke getSelectStroke() {
		return selectStroke;
	}

	public void setSelectStroke(Stroke selectStroke) {
		this.selectStroke = selectStroke;
		this.repaint();
	}

	public int getBorderInflexionSize() {
		return borderInflexionSize;
	}

	public void setBorderInflexionSize(int borderInflexionSize) {
		this.borderInflexionSize = borderInflexionSize;
		this.repaint();
	}

	public Color getBorderInflexionColor() {
		return borderInflexionColor;
	}

	public void setBorderInflexionColor(Color borderInflexionColor) {
		this.borderInflexionColor = borderInflexionColor;
		this.repaint();
	}

	/**
	 * @return the selectedRectangle
	 */
	public Rectangle getSelectedRectangle() {
		return selectedRectangle;
	}

	/**
	 * @return the screenImage
	 */
	public BufferedImage getScreenImage() {
		return screenImage;
	}

	/**
	 * @return the captureWindow
	 */
	public JWindow getCaptureWindow() {
		return captureWindow;
	}

	private void setDefaultValue() {
		if (infoPanel != null) {
			infoPanel.setBounds(leftRectangle);
		}
	}

}