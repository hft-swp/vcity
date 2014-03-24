package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

public abstract class MeshInterface {
	
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}
	public void addTriangle(Triangle t) {
		triangles.add(t);
	}

}
