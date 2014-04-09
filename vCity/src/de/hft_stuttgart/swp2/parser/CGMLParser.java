package de.hft_stuttgart.swp2.parser;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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

/**
 * Main class of the CityGML Parser
 * @author 02grst1bif, 02grfr1bif
 */
public class CGMLParser {

	/**
	 * Read the file,
	 * transform Coordinates to an useful format,
	 * convert Polygons to Vertices and
	 * create Building Objects
	 * @param fileName Input file name
	 * @return List of Buildings
	 */
	public static List<Building> parse(String InputFileName) {
		
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
		
		
		// Walk over Buildings
		GMLWalker walker = new GMLWalker() {
			
			public void visit(AbstractGeometry abstractGeometry) {
				
				// Visit Polygon
				GeometryVisitor gvisit = new GeometryVisitor() {

				    public void visit(Polygon arg0) {
						System.out.println("Polygon ID: " + arg0.getId());
						AbstractRingProperty arp = arg0.getExterior();
						LinearRing lr = (LinearRing) arp.getRing();
						List<Double> coord = lr.getPosList().getValue();
						
						
						System.out.println("Polygon: " + Arrays.toString(coord.toArray()));
				    }
				    
				    // FIXME A bunch of ugly unused Methods.
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
				
//				Building testBuilding = new Building( id , polygon list, polygon vertex list );
				
				abstractGeometry.accept(gvisit);
				super.visit(abstractGeometry);
			}
		};
		
		cityModel.accept(walker);
		
		List<Building> city = null;
		return city;
	}
	
	/**
	 * Exports the Building Objects to a CSV file, which will contain <b>ID</b> and <b>Volume</b> of each Building
	 * @param city List of Buildings
	 * @param fileName Output file name
	 * @return true if the export was successful
	 */
	public static boolean export(List<Building> city, String OutputFileName) {
		
		
		for (Building building : city) {
			
		}
		
		return true;
	}
	
	
}
