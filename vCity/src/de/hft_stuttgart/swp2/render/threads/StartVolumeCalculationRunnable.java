package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.render.Main;

public class StartVolumeCalculationRunnable implements Runnable{

	@Override
	public void run() {
		//TODO Message in status bar => Now calculates the volume, one moment please
		Main.getCityMap3D().stopAnimator();
		Main.calculateVolume();
		Main.getCityMap3D().startAnimator();
	}

}