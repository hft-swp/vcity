package de.hft_stuttgart.swp2.opencl;

/**
 * This class implements the CalculatorInterface if the context creation of the
 * openCL fails it falls back to a java implementation which is slow.
 * 
 * @author group 3/4
 *
 */
public class CalculatorImpl implements CalculatorInterface {

	@Override
	public void calculateVolume() throws OpenClException {
		VolumeCalculatorInterface vc;
		try {
			vc = new VolumeCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using Java Backend");
			vc = new VolumeCalculatorJavaBackend();
		}
		vc.calculateVolume();
	}

	@Override
	public void calculateShadow(ShadowPrecision precision)
			throws OpenClException {
		ShadowCalculatorInterface backend;
		try {
			backend = new ShadowCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using Java Backend");
			backend = new ShadowCalculatorJavaBackend();
		}
		backend.calculateShadow(precision);
	}

	@Override
	public void calculateShadow(ShadowPrecision precision, int splitAzimuth,
			int splitHeight) throws OpenClException {
		ShadowCalculatorInterface backend;
		try {
			backend = new ShadowCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using Java Backend");
			backend = new ShadowCalculatorJavaBackend();
		}
		backend.calculateShadow(precision, splitAzimuth, splitHeight);
		
	}

}
