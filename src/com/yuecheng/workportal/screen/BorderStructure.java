package com.yuecheng.workportal.screen;

import java.awt.Cursor;
import java.awt.geom.Rectangle2D;

/**
 * 
 * @author  zhangtao
 * @msn		zht_dream@hotmail.com
 * @mail    zht_dream@hotmail.com
 * Let's Swing together.
 */
public class BorderStructure {
	public static final int POSITION_TOP = 1;
	public static final int POSITION_BOTTOM = 2;
	public static final int POSITION_LEFT = 3;
	public static final int POSITION_RIGHT = 4;
	public static final int POSITION_TOPLEFT = 5;
	public static final int POSITION_TOPRIGHT = 6;
	public static final int POSITION_BOTTOMLEFT = 7;
	public static final int POSITION_BOTTOMRIGHT = 8;

	public static final Cursor CURSOR_TOP = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	public static final Cursor CURSOR_BOTTOM = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
	public static final Cursor CURSOR_LEFT = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
	public static final Cursor CURSOR_RIGHT = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
	public static final Cursor CURSOR_TOPLEFT = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
	public static final Cursor CURSOR_TOPRIGHT = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
	public static final Cursor CURSOR_BOTTOMLEFT = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
	public static final Cursor CURSOR_BOTTOMRIGHT = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);

	private Rectangle2D.Double border;
	private int position;
	private Cursor cursor;

	public BorderStructure(Rectangle2D.Double border, int position) {
		this.border = border;
		this.position = position;
		setCursor(position);
	}

	private void setCursor(int position) {
		switch (position) {
		case POSITION_TOP:
			cursor = CURSOR_TOP;
			break;
		case POSITION_BOTTOM:
			cursor = CURSOR_BOTTOM;
			break;
		case POSITION_LEFT:
			cursor = CURSOR_LEFT;
			break;
		case POSITION_RIGHT:
			cursor = CURSOR_RIGHT;
			break;
		case POSITION_TOPLEFT:
			cursor = CURSOR_TOPLEFT;
			break;
		case POSITION_TOPRIGHT:
			cursor = CURSOR_TOPRIGHT;
			break;
		case POSITION_BOTTOMLEFT:
			cursor = CURSOR_BOTTOMLEFT;
			break;
		case POSITION_BOTTOMRIGHT:
			cursor = CURSOR_BOTTOMRIGHT;
			break;
		}
	}

	public Rectangle2D.Double getBorder() {
		return border;
	}

	public void setBorder(Rectangle2D.Double border) {
		this.border = border;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
		setCursor(position);
	}

	public Cursor getCursor() {
		return cursor;
	}
}
