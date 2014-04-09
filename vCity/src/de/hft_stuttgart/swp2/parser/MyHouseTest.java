package de.hft_stuttgart.swp2.parser;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.CityGMLBuilder;
import org.citygml4j.model.citygml.ade.ADEComponent;
import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.citygml.appearance.GeoreferencedTexture;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.TexCoordGen;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.X3DMaterial;
import org.citygml4j.model.citygml.bridge.Bridge;
import org.citygml4j.model.citygml.bridge.BridgeConstructionElement;
import org.citygml4j.model.citygml.bridge.BridgeFurniture;
import org.citygml4j.model.citygml.bridge.BridgeInstallation;
import org.citygml4j.model.citygml.bridge.BridgePart;
import org.citygml4j.model.citygml.bridge.BridgeRoom;
import org.citygml4j.model.citygml.bridge.CeilingSurface;
import org.citygml4j.model.citygml.bridge.ClosureSurface;
import org.citygml4j.model.citygml.bridge.Door;
import org.citygml4j.model.citygml.bridge.FloorSurface;
import org.citygml4j.model.citygml.bridge.GroundSurface;
import org.citygml4j.model.citygml.bridge.IntBridgeInstallation;
import org.citygml4j.model.citygml.bridge.InteriorWallSurface;
import org.citygml4j.model.citygml.bridge.OuterCeilingSurface;
import org.citygml4j.model.citygml.bridge.OuterFloorSurface;
import org.citygml4j.model.citygml.bridge.RoofSurface;
import org.citygml4j.model.citygml.bridge.WallSurface;
import org.citygml4j.model.citygml.bridge.Window;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingFurniture;
import org.citygml4j.model.citygml.building.BuildingInstallation;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.IntBuildingInstallation;
import org.citygml4j.model.citygml.building.Room;
import org.citygml4j.model.citygml.cityfurniture.CityFurniture;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.core.Address;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.ImplicitGeometry;
import org.citygml4j.model.citygml.core.LodRepresentation;
import org.citygml4j.model.citygml.generics.GenericCityObject;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.relief.BreaklineRelief;
import org.citygml4j.model.citygml.relief.MassPointRelief;
import org.citygml4j.model.citygml.relief.RasterRelief;
import org.citygml4j.model.citygml.relief.ReliefFeature;
import org.citygml4j.model.citygml.relief.TINRelief;
import org.citygml4j.model.citygml.texturedsurface._Material;
import org.citygml4j.model.citygml.texturedsurface._SimpleTexture;
import org.citygml4j.model.citygml.texturedsurface._TexturedSurface;
import org.citygml4j.model.citygml.transportation.AuxiliaryTrafficArea;
import org.citygml4j.model.citygml.transportation.Railway;
import org.citygml4j.model.citygml.transportation.Road;
import org.citygml4j.model.citygml.transportation.Square;
import org.citygml4j.model.citygml.transportation.Track;
import org.citygml4j.model.citygml.transportation.TrafficArea;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.citygml.tunnel.HollowSpace;
import org.citygml4j.model.citygml.tunnel.IntTunnelInstallation;
import org.citygml4j.model.citygml.tunnel.Tunnel;
import org.citygml4j.model.citygml.tunnel.TunnelFurniture;
import org.citygml4j.model.citygml.tunnel.TunnelInstallation;
import org.citygml4j.model.citygml.tunnel.TunnelPart;
import org.citygml4j.model.citygml.vegetation.PlantCover;
import org.citygml4j.model.citygml.vegetation.SolitaryVegetationObject;
import org.citygml4j.model.citygml.waterbody.WaterBody;
import org.citygml4j.model.citygml.waterbody.WaterClosureSurface;
import org.citygml4j.model.citygml.waterbody.WaterGroundSurface;
import org.citygml4j.model.citygml.waterbody.WaterSurface;
import org.citygml4j.model.common.visitor.GMLVisitor;
import org.citygml4j.model.gml.coverage.RectifiedGridCoverage;
import org.citygml4j.model.gml.feature.AbstractFeature;
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
import org.citygml4j.model.gml.valueObjects.CompositeValue;
import org.citygml4j.model.gml.valueObjects.ValueArray;
import org.citygml4j.util.walker.GMLWalker;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.schema.ElementDecl;
import org.w3c.dom.Element;

