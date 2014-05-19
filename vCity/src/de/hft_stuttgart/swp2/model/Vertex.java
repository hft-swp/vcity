package de.hft_stuttgart.swp2.model;

/**
 * 
 * simple container class for a vertex in a 3D-space
 * 
 * @author 12bema1bif, 12tost1bif, 12riju1bif
 * 
 */
public class Vertex {

	private final float[] coords = new float[3];
	
	private boolean visited = false;

	/**
	 * creates a new vertex from a float array.
	 * 
	 * @param coordinates
	 *            float array of 3 floats
	 */
	public Vertex(final float[] coordinates) {
		if (coordinates.length != 3) {
			throw new IllegalArgumentException(
					"A 3D vertex should have exactly 3 floats, you provided: "
							+ coordinates.length);
		}
		coords[0] = coordinates[0];
		coords[1] = coordinates[1];
		coords[2] = coordinates[2];
	}

	/**
	 * creates a new vertex out of 3 coordinates.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param z
	 *            coordinate
	 */
	public Vertex(float x, float y, float z) {
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
	}

	/**
	 * 
	 * @return all coordinates in one array. No copy is made.
	 */
	public float[] getCoordinates() {
		return coords;
	}

	/**
	 * 
	 * @return the X value of the vertex (same as getCoordinates()[0])
	 */
	public float getX() {
		return coords[0];
	}

	/**
	 * 
	 * @return the Y value of the vertex (same as getCoordinates()[1])
	 */
	public float getY() {
		return coords[1];
	}

	/**
	 * 
	 * @return the Z value of the vertex (same as getCoordinates()[2])
	 */
	public float getZ() {
		return coords[2];
	}

	/**
	 * moves the point according to the given values
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate(float x, float y, float z) {
		coords[0] += x;
		coords[1] += y;
		coords[2] += z;
	}
	
	/**
	 * this scales the vertex with the given values
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scale(float x, float y, float z) {
		coords[0] *= x;
		coords[1] *= y;
		coords[2] *= z;
	}
	
	/**
	 * this is used internally, do not use
	 * @return
	 */
	public boolean wasVisited() {
		return visited;
	}
	
	/**
	 * this is used internally, do not use
	 */
	public void visit() {
		visited = true;
	}
	
	/**
	 * this is used internally, do not use
	 */
	public void resetVisit() {
		visited = false;
	}

}
