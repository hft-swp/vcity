package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;
import java.util.List;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

/**
 * Simple testing, no JUnit yet.
 * @author 02grst1bif
 */
public class ParserTest {

	public static void main(String[] args) throws NullPointerException {
		
//		String fileName = "Gruenbuehl_LOD2.gml";
		String fileName = "einHaus.gml";

		System.out.println("Hello World!");
		System.out.println("Parsing file (This may take a few seconds)...");
		List<Building> city = CGMLParser.parse(fileName);
		System.out.println("Let's see if we were successful...");
		
		try {
		if (city != null)
			System.out.println("List<Building> OK.");
		else
			System.err.println("List<Building> FAILED.");
		
		Building b = city.get(0);
		if (b != null)
			System.out.println("Building OK.");
		else
			System.err.println("Building FAILED.");
		
		ArrayList<Triangle> tri = b.getTriangles();
		if (tri != null)
			System.out.println("ArrayList<Triangle> OK.");
		else
			System.err.println("ArrayList<Triangle> FAILED.");
		
		Triangle t = tri.get(0);
		if (t != null)
			System.out.println("Triangle OK.");
		else
			System.err.println("Triangle FAILED.");
		
		Vertex[] v = t.getVertices();
		if (v != null)
			System.out.println("Vertex[] OK.");
		else
			System.err.println("Vertex[] FAILED.");
		
		int len = v.length;
		if (len != 0)
			System.out.println("Vertex OK.");
		else
			System.err.println("Vertex FAILED.");

		System.out.println("--- Test SUCCEDED. ---");
		} catch (NullPointerException e) {
			System.err.println("--- Test FAILED. ---");
		}
	}

}
