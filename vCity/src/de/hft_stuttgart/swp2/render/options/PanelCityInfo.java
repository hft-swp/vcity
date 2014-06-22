package de.hft_stuttgart.swp2.render.options;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.BoundarySurface.SurfaceType;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.render.Main;

public class PanelCityInfo extends JPanel{
	private static final long serialVersionUID = -5994955918983304356L;
	private static JTextArea txtaCityInfo = new JTextArea(10, 10);
	private static JScrollPane jspPanelShadowOptions;
	private static String text;
	private static String appendStr = "";
	private static String updateInfoText = "";
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
		text = "";
		text = "-------------------------- Allgemeine Stadtinfos ----------------------------------------------------";
		text += Main.newline;
		text += "Anz. Gebäude: " + City.getInstance().getBuildings().size();
		if (City.getInstance().getTotalVolume() > 0.0){
			text += String.format(Main.newline + "Stadtvolumen: %.3f m³", City.getInstance().getTotalVolume());
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
		updateInfoText = text;
		txtaCityInfo.setText(text);
		txtaCityInfo.setCaretPosition(0);
		txtaCityInfo.validate();
	}
	

	public static void appendCityInfoOneBuilding(Building building){
		
		appendStr = "-------------------------- Gebaeuede Infos ----------------------------------------------------------";
		appendStr += Main.newline;
		String strId = "Die Gebaeude-ID lautet: " + building.getId();
		String strStreet = "Straße: " + building.getStreetName();
		String strVolume = "";
		if(building.getVolume() > 0.001){
			strVolume = "Gebaeude-Volumen: " + Math.round(building.getVolume()*1000)/1000.0 + " m³";
		}else{
			strVolume = "Gebaeude-Volumen: - wurde noch nicht berechnet";
		}
		int volumeTriangleAmount = getVolumeTriangleAmount(building);
		String strAmountVolumeTriangles = "";
		if(volumeTriangleAmount > 0){
			strAmountVolumeTriangles = "Anzahl der Dreiecke fuer die " +
					Main.newline + "Volumenberechnung des Gebaeudes: " + volumeTriangleAmount;
		}else{
			strAmountVolumeTriangles = "Anzahl der Dreiecke fuer die " +
					Main.newline + "Volumenberechnung des Gebaeudes: - wurde noch nicht berechnet";
		}

		int [] amount = getShadowTriangleAndPolygonAmount(building);
		String strAmountPolygons = "Anzahl der Polygone: " + amount[0];
		if(amount[0]> 0){
			strAmountPolygons = "Anzahl der Polygone: " + amount[0];
		}else{
			strAmountPolygons = "Anzahl der Polygone: - wurde noch nicht berechnet";
		}

		String strAmountShadowTriangles = "Anzahl der Dreiecke fuer die " + Main.newline +
				"Schattenberechnung des Gebaeudes: " + amount[1];
		if(amount[1]> 0){
			strAmountShadowTriangles = "Anzahl der Dreiecke fuer die " + Main.newline +
					"Schattenberechnung des Gebaeudes: " + amount[1];
		}else{
			strAmountShadowTriangles = "Anzahl der Dreiecke fuer die " + Main.newline +
					"Schattenberechnung des Gebaeudes: - wurde noch nicht berechnet";
		}

		SortedMap <ShadowTriangle,Integer> sorted_map = getProfitableShadowTriangles(building);
		String strProfitableShadowTriangles;
		if(sorted_map != null){
			strProfitableShadowTriangles = "Moegliche Schattendreiecke für Solarzellen auf dem Dach:" + 
					Main.newline + profitableShadowTrianglesMapToString(sorted_map);
		}else{
			strProfitableShadowTriangles = "Es sind (noch) " +
					"keine brauchbaren " + Main.newline +
					"Schattendreiecke für Solarzellen vorhanden.";
		}
		
		appendStr += strId + Main.newline + strStreet + Main.newline + strVolume + Main.newline + strAmountVolumeTriangles + 
				Main.newline + strAmountPolygons + Main.newline + strAmountShadowTriangles + Main.newline + 
				strProfitableShadowTriangles + Main.newline;
		if(!text.contains(appendStr)){
			text = "";
			text = appendStr + Main.newline + updateInfoText;
		}
		
		txtaCityInfo.setText(text);
		txtaCityInfo.setCaretPosition(0);
		txtaCityInfo.validate();
	}
	
