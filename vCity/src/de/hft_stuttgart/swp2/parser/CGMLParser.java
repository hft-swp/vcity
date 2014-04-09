package de.hft_stuttgart.swp2.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBException;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.CityGMLBuilder;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.LodRepresentation;
import org.citygml4j.model.citygml.texturedsurface._TexturedSurface;
import org.citygml4j.model.common.visitor.GeometryVisitor;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurve;
import org.citygml4j.model.gml.geometry.aggregates.MultiGeometry;
import org.citygml4j.model.gml.geometry.aggregates.MultiLineString;
import org.citygml4j.model.gml.geometry.aggregates.MultiPoint;
import org.citygml4j.model.gml.geometry.aggregates.MultiPolygon;
import org.citygml4j.model.gml.geometry.aggregates.MultiSolid;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.complexes.CompositeCurve;
import org.citygml4j.model.gml.geometry.complexes.CompositeSolid;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.complexes.GeometricComplex;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.Curve;
import org.citygml4j.model.gml.geometry.primitives.LineString;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.OrientableCurve;
import org.citygml4j.model.gml.geometry.primitives.OrientableSurface;
import org.citygml4j.model.gml.geometry.primitives.Point;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.Ring;
import org.citygml4j.model.gml.geometry.primitives.Solid;
import org.citygml4j.model.gml.geometry.primitives.Surface;
import org.citygml4j.model.gml.geometry.primitives.Tin;
import org.citygml4j.model.gml.geometry.primitives.TriangulatedSurface;
import org.citygml4j.model.gml.grids.Grid;
import org.citygml4j.model.gml.grids.RectifiedGrid;
import org.citygml4j.util.walker.GMLWalker;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.MeshInterface;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

/**
 * Main class of the CityGML Parser
 * @author 02grst1bif, 02grfr1bif
 */
public class CGMLParser {

