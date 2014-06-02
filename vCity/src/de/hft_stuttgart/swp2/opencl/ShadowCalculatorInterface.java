package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public abstract class ShadowCalculatorInterface {
	
	/**
	 * Calculates the shadow with the given precision
	 * @param precision
	 * @throws OpenClException an error has occured while creating the OpenCL context
	 */
	public abstract void calculateShadow(ShadowPrecision precision, int splitAzimuth, int splitHeight) throws OpenClException;
	
	private static void splitTriangles(Vertex v0, Vertex v1, Vertex v2,
			Polygon p, ShadowPrecision precision, Vertex norm, Building b) {
		float x = 0.5f * (v0.getX() + v1.getX());
		float y = 0.5f * (v0.getY() + v1.getY());
		float z = 0.5f * (v0.getZ() + v1.getZ());
		Vertex vNeu = new Vertex(x, y, z);
		Triangle tNeu1 = new Triangle(v0, vNeu, v2);
		Triangle tNeu2 = new Triangle(v2, vNeu, v1);
		tNeu1.setNormalVector(norm);
		tNeu2.setNormalVector(norm);
		addTriangles(b, p, tNeu1, precision);
		addTriangles(b, p, tNeu2, precision);
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
	
	/**
	 * 
	 * @param v1
	 * @param v0
	 * @return v1 - v0
	 */
	public static Vertex vertexDiff(Vertex v1, Vertex v0) {
		return new Vertex(v1.getX() - v0.getX(), v1.getY() - v0.getY(),
				v1.getZ() - v0.getZ());
	}
	
	private static void addTriangles(Building b, Polygon p, Triangle t, ShadowPrecision precision) {
		float area = getArea(t);
		if (area > precision.getArea()) {
			float dis1 = getDistance(t.getVertices()[0], t.getVertices()[1]);
			float dis2 = getDistance(t.getVertices()[1], t.getVertices()[2]);
			float dis3 = getDistance(t.getVertices()[0], t.getVertices()[2]);
			if (dis1 >= dis2 && dis1 >= dis3) {
				splitTriangles(t.getVertices()[0], t.getVertices()[1],
						t.getVertices()[2], p, precision, t.getNormalVector(), b);
				return;
			}
			if (dis2 >= dis1 && dis2 >= dis3) {
				splitTriangles(t.getVertices()[1], t.getVertices()[2],
						t.getVertices()[0], p, precision, t.getNormalVector(), b);
				return;
			}
			if (dis3 >= dis2 && dis3 >= dis1) {
				splitTriangles(t.getVertices()[2], t.getVertices()[0],
						t.getVertices()[1], p, precision, t.getNormalVector(), b);
				return;
			}
		} else {
			ShadowTriangle st = new ShadowTriangle(t.getVertices(), b);
			st.setNormalVector(t.getNormalVector());
			p.addShadowTriangle(st);
		}
	}
	
	protected void recalculateShadowTriangles(ShadowPrecision precision) {
		for (Building b : City.getInstance().getBuildings()) {
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					p.getShadowTriangles().clear();
					for (Triangle t : p.getTriangles()) {
						addTriangles(b, p, t, precision);
					}
				}
			}
		}
	}
	
	protected Vertex[] calcDirections(int splitAzimuth, int splitHeight) {
		Vertex[] result = new Vertex[splitAzimuth * splitHeight];
		float dv = (float) (Math.PI / splitHeight / 2);
		float dh = (float) (2 * Math.PI / splitAzimuth);
		int count = 0;
		for (int i = 0; i < splitHeight; i++) {
			for (int j = 0; j < splitAzimuth; j++) {
				float v = dv * i + dv / 2;
				float h = dh * (j - (splitAzimuth / 2f)) + dh / 2;
				double sinH = Math.sin(h);
				double sinV = Math.sin(v);
				double cosH = Math.cos(h);
				double cosV = Math.cos(v);

				double posX = cosV * sinH;
				double posY = sinV;
				double posZ = cosV * cosH;
				
				result[count] = new Vertex((float)posX, (float)posY, (float)posZ);
				count++;
			}
		}
		return result;
	}
}
