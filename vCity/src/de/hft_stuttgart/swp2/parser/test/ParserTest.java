package de.hft_stuttgart.swp2.parser.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.parser.CGMLParser;
import de.hft_stuttgart.swp2.parser.PolygonTranslate;

public class ParserTest {

	String fileName = "einHaus.gml";
	
	@Before
	public void setUp() throws Exception {

	}

	@Ignore
	@Test
	public void testParseCity() {
		City c = null;
		
		try {
			c = CGMLParser.getInstance().parse(fileName);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(c);
	}
	
	@Test
	public void testTranslate() {
		
		double[] reference = new double[]{1,1,1};
		ArrayList<Vertex> poly = new ArrayList<Vertex>();
		poly.add(new Vertex(7.0f, 7.0f, 7.0f));
		
		ArrayList<Vertex> poly2 = PolygonTranslate.translateToOrigin(poly, reference);
		
		float[] poly3 = poly2.get(0).getCoordinates();
		
		assertEquals(6.0f, poly3[0], 0.1f);
		assertEquals(6.0f, poly3[1], 0.1f);
		assertEquals(6.0f, poly3[2], 0.1f);
		
	}

	@Ignore
	@Test
	public void testTriangulate() {
		
	}
}
