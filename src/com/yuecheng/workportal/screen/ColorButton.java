/**
 * 类说明：
 * @创建时间 2014-2-12 上午11:55:14
 * @创建人：zdyang
 * @项目名称 capture
 * @类名 ColorPanel.java
 */
package com.yuecheng.workportal.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorButton extends JButton {

	private static final long serialVersionUID = -9053501688051574658L;

	private int size;
	private boolean isBig;
	private Color fillColor;

	private int defaultBigSize = 28;
	private int defaultSmallSize = 14;

	private Color bigBorderColor = new Color(0, 49, 115);
	private Color smallBorderColor = new Color(51, 91, 145);

	public ColorButton(Color fillColor) {
		this(14, fillColor);
	}

	public ColorButton(int size, Color fillColor) {
		this.size = size;
		this.fillColor = fillColor;
		initUI();
	}
	
	public ColorButton(boolean isBig, Color fillColor) {
		this.isBig = isBig;
		this.fillColor = fillColor;
		if (isBig) {
			this.size = defaultBigSize;
		} else {
			this.size = defaultSmallSize;
		}
		initUI();
	}
	
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor ;
		repaint() ;
	}

	private void initUI() {
		setPreferredSize(new Dimension(size, size));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Color color = null;
		if (isBig) {
			color = bigBorderColor;
		} else {
			color = smallBorderColor;
		}
		if (getModel().isRollover() && !isBig) {
			super.paintComponent(g);
			g.setColor(color);
			g.drawRect(0, 0, size - 1, size - 1);
			g.setColor(Color.WHITE) ;
			
			g.drawRect(1, 1, size - 3, size - 3) ;
			g.setColor(fillColor);
			g.fillRect(2, 2, size - 4, size - 4);
			
		} else {
			super.paintComponent(g);
			g.setColor(color);
			g.drawRect(0, 0, size - 1, size - 1);
			g.setColor(Color.WHITE) ;
			g.drawRect(1, 1, size - 3, size - 3) ;

			g.setColor(fillColor);
			if (isBig) {
				g.fillRect(2, 2, size - 4, size - 4);
			} else {
				g.fillRect(1, 1, size - 2, size - 2);
			}
		}
		g.dispose();

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new ColorBoxPanel();
		frame.add(panel);
		frame.setSize(400, 300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