	public final static boolean DEBUG = false;
	
	
	/**
	 * Read the file,
	 * transform Coordinates to an useful format,
	 * convert Polygons to Vertices and
	 * create Building Objects
	 * @param fileName Input file name
	 * @return List of Buildings
	 */
	public static City parse(String InputFileName) {
		
		CityModel cityModel = null;
		
		// Read file
		try {
			CityGMLContext ctx = new CityGMLContext();
			CityGMLBuilder builder = ctx.createCityGMLBuilder();
			CityGMLInputFactory in = builder.createCityGMLInputFactory();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream(InputFileName);
			CityGMLReader reader = in.createCityGMLReader(InputFileName, input);
			cityModel = (CityModel) reader.nextFeature();
			reader.close();
			
		} catch (CityGMLReadException e) {
			System.err.println("Failed to parse GML.");
		} catch (JAXBException e) {
			System.err.println("Failed to parse GML.");
		}
		
		// Create a city object which we will fill with Buildings
		final City city = City.getInstance();
		
		// Walk over Buildings
		GMLWalker walker = new GMLWalker() {
			
			Building building;
			
			
			public void visit(AbstractGeometry abstractGeometry) {
				
				// TODO <Parser> Add ID to building
				
				
//				if (abstractGeometry.getId().contains("UUID")) {
//					building = new Building();
//					String buildingID = abstractGeometry.getId();
//					System.err.println("Building ID:" + buildingID);
//					building.addID(buildingID);
					// FIXME <Model> There is no building.addID(String ID);
//				}
				
				
				// Visit Polygon
				GeometryVisitor gvisit = new GeometryVisitor() {

				    public void visit(Polygon arg0) {
				    	if (DEBUG) System.out.println("Polygon ID: " + arg0.getId());
						AbstractRingProperty arp = arg0.getExterior();
						LinearRing lr = (LinearRing) arp.getRing();
						List<Double> coord = lr.getPosList().getValue();
						
						
						if (DEBUG) System.out.println("Polygon: " + Arrays.toString(coord.toArray()));
						
						// Create List<Vertex>
						ArrayList<Vertex> poly = new ArrayList<Vertex>();
						for (int i = 0; i < coord.size(); i++) {
							poly.add(new Vertex(coord.get(i).floatValue(), coord.get(++i).floatValue(), coord.get(++i).floatValue()));
						}
						if (DEBUG) System.out.println("Number of Vertices: " + poly.size());
						
						// TODO <Parser> Transform to 2D
						poly = PolygonTransformer.transformTo2D(poly);
						// TODO <Parser> Transform to (0,0)
						poly = PolygonTransformer.transformToNullCoordinate(poly);

						
						// TODO <Parser> Triangulate
						ArrayList<Triangle> polyTriangles = PolygonTriangulator.triangulate(poly);
						
						// TODO <Parser> Add triangles to Building
//						building.addPolygon(polyTriangles);
						// FIXME <Model> There is no building.addPolygon(List<Triangle>);
						
				    }
				    
				    // FIXME <Parser> A bunch of ugly unused Methods.
					public void visit(LodRepresentation arg0) {	}
					public void visit(CompositeCurve arg0) {}
					public void visit(CompositeSolid arg0) {}
					public void visit(CompositeSurface arg0) {}
					public void visit(Curve arg0) {}
					public void visit(GeometricComplex arg0) {}
					public void visit(Grid arg0) {}
					public void visit(LinearRing arg0) {}
					public void visit(LineString arg0) {}
					public void visit(MultiCurve arg0) {}
					public void visit(MultiLineString arg0) {}
					public void visit(MultiGeometry arg0) {}
					public void visit(MultiPoint arg0) {}
					public void visit(MultiPolygon arg0) {}
					public void visit(MultiSolid arg0) {}
					public void visit(MultiSurface arg0) {}
					public void visit(OrientableCurve arg0) {}
					public void visit(OrientableSurface arg0) {}
					public void visit(_TexturedSurface arg0) {}
					public void visit(Point arg0) {}
					public void visit(RectifiedGrid arg0) {}
					public void visit(Ring arg0) {}
					public void visit(Solid arg0) {}
					public void visit(Surface arg0) {}
					public void visit(Tin arg0) {}
					public void visit(TriangulatedSurface arg0) {}
					
				};
				
				abstractGeometry.accept(gvisit);
				
				// Add building to our city
				city.addBuilding(building);
				
				super.visit(abstractGeometry);
			}
		};
		
		
		/**
		 * The following is just sample data.
		 */
		cityModel.accept(walker);
		
		Vertex vx = new Vertex(1, 2, 3);
		Vertex vy = new Vertex(4, 5, 6);
		Vertex vz = new Vertex(7, 8, 9);
		
		Triangle tri = new Triangle(vx, vy, vz);

		ArrayList<Vertex> poly = new ArrayList<Vertex>();
		poly.add(vx);
		poly.add(vy);
		poly.add(vz);
		
		Building bui = new Building("BUILD0000", poly);
		
		city.addBuilding(bui);
		/**
		 * End of sample data.
		 */
		
		return city;
	}
	
	/**
	 * Exports the Building Objects to a CSV file, which will contain <b>ID</b> and <b>Volume</b> of each Building
	 * @param city List of Buildings
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 */
	public static boolean export(City city, String OutputFileName) {
		
		List<Building> b = city.getBuildings();
		if (b.isEmpty()) {
			return false;
		}
		
		try {
		    FileWriter writer = new FileWriter(OutputFileName);
	 
		    // Header
		    writer.append("Building");
			writer.append(',');
			writer.append("Volume");
			writer.append('\n');
		    
			// Add each building
		    for (Building building : b) {
		    	if (DEBUG) System.out.println("Building: " + building.getId() + " -- Volume: " + building.getVolume());
				
				writer.append(building.getId());
			    writer.append(',');
			    writer.append(Double.toString(building.getVolume()));
			    writer.append('\n');
				
			}
		    
		    writer.flush();
		    writer.close();
		}
		catch (IOException e) {
			System.err.println("Error writing CSV File.");
		     return false;
		}
		
		return true;
	}
	
}
