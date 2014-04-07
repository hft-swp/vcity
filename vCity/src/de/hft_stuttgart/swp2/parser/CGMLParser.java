package de.hft_stuttgart.swp2.parser;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

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
import org.citygml4j.model.gml.geometry.primitives.Point;
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
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.schema.ElementDecl;
import org.w3c.dom.Element;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.Vertex;

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
		
		// Read file
		try {
			CityGMLContext ctx = new CityGMLContext();
			CityGMLBuilder builder = ctx.createCityGMLBuilder();
			CityGMLInputFactory in = builder.createCityGMLInputFactory();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream(InputFileName);
			CityGMLReader reader = in.createCityGMLReader(InputFileName, input);
			CityModel cityModel = (CityModel) reader.nextFeature();
			reader.close();
			
		} catch (CityGMLReadException e) {
			System.err.println("Failed to parse GML.");
		} catch (JAXBException e) {
			System.err.println("Failed to parse GML.");
		}
		
		
		// Walk over Buildings
		GMLWalker walker = new GMLWalker() {
			
			@Override
		    public void visit(AbstractFeature abstractFeature) {
				// Do nothing
			}
			
			@Override
			public void visit(AbstractGeometry abstractGeometry) {
				
				// Visit Polygon
				GMLVisitor gvisit = new GMLVisitor() {

				    @Override
				    public void visit(Polygon arg0) {
						System.out.println("Polygon ID: " + arg0.getId());
						AbstractRingProperty arp = arg0.getExterior();
						LinearRing lr = (LinearRing) arp.getRing();
						List<Double> coord = lr.getPosList().getValue();
						
						System.out.println("Polygon: " + Arrays.toString(coord.toArray()));
				    }


				    // FIXME A bunch of ugly unused Methods. Is there a way to -NOT- implement them?
					public void visit(Appearance arg0) {}
					public void visit(GeoreferencedTexture arg0) {}
					public void visit(ParameterizedTexture arg0) {}
					public void visit(X3DMaterial arg0) {}
					public void visit(Bridge arg0) {}
					public void visit(BridgeConstructionElement arg0) {}
					public void visit(BridgeFurniture arg0) {}
					public void visit(BridgeInstallation arg0) {}
					public void visit(BridgePart arg0) {}
					public void visit(BridgeRoom arg0) {}
					public void visit(IntBridgeInstallation arg0) {}
					public void visit(CeilingSurface arg0) {}
					public void visit(OuterCeilingSurface arg0) {}
					public void visit(ClosureSurface arg0) {}
					public void visit(FloorSurface arg0) {}
					public void visit(OuterFloorSurface arg0) {}
					public void visit(GroundSurface arg0) {}

					@Override
					public void visit(InteriorWallSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(RoofSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(WallSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Door arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Window arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.Building arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(BuildingFurniture arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(BuildingInstallation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(BuildingPart arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(IntBuildingInstallation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Room arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.CeilingSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.OuterCeilingSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.ClosureSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.FloorSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.OuterFloorSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.GroundSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.InteriorWallSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.RoofSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.WallSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.Door arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.building.Window arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(HollowSpace arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(IntTunnelInstallation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Tunnel arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(TunnelFurniture arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(TunnelInstallation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(TunnelPart arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.CeilingSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.OuterCeilingSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.ClosureSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.FloorSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.OuterFloorSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.GroundSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.InteriorWallSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.RoofSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.WallSurface arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.Door arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(
							org.citygml4j.model.citygml.tunnel.Window arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(CityFurniture arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(CityObjectGroup arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Address arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(CityModel arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(GenericCityObject arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(LandUse arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(BreaklineRelief arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(MassPointRelief arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(RasterRelief arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(ReliefFeature arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(TINRelief arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(AuxiliaryTrafficArea arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Railway arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(RectifiedGridCoverage arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Road arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Square arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(Track arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(TrafficArea arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void visit(TransportationComplex arg0) {
						// TODO Auto-generated method stub
						
					}

					public void visit(PlantCover arg0) {}
					public void visit(SolitaryVegetationObject arg0) {}
					public void visit(WaterBody arg0) {}
					public void visit(WaterClosureSurface arg0) {}
					public void visit(WaterGroundSurface arg0) {}
					public void visit(WaterSurface arg0) {}
					public void visit(ADEComponent arg0) {}
					public void visit(Element arg0, ElementDecl arg1) {}
					public void visit(LodRepresentation arg0) {}
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
					public void visit(CompositeValue arg0) {}
					public void visit(ValueArray arg0) {}
					public void visit(TexCoordGen arg0) {}
					public void visit(TexCoordList arg0) {}
					public void visit(ImplicitGeometry arg0) {}
					public void visit(_Material arg0) {}
					public void visit(_SimpleTexture arg0) {}
				};
				
//				Building testBuilding = new Building( id , polygon list, polygon vertex list );
				
				abstractGeometry.accept(gvisit);
				super.visit(abstractGeometry);
			}
		};
		
		
		
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
