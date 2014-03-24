package de.hft_stuttgart.swp2.model;

public class Vertex {

	private float[] coords = new float[3];

	public Vertex(float x, float y, float z) {
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
	}

	public float[] getCoordinates() {
		return coords;
	}

	public float getX() {
		return coords[0];
	}

	public float getY() {
		return coords[1];
	}

	public float getZ() {
		return coords[2];
	}

}
