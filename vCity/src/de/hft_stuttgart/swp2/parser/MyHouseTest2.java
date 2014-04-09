package de.hft_stuttgart.swp2.parser;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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
import org.citygml4j.xml.io.reader.CityGMLReader;
/*
 * @author 12alsi1bif
 */
public class MyHouseTest2 {
	
	

	public static final String fileName = "einHaus.gml";
	

    public static void main(String[] args) throws Exception {

    System.out.println("setting up citygml4j context and JAXB builder");
	CityGMLContext ctx = new CityGMLContext();
	CityGMLBuilder builder = ctx.createCityGMLBuilder();

	System.out.println("reading CityGML file " + fileName);
	CityGMLInputFactory in = builder.createCityGMLInputFactory();
	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	InputStream input = classLoader.getResourceAsStream(fileName);
	CityGMLReader reader = in.createCityGMLReader(fileName, input);
	CityModel cityModel = (CityModel) reader.nextFeature();
	reader.close();



	GMLWalker walker = new GMLWalker() {
	   

	    @Override
	    public void visit(AbstractGeometry abstractGeometry) {

		GeometryVisitor gvisit = new GeometryVisitor() {
			
			@Override
			public void visit(TriangulatedSurface arg0) {
				
				
			}
			
			@Override
			public void visit(Tin arg0) {
				
				
			}
			
			@Override
			public void visit(Surface arg0) {
				
				
			}
			
			@Override
			public void visit(Solid arg0) {
				
				
			}
			
			@Override
			public void visit(Ring arg0) {
				
				
			}
			
			@Override
			public void visit(RectifiedGrid arg0) {
				
				
			}
			
			
				    public void visit(Polygon arg0) {
					System.err.println("Polygon ID: " + arg0.getId());
					AbstractRingProperty arp = arg0.getExterior();
					LinearRing lr = (LinearRing) arp.getRing();
					List<Double> coord = lr.getPosList().getValue();
					
					System.err.println("Polygon: " + Arrays.toString(coord.toArray()));

				
			}
			
			@Override
			public void visit(org.citygml4j.model.gml.geometry.primitives.Point arg0) {
				
				
			}
			
			@Override
			public void visit(_TexturedSurface arg0) {
				
				
			}
			
			@Override
			public void visit(OrientableSurface arg0) {
				
				
			}
			
			@Override
			public void visit(OrientableCurve arg0) {
				
				
			}
			
			@Override
			public void visit(MultiSurface arg0) {
				
				
			}
			
			@Override
			public void visit(MultiSolid arg0) {
				
				
			}
			
			@Override
			public void visit(MultiPolygon arg0) {
				
				
			}
			
			@Override
			public void visit(MultiPoint arg0) {
				
				
			}
			
			@Override
			public void visit(MultiGeometry arg0) {
				
				
			}
			
			@Override
			public void visit(MultiLineString arg0) {
				
				
			}
			
			@Override
			public void visit(MultiCurve arg0) {
				
				
			}
			
			@Override
			public void visit(LineString arg0) {
				
				
			}
			
			@Override
			public void visit(LinearRing arg0) {
				
				
			}
			
			@Override
			public void visit(Grid arg0) {
				
				
			}
			
			@Override
			public void visit(GeometricComplex arg0) {
				
				
			}
			
			@Override
			public void visit(Curve arg0) {
				
				
			}
			
			@Override
			public void visit(CompositeSurface arg0) {
				
				
			}
			
			@Override
			public void visit(CompositeSolid arg0) {
				
				
			}
			
			@Override
			public void visit(CompositeCurve arg0) {
				
				
			}
			
			@Override
			public void visit(LodRepresentation arg0) {
				
				
			}
		};

		  

		abstractGeometry.accept(gvisit);

		super.visit(abstractGeometry);
	    }

	};

	cityModel.accept(walker);


    }
}
