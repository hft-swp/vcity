package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

/**
 * This is an abstract class containing a list of triangles. Implementors are
 * e.g. Building but can also be used for environment objects
 * 
 * @author many
 *
 */
public abstract class MeshInterface {

	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();

	/**
	 * 
	 * @return the list of triangles
	 */
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}

	/**
	 * adds a triangle to the list stored in the Mesh
	 * 
	 * @param t
	 */
	public void addTriangle(Triangle t) {
		triangles.add(t);
	}

}
