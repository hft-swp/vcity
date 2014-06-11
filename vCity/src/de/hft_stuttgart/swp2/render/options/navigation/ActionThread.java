package de.hft_stuttgart.swp2.render.options.navigation;

import de.hft_stuttgart.swp2.render.Main;

public class ActionThread extends Thread {
	Direction btn;
	Boolean run;

	public ActionThread(Direction btn) {
		this.btn = btn;
		this.run = true;
	}

	@Override
	public void run() {
		System.out.println(this.btn + " Thread gestartet");
		while (run) {
			try {
				if (this.btn == Direction.up) {
					Main.getCityMap3D().camera.strafeForward(1d);
				} else if (this.btn == Direction.down) {
					Main.getCityMap3D().camera.strafeBackwards(1d);
				} else if (this.btn == Direction.left) {
					Main.getCityMap3D().camera.strafeLeft(1d);
				} else if (this.btn == Direction.right) {
					Main.getCityMap3D().camera.strafeRight(1d);
				}
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();				
			}
		}
		System.out.println(this.btn + " Thread beendet");
	}

	public void stopThread() {
		this.run = false;
	}
}
