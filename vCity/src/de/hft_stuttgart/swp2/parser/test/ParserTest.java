package de.hft_stuttgart.swp2.parser.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.model.VertexDouble;
import de.hft_stuttgart.swp2.parser.Parser;
import de.hft_stuttgart.swp2.parser.ParserException;
import de.hft_stuttgart.swp2.parser.PolygonTranslate;
import de.hft_stuttgart.swp2.parser.PolygonTriangulator;

/**
 * Parser JUnit Test
 * 
 * @author 02grst1bif
 */
public class ParserTest {

	@Test
	public void testReadAndParseAndValidateEinHaus() {
		try {

			Parser parser = Parser.getInstance();
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

			ArrayList<BoundarySurface> bsfs = b.getBoundarySurfaces();
			assertNotNull(bsfs);
			assertTrue(bsfs.size() == 6);
			
			BoundarySurface bsf = bsfs.get(0);
			assertNotNull(bsf);
			
			String bsfID = bsf.getId();
			assertEquals("DEBW_LOD2_1007722_5ac9f29a-807c-4801-952e-c5fa976fd6b1", bsfID);
			
			ArrayList<Polygon> ps = bsf.getPolygons();
			assertNotNull(ps);
			assertTrue(ps.size() == 1);
			
			Polygon p = ps.get(0);
			assertNotNull(p);
			
			String pID = p.getId();
			assertEquals("DEBW_LOD2_1007722_5ac9f29a-807c-4801-952e-c5fa976fd6b1_poly", pID);
			
			ArrayList<Triangle> tri = p.getTriangles();
			assertNotNull(tri);
			assertTrue(tri.size() == 2);

			Triangle t = tri.get(0);
			assertNotNull(t);

			Vertex[] v = t.getVertices();
			assertNotNull(v);

			int len = v.length;
			assertTrue(len == 3);

			// System.out.println("v0 " + v[0].getX() + "|" + v[0].getY() + "|" + v[0].getZ());
			// System.out.println("v1 " + v[1].getX() + "|" + v[1].getY() + "|" + v[1].getZ());
			// System.out.println("v2 " + v[2].getX() + "|" + v[2].getY() + "|" + v[2].getZ());

			assertEquals(v[0].getX(), 0.0f, 0.01f);
			assertEquals(v[0].getY(), 0.0f, 0.01f);
			assertEquals(v[0].getZ(), 0.0f, 0.01f);

			assertEquals(v[1].getX(), 0.38f, 0.01f);
			assertEquals(v[1].getY(), 0.0f, 0.01f);
			assertEquals(v[1].getZ(), -5.57f, 0.01f);

			assertEquals(v[2].getX(), 8.58f, 0.01f);
			assertEquals(v[2].getY(), 0.0f, 0.01f);
			assertEquals(v[2].getZ(), -4.87f, 0.01f);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testReadGruenbuehl() {
		try {

			Parser parser = Parser.getInstance();
			assertNotNull(parser);

			City city = parser.parse("Gruenbuehl_LOD2.gml");
			assertNotNull(city);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testReadLudwigsburg() {
		try {

			Parser parser = Parser.getInstance();
			assertNotNull(parser);

			City city = parser.parse("LB_MITTE_CITYGML_LB_3513294_5416846_GML.gml");
			assertNotNull(city);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testFileNotFoundException() {
		try {
			Parser.getInstance().parse("gibtsNicht.gml");
			fail();
		} catch (ParserException expected) {
			String errorMessage = expected.getMessage();
			if (!errorMessage.contains("FileNotFoundException")) {
				fail();
			}
		}
	}

	@Test
	public void testParseException() {
		try {
			Parser.getInstance().parse("brokenHaus.gml");
			fail();
		} catch (ParserException expected) {
			String errorMessage = expected.getMessage();
			if (!errorMessage.contains("ParseError at [row,col]:[1,1]")) {
				fail();
			}
		}
	}

	@Test
	public void testTranslate() {

		double[] reference = new double[] { 1, 1, 1 };
		ArrayList<VertexDouble> poly = new ArrayList<VertexDouble>();
		poly.add(new VertexDouble(7.0d, 7.0d, 7.0d));

		ArrayList<Vertex> poly2 = PolygonTranslate.translateToOrigin(poly, reference);
		assertTrue(poly2.size() == 1);

		float[] poly3 = poly2.get(0).getCoordinates();
		assertTrue(poly3.length == 3);
		// System.out.println("Coords: " + poly3[0] + "" + poly3[1] + "" + poly3[2]);

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
