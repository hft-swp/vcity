package de.hft_stuttgart.swp2.parser;
  
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.CityGMLBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.citygml.generics.AbstractGenericAttribute;
import org.citygml4j.model.citygml.generics.DoubleAttribute;
import org.citygml4j.model.gml.feature.BoundingShape;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.Envelope;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citygml4j.model.module.citygml.CoreModule;
import org.citygml4j.model.xal.Locality;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.CityGMLOutputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.writer.CityGMLWriteException;
import org.citygml4j.xml.io.writer.CityGMLWriter;
  
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.model.VertexDouble;
  
/**
 * Main class of the CityGML Parser
 * 
 * @author 02grst1bif, 02grfr1bif, 12alsi1bif, 02gasa1bif
 */
public class CGMLParser implements ParserInterface {
  
    // Enable Console debug messages
    private final static boolean DEBUG       = false;
  
    // Instance
    public static CGMLParser     parser      = null;
  
    private static CityModel     cityModel   = null;
    private double               reference[] = null;
    private String               epsg        = null;
  
    public static CGMLParser getInstance() {
    if (parser == null) {
        parser = new CGMLParser();
    }
    return parser;
    }
  
    /**
     * Read the file, transform Coordinates to an useful format, convert
     * Polygons to Vertices and create Building Objects
     * 
     * @param fileName Input file name
     * @return List of Buildings
     * @throws Exception 
     */
    public City parse(String InputFileName) throws ParserException {
          
    // Initiate or reset
    reference = new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
    epsg = "undef";
  
    // Read file
    CityGMLContext ctx = new CityGMLContext();
    CityGMLReader reader = null;
    try {
        CityGMLBuilder builder = ctx.createCityGMLBuilder();
        CityGMLInputFactory in = builder.createCityGMLInputFactory();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(InputFileName);
        reader = in.createCityGMLReader(InputFileName, input);
    } catch (CityGMLReadException e) {
            throw new ParserException(e.getCause().getMessage());
    } catch (JAXBException e) {
            throw new ParserException(e.getCause().getMessage());
    } 
      
    // Create a city object which we will fill with Buildings
    final City city = City.getInstance();
  
    try {
        while (reader.hasNext()) {
            CityGML citygml = reader.nextFeature();
  
            if (citygml.getCityGMLClass() == CityGMLClass.CITY_MODEL) {
            cityModel = (CityModel) citygml;
  
            getBuildingCoordinates();
            setBuildingCoordinates(city);
            }
        }
        reader.close();
    } catch (CityGMLReadException e) {
        throw new ParserException(e.getCause().getMessage());
    }
      
    return city;
    }
  
