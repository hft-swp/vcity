package de.hft_stuttgart.swp2.render.city3d;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
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
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
