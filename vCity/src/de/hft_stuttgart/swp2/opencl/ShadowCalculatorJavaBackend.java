package de.hft_stuttgart.swp2.opencl;

import java.util.BitSet;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class ShadowCalculatorJavaBackend extends ShadowCalculatorInterface {
	/**
	 * @deprecated Please use {@link ShadowCalculatorJavaBackend#calculateShadow(ShadowPrecision, int, int)}
	 */
	public void calculateShadow(ShadowPrecision precision) {
		calculateShadow(precision, 12, 12);
	}
	
	/**
	 * The java implementation of the calculation for shadow values
	 */
	public void calculateShadow(ShadowPrecision precision, int splitAzimuth, int splitHeight) {
		Vertex[] directions = calcDirections(splitAzimuth, splitHeight);
		recalculateShadowTriangles(precision);
		for (Building b : City.getInstance().getBuildings()) {
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					double [] percentageShadow = new double[splitAzimuth * splitHeight];
					double shadow = 1.0/p.getShadowTriangles().size();
					for (ShadowTriangle t : p.getShadowTriangles()) {
						t.setShadowSet(new BitSet(splitAzimuth * splitHeight));
						for (int i = 0; i < (splitAzimuth * splitHeight); i++) {
							Vertex himmelV = directions[i];
							boolean hasShadow = false;
							for (Building b2 : City.getInstance().getBuildings()) {
								for (BoundarySurface surface2 : b2.getBoundarySurfaces()) {
									for (Polygon p2 : surface2.getPolygons()) {
										for (Triangle t2 : p2.getTriangles()) {
											if(rayIntersectTriangle(t.getCenter(), himmelV, t2.getVertices()[0], t2.getVertices()[1], t2.getVertices()[2])) {
												t.getShadowSet().set(i);
												hasShadow = true;
												break;
											}
										}
										if(hasShadow)
											break;
									}
								}
							}
							if (hasShadow) {
								percentageShadow[i]+= shadow;
							}
						}
					}
					p.setPercentageShadow(percentageShadow);
				}
			}
		}
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