/**
 * 
 * NOT USED. FOR EXPREMINENTAL USE ONLY!
 *
 */

/**
 * Experiments using CityGML4j parser
 * @author 02grst1bif, 02grfr1bif
 */
public class MyHouseTest {
	
	/**
	 * Todo:
	 * - successfully (!) read Polygons
	 * - transform to (0,0) coordinate
	 * - convert Polygon to Vertices
	 * - new Building ( ID, POLYGON, VERTICES )
	 * - Export csv ( ID, VOLUME ) 
	 */
	
	
	/**
	 * Will be provided by Import Dialog
	 */
//	public static final String fileName = "Gruenbuehl_LOD2.gml";
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

//	final HashMap<CityGMLClass, Integer> features = new HashMap<CityGMLClass, Integer>();
//	final HashMap<GMLClass, Integer> geometries = new HashMap<GMLClass, Integer>();

	GMLWalker walker = new GMLWalker() {

	    @Override
	    public void visit(AbstractFeature abstractFeature) {
//		if (abstractFeature instanceof CityGML) {
//		    CityGMLClass key = ((CityGML) abstractFeature).getCityGMLClass();
//		    int count = features.containsKey(key) ? features.get(key) + 1 : 1;
//		    features.put(key, count);
//		}
//
//		super.visit(abstractFeature);
	    }

	    @Override
	    public void visit(AbstractGeometry abstractGeometry) {
//		GMLClass key = abstractGeometry.getGMLClass();
//		int count = geometries.containsKey(key) ? geometries.get(key) + 1 : 1;
//		geometries.put(key, count);
		GMLVisitor gvisit = new GMLVisitor() {

		    @Override
		    public void visit(Polygon arg0) {
			System.err.println("Polygon ID: " + arg0.getId());
			AbstractRingProperty arp = arg0.getExterior();
			LinearRing lr = (LinearRing) arp.getRing();
			List<Double> coord = lr.getPosList().getValue();
			
			System.err.println("Polygon: " + Arrays.toString(coord.toArray()));
			
			
			

//			BoundingBox bb = ar.calcBoundingBox();
//			Point p1 = bb.getLowerCorner();
//			Point p2 = bb.getUpperCorner();
//			System.err.println("--Polygon Lower Corner: x:" + p1.getX() + " y:" + p1.getY() + " z:" + p1.getZ());
//			System.err.println("--Polygon Upper Corner: x:" + p2.getX() + " y:" + p2.getY() + " z:" + p2.getZ());
//
//			Vector<Vertex> points = new Vector<Vertex>();
//			Vertex v1 = new Vertex((float) p1.getX(), (float) p1.getY(), (float) p1.getZ());
//			Vertex v2 = new Vertex((float) p2.getX(), (float) p2.getY(), (float) p2.getZ());
//			points.add(v1);
//			points.add(v2);
//			TestPolygon testPolygon = new TestPolygon(arg0.getId(), points);

			// one value is missing

			/**
			 * <gml:posList srsDimension="3"> 3515991.16 5415784.17
			 * 288.4 --3515991.54 5415789.74 288.4 3515991.54
			 * 5415789.74 285.93 --3515991.16 5415784.17 285.93
			 * 3515991.16 5415784.17 288.4 </gml:posList>
			 */
		    }

		    @Override
		    public void visit(Appearance arg0) {
			

		    }

		    @Override
		    public void visit(GeoreferencedTexture arg0) {
			

		    }

		    @Override
		    public void visit(ParameterizedTexture arg0) {
			

		    }

		    @Override
		    public void visit(X3DMaterial arg0) {
			

		    }

		    @Override
		    public void visit(Bridge arg0) {
			

		    }

		    @Override
		    public void visit(BridgeConstructionElement arg0) {
			

		    }

		    @Override
		    public void visit(BridgeFurniture arg0) {
			

		    }

		    @Override
		    public void visit(BridgeInstallation arg0) {
			

		    }

		    @Override
		    public void visit(BridgePart arg0) {
			

		    }

		    @Override
		    public void visit(BridgeRoom arg0) {
			

		    }

		    @Override
		    public void visit(IntBridgeInstallation arg0) {
			

		    }

		    @Override
		    public void visit(CeilingSurface arg0) {
			

		    }

		    @Override
		    public void visit(OuterCeilingSurface arg0) {
			

		    }

		    @Override
		    public void visit(ClosureSurface arg0) {
			

		    }

		    @Override
		    public void visit(FloorSurface arg0) {
			

		    }

		    @Override
		    public void visit(OuterFloorSurface arg0) {
			

		    }

		    @Override
		    public void visit(GroundSurface arg0) {
			

		    }

		    @Override
		    public void visit(InteriorWallSurface arg0) {
			

		    }

		    @Override
		    public void visit(RoofSurface arg0) {
			

		    }

		    @Override
		    public void visit(WallSurface arg0) {
			

		    }

		    @Override
		    public void visit(Door arg0) {
			

		    }

		    @Override
		    public void visit(Window arg0) {
			

		    }

		    @Override
		    public void visit(Building arg0) {
			

		    }

		    @Override
		    public void visit(BuildingFurniture arg0) {
			

		    }

		    @Override
		    public void visit(BuildingInstallation arg0) {
			

		    }

		    @Override
		    public void visit(BuildingPart arg0) {
			

		    }

		    @Override
		    public void visit(IntBuildingInstallation arg0) {
			

		    }

		    @Override
		    public void visit(Room arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.CeilingSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.OuterCeilingSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.ClosureSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.FloorSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.OuterFloorSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.GroundSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.InteriorWallSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.RoofSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.WallSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.Door arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.building.Window arg0) {
			

		    }

		    @Override
		    public void visit(HollowSpace arg0) {
			

		    }

		    @Override
		    public void visit(IntTunnelInstallation arg0) {
			

		    }

		    @Override
		    public void visit(Tunnel arg0) {
			

		    }

		    @Override
		    public void visit(TunnelFurniture arg0) {
			

		    }

		    @Override
		    public void visit(TunnelInstallation arg0) {
			

		    }

		    @Override
		    public void visit(TunnelPart arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.CeilingSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.OuterCeilingSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.ClosureSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.FloorSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.OuterFloorSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.GroundSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.InteriorWallSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.RoofSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.WallSurface arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.Door arg0) {
			

		    }

		    @Override
		    public void visit(org.citygml4j.model.citygml.tunnel.Window arg0) {
			

		    }

		    @Override
		    public void visit(CityFurniture arg0) {
			

		    }

		    @Override
		    public void visit(CityObjectGroup arg0) {
			

		    }

		    @Override
		    public void visit(Address arg0) {
			

		    }

		    @Override
		    public void visit(CityModel arg0) {
			

		    }

		    @Override
		    public void visit(GenericCityObject arg0) {
			

		    }

		    @Override
		    public void visit(LandUse arg0) {
			

		    }

		    @Override
		    public void visit(BreaklineRelief arg0) {
			

		    }

		    @Override
		    public void visit(MassPointRelief arg0) {
			

		    }

		    @Override
		    public void visit(RasterRelief arg0) {
			

		    }

		    @Override
		    public void visit(ReliefFeature arg0) {
			

		    }

		    @Override
		    public void visit(TINRelief arg0) {
			

		    }

		    @Override
		    public void visit(AuxiliaryTrafficArea arg0) {
			

		    }

		    @Override
		    public void visit(Railway arg0) {
			

		    }

		    @Override
		    public void visit(RectifiedGridCoverage arg0) {
			

		    }

		    @Override
		    public void visit(Road arg0) {
			

		    }

		    @Override
		    public void visit(Square arg0) {
			

		    }

		    @Override
		    public void visit(Track arg0) {
			

		    }

		    @Override
		    public void visit(TrafficArea arg0) {
			

		    }

		    @Override
		    public void visit(TransportationComplex arg0) {
			

		    }

		    @Override
		    public void visit(PlantCover arg0) {
			

		    }

		    @Override
		    public void visit(SolitaryVegetationObject arg0) {
			

		    }

		    @Override
		    public void visit(WaterBody arg0) {
			

		    }

		    @Override
		    public void visit(WaterClosureSurface arg0) {
			

		    }

		    @Override
		    public void visit(WaterGroundSurface arg0) {
			

		    }

		    @Override
		    public void visit(WaterSurface arg0) {
			

		    }

		    @Override
		    public void visit(ADEComponent arg0) {
			

		    }

		    @Override
		    public void visit(Element arg0, ElementDecl arg1) {
			

		    }

		    @Override
		    public void visit(LodRepresentation arg0) {
			

		    }

		    @Override
		    public void visit(CompositeCurve arg0) {
			

		    }

		    @Override
		    public void visit(CompositeSolid arg0) {
			

		    }

		    @Override
		    public void visit(CompositeSurface arg0) {
			

		    }

		    @Override
		    public void visit(Curve arg0) {
			

		    }

		    @Override
		    public void visit(GeometricComplex arg0) {
			

		    }

		    @Override
		    public void visit(Grid arg0) {
			

		    }

		    @Override
		    public void visit(LinearRing arg0) {
			

		    }

		    @Override
		    public void visit(LineString arg0) {
			

		    }

		    @Override
		    public void visit(MultiCurve arg0) {
			

		    }

		    @Override
		    public void visit(MultiLineString arg0) {
			

		    }

		    @Override
		    public void visit(MultiGeometry arg0) {
			

		    }

		    @Override
		    public void visit(MultiPoint arg0) {
			

		    }

		    @Override
		    public void visit(MultiPolygon arg0) {
			

		    }

		    @Override
		    public void visit(MultiSolid arg0) {
			

		    }

		    @Override
		    public void visit(MultiSurface arg0) {
			

		    }

		    @Override
		    public void visit(OrientableCurve arg0) {
			

		    }

		    @Override
		    public void visit(OrientableSurface arg0) {
			

		    }

		    @Override
		    public void visit(_TexturedSurface arg0) {
			

		    }

		    @Override
		    public void visit(RectifiedGrid arg0) {
			

		    }

		    @Override
		    public void visit(Ring arg0) {
			

		    }

		    @Override
		    public void visit(Solid arg0) {
			

		    }

		    @Override
		    public void visit(Surface arg0) {
			

		    }

		    @Override
		    public void visit(Tin arg0) {
			

		    }

		    @Override
		    public void visit(TriangulatedSurface arg0) {
			

		    }

		    @Override
		    public void visit(CompositeValue arg0) {
			

		    }

		    @Override
		    public void visit(ValueArray arg0) {
			

		    }

		    @Override
		    public void visit(TexCoordGen arg0) {
			

		    }

		    @Override
		    public void visit(TexCoordList arg0) {
			

		    }

		    @Override
		    public void visit(ImplicitGeometry arg0) {
			

		    }

		    @Override
		    public void visit(_Material arg0) {
			

		    }

		    @Override
		    public void visit(_SimpleTexture arg0) {
			

		    }

			@Override
			public void visit(
					org.citygml4j.model.gml.geometry.primitives.Point arg0) {
				
				
			}
		};

		abstractGeometry.accept(gvisit);

		super.visit(abstractGeometry);
	    }

	};

	cityModel.accept(walker);
//
//	System.out.println(df.format(new Date()) + "einHaus.gml contains:");
//	System.out.println("Features:");
//	for (CityGMLClass feature : features.keySet())
//	    System.out.println(feature + ": " + features.get(feature));
//
//	System.out.println("\nGeometries:");
//	for (GMLClass geometry : geometries.keySet())
//	    System.out.println(geometry + ": " + geometries.get(geometry));

	// create List<Vertex> for each surfaces / polygons of the building
//	Building testBuilding = new Building("test", List < Vertex > polygon);

    }
}
