package de.hft_stuttgart.swp2.render;

import java.util.ArrayList;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.Vertex;

public class BuildingTest{
	
	ArrayList <Vertex> polygon;
	public ArrayList <Building> buildings = new ArrayList<Building>();
	
	public BuildingTest(){
		setUp();
		Building building = new Building("1", polygon);
		buildings.add(building);
	}

	private void setUp() {
		Vertex P0 = new Vertex(-2, -2, 2);
		Vertex P1 = new Vertex(2, -2, 2);
		Vertex P2 = new Vertex(2, 2, 2);
		Vertex P3 = new Vertex(-2, 2, 2);
		Vertex P4 = new Vertex(2, -2, -2);
		Vertex P5 = new Vertex(2, 2, -2);
		Vertex P6 = new Vertex(-2, 2, -2);
		Vertex P7 = new Vertex(-2, -2, -2);
		
		Vertex P8 = new Vertex(0, 4, 2);
		Vertex P9 = new Vertex(0, 4, -2);

		polygon = new ArrayList<Vertex>();
		//Front (clockwise)
		polygon.add(P0);
		polygon.add(P1);
		polygon.add(P2);
		polygon.add(P3);
		//Back (counter clockwise)
		polygon.add(P7);
		polygon.add(P6);
		polygon.add(P5);
		polygon.add(P4);
		
		//Top (counter clockwise)
		polygon.add(P3);
		polygon.add(P2);
		polygon.add(P5);
		polygon.add(P6);
		
		//Bottom (clockwise)
		polygon.add(P0);
		polygon.add(P7);
		polygon.add(P4);
		polygon.add(P1);
		
		//Right (counter clockwise)
		polygon.add(P1);
		polygon.add(P4);
		polygon.add(P5);
		polygon.add(P2);
		
		//Left (clockwise)
		polygon.add(P0);
		polygon.add(P3);
		polygon.add(P6);
		polygon.add(P7);
		
		//Roof
		//Front (clockwise)
		polygon.add(P3);
		polygon.add(P2);
		polygon.add(P8);
		
		//Back (counter clockwise)
		polygon.add(P6);
		polygon.add(P9);
		polygon.add(P5);
		
		//Right (counter clockwise)
		polygon.add(P2);
		polygon.add(P5);
		polygon.add(P9);
		polygon.add(P8);
		
		//Left (clockwise)
		polygon.add(P3);
		polygon.add(P8);
		polygon.add(P9);
		polygon.add(P6);
	
		

	}

}
