package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.OptionGUI;
import de.hft_stuttgart.swp2.render.options.PanelCityInfo;

public class StartVolumeCalculationRunnable implements Runnable{

	@Override
	public void run() {
		String oldText = Main.getCityMap3D().getTitle();
		Main.getCityMap3D().setTitle(oldText + " | calculate volume");
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(false);
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(false);
		Main.getCityMap3D().stopAnimator();
		Main.calculateVolume();
		Main.getCityMap3D().startAnimator();
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(true);
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(true);
		oldText = "vCity - 3D Stadtansicht";
		Main.getCityMap3D().setTitle(oldText);
		PanelCityInfo.updateCityInfo();
		Main.getOptionGUI().setCbVolumeAmount(true);
		Main.getOptionGUI().setSelectVolumeView(true);
	}

}