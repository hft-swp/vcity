package de.hft_stuttgart.swp2.opencl;

public class CalculatorImpl implements CalculatorInterface {

	@Override
	public void calculateVolume() {
		VolumeCalculator.calculateVolume();
	}

	@Override
	public void calculateShadow(ShadowPrecision precision) {
		ShadowCalculator.calculateShadow(precision);
	}

}
