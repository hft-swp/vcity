package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains meta data for buildings. It is mostly filled by the parser.
 * 
 * @author many
 *
 */
public class Building extends MeshInterface {

	private String id;
	private String city;
	private String street;
	private double volume;
	private List<Vertex> polygon;
	private ArrayList<ShadowTriangle> shadowTriangles = new ArrayList<>();
	private Vertex center;

	/**
	 * Creates an empty building.
	 */
	public Building() {
	}

	/**
	 * This creates a building with the given id and a polygonlist.
	 * @param id
	 * @param polygon
	 */
	public Building(String id, List<Vertex> polygon) {
		this.id = id;
		this.polygon = polygon;
	}

	/**
	 * This creates a building with the given id and a list of triangles
	 * @param bid
	 * @param polyTriangles
	 */
	public Building(String bid, ArrayList<Triangle> polyTriangles) {
		this.id = bid;
		for (int i=0; i<polyTriangles.size();i++) {
			this.addTriangle(polyTriangles.get(i));
		}
	}

	/**
	 * sets the volume
	 * @param volume the volume
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	/**
	 * 
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return the polygon
	 */
	public List<Vertex> getPolygon() {
		return polygon;
	}

	/**
	 * Adds a ShadowTriangle to the triangles stored.
	 * @param t the triangle which is added
	 */
	public void addShadowTriangle(ShadowTriangle t) {
		shadowTriangles.add(t);
	}

	/**
	 * @return the list of ShadowTriangles
	 */
	public ArrayList<ShadowTriangle> getShadowTriangles() {
		return shadowTriangles;
	}

	/**
	 * moves the building by the given values
	 * @param x translation in x
	 * @param y translation in y
	 * @param z translation in z
	 */
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

	/**
	 * 
	 * @return the center of the building
	 */
	public Vertex getCenter() {
		return center;
	}

	/**
	 * sets the center of the building
	 * @param center
	 */
	public void setCenter(Vertex center) {
		this.center = center;
	}
	
	/**
	 * 
	 * @return the name of the city
	 */
	public String getCityName() {
		return city;
	}

	/**
	 * sets the city name
	 * @param city
	 */
	public void setCityName(String city) {
		this.city = city;
	}

	/**
	 * 
	 * @return the street name in which the building is located
	 */
	public String getStreetName() {
		return street;
	}

	/**
	 * sets the street name
	 * @param street
	 */
	public void setStreetName(String street) {
		this.street = street;
	}

}

