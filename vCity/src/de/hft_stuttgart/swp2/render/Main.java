package de.hft_stuttgart.swp2.render;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.opencl.CalculatorImpl;
import de.hft_stuttgart.swp2.opencl.CalculatorInterface;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.parser.Parser;
import de.hft_stuttgart.swp2.parser.ParserException;
import de.hft_stuttgart.swp2.parser.ParserInterface;
import de.hft_stuttgart.swp2.render.city3d.CityMap3D;
import de.hft_stuttgart.swp2.render.city3d.Message;
import de.hft_stuttgart.swp2.render.city3d.Message.Style;
import de.hft_stuttgart.swp2.render.options.OptionGUI;
import de.hft_stuttgart.swp2.render.options.PanelInformation;
import de.hft_stuttgart.swp2.render.threads.StartShadowCalculationRunnable;
import de.hft_stuttgart.swp2.render.threads.StartVolumeCalculationRunnable;

public class Main {
	
	private static OptionGUI optionGUI;
	private static CityMap3D cityMap3D;
	private static City city;
	private static boolean isParserSuccess = false;
	private static Date currentDate = new Date();
	private static CalculatorInterface backend = new CalculatorImpl();
	public static ExecutorService executor = Executors.newFixedThreadPool(1);
	private static int splitAzimuth = 16;
	private static int splitHeight = 8;
	public static String newline = System.getProperty("line.separator"); 
	
	public static Runnable startVolumeCalculationRunnable = new StartVolumeCalculationRunnable();
	public static Runnable startShadowCalculationRunnable = new StartShadowCalculationRunnable(
			ShadowPrecision.VERY_LOW, splitAzimuth, splitHeight);
	static Message message = new Message(cityMap3D);

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

	/**
	 * Returns the time needed for calculating the sun position from the gregorian calender.
	 * @return
	 */
	public static GregorianCalendar getTimeForSunPosition(){
		return optionGUI.getTime();
	}
	
	/**
	 * Sets the time needed for calculation the sun position.
	 * @param date
	 */
	public static void setTimeForSunPosition(Date date){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		optionGUI.setTime(calendar.getTime(), 
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}
	
	/**
	 * Returns the current date.
	 * @return
	 */
	public static Date getCurrentTime(){
		return currentDate;
	}

	/**
	 * Creates the GUI of the program.
	 */
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
						cityMap3D.setExtendedState(JFrame.MAXIMIZED_BOTH);
//						cityMap3D.setAlwaysOnTop(true);
					}

				} catch (HeadlessException e) {
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
	
	/**
	 * Method for starting volume-calculation of the city.
	 */
	public static void calculateVolume(){
		if (Main.isParserSuccess()) {
			long start;
			long end;
			System.out.println("Starting volume calculation...");			
			start = System.currentTimeMillis();
			try {				
//				message.makeText(cityMap3D,"Starting volume calculation...",Style.NORMAL).display();
				backend.calculateVolume();
				Message.makeText(cityMap3D,"Volume calculation successful",Style.SUCCESS).display(); 
			} catch (OpenClException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
			System.out.printf("calculate volume took %d milliseconds\n",
					(end - start));
		}
	}
	
	/**
	 * Method for starting the shadow-calculation of the city.
	 * @param shadowPrecision
	 * @param splitAzimuth
	 * @param splitHeight
	 */
	public static void calculateShadow(ShadowPrecision shadowPrecision, int splitAzimuth, int splitHeight){
		if (Main.isParserSuccess()) {
			long start;
			long end;
			System.out.println("Starting shadow calculation...");
			start = System.currentTimeMillis();
			try {
				backend.calculateShadow(shadowPrecision, splitAzimuth, splitHeight); // VERY_LOW(5),
				Message.makeText(cityMap3D,"Shadow calculation successful",Style.SUCCESS).display();
				// LOW(2.5f),
				// MID(1.25f),
				// HIGH(0.75f),
				// ULTRA(0.375f),
				// HYPER(0.1f),
				// AWESOME(0.01f)
			} catch (OpenClException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
			System.out.printf("calculate shadow took %d milliseconds\n",
					(end - start));
		}
	}
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public static boolean isCalculateVolume(){
		return optionGUI.isCalculateVolume();
	}
	
	public static boolean isCalculateShadow(){
		return optionGUI.isCalculateShadow();
	}
	
	public static void setCityMap3DToNull(){
		cityMap3D = null;
	}

	/**
	 * starts the parser, path is the path to the CityGML file
	 * @param path
	 */
	public static void startParser(String path) {
		long start;
		long end;
		System.out.println("Starting parsing...");
		message.makeText(cityMap3D, "Start parsing...", Style.NORMAL).display();
		start = System.currentTimeMillis();
		try {
			ParserInterface parser = Parser.getInstance();
			City.getInstance().getBuildings().clear();
			city = parser.parse(path);
			isParserSuccess = true;
			Main.cityMap3D.setIsStartCalculation(true);
		} catch (ParserException e) {
			if(City.getInstance().getBuildings().size() > 1){
				isParserSuccess = true;
			}else{
				isParserSuccess = false;
			}
			PanelInformation.addNewTopic("Importieren von Parser-Daten");
			PanelInformation.addProgrammInfo("Die Daten konnten nicht erfolgreich eingelesen werden.");
			PanelInformation.addProgrammInfo(e.getMessage());
		}
		end = System.currentTimeMillis();
		System.out.printf("parsing took %d milliseconds\n",
				(end - start));
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

	/**
	 * returns true if parser was successful
	 * @return
	 */
	
	public static boolean isParserSuccess() {
		return isParserSuccess;
	}

	public static String getPath() {
		return optionGUI.getPath();
	}

	public static int getSplitAzimuth() {
		return splitAzimuth;
	}

	public static void setSplitAzimuth(int splitAzimuth) {
		Main.splitAzimuth = splitAzimuth;
	}

	public static int getSplitHeight() {
		return splitHeight;
	}

	public static void setSplitHeight(int splitHeight) {
		Main.splitHeight = splitHeight;
	}


}
