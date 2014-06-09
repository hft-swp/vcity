package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.OptionGUI;

public class StartParserRunnable implements Runnable{
	private String pathToGmlFile;
	public StartParserRunnable(String pathToGmlFile){
		this.pathToGmlFile = pathToGmlFile;
	}

	@Override
	public void run() {
		String oldText = Main.getCityMap3D().getTitle();
		Main.getCityMap3D().setTitle(oldText + " | parse file");
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(false);
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(false);
		Main.getCityMap3D().stopAnimator();
		Main.startParser(pathToGmlFile);
		Main.getCityMap3D().startAnimator();
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(true);
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(true);
		Main.getCityMap3D().setTitle(oldText);
		OptionGUI.updateCityInfo();
	}

}
