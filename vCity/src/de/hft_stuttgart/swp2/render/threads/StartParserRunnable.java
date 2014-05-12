package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.render.Main;

public class StartParserRunnable implements Runnable{
	private String pathToGmlFile;
	public StartParserRunnable(String pathToGmlFile){
		this.pathToGmlFile = pathToGmlFile;
	}

	@Override
	public void run() {
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(false);
		Main.getCityMap3D().stopAnimator();
		Main.startParser(pathToGmlFile);
		Main.getCityMap3D().startAnimator();
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(true);
	}

}
