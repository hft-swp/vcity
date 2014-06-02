package de.hft_stuttgart.swp2.parser;

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
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.xal.Locality;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.BoundarySurface.SurfaceType;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.model.VertexDouble;

/**
 * Import class of the CityGML Parser
 * 
 * @author 02grst1bif, 02grfr1bif, 12alsi1bif, 02gasa1bif
 */
public class Parser implements ParserInterface {

	// Instance
	public static Parser parser = null;

	private static CityModel cityModel = null;
	private City city = City.getInstance();
	private double reference[] = null;
	private String epsg = null;

	/**
	 * Singleton
	 * 
	 * @return Instance
	 */
	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
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

		reference = new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
		epsg = "undef";

		CityGMLContext ctx = new CityGMLContext();
		CityGMLReader reader = null;

		try {
			CityGMLBuilder builder = ctx.createCityGMLBuilder();
			CityGMLInputFactory in = builder.createCityGMLInputFactory();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream(InputFileName);
			reader = in.createCityGMLReader(InputFileName, input);

			while (reader.hasNext()) {
				CityGML citygml = reader.nextFeature();

				if (citygml.getCityGMLClass() == CityGMLClass.CITY_MODEL) {
					cityModel = (CityModel) citygml;
					findReferenceValue();
					parseCityModel();
				}
			}

			reader.close();

		} catch (CityGMLReadException e) {
			throw new ParserException(e.getCause().getMessage());
		} catch (JAXBException e) {
			throw new ParserException(e.getCause().getMessage());
		}

