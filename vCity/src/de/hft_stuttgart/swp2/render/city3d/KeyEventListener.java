package de.hft_stuttgart.swp2.render.city3d;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;

public class KeyEventListener implements KeyListener{
	private CityMap3D cityMap3D;

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
			cityMap3D.month++;
			if (cityMap3D.month > 11) {
				cityMap3D.month = 11;
			}
			cityMap3D.utcCal.set(2014, cityMap3D.month + 1, 1, cityMap3D.hour, 0, 0);
			cityMap3D.sunPos = new SunPositionCalculator(cityMap3D.utcCal.getTime(), 11.6, 48.1);
			cityMap3D.ray = cityMap3D.sunPos.getSunPosition();
		}
		if (e.getKeyCode() == KeyEvent.VK_K) {
			cityMap3D.month--;
			if (cityMap3D.month < 0) {
				cityMap3D.month = 0;
			}
			cityMap3D.utcCal.set(2014, cityMap3D.month + 1, 1, cityMap3D.hour, 0, 0);
			cityMap3D.sunPos = new SunPositionCalculator(cityMap3D.utcCal.getTime(), 11.6, 48.1);
			cityMap3D.ray = cityMap3D.sunPos.getSunPosition();
		}
		if (e.getKeyCode() == KeyEvent.VK_U) {
			cityMap3D.hour++;
			if (cityMap3D.hour > 23) {
				cityMap3D.hour = 0;
			}
			cityMap3D.utcCal.set(2014, cityMap3D.month + 1, 1, cityMap3D.hour, 0, 0);
			cityMap3D.sunPos = new SunPositionCalculator(cityMap3D.utcCal.getTime(), 11.6, 48.1);
			cityMap3D.ray = cityMap3D.sunPos.getSunPosition();

		}
		if (e.getKeyCode() == KeyEvent.VK_J) {
			cityMap3D.hour--;
			if (cityMap3D.hour < 0) {
				cityMap3D.hour = 23;
			}
			cityMap3D.utcCal.set(2014, cityMap3D.month + 1, 1, cityMap3D.hour, 0, 0);
			cityMap3D.sunPos = new SunPositionCalculator(cityMap3D.utcCal.getTime(), 11.6, 48.1);
			cityMap3D.ray = cityMap3D.sunPos.getSunPosition();
		}
		
		
		
		


	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
