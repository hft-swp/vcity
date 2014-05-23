package de.hft_stuttgart.swp2.opencl.test;

import java.util.BitSet;

import org.junit.Assert;
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
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorJavaBackend;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorOpenClBackend;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.VolumeTest;

public class ShadowCalculatorOpenClBackendTest {
	
	BitSet set1 = BitSet.valueOf(new byte[] {-8, -127, 31, -8, -127, 31, -8, -127, 31, 7, 126, -32, 7, 126, -32, 7, 126, -32});
	BitSet set2 = BitSet.valueOf(new byte[] {-8, -127, 31, -8, -127, 31, -8, -127, 31, 7, 126, -32, 7, 126, -32, 7, 126, -32});
	BitSet set3 = BitSet.valueOf(new byte[] {63, -16, 3, 63, -16, 3, 63, -16, 3, -64, 15, -4, -64, 15, -4, -64, 15, -4});
	BitSet set4 = BitSet.valueOf(new byte[] {63, -16, 3, 63, -16, 3, 63, -16, 3, -64, 15, -4, -64, 15, -4, -64, 15, -4});
	BitSet set5 = BitSet.valueOf(new byte[] {28, -64, 1, 28, -64, 0, 0, 0, 0, 0, 0, 0, 0, 3, 48, 0, 7, 48});
	BitSet set6 = BitSet.valueOf(new byte[] {30, -32, 1, 30, -32, 1, 12, 0, 0, 0, 0, 48, -128, 7, 120, -128, 3, 120});
	BitSet set7 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet set8 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet set9 = BitSet.valueOf(new byte[] {7, 126, -32, 7, 126, -32, 7, 126, -32, -8, -127, 31, -8, -127, 31, -8, -127, 31});
	BitSet set10 = BitSet.valueOf(new byte[] {7, 126, -32, 7, 126, -32, 7, 126, -32, -8, -127, 31, -8, -127, 31, -8, -127, 31});
	BitSet set11 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet set12 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet set13 = BitSet.valueOf(new byte[] {-8, -127, 31, -8, -127, 31, -8, -127, 31, 7, 126, -32, 7, 126, -32, 7, 126, -32});
	BitSet set14 = BitSet.valueOf(new byte[] {-8, -127, 31, -8, -127, 31, -8, -127, 31, 7, 126, -32, 7, 126, -32, 7, 126, -32});
	BitSet set15 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet set16 = BitSet.valueOf(new byte[] {63, -16, 3, 63, -16, 3, 63, -16, 3, -64, 15, -4, -64, 15, -4, -64, 15, -4});
	BitSet set17 = BitSet.valueOf(new byte[] {});
	BitSet set18 = BitSet.valueOf(new byte[] {});
	BitSet set19 = BitSet.valueOf(new byte[] {-64, 15, -4, -64, 15, -4, -64, 15, -4, 63, -16, 3, 63, -16, 3, 63, -16, 3});
	BitSet set20 = BitSet.valueOf(new byte[] {-64, 15, -4, -64, 15, -4, -64, 15, -4, 63, -16, 3, 63, -16, 3, 63, -16, 3});
	BitSet set21 = BitSet.valueOf(new byte[] {7, 126, -32, 7, 126, -32, 7, 126, -32, -8, -127, 31, -8, -127, 31, -8, -127, 31});
	BitSet set22 = BitSet.valueOf(new byte[] {7, 126, -32, 7, 126, -32, 7, 126, -32, -8, -127, 31, -8, -127, 31, -8, -127, 31});
	BitSet set23 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet set24 = BitSet.valueOf(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
	BitSet[] Bitarray = new BitSet[] {set1, set2, set3, set4, set5, set6, set7, set8, set9, set10, set11, set12, set13, set14, set15, set16, set17, set18, set19, set20, set21, set22, set23, set24};	

	@Test
	public void testCalculateShadowOpenCl() throws OpenClException {
		VolumeTest.testCity2();
		VolumeTest.testCity2();
		City.getInstance().getBuildings().get(0).translate(2, 0, 0);
		City.getInstance().getBuildings().get(1).scale(1, 2, 1);
		
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
		
		int bitIdx = 0;
		for (ShadowTriangle t : City.getInstance().getBuildings().get(0).getBoundarySurfaces().get(0).getPolygons().get(0).getShadowTriangles()) {
			Assert.assertEquals(Bitarray[bitIdx], t.getShadowSet());
			bitIdx++;
		}
		for (ShadowTriangle t : City.getInstance().getBuildings().get(1).getBoundarySurfaces().get(0).getPolygons().get(0).getShadowTriangles()) {
			Assert.assertEquals(Bitarray[bitIdx], t.getShadowSet());
			bitIdx++;
		}
	}
	
	@Test
	public void testCalculateShadowJava() throws OpenClException {
		VolumeTest.testCity2();
		VolumeTest.testCity2();
		City.getInstance().getBuildings().get(0).translate(2, 0, 0);
		City.getInstance().getBuildings().get(1).scale(1, 2, 1);
		
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
		
		ShadowCalculatorJavaBackend calc = new ShadowCalculatorJavaBackend();
		calc.calculateShadow(ShadowPrecision.VERY_LOW);
		
		int bitIdx = 0;
		for (ShadowTriangle t : City.getInstance().getBuildings().get(0).getBoundarySurfaces().get(0).getPolygons().get(0).getShadowTriangles()) {
//			System.out.println(Bitarray[bitIdx] + "\n" + t.getShadowSet() + "\n");
			Assert.assertEquals(Bitarray[bitIdx], t.getShadowSet());
			bitIdx++;
		}
		for (ShadowTriangle t : City.getInstance().getBuildings().get(1).getBoundarySurfaces().get(0).getPolygons().get(0).getShadowTriangles()) {
//			System.out.println(Bitarray[bitIdx] + "\n" + t.getShadowSet() + "\n");
			Assert.assertEquals(Bitarray[bitIdx], t.getShadowSet());
			bitIdx++;
		}
	}
	
	
	private Vertex cross(Vertex v0, Vertex v1) {
		float x = v0.getY() * v1.getZ() - v0.getZ() * v1.getY();
		float y = v0.getZ() * v1.getX() - v0.getX() * v1.getZ();
		float z = v0.getX() * v1.getY() - v0.getY() * v1.getX();
		return new Vertex(x, y, z);
	}
}
