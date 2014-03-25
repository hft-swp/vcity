package de.hft_stuttgart.swp2.model;

/**
 * This is a simple container class that represents a triangle
 * 
 * @author 12bema1bif, 12tost1bif, 12riju1bif
 * 
 */
public class Triangle {

	private final Vertex[] vertices;

	/**
	 * creates a new triangle out of 3 vertcies. If anything other than 3
	 * vertices is given this throws a IllegalArgumentException.
	 * 
	 * @param vertices
	 *            the 3 vertices that define the triangle
	 */
	public Triangle(final Vertex[] vertices) {
		if (vertices.length != 3) {
			throw new IllegalArgumentException(
					"A triangle always consists of 3 vertices\nYou created one with "
							+ vertices.length);
		}
		this.vertices = vertices;
	}

	/**
	 * 
	 * @return the vertices that the triangle consists of
	 */
	public Vertex[] getVertices() {
		return vertices;
	}

}
