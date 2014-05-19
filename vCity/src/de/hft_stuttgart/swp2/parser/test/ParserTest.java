package de.hft_stuttgart.swp2.parser.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.model.VertexDouble;
import de.hft_stuttgart.swp2.parser.CGMLParser;
import de.hft_stuttgart.swp2.parser.ParserException;
import de.hft_stuttgart.swp2.parser.PolygonTranslate;
import de.hft_stuttgart.swp2.parser.PolygonTriangulator;

/**
 * Parser JUnit Test
 * @author 02grst1bif
 *
 */
public class ParserTest {

	@Test
	public void testReadAndParseAndValidateCity() {
		try {

			CGMLParser parser = CGMLParser.getInstance();
			assertNotNull(parser);
			
			City city = parser.parse("einHaus.gml");
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
	public void testFileNotFoundException() {
		try {
			CGMLParser.getInstance().parse("gibtsNicht.gml");
			fail();
		} catch (ParserException expected) {
			String errorMessage = expected.getMessage();
			if (!errorMessage.contains("FileNotFoundException")){
				fail();
			}
		}
	}
	
	@Ignore
	@Test
	public void testParseException() {
		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			
			/**
			 * Die IL Rechner schmieren hier mit bluescreen ab :)
			 */
			
			String path = dir.toString();
			String testFile = path + "/src/de/hft_stuttgart/swp2/parser/test/brokenHaus.gml";
			System.out.println(testFile);
			CGMLParser.getInstance().parse(testFile);
			fail();
		} catch (ParserException expected) {
			String errorMessage = expected.getMessage();
			System.out.println(errorMessage);
//			if (!errorMessage.contains("FileNotFoundException")){
//				fail();
//			}
		}
	}
	
	@Test
	public void testTranslate() {
		
		double[] reference = new double[]{1,1,1};
		ArrayList<VertexDouble> poly = new ArrayList<VertexDouble>();
		poly.add(new VertexDouble(7.0d, 7.0d, 7.0d));
		
		ArrayList<Vertex> poly2 = PolygonTranslate.translateToOrigin(poly, reference);
		assertTrue(poly2.size() == 1);
		
		float[] poly3 = poly2.get(0).getCoordinates();
		assertTrue(poly3.length == 3);
//		System.out.println("Coords: " + poly3[0] + "" + poly3[1] + "" + poly3[2]);
		
		assertEquals(6.0f, poly3[0], 0.1f);
		assertEquals(6.0f, poly3[1], 0.1f);
		assertEquals(-6.0f, poly3[2], 0.1f);
		
	}
	
	@Test
	public void testTriangulate() {
		
		ArrayList<Vertex> poly = new ArrayList<Vertex>();
		poly.add(new Vertex(1.0f, 1.0f, 0.0f));
		poly.add(new Vertex(5.0f, 1.0f, 0.0f));
		poly.add(new Vertex(1.0f, 5.0f, 0.0f));
		poly.add(new Vertex(5.0f, 5.0f, 0.0f));
		
		ArrayList<Triangle> tri = PolygonTriangulator.triangulate(poly);
		assertTrue(tri.size() == 2);

		Vertex[] v = tri.get(0).getVertices();
		assertTrue(v.length == 3);

		assertEquals(v[0].getX(), 5.0f, 0.01f);
		assertEquals(v[0].getY(), 5.0f, 0.01f);
		assertEquals(v[0].getZ(), 0.0f, 0.01f);
		
		assertEquals(v[1].getX(), 3.0f, 0.01f);
		assertEquals(v[1].getY(), 3.0f, 0.01f);
		assertEquals(v[1].getZ(), 0.0f, 0.01f);
		
		assertEquals(v[2].getX(), 1.0f, 0.01f);
		assertEquals(v[2].getY(), 5.0f, 0.01f);
		assertEquals(v[2].getZ(), 0.0f, 0.01f);
		
		v = tri.get(1).getVertices();
		assertTrue(v.length == 3);
		
		assertEquals(v[0].getX(), 5.0f, 0.01f);
		assertEquals(v[0].getY(), 1.0f, 0.01f);
		assertEquals(v[0].getZ(), 0.0f, 0.01f);
		
		assertEquals(v[1].getX(), 1.0f, 0.01f);
		assertEquals(v[1].getY(), 1.0f, 0.01f);
		assertEquals(v[1].getZ(), 0.0f, 0.01f);
		
		assertEquals(v[2].getX(), 3.0f, 0.01f);
		assertEquals(v[2].getY(), 3.0f, 0.01f);
		assertEquals(v[2].getZ(), 0.0f, 0.01f);
		
	}

}
