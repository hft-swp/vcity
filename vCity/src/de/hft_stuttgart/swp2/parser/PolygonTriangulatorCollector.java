package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

/**
 * Collects generated vertices and creates triangles that can be used later.
 * 
 * @author 12alsi1bif, 02grst1bif
 */
public class PolygonTriangulatorCollector {

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

	/**
	 * based on type writes the triangles into result 
	 * <br> the orientation of the triangles is counter clock wise
	 * @see <a href="http://profs.sci.univr.it/~colombar/html_openGL_tutorial/en/02rendering_022.html">Triangle Strips and Fans</a>
     */
	
	public void end() {

		ArrayList<Vertex> vertexcoll = new ArrayList<Vertex>();

		if (type == GL_TRIANGLES) {

			for (int i = 0; i < collection.size(); i += 3) {
				Vertex v = new Vertex(
						collection.get(i).floatValue(),
						collection.get(i + 1).floatValue(),
						collection.get(i + 2).floatValue()
						);
				vertexcoll.add(v);
			}

			for (int j = 0; j < vertexcoll.size(); j += 3) {
				Triangle t = new Triangle(
						vertexcoll.get(j),
						vertexcoll.get(j + 1),
						vertexcoll.get(j + 2)
						);
				result.add(t);

			}

		} else if (type == GL_TRIANGLE_STRIP) {
			for (int i = 0; i < collection.size(); i += 3) {
				Vertex v = new Vertex(
						collection.get(i).floatValue(),
						collection.get(i + 1).floatValue(),
						collection.get(i + 2).floatValue()
						);
				vertexcoll.add(v);
			}

			for (int j = 0; j < vertexcoll.size() - 2; j++) {
				if (j % 2 == 0) {
					Triangle t = new Triangle(
							vertexcoll.get(j),
							vertexcoll.get(j + 1),
							vertexcoll.get(j + 2)
							);
					result.add(t);
				} else if (j % 2 == 1) {
					Triangle t = new Triangle(
							vertexcoll.get(j + 1),
							vertexcoll.get(j),
							vertexcoll.get(j + 2)
							);
					result.add(t);
				}

			}

		} else if (type == GL_TRIANGLE_FAN) {

			for (int i = 0; i < collection.size(); i += 3) {
				Vertex v = new Vertex(
						collection.get(i).floatValue(),
						collection.get(i + 1).floatValue(),
						collection.get(i + 2).floatValue()
						);
				vertexcoll.add(v);
			}

			for (int j = 0; j < vertexcoll.size() - 2; j++) {
				Triangle t = new Triangle(
						vertexcoll.get(0),
						vertexcoll.get(j + 1),
						vertexcoll.get(j + 2)
						);
				result.add(t);
			}
		}
	}

	public ArrayList<Triangle> getResult() {
		return result;
	}

}
