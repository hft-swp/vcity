package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

/**
 * This class contains meta data for buildings. It is mostly filled by the parser.
 * 
 * @author vCity team
 *
 */
public class Building extends MeshInterface {

	private String id;
	private String city;
	private String street;
	private double volume;
	private ArrayList<BoundarySurface> boundarySurface = new ArrayList<>();
	@Deprecated
	private ArrayList<ShadowTriangle> shadowTriangles = new ArrayList<>();
	private Vertex center;

	/**
	 * Creates an empty building.
	 */
	public Building() {
		// Empty constructor
	}

	/**
	 * This creates a building with the given id
	 * @param bid
	 */
	public Building(String bid) {
		this.id = bid;
	}

	/**
	 * sets the volume
	 * @param volume the volume
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}
	
	/**
	 * adds {@link BoundarySurface}
	 * @param bs the boundary surface
	 */
	public void addBoundarySurface(BoundarySurface bs) {
		boundarySurface.add(bs);
	}
	
	/**
	 * adds {@link BoundarySurface}
	 * @param bs the boundary surface
	 */
	public void addBoundarySurface(ArrayList<BoundarySurface> bs) {
		boundarySurface.addAll(bs);
	}

	/**
	 * @return the boundarySurface
	 */
	public ArrayList<BoundarySurface> getBoundarySurfaces() {
		return boundarySurface;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Adds a ShadowTriangle to the triangles stored.
	 * @param t the triangle which is added
	 * @deprecated Please use {@link Polygon#addShadowTriangle(ShadowTriangle)}
	 */
	@Deprecated
	public void addShadowTriangle(ShadowTriangle t) {
		shadowTriangles.add(t);
	}

	/**
	 * @return the list of ShadowTriangles
	 * @deprecated Please use {@link Polygon#getShadowTriangles()}
	 */
	@Deprecated
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
		for (BoundarySurface bs : getBoundarySurfaces()) {
			for (Polygon p : bs.getPolygons()) {
				for (Triangle t : p.getTriangles()) {
					for (Vertex v : t.getVertices()) {
						v.resetVisit();
					}
				}
			}
		}
		for (BoundarySurface bs : getBoundarySurfaces()) {
			for (Polygon p : bs.getPolygons()) {
				for (Triangle t : p.getTriangles()) {
					for (Vertex v : t.getVertices()) {
						if (!v.wasVisited()) {
							v.visit();
							v.translate(x, y, z);
						}
					}
				}
			}
		}
		for (BoundarySurface bs : getBoundarySurfaces()) {
			for (Polygon p : bs.getPolygons()) {
				for (ShadowTriangle t : p.getShadowTriangles()) {
					for (Vertex v : t.getVertices()) {
						if (!v.wasVisited()) {
							v.visit();
							v.translate(x, y, z);
						}
					}
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
		for (BoundarySurface bs : getBoundarySurfaces()) {
			for (Polygon p : bs.getPolygons()) {
				for (Triangle t : p.getTriangles()) {
					for (Vertex v : t.getVertices()) {
						v.resetVisit();
					}
				}
			}
		}
		for (BoundarySurface bs : getBoundarySurfaces()) {
			for (Polygon p : bs.getPolygons()) {
				for (Triangle t : p.getTriangles()) {
					for (Vertex v : t.getVertices()) {
						if (!v.wasVisited()) {
							v.visit();
							v.scale(x, y, z);
						}
					}
				}
			}
		}
	}

	/**
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

