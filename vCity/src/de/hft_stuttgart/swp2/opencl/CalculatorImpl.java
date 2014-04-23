package de.hft_stuttgart.swp2.opencl;

public class CalculatorImpl implements CalculatorInterface {

	@Override
	public void calculateVolume() throws OpenClException {
		VolumeCalculator vc = new VolumeCalculator();
		vc.calculateVolume();
	}

	@Override
	public void calculateShadow(ShadowPrecision precision) throws OpenClException {
		ShadowCalculatorInterface backend = new ShadowCalculatorJavaBackend();
		backend.calculateShadow(precision);
	}

}
