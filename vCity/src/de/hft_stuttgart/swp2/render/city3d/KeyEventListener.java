package de.hft_stuttgart.swp2.render.city3d;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.GregorianCalendar;
import de.hft_stuttgart.swp2.render.Main;

public class KeyEventListener implements KeyListener {
	private CityMap3D cityMap3D;
	private Date date;
	private int hour = 12;
	private int month;

	private GregorianCalendar gc = new GregorianCalendar();

	public KeyEventListener(CityMap3D cityMap3D) {
		gc.setTime(new Date());
		month = gc.get(GregorianCalendar.MONTH);
		gc.set(GregorianCalendar.HOUR_OF_DAY, 12);
		this.cityMap3D = cityMap3D;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_W)
				|| (e.getKeyCode() == KeyEvent.VK_UP)) {
			cityMap3D.camera.moveForward(0.5d);
		}
		if ((e.getKeyCode() == KeyEvent.VK_S)
				|| (e.getKeyCode() == KeyEvent.VK_DOWN)) {
			cityMap3D.camera.moveBackwards(0.5d);
		}
		if ((e.getKeyCode() == KeyEvent.VK_A)
				|| (e.getKeyCode() == KeyEvent.VK_LEFT)) {
			cityMap3D.camera.strafeLeft(0.5d);
		}
		if ((e.getKeyCode() == KeyEvent.VK_D)
				|| (e.getKeyCode() == KeyEvent.VK_RIGHT)) {
			cityMap3D.camera.strafeRight(0.5d);
		}
		if (e.getKeyCode() == KeyEvent.VK_O) {
			cityMap3D.ray++;
			cityMap3D.ray = cityMap3D.ray % (Main.getSplitAzimuth() * Main.getSplitHeight());
		}
		if (e.getKeyCode() == KeyEvent.VK_L) {
			cityMap3D.ray--;
			if (cityMap3D.ray < 0) {
				cityMap3D.ray = (Main.getSplitAzimuth() * Main.getSplitHeight()) - 1;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			cityMap3D.enableDrawCenters = !cityMap3D.enableDrawCenters;
		}
		if (e.getKeyCode() == KeyEvent.VK_I) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			month = gc.get(GregorianCalendar.MONTH);
			month++;
			if (month > 11) {
				month = 0;
				gc.add(GregorianCalendar.YEAR, 1);
			}
			gc.set(GregorianCalendar.MONTH, month);
			Main.getOptionGUI().setTime(gc.getTime(),
					gc.get(GregorianCalendar.HOUR_OF_DAY),
					gc.get(GregorianCalendar.MINUTE));
		}
		if (e.getKeyCode() == KeyEvent.VK_K) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			month = gc.get(GregorianCalendar.MONTH);
			month--;
			if (month < 0) {
				month = 11;
				gc.add(GregorianCalendar.YEAR, -1);
			}
			gc.set(GregorianCalendar.MONTH, month);
			Main.getOptionGUI().setTime(gc.getTime(),
					gc.get(GregorianCalendar.HOUR_OF_DAY),
					gc.get(GregorianCalendar.MINUTE));
		}
		if (e.getKeyCode() == KeyEvent.VK_U) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
			hour++;
			if (hour > 23) {
				hour = 0;
				gc.add(GregorianCalendar.DAY_OF_MONTH, 1);
			}
			gc.set(GregorianCalendar.HOUR_OF_DAY, hour);
			Main.getOptionGUI().setTime(gc.getTime(),
					gc.get(GregorianCalendar.HOUR_OF_DAY),
					gc.get(GregorianCalendar.MINUTE));
		}
		if (e.getKeyCode() == KeyEvent.VK_J) {
			date = Main.getOptionGUI().getTime();
			gc.setTime(date);
			hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
			hour--;
			if (hour < 0) {
				hour = 23;
				gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
			}
			gc.set(GregorianCalendar.HOUR_OF_DAY, hour);
			Main.getOptionGUI().setTime(gc.getTime(),
					gc.get(GregorianCalendar.HOUR_OF_DAY),
					gc.get(GregorianCalendar.MINUTE));
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
