package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;
import java.util.List;

public class Building extends MeshInterface {

	private String id;
	private double volume;
	private List<Vertex> polygon;
	private ArrayList<ShadowTriangle> shadowTriangles = new ArrayList<>();
	private Vertex center;

	public Building() {
	}

	public Building(String id, List<Vertex> polygon) {
		this.id = id;
		this.polygon = polygon;
	}

	public Building(String bid, ArrayList<Triangle> polyTriangles) {
		// TODO Auto-generated constructor stub
		this.id=id;
		for (int i=0; i<polyTriangles.size();i++) {
			this.addTriangle(polyTriangles.get(i));
		}
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getVolume() {
		return volume;
	}

	public String getId() {
		return id;
	}

	public List<Vertex> getPolygon() {
		return polygon;
	}

	public void addShadowTriangle(ShadowTriangle t) {
		shadowTriangles.add(t);
	}

	public ArrayList<ShadowTriangle> getShadowTriangles() {
		return shadowTriangles;
	}

	public void translate(float x, float y, float z) {
		for (Triangle t : getTriangles()) {
			for (Vertex v : t.getVertices()) {
				v.resetVisit();
			}
		}
		for (Triangle t : getTriangles()) {
			for (Vertex v : t.getVertices()) {
				if (!v.wasVisited()) {
					v.visit();
					v.translate(x, y, z);
				}
			}
		}
		for (ShadowTriangle t : shadowTriangles) {
			for (Vertex v : t.getVertices()) {
				if (!v.wasVisited()) {
					v.visit();
					v.translate(x, y, z);
				}
			}
		}
	}

	/**
	 * This method must be called be before calculateShadow(), otherwise the
	 * shadow triangles can be too big or too small.
	 * The values are multiplied to the vertices so for keeping the original size
	 * the scaling has to be 1.
	 * 
	 * @param x scaling in x
	 * @param y scaling in y
	 * @param z scaling in z
	 */
	public void scale(float x, float y, float z) {
		for (Triangle t : getTriangles()) {
			for (Vertex v : t.getVertices()) {
				v.resetVisit();
			}
		}
		for (Triangle t : getTriangles()) {
			for (Vertex v : t.getVertices()) {
				if (!v.wasVisited()) {
					v.visit();
					v.scale(x, y, z);
				}
			}
		}
	}

	public Vertex getCenter() {
		return center;
	}

	public void setCenter(Vertex center) {
		this.center = center;
	}

}
