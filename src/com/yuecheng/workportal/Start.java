package com.yuecheng.workportal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.teamdev.jxbrowser.chromium.ba;
import com.yuecheng.workportal.ui.Main;

public class Start {
	static {
		try {
			Field e = ba.class.getDeclaredField("e");
			e.setAccessible(true);
			Field f = ba.class.getDeclaredField("f");
			f.setAccessible(true);
			Field modifersField = Field.class.getDeclaredField("modifiers");
			modifersField.setAccessible(true);
			modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
			modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			e.set(null, new BigInteger("1"));
			f.set(null, new BigInteger("1"));
			modifersField.setAccessible(false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main();
			}
		});
	}
}
