package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;
import java.util.List;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

/**
 * Will transform a Polygon to our needs
 * @author 02grst1bif
 *
 */
public class PolygonTransformer {
	

	/**
	 * Transform a Polygon to smaller data values
	 * Note that the Polygon has to be transformed to 2D first
	 * @param poly The 2D Polygon to be transformed
	 * @return transformed 2D Polygon
	 */
	protected static ArrayList<Vertex> transformToNullCoordinate(City city, double reference) {
		
		Building buildNew; 
		for (Building building : city.getBuildings()) {
			for (Triangle t : building.getTriangles()) {
				for (Vertex v : t.getVertices()) {
					for (float p : v.getCoordinates()) {
						System.out.println("p: " + p + " p New: " + (float) (p - reference));
					}
				}
			}
		}
		
		// ...
		
		return new ArrayList<Vertex>();
	} 
	
	
	/**
	 * Transform a 3D Polygon to a 2D (x,y) Polygon
	 * @param poly The 3D Polygon to be transformed
	 * @return transformed 2D Polygon
	 */
	protected static ArrayList<Vertex> transformTo2D(ArrayList<Vertex> poly) {
		
		/**
		 * Idea:
		 * x2 = cos(30)*x - cos(30)*y
		 * y2 = sin(30)*x + sin(30)*y + z
		 * @see http://stackoverflow.com/questions/5883979/translate-java-3d-coordinates-to-2d-screen-coordinates
		 */
		ArrayList<Vertex> polyNew = new ArrayList<Vertex>();
		for (Vertex v : poly) {
			float x = Float.valueOf(Math.round(Math.cos(30) * v.getX() * Math.cos(30) * v.getY()));
			float y = Float.valueOf(Math.round(Math.sin(30) * v.getX() * Math.sin(30) * v.getY() + v.getZ()));
			polyNew.add(new Vertex(x,y,0.0f));
		}
		
		return polyNew;
	} 

}
