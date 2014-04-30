package de.hft_stuttgart.swp2.opencl;

public class CalculatorImpl implements CalculatorInterface {

	@Override
	public void calculateVolume() throws OpenClException {
		VolumeCalculatorInterface vc;
		try {
			vc = new VolumeCalculatorOpenClBackend();
		} catch (OpenClException e) {
			vc = new VolumeCalculatorJavaBackend();
		}
		
		vc.calculateVolume();
	}

	@Override
	public void calculateShadow(ShadowPrecision precision) throws OpenClException {
		ShadowCalculatorInterface backend;
		try {
			backend = new ShadowCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using java backend");
			backend = new ShadowCalculatorJavaBackend();
		}
		backend.calculateShadow(precision);
	}

}
