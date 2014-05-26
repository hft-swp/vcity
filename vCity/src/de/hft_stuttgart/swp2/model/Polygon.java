package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

/**
 * Holds the {@link Triangle}s a polygon is made of
 * 
 * @author 02grst1bif
 */
public class Polygon {

	private ArrayList<Triangle> triangles = new ArrayList<>();
	private ArrayList<ShadowTriangle> shadowTriangles = new ArrayList<>();
	private String id;
	private double area;
	private double[] percentageShadow;

	/**
	 * @param percentageShadow the shadow percentage of the polygon
	 */
	public void setPercentageShadow(double[] percentageShadow) {
		this.percentageShadow = percentageShadow;
	}

	/** 
	 * @return the shadow percentage of polygon
	 */
	public double[] getPercentageShadow() {
		return percentageShadow;
	}
	

	/**
	 * Creates a Polygon
	 * @param id Polygon ID
	 */
	public Polygon(String id) {
		this.id = id;
	}
	

	/**
	 * @return ID of the Polygon
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return area of the Polygon
	 */
	public double getArea() {
		return area;
	}

	/**
	 * Add area to the Polygon
	 * @param p Triangle to be added
	 */
	public void setArea(double a) {
		this.area = a;
	}

	/**
	 * @return All {@link Triangle}s in this Polygon
	 */
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}

	/**
	 * Add a single {@link Triangle} to the Polygon
	 * @param p Triangle to be added
	 */
	public void addTriangle(Triangle t) {
		triangles.add(t);
	}

	/**
	 * Add a list of {@link Triangle}s to the Polygon
	 * @param p Triangles to be added
	 */
	public void addTriangles(ArrayList<Triangle> t) {
		triangles.addAll(t);
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
	
}
