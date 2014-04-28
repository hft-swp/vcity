package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

import de.hft_stuttgart.swp2.model.Triangle;

/**
 * Collects generated vertices and creates triangles that can be used later.
 * @author 02grst1bif
 *
 */
public class PolygonTriangulatorCollector {

	private static final int GL_LINE_LOOP = 2;
	private static final int GL_TRIANGLES = 4;
	private static final int GL_TRIANGLE_STRIP = 5;
	private static final int GL_TRIANGLE_FAN = 6;
	
	private int type;
	private ArrayList<Double> collection = new ArrayList<Double>();
	private ArrayList<Triangle> result = new ArrayList<Triangle>();
	
	
	public PolygonTriangulatorCollector() {
		result.clear();
	}
	
	public void collect(double[] coords) {
		for (int i = 0; i < coords.length; i++) {
			collection.add(coords[i]);
		}
	}

	public void begin(int type) {
		this.type = type;
		collection.clear();
	}

	public void end() {
		
		if (type == GL_LINE_LOOP) {
			// TODO
			
			
		} else if (type == GL_TRIANGLES) {
			// TODO
			
			
		} else if (type == GL_TRIANGLE_STRIP) {
			// TODO
			
			
		} else if (type == GL_TRIANGLE_FAN) {
			// TODO
			
			
		}
		
		// TODO Je nach typ irgendwas tun
		// Die fertigen Dreiecke muessen zum "result" hinzugefuegt werden
		// result.add( new Triangle ( ...... ) );
		
		
	}
	
	public ArrayList<Triangle> getResult() {
		return result;
	}
	
	
}
