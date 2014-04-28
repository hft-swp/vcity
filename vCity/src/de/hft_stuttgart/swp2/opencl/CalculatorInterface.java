package de.hft_stuttgart.swp2.opencl;

public interface CalculatorInterface {
	
	public void calculateVolume() throws OpenClException;
	public void calculateShadow(ShadowPrecision precision) throws OpenClException;

}