package de.hft_stuttgart.swp2.parser.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.parser.CGMLParser;
import de.hft_stuttgart.swp2.parser.PolygonTranslate;

/**
 * Parser JUnit Test
 * @author 02grst1bif
 *
 */
public class ParserTest {

	String fileName = "einHaus.gml";
	
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testParseCity() {
		try {

			CGMLParser parser = CGMLParser.getInstance();
			assertNotNull(parser);

			City city = parser.parse(fileName);
			assertNotNull(city);
		
			ArrayList<Building> bs = city.getBuildings();
			assertNotNull(bs);
			assertTrue(bs.size() == 1);
			
			Building b = bs.get(0);
			assertNotNull(b);
			
			String bName = b.getId();
			assertEquals("DEBW_LOD2_1007722", bName);
			
			ArrayList<Triangle> tri = b.getTriangles();
			assertNotNull(tri);
			assertTrue(tri.size() == 12);

			Triangle t = tri.get(0);
			assertNotNull(t);

			Vertex[] v = t.getVertices();
			assertNotNull(v);
			
			int len = v.length;
			assertTrue(len == 3);
			
//			System.out.println("v0 " + v[0].getX() + "|" + v[0].getY() + "|" + v[0].getZ());
//			System.out.println("v1 " + v[1].getX() + "|" + v[1].getY() + "|" + v[1].getZ());
//			System.out.println("v2 " + v[2].getX() + "|" + v[2].getY() + "|" + v[2].getZ());
			
			assertEquals(v[0].getX(), 0.09f, 0.01f);
			assertEquals(v[0].getY(), 0.0f, 0.01f);
			assertEquals(v[0].getZ(), 0.17f, 0.01f);
			
			assertEquals(v[1].getX(), 0.34f, 0.01f);
			assertEquals(v[1].getY(), 0.0f, 0.01f);
			assertEquals(v[1].getZ(), -5.33f, 0.01f);
			
			assertEquals(v[2].getX(), 8.59f, 0.01f);
			assertEquals(v[2].getY(), 0.0f, 0.01f);
			assertEquals(v[2].getZ(), -4.83f, 0.01f);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testTranslate() {
		
		double[] reference = new double[]{1,1,1};
		ArrayList<Vertex> poly = new ArrayList<Vertex>();
		poly.add(new Vertex(7.0f, 7.0f, 7.0f));
		
		ArrayList<Vertex> poly2 = PolygonTranslate.translateToOrigin(poly, reference);
		
		float[] poly3 = poly2.get(0).getCoordinates();
//		System.out.println("Coords: " + poly3[0] + "" + poly3[1] + "" + poly3[2]);
		
		assertEquals(6.0f, poly3[0], 0.1f);
		assertEquals(6.0f, poly3[1], 0.1f);
		assertEquals(-6.0f, poly3[2], 0.1f);
		
	}

	
}
