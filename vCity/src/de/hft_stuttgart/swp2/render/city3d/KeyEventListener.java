package de.hft_stuttgart.swp2.render.city3d;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;
import de.hft_stuttgart.swp2.render.Main;

public class KeyEventListener implements KeyListener{
	private CityMap3D cityMap3D;
	private Date date;
	private int hour;
	private GregorianCalendar gc = new GregorianCalendar();

	public KeyEventListener(CityMap3D cityMap3D) {
		this.cityMap3D = cityMap3D;
	}

	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_UP)) {
			cityMap3D.camera.moveForward(0.5d);
		}
		if ((e.getKeyCode() == KeyEvent.VK_S) || (e.getKeyCode() == KeyEvent.VK_DOWN)) {
			cityMap3D.camera.moveBackwards(0.5d);
		}
		if ((e.getKeyCode() == KeyEvent.VK_A) || (e.getKeyCode() == KeyEvent.VK_LEFT)) {
			cityMap3D.camera.strafeLeft(0.5d);
		}
		if ((e.getKeyCode() == KeyEvent.VK_D)|| (e.getKeyCode() == KeyEvent.VK_RIGHT)) {
			cityMap3D.camera.strafeRight(0.5d);
		}
		if (e.getKeyCode() == KeyEvent.VK_O) {
			cityMap3D.ray++;
			cityMap3D.ray = cityMap3D.ray % 144;
		}
		if (e.getKeyCode() == KeyEvent.VK_L) {
			cityMap3D.ray--;
			if (cityMap3D.ray < 0) {
				cityMap3D.ray = 143;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			cityMap3D.enableDrawCenters = !cityMap3D.enableDrawCenters;
		}
		if (e.getKeyCode() == KeyEvent.VK_I) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			cityMap3D.month = gc.get(GregorianCalendar.MONTH);
			cityMap3D.month++;
			if (cityMap3D.month > 11) {
				cityMap3D.month = 11;
			}
			setMonth();
		}
		if (e.getKeyCode() == KeyEvent.VK_K) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			cityMap3D.month = gc.get(GregorianCalendar.MONTH);
			cityMap3D.month--;
			if (cityMap3D.month < 0) {
				cityMap3D.month = 0;
			}
			setMonth();
		}
		if (e.getKeyCode() == KeyEvent.VK_U) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
			hour++;
			if (hour > 23) {
				hour = 0;
			}
			setHour(hour);
		}
		if (e.getKeyCode() == KeyEvent.VK_J) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
			hour--;
			if (hour < 0) {
				hour = 23;
			}
			setHour(hour);
		}
		
		
		
		


	}


	private void setMonth() {
		date = Main.getOptionGUI().getTime();
		gc.setTime(date);
		gc.set(GregorianCalendar.MONTH, cityMap3D.month);
		Main.getOptionGUI().setTime(gc.getTime(), gc.get(GregorianCalendar.HOUR_OF_DAY),
				gc.get(GregorianCalendar.MONTH));
		cityMap3D.utcCal.set(gc.get(GregorianCalendar.YEAR), cityMap3D.month, gc.get(GregorianCalendar.DATE), gc.get(GregorianCalendar.HOUR_OF_DAY), 0, 0);
		cityMap3D.sunPos = new SunPositionCalculator(cityMap3D.utcCal.getTime(), 11.6, 48.1);
		cityMap3D.ray = cityMap3D.sunPos.getSunPosition();
	}
	
	private void setHour(int hour) {
		date = Main.getOptionGUI().getTime();
		gc.setTime(date);
		gc.set(gc.get(GregorianCalendar.HOUR_OF_DAY), hour);
		Main.getOptionGUI().setTime(gc.getTime(), gc.get(GregorianCalendar.HOUR_OF_DAY),
				gc.get(GregorianCalendar.MONTH));
		cityMap3D.utcCal.set(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH), 
				gc.get(GregorianCalendar.DATE), gc.get(GregorianCalendar.HOUR_OF_DAY), 0, 0);
		cityMap3D.sunPos = new SunPositionCalculator(cityMap3D.utcCal.getTime(), 11.6, 48.1);
		cityMap3D.ray = cityMap3D.sunPos.getSunPosition();
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
