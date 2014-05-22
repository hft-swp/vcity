package de.hft_stuttgart.swp2.parser;

import de.hft_stuttgart.swp2.model.City;

/**
 * Interface of the parser import
 * 
 * @author 02grst1bif
 */
public interface ParserInterface {

	/**
	 * Read the file, transform Coordinates to an useful format, convert
	 * Polygons to Vertices and create Building Objects
	 * 
	 * @param fileName Input file name
	 * @return List of Buildings
	 * @throws ParserException
	 */
	public City parse(String InputFileName) throws ParserException;

	/**
	 * Returns the reference value used for the coordinate translation
	 * 
	 * @return reference value
	 */
	public double[] getReference();

	/**
	 * Returns EPSG used in GML file
	 * 
	 * @return EPSG
	 */
	public String getEPSG();

}
