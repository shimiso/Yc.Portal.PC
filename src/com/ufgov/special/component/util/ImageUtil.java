package com.ufgov.special.component.util;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;

/**
 * @describe 图片工具类
 * @author suyanga
 * @date 2015年5月10日 下午5:23:42
 */
public class ImageUtil extends ImageIcon implements Transferable, ClipboardOwner {

  private static final long serialVersionUID = 1L;

  private DataFlavor[] flavors;

  private Image img;

  /**
   * @describe Image 转 BufferedImage
   * @param image
   * @return 
   * @author suyanga
   * @date 2015年5月10日 下午5:21:08
   */
  public static BufferedImage toBufferedImage(Image image) {
    if (image instanceof BufferedImage) {
      return (BufferedImage) image;
    }

    // This code ensures that all the pixels in the image are loaded
    image = new ImageIcon(image).getImage();

    // Determine if the image has transparent pixels; for this method's
    // implementation, see e661 Determining If an Image Has Transparent Pixels
    //boolean hasAlpha = hasAlpha(image);

    // Create a buffered image with a format that's compatible with the screen
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      // Determine the type of transparency of the new buffered image
      int transparency = Transparency.OPAQUE;
      /* if (hasAlpha) {
        transparency = Transparency.BITMASK;
        }*/

      // Create the buffered image
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
    } catch (HeadlessException e) {
      // The system does not have a screen
    }

    if (bimage == null) {
      // Create a buffered image using the default color model
      int type = BufferedImage.TYPE_INT_RGB;
      //int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
      /*if (hasAlpha) {
       type = BufferedImage.TYPE_INT_ARGB;
       }*/
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    }

    // Copy image to buffered image
    Graphics g = bimage.createGraphics();

    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return bimage;
  }

  /**
   * @describe 图像转换Tansferable对象
   * @param img 
   * @author suyanga
   * @date 2015年5月10日 下午5:32:16
   */
  public ImageUtil(Image img) {
    flavors = new DataFlavor[] { DataFlavor.imageFlavor };
    this.img = img;
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    //  System.out.println("lostownership");
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    if (flavor.equals(flavors[0]))
      return true;
    return false;
  }

  @Override
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(flavors[0])) {
      return img;
    }
    return null;
  }

}
