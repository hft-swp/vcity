package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.render.Main;

public class StartShadowCalculationRunnable  implements Runnable{
	private ShadowPrecision shadowPrecision;
	public StartShadowCalculationRunnable(ShadowPrecision shadowPrecision){
		this.shadowPrecision = shadowPrecision;
	}

	@Override
	public void run() {
		//TODO Message in status bar => Now calculates the shadow, one moment please
		Main.getCityMap3D().stopAnimator();
		Main.calculateShadow(shadowPrecision);
		Main.getCityMap3D().startAnimator();
	}

}