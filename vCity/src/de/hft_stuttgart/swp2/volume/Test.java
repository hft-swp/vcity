package de.hft_stuttgart.swp2.volume;

import java.util.Arrays;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class Test {

	public static void main(String[] args) {
		Vertex v = new Vertex(0, 0, 0);
		Triangle t = new Triangle(new Vertex[] { v, v, v });
		Building b = new Building();
		b.addTriangle(t);

		City.getInstance().addBuilding(b);
		// =================================================

		System.out
				.println(Arrays.toString(City.getInstance().getBuildings()
						.get(0).getTriangles().get(0).getVertices()[0]
						.getCoordinates()));
	}
}
