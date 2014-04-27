package de.hft_stuttgart.swp2.parser;

import java.io.File;
import java.util.ArrayList;
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
import org.citygml4j.model.gml.geometry.primitives.Curve;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
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

import de.hft_stuttgart.swp2.model.Vertex;

/*
 * @author 12alsi1bif
 */
//Class to test PolygonTranslate
public class TestPolygonTranslate {

	public static final String fileName = "einHaus.gml";

	public static void main(String[] args) throws Exception {

		System.out.println("setting up citygml4j context and JAXB builder");
		CityGMLContext ctx = new CityGMLContext();
		CityGMLBuilder builder = ctx.createCityGMLBuilder();

		System.out.println("reading CityGML file " + fileName);
		System.out.println("reading CityGML file LOD2_Building_v100.gml");
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		CityGMLReader reader = in.createCityGMLReader(new File("einHaus.gml"));
		CityModel cityModel = (CityModel) reader.nextFeature();

		final ArrayList<Vertex> poly = new ArrayList<Vertex>();
		final double[] reference = new double[3];
		ArrayList<Vertex> polynew = new ArrayList<Vertex>();
		

		System.out.println("CityGML file LOD2_Building_v200.gml written");
		System.out
				.println("sample citygml4j application successfully finished");

		GMLWalker walker = new GMLWalker() {

			@Override
			public void visit(AbstractGeometry abstractGeometry) {

				GeometryVisitor gvisit = new GeometryVisitor() {

					@Override
					public void visit(TriangulatedSurface arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(Tin arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(Surface arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(Solid arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(Ring arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(RectifiedGrid arg0) {
						// TODO Auto-generated method stub

					}

					public void visit(Polygon arg0) {
						
					
				}

					@Override
					public void visit(
							org.citygml4j.model.gml.geometry.primitives.Point arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(_TexturedSurface arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(OrientableSurface arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(OrientableCurve arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiSurface arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiSolid arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiPolygon arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiPoint arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiGeometry arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiLineString arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(MultiCurve arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(LineString arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(LinearRing linearRing) {
						
						reference[0]=Double.MAX_VALUE;
						reference[1]=Double.MAX_VALUE;
						reference[2]=Double.MAX_VALUE;
						
						DirectPositionList posList = linearRing.getPosList();
						List<Double> points = posList.toList3d();
						for (int i = 0; i < points.size(); i += 3) {
							
							if(points.get(i)<reference[0]){
							     reference[0] =points.get(i);
							     reference[1] = points.get(i+1);
							     reference[2] = points.get(i+2);
							}
							
							double[] vals = new double[] { points.get(i),
									points.get(i + 1), points.get(i + 2) };
							System.out.print("x " + vals[0] + " y " + vals[1]
									+ " z " + vals[2] + "\n");

							poly.add(new Vertex((float) points.get(i)
									.doubleValue(), (float) points.get(i + 1)
									.doubleValue(), (float) points.get(i + 2)
									.doubleValue()));

						
						}
						
					}

					@Override
					public void visit(Grid arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(GeometricComplex arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(Curve arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(CompositeSurface arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(CompositeSolid arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(CompositeCurve arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void visit(LodRepresentation arg0) {
						// TODO Auto-generated method stub

					}
				};

				abstractGeometry.accept(gvisit);

				super.visit(abstractGeometry);
			}

		};

		cityModel.accept(walker);

		polynew = PolygonTranslate.translateToOrigin(poly,reference);
	

    	for (int i = 0; i < polynew.size(); i++) {
			System.out.println("x:" +polynew.get(i).getX() + " y:"+ polynew.get(i).getY() + " z:" +polynew.get(i).getZ());
		}

	}
}