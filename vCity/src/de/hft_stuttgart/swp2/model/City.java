package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

/**
 * This is the singleton class which provides access to everything stored in the model.
 * @author many
 *
 */
public class City {

	private static City instance;
	private Vertex center;

	private ArrayList<Building> buildings;

	/**
	 * 
	 * @return the only instance of City
	 */
	public static City getInstance() {
		if (instance == null) {
			instance = new City();
		}
		return instance;
	}

	private City() {
		buildings = new ArrayList<Building>();
	}

	/**
	 * adds a building to the building list stored in the city singleton
	 * @param b
	 */
	public void addBuilding(Building b) {
		buildings.add(b);
	}

	/**
	 * 
	 * @return the buildinglist
	 */
	public ArrayList<Building> getBuildings() {
		return buildings;
	}

	/**
	 * 
	 * @return the center of the city
	 */
	public Vertex getCenter() {
		return this.center;
	}

	/**
	 * sets the center of the city
	 * @param center
	 */
	public void setCenter(Vertex center) {
		this.center = center;
	}

}
