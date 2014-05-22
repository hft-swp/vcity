package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorJavaBackend;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.VolumeCalculatorJavaBackend;

/**
 * Simple testing, no JUnit yet.
 * 
 * @author 02grst1bif
 */
public class ParserTest {
	
	private static City city = null;

	@SuppressWarnings("all")
	public static void main(String[] args) throws NullPointerException {
		
		long id = System.currentTimeMillis();
		
//	String inputFileName = "Gruenbuehl_LOD2.gml";
//	String inputFileName = "LB_MITTE_CITYGML_LB_3513294_5416846_GML.gml";
	String inputFileName = "einHaus.gml";
		
//		String outputFileNameCsv = "testCSV_" + Long.toString(id) + ".csv";
		String outputFileNameCgml = "C:\\temp\\testCGML_" + Long.toString(id) + ".gml";
		String outputFileNameXml = "C:\\temp\\testXml_" + Long.toString(id) + ".xml";
		
		// Tests
		testParse(inputFileName);
		
//		testExportToGml(outputFileNameCgml);
		
		/**
		 *  Note: Do not run this with anything else than einHaus, else calculation will take forever.
		 */
//		testExportToXml(outputFileNameXml);

	}
	
	private static void testParse(String fileName) {
		try {
			System.out.println("Creating parser Instance");
			Parser parser = Parser.getInstance();
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
			
			ArrayList<BoundarySurface> bs = b.getBoundarySurfaces();
			if (bs != null && bs.size() != 0)
				System.out.println("BoundarySurface OK.");
			else
				System.err.println("BoundarySurface FAILED.");
			
			ArrayList<Polygon> p = bs.get(0).getPolygons();
			if (p != null && p.size() != 0)
				System.out.println("Polygon OK.");
			else
				System.err.println("Polygon FAILED.");
			
			ArrayList<Triangle> tri = p.get(0).getTriangles();
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
			ParserExport pe = new ParserExport();
			pe.exportToCGML(outputFileName);
		} catch (ParserException e) {
		}
	}
	
	@SuppressWarnings("all")
	private static void testExportToXml(String outputFileName) {
		try {
					
			System.out.println("Schattenberechnung...");
			ShadowCalculatorJavaBackend scjb = new ShadowCalculatorJavaBackend();
			scjb.calculateShadow(ShadowPrecision.HIGH);
			
			System.out.println("Volumenberechnug...");
			VolumeCalculatorJavaBackend vcjb = new VolumeCalculatorJavaBackend();
			vcjb.calculateVolume();
			
			System.out.println("Export...");
			ParserExport pe = new ParserExport();
			pe.exportToXml(outputFileName);
		} catch (ParserException e) {
		} 
	}
	
}
