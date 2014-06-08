package de.hft_stuttgart.swp2.parser;

/**
 * Exception class, thrown when there is a error in the parsing process 
 * @author 02gasa1bif
 *
 */

@SuppressWarnings("serial")
public class ParserException extends Exception {

	public ParserException() {
	}

	public ParserException(String failureMessage) {
		super(failureMessage);
	}

}
