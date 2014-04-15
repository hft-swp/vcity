package de.hft_stuttgart.swp2.render;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	
	private static GUI gui;
	private static CityMap3D cityMap3D;

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
				gui = new GUI();
				gui.setVisible(true);
			}
		});
	}
	
	
	/**
	 * Ruft das Hauptfenster auf.
	 */
	public static void execute3DRendering(final int resWidth, final int resHeigth) {
		if(gui != null) {
			gui.dispose();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				cityMap3D = new CityMap3D("3D City Map",resWidth,resHeigth);  
				cityMap3D.setVisible(true);
			}
		});
	}
	

	public static void startParser(String path) {
		// TODO Auto-generated method stub
	}

}
