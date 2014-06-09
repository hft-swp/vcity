package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.OptionGUI;

public class StartShadowCalculationRunnable  implements Runnable{
	private ShadowPrecision shadowPrecision;
	private int splitAzimuth;
	private int splitHeight;
	public StartShadowCalculationRunnable(ShadowPrecision shadowPrecision, int splitAzimuth, int splitHeight){
		this.shadowPrecision = shadowPrecision;
		this.splitAzimuth = splitAzimuth;
		this.splitHeight = splitHeight;
	}

	@Override
	public void run() {
		String oldText = Main.getCityMap3D().getTitle();
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(false);
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(false);
		Main.getCityMap3D().setTitle(oldText + " | calulate shadow");
		Main.getCityMap3D().stopAnimator();
		Main.calculateShadow(shadowPrecision, splitAzimuth, splitHeight);
		Main.getCityMap3D().startAnimator();
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(true);
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(true);
		Main.getCityMap3D().setTitle(oldText);
		OptionGUI.updateCityInfo();
	}

}