package de.hft_stuttgart.swp2.parser;

import javax.xml.bind.JAXBException;

import de.hft_stuttgart.swp2.model.City;


/**
 * Interface of the parser group
 * @author 02grst1bif
 */
public interface ParserInterface {

	/**
	 * Read the file,
	 * transform Coordinates to an useful format,
	 * convert Polygons to Vertices and
	 * create Building Objects
	 * @param fileName Input file name
	 * @return List of Buildings
	 * @throws Exception 
	 */
	public City parse(String InputFileName) throws ParserException;
	
	/**
	 * Exports the Building Objects to a CSV file, which will contain <b>ID</b> and <b>Volume</b> of each Building
	 * @param city List of Buildings
	 * @param outputFileName Output file name
	 * @return true if the export was successful
	 */
	
	public boolean exportToCsv(City city, String outputFileName) throws ParserException;
	
	/**
	 * Exports the Building Objects to a cityGML file
	 * @param city List of Buildings
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 * @throws NullPointerException 
	 * @throws JAXBException 
	 */
	public boolean exportToCGML(City city, String outputFileName) throws ParserException;
	
	/**
	 * Returns the reference value used for the coordinate translation
	 * @return reference value
	 */
	public double[] getReference();
	
	/**
	 * Returns EPSG used in GML file
	 * @return EPSG
	 */
	public String getEPSG();
	
}
