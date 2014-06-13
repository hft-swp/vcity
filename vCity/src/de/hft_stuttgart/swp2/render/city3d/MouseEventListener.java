package de.hft_stuttgart.swp2.render.city3d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseEventListener implements MouseListener, MouseWheelListener,
		MouseMotionListener {

	CityMap3D cityMap3D;
	private double mouse_x;
	public double getMouse_x() {
		return mouse_x;
	}

	private double mouse_y;

	public double getMouse_y() {
		return mouse_y;
	}

	public MouseEventListener(CityMap3D cityMap3D) {
		this.cityMap3D = cityMap3D;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();
		cityMap3D.setCmd(CityMap3D.getSelect());
//		if(e.getClickCount() == 2){
//			mouse_x = e.getX();
//			mouse_y = e.getY();
//			cityMap3D.setCmd(CityMap3D.getSelect());
//		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		cityMap3D.halfScreenWidth = e.getXOnScreen();
		cityMap3D.halfScreenHeight = e.getYOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (cityMap3D.camera == null) {
			return;
		}
		int dx = e.getXOnScreen() - cityMap3D.halfScreenWidth;
		int dy = e.getYOnScreen() - cityMap3D.halfScreenHeight;
		cityMap3D.robot.mouseMove(cityMap3D.halfScreenWidth,
				cityMap3D.halfScreenHeight);
		cityMap3D.camera.turnLeft(dx * 2 * Math.PI / 360 / 8);
		cityMap3D.camera.turnDown(dy * 2 * Math.PI / 360 / 8);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches >= 0) {
			cityMap3D.camera.moveBackwards(8);
		} else {
			cityMap3D.camera.moveForward(8);
		}

	}

}
