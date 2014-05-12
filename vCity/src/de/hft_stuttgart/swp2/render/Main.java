package de.hft_stuttgart.swp2.render;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.Date;

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
	private static Boolean isParserSuccess = false;
	private static Date currentDate = new Date();

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
		
		createGUI();
	}

	public static Date getTimeForSunPosition(){
		return optionGUI.getTime();
	}
	
	public static Date getCurrentTime(){
		return currentDate;
	}

	public static void createGUI() {
		// Willkommensfenster aufrufen und Fehlerfenster initialisieren
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if(cityMap3D == null){
						int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
						int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
						cityMap3D = new CityMap3D(width,height);
						cityMap3D.setVisible(true);
						cityMap3D.setAlwaysOnTop(true);
					}

				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OpenClException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(optionGUI == null){
					optionGUI = new OptionGUI();
					optionGUI.setVisible(true);
				}
			}
		});
	}
	
	public static Boolean isCalculateVolume(){
		return optionGUI.isCalculateVolume();
	}
	
	public static Boolean isCalculateShadow(){
		return optionGUI.isCalculateShadow();
	}
	
	public static void setCityMap3DToNull(){
		cityMap3D = null;
	}

	public static void startParser(String path) {
		try {
			City.getInstance().getBuildings().clear();
			city = CGMLParser.getInstance().parse(path);
			isParserSuccess = true;
			Main.cityMap3D.setIsStartCalculation(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(City.getInstance().getBuildings().size() > 1){
				isParserSuccess = true;
			}else{
				isParserSuccess = false;
			}

			e.getMessage(); // Contains the error message
		}
	}


	public static City getCity() {
		return city;
	}



	public static OptionGUI getOptionGUI() {
		return optionGUI;
	}



	public static CityMap3D getCityMap3D() {
		return cityMap3D;
	}

	public static Boolean isParserSuccess() {
		return isParserSuccess;
	}






}
