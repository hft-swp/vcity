package de.hft_stuttgart.swp2.opencl;

public interface CalculatorInterface {

	/**
	 * This method calculates the volume of all buildings stored in the city
	 * singleton.
	 * 
	 * @throws OpenClException
	 *             if the initialization of the OpenCL context creation fails.
	 */
	public void calculateVolume() throws OpenClException;

	/**
	 * use {@link CalculatorInterface#calculateShadow(ShadowPrecision, int, int)} instead <br>
	 * 
	 * This method calculates the shadows of all buildings stored in the city
	 * singleton. The triangles are therefore split into smaller triangles
	 * called ShadowTriangles to ensure a certain amount of precision for the
	 * calculation. The maximum area can be set by the precision parameter. The
	 * calculation itself is not a 100% accurate.
	 * 
	 * @param precision
	 *            level of precision.
	 * @throws OpenClException
	 *             if the initialization of the OpenCL context creation fails.
	 */
	public void calculateShadow(ShadowPrecision precision, int splitAzimuth, int splitHeight) throws OpenClException;
	
	/**
	 * Calculates the area for each polygon
	 */
	public void calculateArea();
	
	

}