    /**
     * Searches in all buildings in the cityModel for their surfaces, gets their
     * polygons and saves the (x,y,z) references of each surface's edge
     */
    private void getBuildingCoordinates() {
    // Step 1: Find reference coordinate
    for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
        AbstractCityObject cityObject = cityObjectMember.getCityObject();
  
        // Step 2: Find all buildings
        if (cityObject.getCityGMLClass() == CityGMLClass.BUILDING) {
        org.citygml4j.model.citygml.building.Building building = (org.citygml4j.model.citygml.building.Building) cityObject;
  
        if (building.isSetBoundedBySurface()) {
  
            // read EPSG
            BoundingShape bs = building.getBoundedBy();
            Envelope env = bs.getEnvelope();
            epsg = env.getSrsName();
  
            // Step 3: Find all surfaces
            for (BoundarySurfaceProperty property : building.getBoundedBySurface()) {
  
            if (property.isSetObject()) {
                AbstractBoundarySurface boundarySurface = property.getObject();
  
                if (boundarySurface.isSetLod2MultiSurface() && boundarySurface.getLod2MultiSurface().isSetMultiSurface()
                    && boundarySurface.getLod2MultiSurface().getMultiSurface().isSetSurfaceMember()) {
  
                List<SurfaceProperty> surfaces = boundarySurface.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
                // Step 4: Find the positions of each polygon's
                // edges
                for (int i = 0; i < surfaces.size(); i++) {
  
                    Polygon poly = (Polygon) surfaces.get(i).getSurface();
                    AbstractRingProperty ringp = poly.getExterior();
                    LinearRing ring = (LinearRing) ringp.getRing();
                    DirectPositionList poslist = ring.getPosList();
                    List<Double> polypoints = poslist.getValue();
  
                    for (int j = 0; j < polypoints.size(); j += 3) {
                    // Step 5: Save the coordinates
                    if (polypoints.get(j) < reference[0]) {
                        reference[0] = polypoints.get(j);
                        reference[1] = polypoints.get(j + 1);
                        reference[2] = polypoints.get(j + 2);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
    /**
     * Uses the PolygonTriangulator to triangulate the polygons of the surfaces,
     * uses the PolygonTranslate to move the buildings to the right position of
     * the coordinate system
     * 
     * @param city contains the data (coordinates of the polygons) of the new parsed city
     * @param polyTriangles
     */
    private void setBuildingCoordinates(final City city) {
    ArrayList<Triangle> polyTriangles = new ArrayList<Triangle>();
  
    for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
        AbstractCityObject cityObject = cityObjectMember.getCityObject();
  
        if (cityObject.getCityGMLClass() == CityGMLClass.BUILDING) {
        org.citygml4j.model.citygml.building.Building building = (org.citygml4j.model.citygml.building.Building) cityObject;
        polyTriangles.clear();
          
        Locality loc = building.getAddress().get(0).getAddress().getXalAddress().getAddressDetails().getCountry().getLocality();
        String streetName = loc.getThoroughfare().getThoroughfareName().get(0).getContent();
        String cityName = loc.getLocalityName().get(0).getContent();
          
          
        if (building.isSetBoundedBySurface()) {
  
            for (BoundarySurfaceProperty property : building.getBoundedBySurface()) {
  
            if (property.isSetObject()) {
                AbstractBoundarySurface boundarySurface = property.getObject();
  
                if (boundarySurface.isSetLod2MultiSurface() && boundarySurface.getLod2MultiSurface().isSetMultiSurface()
                    && boundarySurface.getLod2MultiSurface().getMultiSurface().isSetSurfaceMember()) {
  
                List<SurfaceProperty> surfaces = boundarySurface.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
                for (int i = 0; i < surfaces.size(); i++) {
  
                    Polygon poly = (Polygon) surfaces.get(i).getSurface();
  
                    AbstractRingProperty ringp = poly.getExterior();
                    LinearRing ring = (LinearRing) ringp.getRing();
                    DirectPositionList poslist = ring.getPosList();
  
                    List<Double> polypoints = poslist.getValue();
  
                    ArrayList<VertexDouble> p = new ArrayList<VertexDouble>();
  
                    for (int j = 0; j < polypoints.size(); j++) {
                    p.add(new VertexDouble(polypoints.get(j), polypoints.get(++j), polypoints.get(
                            ++j)));
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
  
        // TODO Add the building to our city
        String bid = "" + building.getId();
        Building build = new Building(bid, polyTriangles);
        build.setCityName(cityName);
        build.setStreetName(streetName);
        city.addBuilding(build);
        }
      }
    }
  
    /**
     * Exports the Building Objects to a CSV file, which will contain <b>ID</b>
     * and <b>Volume</b> of each Building
     * 
     * @param city List of Buildings
     * @param outputFileName Output file name
     * @return true if the export was successful
     */
    public boolean exportToCsv(City city, String outputFileName) throws ParserException{
  
    List<Building> b = city.getBuildings();
    if (b.isEmpty()) {
        throw new ParserException("City is empty.");
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
        if (DEBUG)
            System.out.println("Building: " + building.getId() + " -- Volume: " + building.getVolume());
  
        writer.append(building.getId());
        writer.append(',');
        writer.append(Double.toString(building.getVolume()));
        writer.append('\n');
  
        }
  
        writer.flush();
        writer.close();
    } catch (IOException e) {
        throw new ParserException(e.getCause().getMessage());
    }
  
    return true;
    }
  
    /**
     * Exports the Building Objects to a cityGML file
     * 
     * @param city List of Buildings
     * @param OutputFileName Output file name
     * @return true if the export was successful
     */
    public boolean exportToCGML(City city, String outputFileName) throws ParserException {
  
    ArrayList<Building> builds = city.getBuildings();
    if (builds.isEmpty()) {
        throw new ParserException("City is empty.");
    }
  
  
    for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
        if (cityObjectMember.getCityObject().getCityGMLClass() == CityGMLClass.BUILDING) {
  
        String currentID = ((org.citygml4j.model.citygml.building.Building) (cityObjectMember.getCityObject())).getId();
  
        for (Building b : builds) {
            if (b.getId().equalsIgnoreCase(currentID)) {
                List<AbstractGenericAttribute> att = cityObjectMember.getCityObject().getGenericAttribute();
                DoubleAttribute da = new DoubleAttribute();
                da.setName("Volume");
                da.setValue(b.getVolume());
                att.add(da);
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
        throw new ParserException(e.getCause().getMessage());
    } catch (JAXBException e) {
        throw new ParserException(e.getCause().getMessage());
    }
  
    return true;
    }
  
    /**
     * Exports the Shadow calculations to an XML file "INSEL" will can read.
     * 
     * @param city List of Buildings
     * @param OutputFileName Output file name
     * @return true if the export was successful
     */
    public boolean exportToXml(City city, String outputFileName) throws ParserException {
    	
    	/**
    	 * <SkyModel> .... </SkyModel>
    	 * <Building>
			<id>
    	 *  <BoundarySurface>
    	 *   <Polygon>
    	 *    <Triangle>					// ShadowTriangle
    	 *     <x>
    	 *     <y>
    	 *     <z>
    	 *    <Shadows> T, F, T, T, ....  	// Schtten pro SkyPatch
    	 *    </Shadows>
    	 *    </Triangle>
    	 *    <Triangle> ....
    	 */    	
    	
    	try {
    		 
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Document doc = docBuilder.newDocument();
    		
    		Element rootCity = doc.createElement("City");
    		doc.appendChild(rootCity);
    		
    		Element skyModel = doc.createElement("SkyModel");
    		Attr modelName = doc.createAttribute("name");
    		modelName.setValue(epsg);
    		skyModel.setAttributeNode(modelName);
    		rootCity.appendChild(skyModel);
    		
    		for (Building b : city.getBuildings()) {
    			
    			Element building = doc.createElement("Building");
    			
    			Attr buildID = doc.createAttribute("id");
    			buildID.setValue(b.getId());
    			building.setAttributeNode(buildID);
    			
    			Attr buildVol = doc.createAttribute("volume");
    			buildVol.setValue(Double.toString(b.getVolume()));
    			building.setAttributeNode(buildVol);
    			
    			for (ShadowTriangle t : b.getShadowTriangles()) {
    				
    				Element triangle = doc.createElement("Triangle");
    				
    				Element bitset = doc.createElement("ShadowSet");
    				BitSet bs = t.getShadowSet();
    				StringBuilder sb = new StringBuilder();
    				
    				for (int i = 0; i < 144; i++) {
						sb.append( (boolean) bs.get(i) == true ? "T" : "F");
						if (i != 143) sb.append(",");
					}
    				
    				bitset.appendChild(doc.createTextNode(sb.toString()));
    				
    				triangle.appendChild(bitset);
    				
    				for (Vertex v : t.getVertices()) {
    					
    					Element vertex = doc.createElement("Vertex");
    					
//    					VertexDouble trv = PolygonTranslate.translateBack(v, reference);
    					Vertex trv = v;
    					
    					vertex.appendChild(doc.createTextNode(trv.getX() + "," + trv.getY() + "," + trv.getZ()));
    					triangle.appendChild(vertex);
    					
    				}
    				
    				building.appendChild(triangle);
    			}
    			
    			rootCity.appendChild(building);
    		}
    		
    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
    		Transformer transformer = transformerFactory.newTransformer();
    		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    		DOMSource source = new DOMSource(doc);
    		StreamResult result = new StreamResult(new File(outputFileName));

    		transformer.transform(source, result);
    		
    	  } catch (ParserConfigurationException e) {
    		  throw new ParserException(e.getCause().getMessage());
    	  } catch (TransformerException e) {
    		  throw new ParserException(e.getCause().getMessage());
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