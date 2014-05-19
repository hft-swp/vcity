package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorJavaBackend;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.VolumeCalculatorJavaBackend;

/**
 * Simple testing, no JUnit yet.
 * @author 02grst1bif
 */
public class ParserTest {
	
	private static City city = null;

	@SuppressWarnings("all")
	public static void main(String[] args) throws NullPointerException {
		
		long id = System.currentTimeMillis();
		
//	String inputFileName = "Gruenbuehl_LOD2.gml";
	String inputFileName = "einHaus.gml";
		
//		String outputFileNameCsv = "testCSV_" + Long.toString(id) + ".csv";
		String outputFileNameCgml = "C:\\temp\\testCGML_" + Long.toString(id) + ".gml";
		String outputFileNameXml = "C:\\temp\\testXml_" + Long.toString(id) + ".xml";
		
		// Tests
		testParse(inputFileName);
		
//		testExportToGml(outputFileNameCgml);
		testExportToXml(outputFileNameXml);

	}
	
	private static void testParse(String fileName) {
		try {
			System.out.println("Creating parser Instance");
			CGMLParser parser = CGMLParser.getInstance();
			if (parser != null)
				System.out.println("CGMLParser OK.");
			else
				System.err.println("CGMLParser FAILED.");
		
			System.out.println("Parsing file (This may take a few seconds)...");
			city = parser.parse(fileName);
			System.out.println("Let's see if we were successful...");
		
			if (city != null)
				System.out.println("City OK.");
			else
				System.err.println("City FAILED.");
			
			Building b = city.getBuildings().get(0);
			if (b != null)
				System.out.println("Building OK.");
			else
				System.err.println("Building FAILED.");
			
			ArrayList<Triangle> tri = b.getTriangles();
			if (tri != null && tri.size() != 0)
				System.out.println("ArrayList<Triangle> OK.");
			else
				System.err.println("ArrayList<Triangle> FAILED.");
			
			Triangle t = tri.get(0);
			if (t != null)
				System.out.println("Triangle OK.");
			else
				System.err.println("Triangle FAILED.");
			
			Vertex[] v = t.getVertices();
			if (v != null)
				System.out.println("Vertex[] OK.");
			else
				System.err.println("Vertex[] FAILED.");
			
			int len = v.length;
			if (len != 0)
				System.out.println("Vertex OK.");
			else
				System.err.println("Vertex FAILED.");
	
			System.out.println("--- Test SUCCEDED. ---");
		} catch (Exception e) {
			System.err.println("--- Test FAILED. ---");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("all")
	private static void testExportToGml(String outputFileName) {
		try {
			CGMLParser.getInstance().exportToCGML(city, outputFileName);
		} catch (ParserException e) {
		}
	}
	
	@SuppressWarnings("all")
	private static void testExportToXml(String outputFileName) {
		try {
			
			// Note: Do not run this with Gruenbuehl, else calculation will take forever.
			
			ShadowCalculatorJavaBackend scjb = new ShadowCalculatorJavaBackend();
			scjb.calculateShadow(ShadowPrecision.HIGH);
			
			VolumeCalculatorJavaBackend vcjb = new VolumeCalculatorJavaBackend();
			vcjb.calculateVolume();
			
			CGMLParser.getInstance().exportToXml(city, outputFileName);
		} catch (ParserException e) {
		} 
	}
	
}
