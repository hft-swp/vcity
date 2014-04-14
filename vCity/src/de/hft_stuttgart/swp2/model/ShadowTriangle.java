package de.hft_stuttgart.swp2.model;

import java.util.BitSet;

public class ShadowTriangle extends Triangle{
	
	private BitSet shadowSet = new BitSet();
	private Vertex center;
	
	public ShadowTriangle(Vertex[] vertices) {
		super(vertices);
		calcCenter();
	}
	
	public ShadowTriangle(Vertex v0, Vertex v1, Vertex v2) {
		super(v0, v1, v2);
		calcCenter();
	}

	public BitSet getShadowSet() {
		return shadowSet;
	}

	public void setShadowSet(BitSet shadowSet) {
		this.shadowSet = shadowSet;
	}
	
	private void calcCenter() {
		float x = 1/3f * (getVertices()[0].getX() + getVertices()[1].getX() + getVertices()[2].getX());
		float y = 1/3f * (getVertices()[0].getY() + getVertices()[1].getY() + getVertices()[2].getY());
		float z = 1/3f * (getVertices()[0].getZ() + getVertices()[1].getZ() + getVertices()[2].getZ());
		center = new Vertex(x, y, z);
	}
	
	public Vertex getCenter() {
		return center;
		
	}
	
	

}
