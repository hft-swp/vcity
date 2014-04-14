package de.hft_stuttgart.swp2.render;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyEventListener implements KeyListener{
	private CoordinateSystem coordinateSystem;

	public KeyEventListener(CoordinateSystem coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//in Richtung Z Achse mit oben und unten Taste gehen
		if (e.getKeyCode() == KeyEvent.VK_UP){
			coordinateSystem.setTransz(coordinateSystem.getTransz() - 0.2f);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN){
			coordinateSystem.setTransz(coordinateSystem.getTransz() + 0.2f);
		}
		
		//in Richtung Z Achse mit oben und unten Taste gehen
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){
			coordinateSystem.setTransx(coordinateSystem.getTransx() - 0.2f);
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT){
			coordinateSystem.setTransx(coordinateSystem.getTransx() + 0.2f);
		}
		if (e.getKeyCode() == KeyEvent.VK_Y){
			coordinateSystem.setTransy(coordinateSystem.getTransy() - 0.2f);
		}
		else if (e.getKeyCode() == KeyEvent.VK_A){
			coordinateSystem.setTransy(coordinateSystem.getTransy() + 0.2f);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}



}
