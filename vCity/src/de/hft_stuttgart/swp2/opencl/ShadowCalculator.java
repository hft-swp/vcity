package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class ShadowCalculator {

	/**
	 * Angenommen koordianten in meter
	 */
	public static void calculateShadow() {
		for (Building b : City.getInstance().getBuildings()) {
			for (Triangle t : b.getTriangles()) {
				addTriangles(b, t);
			}
		}
	}

	public static void main(String[] args) {
		VolumeTest.testCity1();
		Building b = City.getInstance().getBuildings().get(0);
		addTriangles(b, b.getTriangles().get(0));
	}

	private static void addTriangles(Building b, Triangle t) {
		float area = getArea(t);
		if (area > 1.5f) {
			float dis1 = getDistance(t.getVertices()[0], t.getVertices()[1]);
			float dis2 = getDistance(t.getVertices()[1], t.getVertices()[2]);
			float dis3 = getDistance(t.getVertices()[0], t.getVertices()[2]);
			if (dis1 >= dis2 && dis1 >= dis3) {
				splitTriangles(t.getVertices()[0], t.getVertices()[1],
						t.getVertices()[2], b);
				return;
			}
			if (dis2 >= dis1 && dis2 >= dis3) {
				splitTriangles(t.getVertices()[1], t.getVertices()[2],
						t.getVertices()[0], b);
				return;
			}
			if (dis3 >= dis2 && dis3 >= dis1) {
				splitTriangles(t.getVertices()[0], t.getVertices()[2],
						t.getVertices()[1], b);
				return;
			}
		} else {
			b.addShadowTriangle(new ShadowTriangle(t.getVertices()));
		}

	}

	private static void splitTriangles(Vertex v0, Vertex v1, Vertex v2,
			Building b) {
		float x = 0.5f * (v0.getX() + v1.getX());
		float y = 0.5f * (v0.getY() + v1.getY());
		float z = 0.5f * (v0.getZ() + v1.getZ());
		Vertex vNeu = new Vertex(x, y, z);
		Triangle tNeu1 = new Triangle(v0, vNeu, v2);
		Triangle tNeu2 = new Triangle(v2, v1, vNeu);
		addTriangles(b, tNeu1);
		addTriangles(b, tNeu2);
	}

	private static float getArea(Triangle t) {
		float a = getDistance(t.getVertices()[0], t.getVertices()[1]);
		float b = getDistance(t.getVertices()[1], t.getVertices()[2]);
		float c = getDistance(t.getVertices()[0], t.getVertices()[2]);
		float s = 0.5f * (a + b + c);
		return (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	private static float getDistance(Vertex v0, Vertex v1) {
		float x = v1.getX() - v0.getX();
		float y = v1.getY() - v0.getY();
		float z = v1.getZ() - v0.getZ();
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

}