		return city;
	}

	/**
	 * find the smallest vertex value
	 */
	private void findReferenceValue() {

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

							if (boundarySurface.isSetLod2MultiSurface()
									&& boundarySurface.getLod2MultiSurface().isSetMultiSurface()
									&& boundarySurface.getLod2MultiSurface().getMultiSurface().isSetSurfaceMember()) {

								List<SurfaceProperty> surfaces = boundarySurface.getLod2MultiSurface().getMultiSurface().getSurfaceMember();

								// Step 4: Find the positions of each polygon's edges
								for (int i = 0; i < surfaces.size(); i++) {

									org.citygml4j.model.gml.geometry.primitives.Polygon poly = (org.citygml4j.model.gml.geometry.primitives.Polygon) surfaces.get(i).getSurface();
									AbstractRingProperty ringp = poly.getExterior();
									LinearRing ring = (LinearRing) ringp.getRing();
									DirectPositionList poslist = ring.getPosList();
									List<Double> polypoints = poslist.getValue();

									for (int j = 0; j < polypoints.size(); j += 3) {

										// Step 5: If the coordinate is smaller than the current reference, replace it
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
	 * Read the buildings - read BoundarySurfaces - read Polygons + translate to
	 * reference + triangulate + round + calculate norm vector and write
	 * everything into our city
	 */
	private void parseCityModel() {
		ArrayList<Triangle> polyTriangles = new ArrayList<Triangle>();

		for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
			AbstractCityObject cityObject = cityObjectMember.getCityObject();

			if (cityObject.getCityGMLClass() == CityGMLClass.BUILDING) {
				
				// EXTERNAL Building
				org.citygml4j.model.citygml.building.Building building = (org.citygml4j.model.citygml.building.Building) cityObject;
				
				// INTERNAL Building
				Building build = new Building("" + building.getId());

				Locality loc = building.getAddress().get(0).getAddress().getXalAddress().getAddressDetails().getCountry().getLocality();

				String streetName = "";
				String cityName = "";
				try {
					streetName = loc.getThoroughfare().getThoroughfareName().get(0).getContent();
					cityName = loc.getLocalityName().get(0).getContent();
				} catch (NullPointerException e) {
				}
				
				build.setCityName(cityName);
				build.setStreetName(streetName);

				if (building.isSetBoundedBySurface()) {

					for (BoundarySurfaceProperty property : building.getBoundedBySurface()) {

						if (property.isSetObject()) {
							AbstractBoundarySurface boundarySurface = property.getObject();

							if (boundarySurface.isSetLod2MultiSurface()
									&& boundarySurface.getLod2MultiSurface().isSetMultiSurface()
									&& boundarySurface.getLod2MultiSurface().getMultiSurface().isSetSurfaceMember()) {
								
								
								
								List<SurfaceProperty> surfaces = boundarySurface.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
								
								BoundarySurface bSurface = new BoundarySurface(boundarySurface.getId());
								
								CityGMLClass cg = boundarySurface.getCityGMLClass();
								if (cg == CityGMLClass.BUILDING_GROUND_SURFACE){
									bSurface.setType(SurfaceType.GROUND);
								}else if (cg == CityGMLClass.BUILDING_ROOF_SURFACE){
									bSurface.setType(SurfaceType.ROOF);
								}else if (cg == CityGMLClass.BUILDING_WALL_SURFACE){
									bSurface.setType(SurfaceType.WALL);
								}else {
									bSurface.setType(SurfaceType.OTHER);
								}
								
								for (int i = 0; i < surfaces.size(); i++) {
									polyTriangles.clear();
									org.citygml4j.model.gml.geometry.primitives.Polygon poly = (org.citygml4j.model.gml.geometry.primitives.Polygon) surfaces.get(i).getSurface();
									Polygon bPolygon = new Polygon(poly.getId());

									AbstractRingProperty ringp = poly.getExterior();
									LinearRing ring = (LinearRing) ringp.getRing();
									DirectPositionList poslist = ring.getPosList();

									List<Double> polypoints = poslist.getValue();

									ArrayList<VertexDouble> p = new ArrayList<VertexDouble>();

									for (int j = 0; j < polypoints.size(); j++) {
										p.add(new VertexDouble(
												polypoints.get(j),
												polypoints.get(++j),
												polypoints.get(++j)
												)
										);
									}

									// Translate to Origin
									ArrayList<Vertex> pp = PolygonTranslate.translateToOrigin(p, reference);

									// Triangulate
									ArrayList<Triangle> tri = PolygonTriangulator.triangulate(pp);

									// Round
									ArrayList<Vertex> vertNew = new ArrayList<Vertex>();

									for (Triangle t : tri) {
										vertNew.clear();
										for (Vertex ve : t.getVertices()) {
											float newx = (float) ((Math.round(ve.getX() * 1000.0)) / 1000.0);
											float newy = (float) ((Math.round(ve.getY() * 1000.0)) / 1000.0);
											float newz = (float) ((Math.round(ve.getZ() * 1000.0)) / 1000.0);
											vertNew.add(new Vertex(newx, newy, newz));
										}
										Triangle tNew = new Triangle(vertNew.get(0), vertNew.get(1), vertNew.get(2));

										// Norm vector
										tNew.setNormalVector(calculateNormalVector(t));

										bPolygon.addTriangle(tNew);
									}
									bSurface.addPolygon(bPolygon);
								}
								build.addBoundarySurface(bSurface);
							}
						}
					}
				}
				city.addBuilding(build);
			}
		}
	}

	/**
	 * Calculates the norm Vertex of a triangle.
	 * 
	 * @param t Triangle
	 * @return norm vertex
	 */
	private Vertex calculateNormalVector(Triangle t) {

		Vertex[] nvert = t.getVertices();

		Vertex v1 = new Vertex(
				(nvert[1].getX() - nvert[0].getX()),
				(nvert[1].getY() - nvert[0].getY()),
				(nvert[1].getZ() - nvert[0].getZ())
				);

		Vertex v2 = new Vertex(
				(nvert[2].getX() - nvert[0].getX()),
				(nvert[2].getY() - nvert[0].getY()),
				(nvert[2].getZ() - nvert[0].getZ())
				);

		Vertex kreuz = new Vertex(
				(v1.getY() * v2.getZ() - v1.getZ() * v2.getY()),
				(v1.getZ() * v2.getX() - v1.getX() * v2.getZ()),
				(v1.getX() * v2.getY() - v1.getY() * v2.getX())
				);

		float n = (float) Math.sqrt(
				kreuz.getX() * kreuz.getX() +
				kreuz.getY() * kreuz.getY() +
				kreuz.getZ() * kreuz.getZ()
				);

		Vertex norm = new Vertex(
				kreuz.getX() / n,
				kreuz.getY() / n,
				kreuz.getZ() / n
				);

		return norm;
	}

	/**
	 * @return Reference value used by the parser to center the coordinates
	 */
	public double[] getReference() {
		return reference;
	}

	/**
	 * @return CityGML EPSG String
	 */
	public String getEPSG() {
		return epsg;
	}

	/**
	 * @return parsed city model object
	 * @see ParserExport#exportToCGML()
	 */
	protected CityModel getCityModel() {
		return cityModel;
	}

}