	private static SortedMap <ShadowTriangle,Integer> getProfitableShadowTriangles(Building building){
		
		ArrayList <ShadowTriangle> listShadowTriangles = getShadowTriangleListForSolarCalculation(building);
		if(listShadowTriangles.size() > 0){
			HashMap <ShadowTriangle, Integer> hm = new HashMap <ShadowTriangle, Integer>();
			for(ShadowTriangle t: listShadowTriangles){
				if(hm.containsKey(t)){
					hm.put(t,(hm.get(t)+1));
				}else{
					hm.put(t,1);
				}
			}
			class ValueComparator implements Comparator<ShadowTriangle> {
			    Map<ShadowTriangle, Integer> base;
			    public ValueComparator(Map<ShadowTriangle, Integer> base) {
			        this.base = base;
			    }
			    // Note: this comparator imposes orderings that are inconsistent with equals.    
			    public int compare(ShadowTriangle a, ShadowTriangle b) {
			        if (base.get(a) >= base.get(b)) {
			            return -1;
			        } else {
			            return 1;
			        } // returning 0 would merge keys
			    }
			}
			ValueComparator valueComparator =  new ValueComparator(hm);
	        SortedMap <ShadowTriangle,Integer> sortedMap = Collections.synchronizedSortedMap(new TreeMap<ShadowTriangle,Integer>(valueComparator));
	        sortedMap.putAll(hm);
	        return sortedMap;
		}else{
			return null;
		}

	}
	
	
	private static String profitableShadowTrianglesMapToString(
			SortedMap<ShadowTriangle, Integer> profitableShadowTrianglesMap) {
        Set <ShadowTriangle> setKeysOfShadowTriangles = profitableShadowTrianglesMap.keySet();
        ShadowTriangle [] keysOfShadowTriangles = (ShadowTriangle[]) setKeysOfShadowTriangles.toArray(new ShadowTriangle[setKeysOfShadowTriangles.size()]);
        int counter = 0;
        String strShadowTriangles = "";
        for(Map.Entry<ShadowTriangle,Integer> entry : profitableShadowTrianglesMap.entrySet()) {
        	   Integer value = entry.getValue();
        	   String [] strVertices = new String [keysOfShadowTriangles[counter].getVertices().length];
        	   int vertexCounter = 0;
        	   for(Vertex v: keysOfShadowTriangles[counter].getVertices()){
        		   String strX = String.valueOf(v.getX()) ; String strY = String.valueOf(v.getY()); String strZ = String.valueOf(v.getZ());
        		   strVertices[vertexCounter] = "["+strX+"," + strY + "," + strZ + "]";
        		   vertexCounter++;
        	   }
        	   strShadowTriangles = strShadowTriangles + "Schattendreieck " + String.valueOf(counter+1) + ":" + Main.newline;
        	   for(int i = 0; i <3; i++){
        		   strShadowTriangles = strShadowTriangles + strVertices[i];
        	   }
        	   strShadowTriangles = strShadowTriangles + " = " + String.valueOf((Math.round(value*1000)/1000.0)) + " Stunden Sonne" + Main.newline;
        	   counter++;
        }
        return strShadowTriangles;
	}
	
	private static ArrayList<ShadowTriangle> getShadowTriangleListForSolarCalculation(Building building) {
		ArrayList<ShadowTriangle> listShadowTriangles = new ArrayList<ShadowTriangle> ();
		Date date = Main.getOptionGUI().getTime().getTime();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		int currentMonth = gc.get(GregorianCalendar.MONTH);
		int minhour = 8;
		int maxhour = 19;
		
		switch(currentMonth){
			case 11: case 0: case 1: 
				minhour = 10;
				maxhour = 15;
				break;
			case 2: case 3: case 4: case 8: 
			case 9: case 10:	
				minhour = 9;
				maxhour = 17;
				break;
			case 5: case 6: case 7:
				minhour = 8;
				maxhour = 19;
				break;
			default:
				minhour = 8;
				maxhour = 19;
				break;
		}
		
		int ray = Main.getCityMap3D().ray;

		for(int hour = minhour; hour < maxhour; hour ++){
			gc.set(GregorianCalendar.HOUR_OF_DAY, hour);	
			setDatePlusOneHour(gc);
			ray = Main.getCityMap3D().ray;
			for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = building.getBoundarySurfaces().get(i);
				for (int j = 0; j < surface.getPolygons().size(); ++j) {
					Polygon polygon = surface.getPolygons().get(j);
					
					for (ShadowTriangle t : polygon.getShadowTriangles()) {
						if (!t.getShadowSet().get(ray)) {
							if (surface.getType() == SurfaceType.ROOF) {
								listShadowTriangles.add(t);
							}
							
						} 
					}
				}
			}
		}
		gc.setTime(date);
		Main.getCityMap3D().setSunPosition(gc);

		return listShadowTriangles;
	}
	
	private static void setDatePlusOneHour(GregorianCalendar gc){
		int hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
		hour++;
		if (hour > 23) {
			hour = 0;
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);
		}
		gc.set(GregorianCalendar.HOUR_OF_DAY, hour);
		Main.getCityMap3D().setSunPosition(gc);
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
