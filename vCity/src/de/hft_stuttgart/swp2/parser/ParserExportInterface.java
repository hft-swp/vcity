package de.hft_stuttgart.swp2.parser;

/**
 * Interface of the parser export
 * 
 * @author 02grst1bif
 */
public interface ParserExportInterface {

	/**
	 * Exports the Building Objects to a CSV file, which will contain <b>ID</b>
	 * and <b>Volume</b> of each Building
	 * 
	 * @param outputFileName Output file name
	 * @return true if the export was successful
	 * @throws ParserException
	 */
	public boolean exportToCsv(String outputFileName) throws ParserException;

	/**
	 * Exports the Building Objects to a cityGML file
	 * 
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 * @throws ParserException
	 */
	public boolean exportToCGML(String outputFileName) throws ParserException;

	/**
	 * Exports the Shadow calculations to an XML file "INSEL" will can read.
	 * 
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 * @throws ParserException
	 */
	public boolean exportToXml(String outputFileName) throws ParserException;

}
