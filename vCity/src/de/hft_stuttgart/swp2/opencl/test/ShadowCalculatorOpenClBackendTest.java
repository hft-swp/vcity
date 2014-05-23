package de.hft_stuttgart.swp2.opencl.test;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorInterface;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorOpenClBackend;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.VolumeTest;

public class ShadowCalculatorOpenClBackendTest {

	@Test
	public void testCalculateShadow() throws OpenClException {
		VolumeTest.testCity2();
		
		for (Building b2 : City.getInstance().getBuildings()) {
			for (BoundarySurface surface2 : b2.getBoundarySurfaces()) {
				for (Polygon p2 : surface2.getPolygons()) {
					for (Triangle t : p2.getTriangles()) {
						Vertex d0 = ShadowCalculatorInterface.vertexDiff(t.getVertices()[1], t.getVertices()[0]);
						Vertex d1 = ShadowCalculatorInterface.vertexDiff(t.getVertices()[2], t.getVertices()[0]);
						t.setNormalVector(cross(d0, d1));
					}
				}
			}
		}
		
		ShadowCalculatorOpenClBackend calc = new ShadowCalculatorOpenClBackend();
		calc.calculateShadow(ShadowPrecision.VERY_LOW);
		
		
		for (ShadowTriangle t : City.getInstance().getBuildings().get(0).getBoundarySurfaces().get(0).getPolygons().get(0).getShadowTriangles()) {
			System.out.println(Arrays.toString(t.getShadowSet().toByteArray()) + " " + t.getShadowSet().toByteArray().length);
		}
		fail("because");
	}
	
	
	private Vertex cross(Vertex v0, Vertex v1) {
		float x = v0.getY() * v1.getZ() - v0.getZ() * v1.getY();
		float y = v0.getZ() * v1.getX() - v0.getX() * v1.getZ();
		float z = v0.getX() * v1.getY() - v0.getY() * v1.getX();
		return new Vertex(x, y, z);
	}
}
