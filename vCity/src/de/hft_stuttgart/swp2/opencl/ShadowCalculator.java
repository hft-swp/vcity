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
	public static void calculateShadow(ShadowPrecision precision) {
		Vertex[] directions = calcDirections();
		int tru = 0;
		int fals = 0;
		for (Building b : City.getInstance().getBuildings()) {
			b.getShadowTriangles().clear();
			for (Triangle t : b.getTriangles()) {
				addTriangles(b, t, precision);
			}
			for (ShadowTriangle t : b.getShadowTriangles()) {
				for (int i = 0; i < 144; i++) {
					Vertex himmelV = directions[i];
					boolean hasShadow = false;
					for (Building b2 : City.getInstance().getBuildings()) {
//						if (b == b2) {
//							continue;
//						}
						if (hasShadow) {
							break;
						}
						for (Triangle t2 : b2.getTriangles()) {
							hasShadow |= rayIntersectTriangle(t.getCenter(), himmelV, t2.getVertices()[0], t2.getVertices()[1], t2.getVertices()[2]);
						}
					}
					if (hasShadow) {
						tru++;
						t.getShadowSet().set(i);
					}
					else
						fals++;
				}
			}
		}
		System.out.println("True: " + tru);
		System.out.println("False: " + fals);
	}
	
	private static Vertex[] calcDirections() {
		Vertex[] result = new Vertex[144];
		float dv = (float) (Math.PI / 12);
		float dh = (float) (2 * Math.PI / 12);
		int count = 0;
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				float v = dv * i + dv / 2;
				float h = dh * (j - 6) + dh / 2;
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

	public static void main(String[] args) {
//		VolumeTest.testCity1();
//		Building b = City.getInstance().getBuildings().get(0);
//		addTriangles(b, b.getTriangles().get(0));
		
//		Vertex v0 = new Vertex(1, 0, 0);
//		Vertex v1 = new Vertex(0, 1, 0);
//		Vertex v2 = new Vertex(0, 0, 1);
//		
//		Vertex p = new Vertex (0, 0, 0);
//		Vertex d = new Vertex (1, 1, 1);
//		
//		System.out.println(rayIntersectTriangle(p, d, v0, v1, v2));
		
		VolumeTest.testCity2();
		VolumeTest.testCity2();
		VolumeTest.testCity2();
		City.getInstance().getBuildings().get(1).translate(0, 0, 5);
		City.getInstance().getBuildings().get(2).translate(5, 0, 5);
		City.getInstance().getBuildings().get(2).scale(1, 5, 1);

		calculateShadow(ShadowPrecision.HIGH);
	}

	private static void addTriangles(Building b, Triangle t, ShadowPrecision precision) {
		float area = getArea(t);
		if (area > precision.getArea()) {
			float dis1 = getDistance(t.getVertices()[0], t.getVertices()[1]);
			float dis2 = getDistance(t.getVertices()[1], t.getVertices()[2]);
			float dis3 = getDistance(t.getVertices()[0], t.getVertices()[2]);
			if (dis1 >= dis2 && dis1 >= dis3) {
				splitTriangles(t.getVertices()[0], t.getVertices()[1],
						t.getVertices()[2], b, precision);
				return;
			}
			if (dis2 >= dis1 && dis2 >= dis3) {
				splitTriangles(t.getVertices()[1], t.getVertices()[2],
						t.getVertices()[0], b, precision);
				return;
			}
			if (dis3 >= dis2 && dis3 >= dis1) {
				splitTriangles(t.getVertices()[0], t.getVertices()[2],
						t.getVertices()[1], b, precision);
				return;
			}
		} else {
			b.addShadowTriangle(new ShadowTriangle(t.getVertices()));
		}

	}

	private static void splitTriangles(Vertex v0, Vertex v1, Vertex v2,
			Building b, ShadowPrecision precision) {
		float x = 0.5f * (v0.getX() + v1.getX());
		float y = 0.5f * (v0.getY() + v1.getY());
		float z = 0.5f * (v0.getZ() + v1.getZ());
		Vertex vNeu = new Vertex(x, y, z);
		Triangle tNeu1 = new Triangle(v0, vNeu, v2);
		Triangle tNeu2 = new Triangle(v2, v1, vNeu);
		addTriangles(b, tNeu1, precision);
		addTriangles(b, tNeu2, precision);
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

	private static Vertex vertexDiff(Vertex v1, Vertex v0) {
		return new Vertex(v1.getX() - v0.getX(), v1.getY() - v0.getY(),
				v1.getZ() - v0.getZ());
	}

	private static Vertex cross(Vertex v0, Vertex v1) {
		float upX = v0.getY() * v1.getZ() - v0.getZ() * v1.getY();
		float upY = v0.getZ() * v1.getX() - v0.getX() * v1.getZ();
		float upZ = v0.getX() * v1.getY() - v0.getY() * v1.getX();
		return new Vertex(upX, upY, upZ);
	}

	private static float dot(Vertex v0, Vertex v1) {
		return v0.getX() * v1.getX() + v0.getY() * v1.getY() + v0.getZ()
				* v1.getZ();
	}

	// private static boolean rayIntersectTriangle(Vertex l1, Vertex l2,
	// Triangle t) {
	//
	// return false;
	// }

	private static boolean rayIntersectTriangle(Vertex p, Vertex d, Vertex v0,
			Vertex v1, Vertex v2) {
		// "VektorDifferenz" zieht einfach vom Vektor V1 den Vektor V0 ab und
		// speichert den neuen Vektor in E1
		Vertex e1 = vertexDiff(v1, v0);
		Vertex e2 = vertexDiff(v2, v0);

		Vertex h = cross(d, e2);

		float a = dot(e1, h);

		// �bersetzt: Wenn a = 0 (also Gerade parallel zur Fl�che), der PC
		// rundet aber nicht/falsch, deshalb verschafft man ihm etwas
		// "Spielraum"
		if ((a > -0.00001) && (a < 0.00001)) {
			return false;
		}

		float f = 1 / a;

		Vertex s2 = vertexDiff(p, v0);

		float u = f * dot(s2, h);

		// �bersetzt: Wenn u < 0 oder u > 1
		if ((u < 0.0) || (u > 1.0)) {
			return false;
		}

		Vertex q = cross(s2, e1);

		float v = f * dot(d, q);

		// �bersetzt: Wenn v < 0 oder (u+v) > 1
		if (!((v < 0.0) || ((u + v) > 1.0))) {
			float x = v0.getX()+e1.getX()*u + e2.getX()*v;
			float y = v0.getY()+e1.getY()*u + e2.getY()*v;
			float z = v0.getZ()+e1.getZ()*u + e2.getZ()*v;
			Vertex s = vertexDiff(new Vertex(x, y, z), p);
			
			if (dot(s, d) > 0.01) {
				return true;
			}
		}
		return false;
	}
}
