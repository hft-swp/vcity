package de.hft_stuttgart.swp2.model;

import java.util.BitSet;

public class ShadowTriangle extends Triangle{
	
	private BitSet shadowSet = new BitSet();
	
	public ShadowTriangle(Vertex[] vertices) {
		super(vertices);
	}
	
	public ShadowTriangle(Vertex v0, Vertex v1, Vertex v2) {
		super(v0, v1, v2);
	}

	public BitSet getShadowSet() {
		return shadowSet;
	}

	public void setShadowSet(BitSet shadowSet) {
		this.shadowSet = shadowSet;
	}
	
	

}
