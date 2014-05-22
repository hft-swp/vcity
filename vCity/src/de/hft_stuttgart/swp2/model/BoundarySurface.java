package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

/**
 * Holds the {@link Polygon}s a {@link Building} is made of
 * 
 * @author 02grst1bif
 */
public class BoundarySurface {

	private ArrayList<Polygon> polygon = new ArrayList<>();
	private String id;

	/**
	 * Creates a BoundarySurface
	 * @param id BoundarySurface ID
	 */
	public BoundarySurface(String id) {
		this.id = id;
	}
	
	/**
	 * @return ID of the BoundarySurface
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return All {@link Polygon}s in this BoundarySurface
	 */
	public ArrayList<Polygon> getPolygons() {
		return polygon;
	}
	
	/**
	 * Add a single {@link Polygon} to the BoundarySurface
	 * @param p Polygon to be added
	 */
	public void addPolygon(Polygon p) {
		polygon.add(p);
	}
	
	/**
	 * Add a list of {@link Polygon}s to the BoundarySurface
	 * @param p Polygons to be added
	 */
	public void addPolygons(ArrayList<Polygon> p) {
		polygon.addAll(p);
	}
	
}
