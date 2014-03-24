package de.hft_stuttgart.swp2.model;

public class Triangle {
	
	private final Vertex[] vertices;
	
	public Triangle(final Vertex[] vertices) {
		this.vertices = vertices;
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}

}
