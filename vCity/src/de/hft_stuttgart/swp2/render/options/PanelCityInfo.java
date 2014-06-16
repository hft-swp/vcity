package de.hft_stuttgart.swp2.render.options;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.render.Main;

public class PanelCityInfo extends JPanel{
	private static final long serialVersionUID = -5994955918983304356L;
	private static JTextArea txtaCityInfo = new JTextArea(10, 10);
	private static JScrollPane jspPanelShadowOptions;
	private static String text;
	private static int polygonCityCounter = 0;
	private static int triangleCityCounter = 0;
	
	public PanelCityInfo() {
		// TODO Auto-generated constructor stub
		this.setLayout(new BorderLayout());
		txtaCityInfo.setEditable(false);
		jspPanelShadowOptions = new JScrollPane(txtaCityInfo);
		this.add(jspPanelShadowOptions, BorderLayout.CENTER);
	}
	
	/**
	 * update the text in the city info panel
	 */
	public static void updateCityInfo() {
		text = "-------------------------- Allgemeine Stadtinfos --------------------------";
		text += Main.newline;
		text += "Anz. Geb‰ude: " + City.getInstance().getBuildings().size();
		if (City.getInstance().getTotalVolume() > 0.0){
			text += String.format(Main.newline + "Stadtvolumen: %.3f m≥", City.getInstance().getTotalVolume());
		}
		polygonCityCounter = getPolygonCountOfCity(Main.getCity());
		if (polygonCityCounter > 0){
			text += Main.newline + "anz. Polygone: " + polygonCityCounter;
		}
		
		triangleCityCounter = getTrianglesCountOfCity(Main.getCity());
		if (triangleCityCounter > 0){
			text += Main.newline + "anz. Dreiecke: " + triangleCityCounter;
		}
		
		if (City.getInstance().getTotalShadowTrianglesCount() > 0){
			text += Main.newline + "anz. Schattendreiecke: " + City.getInstance().getTotalShadowTrianglesCount();
		}
		txtaCityInfo.setText(text);		
	}
	
	public static void appendCityInfoOneBuilding(Building building){
		String appendStr = "-------------------------- Gebaeuede Infos --------------------------";
		appendStr += Main.newline;
		String strId = "Die Gebaeude-ID lautet: " + building.getId();
		String strStreet = "Straﬂe: " + building.getStreetName();
		String strVolume = "Gebaeude-Volumen: " + Math.round(building.getVolume()*1000)/1000.0 + " m≥";
		String strAmountVolumeTriangles = "Anzahl der Dreiecke fuer die " +
				Main.newline + "Volumenberechnung des Gebaeudes: " + getVolumeTriangleAmount(building);
		int [] amount = getShadowTriangleAndPolygonAmount(building);
		String strAmountPolygons = "Anzahl der Polygone: " + amount[0];
		String strAmountShadowTriangles = "Anzahl der Dreiecke fuer die " + Main.newline +
				"Schattenberechnung des Gebaeudes: " + amount[1];
		appendStr += strId + Main.newline + strStreet + Main.newline + strVolume + Main.newline + strAmountVolumeTriangles + 
				Main.newline + strAmountPolygons + Main.newline + strAmountShadowTriangles + Main.newline;
		if(!text.contains(appendStr)){
			text = appendStr + Main.newline + text;
		}
		txtaCityInfo.setText(text);
		txtaCityInfo.validate();
	}
	
	
	private static int getVolumeTriangleAmount(Building building) {
		int counterOfVolumeTriangles = 0;
		for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
			BoundarySurface surface = building.getBoundarySurfaces().get(i);
			for (int j = 0; j < surface.getPolygons().size(); ++j) {
				Polygon p = surface.getPolygons().get(j);
				counterOfVolumeTriangles = p.getTriangles().size() + counterOfVolumeTriangles;
			}
		}
		return counterOfVolumeTriangles;
	}
	
	
	private static int [] getShadowTriangleAndPolygonAmount(Building building) {
		int counterOfShadowTriangles = 0;
		int counterOfShadowPolygons = 0;
		try {
			for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = building.getBoundarySurfaces().get(i);
				counterOfShadowPolygons = surface.getPolygons().size() + counterOfShadowPolygons;
				for (int j = 0; j < surface.getPolygons().size(); ++j) {
					Polygon polygon = surface.getPolygons().get(j);
					counterOfShadowTriangles = polygon.getShadowTriangles().size() + counterOfShadowTriangles;
				}
			}
			int [] count =  {counterOfShadowPolygons, counterOfShadowTriangles};
			return count;
		} catch (Exception e) {
			int [] count =  {0, 0};
			return count;
		}
			
	}
	
	private static int getPolygonCountOfCity(City city){
		int counterOfPolygons = 0;
		for (Building building : city.getBuildings()){
			for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = building.getBoundarySurfaces().get(i);
				counterOfPolygons = surface.getPolygons().size() + counterOfPolygons;
			}
		}
		return counterOfPolygons;
	}
	
	private static int getTrianglesCountOfCity(City city){
		int counterOfTriangles = 0;
		for (Building building : city.getBuildings()){
			for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = building.getBoundarySurfaces().get(i);
				for (int j = 0; j < surface.getPolygons().size(); ++j) {
					Polygon p = surface.getPolygons().get(j);
					counterOfTriangles = p.getTriangles().size() + counterOfTriangles;
				}
			}
		}
		return counterOfTriangles;
	}
	
}
