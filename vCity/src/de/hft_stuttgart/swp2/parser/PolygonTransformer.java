package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

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
	 * @param reference A reference value
	 * @return transformed Polygon
	 */
	protected static ArrayList<Vertex> transformToNullCoordinate(ArrayList<Vertex> poly) {
		
		// TODO <Parser> We need something like a reference to (0,0)
		
		// ...
		
		return new ArrayList<Vertex>();
	} 
	
	
	/**
	 * Transform a Polygon to smaller data values
	 * @param poly The Polygon to be transformed
	 * @param reference A reference value
	 * @return transformed Polygon
	 */
	protected static ArrayList<Vertex> transformTo2D(ArrayList<Vertex> poly) {
		
		// TODO <Parser>
		
		return new ArrayList<Vertex>();
	} 

}
