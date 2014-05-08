package de.hft_stuttgart.swp2.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.CityGMLBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.gml.feature.BoundingShape;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.Envelope;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citygml4j.model.module.citygml.CoreModule;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.CityGMLOutputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.writer.CityGMLWriteException;
import org.citygml4j.xml.io.writer.CityGMLWriter;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

/**
 * Main class of the CityGML Parser
 * @author 02grst1bif, 02grfr1bif, 12alsi1bif
 */
public class CGMLParser implements ParserInterface {

	// Enable Console debug messages
	private final static boolean DEBUG = false;
	
	// Instance
	public static CGMLParser parser = null;
	
	private static CityModel cityModel = null;
	private double reference[] = null;
	private String epsg = null;
	
	public static CGMLParser getInstance() {
		if (parser == null) {
			parser =  new CGMLParser();
		}
		return parser;
	}
	
	
	/**
	 * Read the file,
	 * transform Coordinates to an useful format,
	 * convert Polygons to Vertices and
	 * create Building Objects
	 * @param fileName Input file name
	 * @return List of Buildings
	 */
	public City parse(String InputFileName) throws Exception {
		
		// Initiate or reset
		reference = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		epsg = "undef";
				
		// Read file
		CityGMLContext ctx = new CityGMLContext();
		CityGMLBuilder builder = ctx.createCityGMLBuilder();
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(InputFileName);
		CityGMLReader reader = in.createCityGMLReader(InputFileName, input);			
		
		// Create a city object which we will fill with Buildings
		final City city = City.getInstance(); 
		
		ArrayList<Triangle> polyTriangles = new ArrayList<Triangle>();
		
		while (reader.hasNext()) {
			CityGML citygml = reader.nextFeature();
			
			if (citygml.getCityGMLClass() == CityGMLClass.CITY_MODEL) {
				cityModel = (CityModel) citygml;
				
				// Step 1: Find reference coordinate
				for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
					AbstractCityObject cityObject = cityObjectMember.getCityObject();
					
					if (cityObject.getCityGMLClass() == CityGMLClass.BUILDING) {
						org.citygml4j.model.citygml.building.Building building = (org.citygml4j.model.citygml.building.Building) cityObject;
						
						if (building.isSetBoundedBySurface()) {
							
							// read EPSG
							BoundingShape bs = building.getBoundedBy();
							Envelope env = bs.getEnvelope();
							epsg = env.getSrsName();
							
							for (BoundarySurfaceProperty property : building.getBoundedBySurface()) {
								
								if (property.isSetObject()) {
									AbstractBoundarySurface boundarySurface = property.getObject();

									if (boundarySurface.isSetLod2MultiSurface() && boundarySurface.getLod2MultiSurface().isSetMultiSurface() && boundarySurface.getLod2MultiSurface().getMultiSurface().isSetSurfaceMember()) {
										
										List<SurfaceProperty> surfaces = boundarySurface.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
										for (int i = 0; i < surfaces.size(); i++) {
											
											Polygon poly = (Polygon) surfaces.get(i).getSurface();
											AbstractRingProperty ringp = poly.getExterior();
											LinearRing ring = (LinearRing) ringp.getRing();
											DirectPositionList poslist = ring.getPosList();
											List<Double> polypoints = poslist.getValue();
											
											for (int j = 0; j < polypoints.size(); j += 3) {
												
												if(polypoints.get(j) < reference[0]){
												     reference[0] = polypoints.get(j);
												     reference[1] = polypoints.get(j+1);
												     reference[2] = polypoints.get(j+2);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				
				// Step 2
				for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
					AbstractCityObject cityObject = cityObjectMember.getCityObject();
					
					if (cityObject.getCityGMLClass() == CityGMLClass.BUILDING) {
						org.citygml4j.model.citygml.building.Building building = (org.citygml4j.model.citygml.building.Building) cityObject;
						polyTriangles.clear();
						
						if (building.isSetBoundedBySurface()) {
							
							for (BoundarySurfaceProperty property : building.getBoundedBySurface()) {
								
								if (property.isSetObject()) {
									AbstractBoundarySurface boundarySurface = property.getObject();

									if (boundarySurface.isSetLod2MultiSurface() && boundarySurface.getLod2MultiSurface().isSetMultiSurface() && boundarySurface.getLod2MultiSurface().getMultiSurface().isSetSurfaceMember()) {
										
										List<SurfaceProperty> surfaces = boundarySurface.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
										for (int i = 0; i < surfaces.size(); i++) {

											Polygon poly = (Polygon) surfaces.get(i).getSurface();

											AbstractRingProperty ringp = poly.getExterior();
											LinearRing ring = (LinearRing) ringp.getRing();
											DirectPositionList poslist = ring.getPosList();

											List<Double> polypoints = poslist.getValue();
											
											ArrayList<Vertex> p = new ArrayList<Vertex>();
											
											for (int j = 0; j < polypoints.size(); j++) {
												p.add(new Vertex(polypoints.get(j).floatValue(),polypoints.get(++j).floatValue(),polypoints.get(++j).floatValue()));
											}
											
											// Translate to Origin
											ArrayList<Vertex> pp = PolygonTranslate.translateToOrigin(p, reference);
											
											// Triangulate
											ArrayList<Triangle> tri = PolygonTriangulator.triangulate(pp);
											
											
											
											ArrayList<Triangle> triNew = new ArrayList<Triangle>();
											ArrayList<Vertex> vertNew = new ArrayList<Vertex>();
											
											// Round
											for (Triangle t : tri) {
											    vertNew.clear();
												for (Vertex ve : t.getVertices()) {
												float newx = (float) ((Math.round(ve.getX() * 1000.0)) / 1000.0);
												float newy = (float) ((Math.round(ve.getY() * 1000.0)) / 1000.0);
												float newz = (float) ((Math.round(ve.getZ() * 1000.0)) / 1000.0);
												vertNew.add(new Vertex(newx, newy, newz));
												}
												triNew.add(new Triangle(vertNew.get(0), vertNew.get(1), vertNew.get(2)));
											}

											polyTriangles.addAll(triNew);
											

										}
									}
								}
							}
						}
						
						 //TODO Add the building to our city
						String bid = "" + building.getId();
						city.addBuilding(new Building(bid, polyTriangles));
						
					}
				}
			}
		}
		
		reader.close();		
		return city;
	}
	
	/**
	 * Exports the Building Objects to a CSV file, which will contain <b>ID</b> and <b>Volume</b> of each Building
	 * @param city List of Buildings
	 * @param outputFileName Output file name
	 * @return true if the export was successful
	 */
	public boolean exportToCsv(City city, String outputFileName) {
		
		List<Building> b = city.getBuildings();
		if (b.isEmpty()) {
			return false;
		}
		
		try {
		    FileWriter writer = new FileWriter(outputFileName);
	 
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
	
	/**
	 * Exports the Building Objects to a cityGML file
	 * @param city List of Buildings
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 */
	public boolean exportToCGML(City city, String outputFileName) {
		
		ArrayList<Building> builds = city.getBuildings();
		
		for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
			if (cityObjectMember.getCityObject().getCityGMLClass() == CityGMLClass.BUILDING) {
				
				String currentID = ((org.citygml4j.model.citygml.building.Building) (cityObjectMember.getCityObject())).getId();
				
				for(Building b : builds) {
					System.out.println("Local: " + b.getId() + "| cgml:" + currentID);
					// FIXME TODO Warum ist die Building ID Null ? Artjom sagt: Sinan macht!!!
					if (b.getId().equalsIgnoreCase(currentID)) {
						cityObjectMember.getCityObject().setLocalProperty("Volume", b.getVolume());
						break;
					}
				}
			}
		}
				
		
		// Write out file
		try {
		CityGMLContext ctx = new CityGMLContext();
		CityGMLBuilder builder = ctx.createCityGMLBuilder();
		CityGMLOutputFactory out = builder.createCityGMLOutputFactory(CityGMLVersion.v2_0_0);
		
		CityGMLWriter writer = out.createCityGMLWriter(new File(outputFileName));
		writer.setPrefixes(CityGMLVersion.v2_0_0);
		writer.setDefaultNamespace(CoreModule.v2_0_0);
		writer.setSchemaLocations(CityGMLVersion.v2_0_0);
		writer.setIndentString(" ");
		writer.setHeaderComment("written by vCity. http://hft-swp.github.io");
		writer.write(cityModel);
		writer.close();
		} catch (CityGMLWriteException e) {
			e.printStackTrace();
			return false;
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public double[] getReference() {
		return reference;
	}

	public String getEPSG() {
		return epsg;
	}
	
}
