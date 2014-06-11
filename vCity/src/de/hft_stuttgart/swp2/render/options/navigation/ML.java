package de.hft_stuttgart.swp2.render.options.navigation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ML implements MouseListener {
	private Direction btn;
	private ActionThread at;
	
	public ML(Direction btn){
		this.btn = btn;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * Initializes and starts the movement-thread if arrow-button is pressed.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		this.at = new ActionThread(this.btn);
		this.at.start();
	}

	/**
	 * Stops/Ends the movement-thread if mouse-button is released.
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.at.stopThread();
	}
}