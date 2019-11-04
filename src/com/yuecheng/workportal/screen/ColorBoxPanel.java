/**
 * 类说明：
 * @创建时间 2014-2-12 上午11:51:29
 * @创建人：zdyang
 * @项目名称 capture
 * @类名 ColorInfoPanel.java
 */
package com.yuecheng.workportal.screen;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * 颜料盒 面板
 */
public class ColorBoxPanel extends JPanel {
	
	private static final long serialVersionUID = 3542948300616578568L;
	
	private Color selectedColor = Color.RED;
	
	private Color[] colors = new Color[]{new Color(0, 0, 0), new Color(128, 128, 128), new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128), new Color(128, 0, 128), new Color(0, 128, 128), new Color(255, 255, 255), new Color(255, 0, 0), new Color(255, 255, 0), new Color(0, 255, 0), new Color(0, 0, 255), new Color(255, 0, 255), new Color(0, 255, 255)} ;
	
	public ColorBoxPanel() {
		JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) ;
		final ColorButton tempButton = new ColorButton(true, Color.RED) ;
		firstPanel.add(tempButton) ;
		
		JPanel secondPanel = new JPanel(new GridLayout(2, 8, 2, 2)) ;
		for(int i = 0; i < colors.length; i ++) {
			final Color fillColor = colors[i] ;
			ColorButton colorButton = new ColorButton(fillColor) ;
			colorButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedColor = fillColor ;
					tempButton.setFillColor(fillColor) ;
				}
			}) ;
			secondPanel.add(colorButton) ;
		}
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2)) ;
		add(firstPanel) ;
		add(secondPanel) ;
		firstPanel.setOpaque(false) ;
		secondPanel.setOpaque(false) ;
		setOpaque(false) ;
	}
	
	/**
	 * @return the selectedColor
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}

	/**
	 * @param selectedColor the selectedColor to set
	 */
	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

}
