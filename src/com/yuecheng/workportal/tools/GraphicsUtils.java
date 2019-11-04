package com.yuecheng.workportal.tools;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class GraphicsUtils {
	
	/**
     * 截图屏幕中制定区域的图片
     * @param x
     * @param y
     * @param w
     * @param h
     * @return 被截部分的BufferedImage对象
     * @throws AWTException
     * @throws InterruptedException
     */
    public static BufferedImage getScreenImage(int x, int y, int w, int h) throws AWTException, InterruptedException {
		Robot robot = new Robot();
		BufferedImage screen = robot.createScreenCapture(new Rectangle(x, y, w, h));
		return screen;
	}
    
    /**
     * 给图片添加文字水印
     * @param targetImage 需要加上水印的图片
     * @param text 用做水印的文字
     * @param font 水印文字的字体
     * @param color 水印文字的颜色
     * @param x
     * @param y
     * @return 加上水印后的BufferedImage对象
     */
    public static BufferedImage addImageWaterMark(Image targetImage, String text, Font font, Color color, int x, int y) {
    	int width = targetImage.getWidth(null);
    	int height = targetImage.getHeight(null);
    	
    	BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	Graphics g = bi.getGraphics();
    	g.drawImage(targetImage, 0, 0, null);
    	g.setFont(font);
    	g.setColor(color);
    	g.drawString(text, x, y);
    	g.dispose();
    	
    	return bi;
    }
    
    /**
     * 给图片添加图片水印
     * @param markImage 用做水印的图片
     * @param targetImage 需要加上水印的图片 
     * @param x 
     * @param y 
     * @return 加上水印后的BufferedImage对象
     */  
    public static BufferedImage addImageWaterMark(Image targetImage, Image markImage, int x, int y) {  
        int wideth = targetImage.getWidth(null);  
        int height = targetImage.getHeight(null);  
        
        BufferedImage  bi = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);  
        Graphics g = bi.createGraphics();  
        g.drawImage(targetImage, 0, 0, null);  
        g.drawImage(markImage, x, y, null);              
        g.dispose();  
       
        return bi; 
    }
    
    /**
     * 将指定图片写入系统剪贴板
     * @param image
     */
    public static void setClipboardImage(final Image image) {
    	Transferable trans = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}

		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}
    
    /**
	 * 将ImageIcon转化为BufferedImage
	 * @param icon
	 * @return
	 */
	public static BufferedImage getBufferedImage(ImageIcon icon) {
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		ImageObserver observer = icon.getImageObserver();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics gc = bufferedImage.createGraphics();
		gc.drawImage(icon.getImage(), 0, 0, observer);
		return bufferedImage;
	}
	/**
	 * 将bufferedimage保存至本地文件
	 * @param img
	 * @param parentComponent
	 * @return 
	 */
	public static boolean exportImage(BufferedImage img, Component parentComponent) {
		boolean flag = false ;
		File file = null;
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.png)", "png");
	    chooser.setFileFilter(filter);
		FileSystemView fsv = FileSystemView.getFileSystemView() ;
		chooser.setCurrentDirectory(fsv.getHomeDirectory());//设置默认目录 打开直接默认桌面
		chooser.setDialogTitle("另存为"); //自定义选择框标题
		chooser.setSelectedFile(new File(DateToStringUtil.getStringByDate())); //设置默认文件名
		int returnVal = chooser.showDialog(parentComponent, "保存");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			flag = true ;
		}

		try {
			if (file != null) {
				ImageIO.write(img, "png", file);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return flag ;
	}
	
	public static File selectedLocalImage(Component parentComponent) {
		JFileChooser chooser = new JFileChooser();
		File file = null;
		FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.jpg, *.png, *.bmp)", "jpg", "png", "bmp");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(parentComponent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	file = chooser.getSelectedFile() ;
	    }
	    return file ;
	}
	/**
	 * 根据文件全路径得到ImageIcon对象
	 * @param path
	 * @return
	 */
	public static ImageIcon getImageIcon(String path) {
		return new ImageIcon(path) ;
	}
}