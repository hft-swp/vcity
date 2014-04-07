package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;

public class City {

	private static City instance;

	private ArrayList<Building> buildings;

	public static City getInstance() {
		if (instance == null) {
			instance = new City();
		}
		return instance;
	}

	private City() {
		buildings = new ArrayList<Building>();
	}

	public void addBuilding(Building b) {
		buildings.add(b);
	}

	public ArrayList<Building> getBuildings() {
		return buildings;
	}

}
