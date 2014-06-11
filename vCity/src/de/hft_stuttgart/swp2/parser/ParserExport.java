package de.hft_stuttgart.swp2.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.citygml.generics.AbstractGenericAttribute;
import org.citygml4j.model.citygml.generics.DoubleAttribute;
import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citygml4j.model.module.citygml.CoreModule;
import org.citygml4j.xml.io.CityGMLOutputFactory;
import org.citygml4j.xml.io.writer.CityGMLWriteException;
import org.citygml4j.xml.io.writer.CityGMLWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.opencl.CalculatorImpl;
import de.hft_stuttgart.swp2.opencl.CalculatorInterface;
import de.hft_stuttgart.swp2.render.Main;

/**
 * Export class of the CityGML Parser
 * 
 * @author 02grst1bif
 */
public class ParserExport implements ParserExportInterface {

	public ParserExport() {
		// Empty Constructor
	}

	/**
	 * Exports the Building Objects to a CSV file, which will contain <b>ID</b>
	 * and <b>Volume</b> of each Building
	 * 
	 * @param city List of Buildings
	 * @param outputFileName Output file name
	 * @return true if the export was successful
	 */
	public boolean exportToCsv(String outputFileName) throws ParserException {

		List<Building> b = City.getInstance().getBuildings();
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
				writer.append(building.getId());
				writer.append(',');
				writer.append(Double.toString(building.getVolume()));
				writer.append('\n');
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new ParserException(e.getMessage());
		}

		return true;
	}

	/**
	 * Exports the Building Objects to a cityGML file
	 * 
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 */
	public boolean exportToCGML(String outputFileName) throws ParserException {

		CityModel cityModel = Parser.getInstance().getCityModel();

		ArrayList<Building> builds = City.getInstance().getBuildings();
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
	 * Exports the vCity results to an XML file for usage in "INSEL"
	 * 
	 * @param OutputFileName Output file name
	 * @return true if the export was successful
	 */
	public boolean exportToXml(String outputFileName) throws ParserException {

		try {
			
			if (City.getInstance().getBuildings().isEmpty()) {
				throw new ParserException("City is empty.");
			}
			
			// Calculate area of a polygon
			CalculatorInterface calc = new CalculatorImpl();
			calc.calculateArea();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element rootCity = doc.createElement("City");
			doc.appendChild(rootCity);

			Element skyModel = doc.createElement("SkyModel");
			
				Element azimuthwinkel = doc.createElement("SplitAzimuth");
				azimuthwinkel.appendChild(doc.createTextNode(Integer.toString(Main.getSplitAzimuth())));
				skyModel.appendChild(azimuthwinkel);
				
				Element hoehenwinkel = doc.createElement("SplitHeight");
				hoehenwinkel.appendChild(doc.createTextNode(Integer.toString(Main.getSplitHeight())));
				skyModel.appendChild(hoehenwinkel);
			
			rootCity.appendChild(skyModel);

			for (Building b : City.getInstance().getBuildings()) {

				Element building = doc.createElement("Building");

				Attr buildID = doc.createAttribute("id");
				buildID.setValue(b.getId());
				building.setAttributeNode(buildID);

				Element buildVol = doc.createElement("Volume");
				Attr vUom = doc.createAttribute("uom");
				vUom.setValue("urn:ogc:def:uom:UCUM::m3");
				buildVol.setAttributeNode(vUom);
				buildVol.appendChild(doc.createTextNode(Double.toString(b.getVolume())));
				building.appendChild(buildVol);

				for (BoundarySurface bs : b.getBoundarySurfaces()) {
					Element bounds = doc.createElement("BoundarySurface");
					
					Attr bid = doc.createAttribute("id");
					bid.setValue(bs.getId());
					bounds.setAttributeNode(bid);
					
					Attr btype = doc.createAttribute("type");
					btype.setValue(bs.getType().toString());
					bounds.setAttributeNode(btype);

					for (Polygon p : bs.getPolygons()) {
						Element poly = doc.createElement("Polygon");
						
						Attr pid = doc.createAttribute("id");
						pid.setValue(p.getId());
						poly.setAttributeNode(pid);

						Element area = doc.createElement("Area");
						Attr uarea = doc.createAttribute("uom");
						uarea.setValue("urn:ogc:def:uom:UCUM::m2");
						area.setAttributeNode(uarea);
						area.appendChild(doc.createTextNode(Double.toString(p.getArea())));
						poly.appendChild(area);
						
						Element shadow = doc.createElement("Shadow");
						StringBuilder sb = new StringBuilder();
						double[] shadowPercent = p.getPercentageShadow();
						if (shadowPercent != null) {
							for (int i = 0; i < shadowPercent.length; i++) {
								sb.append(Double.toString(shadowPercent[i]));
								
								if (i < shadowPercent.length-1) {
									sb.append(",");
								}
							}
						}
						shadow.appendChild(doc.createTextNode(sb.toString()));
						poly.appendChild(shadow);

						bounds.appendChild(poly);
					}
					building.appendChild(bounds);
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

}