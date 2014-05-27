package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.render.Main;

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
		//TODO Message in status bar => Now calculates the shadow, one moment please
		Main.getCityMap3D().stopAnimator();
		Main.calculateShadow(shadowPrecision, splitAzimuth, splitHeight);
		Main.getCityMap3D().startAnimator();
	}

}