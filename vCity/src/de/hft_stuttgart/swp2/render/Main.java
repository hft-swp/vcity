package de.hft_stuttgart.swp2.render;

import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.parser.CGMLParser;
import de.hft_stuttgart.swp2.render.city3d.CityMap3D;
import de.hft_stuttgart.swp2.render.options.OptionGUI;

public class Main {
	
	private static OptionGUI optionGUI;
	private static CityMap3D cityMap3D;
	private static City city;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UIManager.setLookAndFeel( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel" );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Willkommensfenster aufrufen und Fehlerfenster initialisieren
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
					int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
					cityMap3D = new CityMap3D(width,height);
					cityMap3D.setVisible(true);
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OpenClException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				optionGUI = new OptionGUI();
				optionGUI.setVisible(true);
			}
		});
	}
	
	

	public static void startParser(String path) {
		city = CGMLParser.getInstance().parse(path);
	}


	public static City getCity() {
		return city;
	}




}
