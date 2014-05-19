package de.hft_stuttgart.swp2.model;

import java.util.BitSet;

/**
 * This is a special triangle which stores boolean values for shadow usage in
 * addtion to the normal vertices.
 * 
 * @author 12bema1bif
 *
 */
public class ShadowTriangle extends Triangle {

	private BitSet shadowSet = new BitSet();
	private Vertex center;

	/**
	 * Creates a ShadowTriangle from the given vertices
	 * 
	 * @param vertices
	 *            Vertices array of length 3
	 */
	public ShadowTriangle(Vertex[] vertices) {
		super(vertices);
		calcCenter();
	}

	/**
	 * Creates a ShadowTriangle from the given vertices
	 * 
	 * @param v0
	 * @param v1
	 * @param v2
	 */
	public ShadowTriangle(Vertex v0, Vertex v1, Vertex v2) {
		super(v0, v1, v2);
		calcCenter();
	}

	/**
	 * 
	 * @return the bitset containing the shadow values
	 */
	public BitSet getShadowSet() {
		return shadowSet;
	}

	/**
	 * sets the bitset containg the shadow values
	 * 
	 * @param shadowSet
	 */
	public void setShadowSet(BitSet shadowSet) {
		this.shadowSet = shadowSet;
	}

	private void calcCenter() {
		float x = 1 / 3f * (getVertices()[0].getX() + getVertices()[1].getX() + getVertices()[2]
				.getX());
		float y = 1 / 3f * (getVertices()[0].getY() + getVertices()[1].getY() + getVertices()[2]
				.getY());
		float z = 1 / 3f * (getVertices()[0].getZ() + getVertices()[1].getZ() + getVertices()[2]
				.getZ());
		center = new Vertex(x, y, z);
	}

	/**
	 * 
	 * @return the center of the triangle
	 */
	public Vertex getCenter() {
		return center;

	}

}
