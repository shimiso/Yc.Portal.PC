package com.yuecheng.workportal.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JViewport;

/**
 * 
 * @author  zhangtao
 * @msn		zht_dream@hotmail.com
 * @mail    zht_dream@hotmail.com
 * Let's Swing together.
 */
public class OverViewPanel extends JPanel {
	private static final long serialVersionUID = 8891594031606252274L;
	private BufferedImage selectedImage;

	public OverViewPanel() {
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		int width = this.getWidth();
		int height = this.getHeight();
		g.fillRect(0, 0, width, height);
		if (selectedImage != null) {
			Graphics2D g2d = (Graphics2D) g;
			int x = 0;
			int y = 0;
			if (selectedImage.getWidth() < width) {
				x = (width - selectedImage.getWidth()) / 2;
			}
			if (selectedImage.getHeight() < height) {
				y = (height - selectedImage.getHeight()) / 2;
			}
			g2d.setColor(Color.RED);
			g2d.draw3DRect(x - 2, y - 2, selectedImage.getWidth() + 4, selectedImage.getHeight() + 4, true);
			g2d.drawImage(selectedImage, x, y, this);
		}
	}

	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		if (selectedImage != null) {
			if (size.width < selectedImage.getWidth()) {
				size.width = (int) (selectedImage.getWidth());
			}
			if (size.height < selectedImage.getHeight()) {
				size.height = (int) (selectedImage.getHeight());
			}
		}
		return size;
	}

	public BufferedImage getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(BufferedImage selectedImage) {
		this.selectedImage = selectedImage;
		this.validate();
		Component parent = this.getParent();
		if (parent != null && parent instanceof JViewport) {
			JViewport view = (JViewport) parent;
			view.setViewSize(getPreferredSize());
		}
	}

}
