package de.hft_stuttgart.swp2.opencl;

/**
 * 
 * This interface provides a method for calculating the volume of all buildings
 * in the city
 * 
 * @author group 3/4
 *
 */
public interface VolumeCalculatorInterface {

	/**
	 * Calculates the volume of all buildings. It first tries to use the gpu, if
	 * that fails it falls back to a java implementation.
	 */
	public void calculateVolume();
}
