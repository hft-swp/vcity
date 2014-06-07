package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.OptionGUI;

public class StartVolumeCalculationRunnable implements Runnable{

	@Override
	public void run() {
		String oldText = Main.getCityMap3D().getTitle();
		Main.getCityMap3D().setTitle(oldText + " | calculate volume");
		Main.getCityMap3D().stopAnimator();
		Main.calculateVolume();
		Main.getCityMap3D().startAnimator();
		Main.getCityMap3D().setTitle(oldText);
		OptionGUI.updateCityInfo();
	}

}