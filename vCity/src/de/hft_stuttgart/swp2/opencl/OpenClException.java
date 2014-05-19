package de.hft_stuttgart.swp2.opencl;

/**
 * An general OpenCL error has occured.
 * @author group 3/4
 *
 */
public class OpenClException extends Exception {

	private static final long serialVersionUID = -2593483705774091662L;

	public OpenClException(String s) {
		super(s);
	}
}
