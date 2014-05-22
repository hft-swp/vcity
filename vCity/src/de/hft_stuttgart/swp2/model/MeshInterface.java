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

	@Deprecated
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();

	/**
	 * 
	 * @return the list of triangles
	 * @deprecated {@link Building} does not have {@link Triangle}s anymore.
	 * @see {@link Polygon#getTriangles()}
	 */
	@Deprecated
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}

	/**
	 * adds a triangle to the list stored in the Mesh
	 * 
	 * @param t
	 * @deprecated {@link Building} does not have {@link Triangle}s anymore.
	 * @see {@link Polygon#addTriangle(Triangle)}
	 */
	@Deprecated
	public void addTriangle(Triangle t) {
		triangles.add(t);
	}

}
