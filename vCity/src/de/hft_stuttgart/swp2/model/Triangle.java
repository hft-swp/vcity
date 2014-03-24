package de.hft_stuttgart.swp2.model;

public class Triangle {

	private final Vertex[] vertices;

	public Triangle(final Vertex[] vertices) {
		if (vertices.length != 3) {
			throw new IllegalArgumentException(
					"A triangle always consists of 3 vertices\nYou created one with "
							+ vertices.length);
		}
		this.vertices = vertices;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

}
