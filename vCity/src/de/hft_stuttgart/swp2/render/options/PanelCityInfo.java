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
	
	private enum TimeScale {
	    DAY, YEAR
	}
	
	
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
		
		SortedMap <ShadowTriangle,Integer> sorted_map_year = 
				getProfitableShadowTriangles(building, TimeScale.YEAR);
		String strProfitableShadowTrianglesYear;
		if(sorted_map_year != null){
			strProfitableShadowTrianglesYear = profitableShadowTrianglesYearMapToString(sorted_map_year, building);
		}else{
			strProfitableShadowTrianglesYear = "Noch keine Statistik über die möglichen Sonnenstuden im Jahr verfügbar";
		}

		SortedMap <ShadowTriangle,Integer> sorted_map = 
				getProfitableShadowTriangles(building, TimeScale.DAY);
		String strProfitableShadowTriangles;
		if(sorted_map != null){
			Date date = Main.getOptionGUI().getTime().getTime();
			strProfitableShadowTriangles = "Moegliche Schattendreiecke für Solarzellen " +
					Main.newline + "auf dem Dach (pro aktuellem Tag):" + 
					Main.newline + profitableShadowTrianglesMapToString(sorted_map, date);
		}else{
			strProfitableShadowTriangles = "Es sind (noch) " +
					"keine brauchbaren " + Main.newline +
					"Schattendreiecke für Solarzellen vorhanden.";
		}
		
		appendStr += strId + Main.newline + strStreet + Main.newline + strVolume + Main.newline + strAmountVolumeTriangles + 
				Main.newline + strAmountPolygons + Main.newline + strAmountShadowTriangles + Main.newline + 
				strProfitableShadowTrianglesYear + Main.newline + strProfitableShadowTriangles + Main.newline;
		if(!text.contains(appendStr)){
			text = "";
			text = appendStr + Main.newline + updateInfoText;
		}
		
		txtaCityInfo.setText(text);
		txtaCityInfo.setCaretPosition(0);
		txtaCityInfo.validate();
	}
	
	private static SortedMap <ShadowTriangle,Integer> getProfitableShadowTriangles(Building building, TimeScale timeScale){
		ArrayList <ShadowTriangle> listShadowTriangles = new ArrayList<ShadowTriangle>();
		Date date = Main.getOptionGUI().getTime().getTime();
		if(timeScale.equals(TimeScale.DAY)){
			listShadowTriangles = getShadowTriangleListForSolarCalculation(building, date);
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			Main.getCityMap3D().setSunPosition(gc);
		}else{
			listShadowTriangles = getShadowTriangleListForSolarCalculationYear(building, date);
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			Main.getCityMap3D().setSunPosition(gc);
		}

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
	
	private static String profitableShadowTrianglesYearMapToString(
			SortedMap<ShadowTriangle, Integer> profitableShadowTrianglesMap, Building building) {
        String strShadowTriangles = "";
        int gesValue = 0;
        for(Map.Entry<ShadowTriangle,Integer> entry : profitableShadowTrianglesMap.entrySet()) {
        	gesValue = entry.getValue() + gesValue;
        }
        //gesValue = profitableShadowTrianglesMap.size(); //(gesValue / 365)* profitableShadowTrianglesMap.size();
        //(gesValue / 365)* profitableShadowTrianglesMap.size();
        if(gesValue == 0){
        	strShadowTriangles = "Noch keine Statistik über die möglichen Sonnenstuden im Jahr verfügbar.";
        }else{
        	strShadowTriangles = "Es sind insgesamt " + gesValue + 
        			Main.newline + "Sonnenstunden im Jahr für alle Schattendreiecke auf dem Dach." + Main.newline;
        	strShadowTriangles += "Es sind durschnittlich " + (gesValue/profitableShadowTrianglesMap.size()) + Main.newline + 
        			"Sonnenstunden/Schattendreieck im Jahr auf dem Dach" + Main.newline;
        	strShadowTriangles += "Durschnittlich sind das: "  + Main.newline + 
        			((gesValue/profitableShadowTrianglesMap.size())/365) + " Sonnenstunden/Schattendreieck am Tag" + Main.newline;
        	strShadowTriangles += "und"  + Main.newline + 
        			((gesValue)/365) + " Sonnenstunden aller Schattendreiecke am Tag." + Main.newline;
            Set <ShadowTriangle> setKeysOfShadowTriangles = profitableShadowTrianglesMap.keySet();
            ShadowTriangle [] keysOfShadowTriangles = (ShadowTriangle[]) setKeysOfShadowTriangles.toArray(new ShadowTriangle[setKeysOfShadowTriangles.size()]);
        	double shadowTriangleArea = getRoofAreaOfShadowTriangle(building, keysOfShadowTriangles[0]);
        	double roofArea = shadowTriangleArea * profitableShadowTrianglesMap.size();
        	System.out.println(roofArea);
        	if(roofArea != 0){
        		strShadowTriangles += "Bei einer Dachfläche von: "  + (Math.round(roofArea*100)/100.0) + "m²" +
        				" und einer Fläche von: " + (Math.round(shadowTriangleArea*100)/100.0)+ "m²" + " pro Schattendreieck" + Main.newline;
        		//1m2 = 0,1 kwh pro Tag => 0.0125 kw/h pro stunde sonne bei 0,654m2 => ca 1m2 pro solarzelle
        		strShadowTriangles += "erbringen die Solarzellen (1m²/0.00125kWh) pro Sonnenstunde mit " + Main.newline +
        				"durchschnittlicher Sonnenstundenanzahl gerechnet:" + Main.newline;
        		double kwPerSunInOneHour = 0.00125;
        		if(roofArea > 100){
        			//Scaling
        			kwPerSunInOneHour = 0.000125;
        		}
        		double kwPerDay = (((gesValue)/365.0)*roofArea) * kwPerSunInOneHour;
        		double kwPerYear = kwPerDay * 365.0;	
        		strShadowTriangles += (Math.round(kwPerDay*100)/100.0) + " kWh/Tag" + Main.newline;
        		strShadowTriangles += "und " + (Math.round(kwPerYear*100)/100.0) + " kWh/Jahr"+ Main.newline;
        	}
        	
        }
        return strShadowTriangles;
	}
	
	private static double getRoofAreaOfShadowTriangle(Building building, ShadowTriangle t) {
		double area = 0.0;
		
		Vertex [] vertices = t.getVertices();
		Vertex vertexA = vertices[0];
		Vertex vertexB = vertices[1];
		Vertex vertexC = vertices[2];
		float ux = vertexB.getX() - vertexA.getX();
		float uy = vertexB.getY() - vertexA.getY();
		float uz = vertexB.getZ() - vertexA.getZ();
		
		float vx = vertexC.getX() - vertexA.getX();
		float vy = vertexC.getY() - vertexA.getY();
		float vz = vertexC.getZ() - vertexA.getZ();
		//cross product
		float kx = uy*vz - uz*vy;
		float ky = uz*vx - ux*vz;
		float kz = ux*vy - uy*vx;
		
		area = 0.5f * (float)Math.sqrt((kx*kx) + (ky*ky) + (kz*kz));
		return area;
	}


	private static String profitableShadowTrianglesMapToString(
			SortedMap<ShadowTriangle, Integer> profitableShadowTrianglesMap, Date date) {
        Set <ShadowTriangle> setKeysOfShadowTriangles = profitableShadowTrianglesMap.keySet();
        ShadowTriangle [] keysOfShadowTriangles = (ShadowTriangle[]) setKeysOfShadowTriangles.toArray(new ShadowTriangle[setKeysOfShadowTriangles.size()]);
        int counter = 0;
        String strShadowTriangles = "";
        int amount = 0;
        for(Map.Entry<ShadowTriangle,Integer> entry : profitableShadowTrianglesMap.entrySet()) {
        	amount = entry.getValue() + amount;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        String strDate = gc.get(GregorianCalendar.DAY_OF_MONTH) +"." + (gc.get(GregorianCalendar.MONTH) + 1)+ "." + gc.get(GregorianCalendar.YEAR);
        strShadowTriangles += "Aktuell "+ strDate + " gibt es insgesamt " + amount + " Sonnenstunden," + Main.newline;
        strShadowTriangles += "Durschnittl. Sonnenstunden pro Schattendreieck: " + (amount/profitableShadowTrianglesMap.size()) + Main.newline ;
        strShadowTriangles += "Auflistung aller Schattendreiecke mit Sonnenstunden:"+ Main.newline ;
        for(Map.Entry<ShadowTriangle,Integer> entry : profitableShadowTrianglesMap.entrySet()) {
        	   Integer value = entry.getValue();
        	   String [] strVertices = new String [keysOfShadowTriangles[counter].getVertices().length];
        	   int vertexCounter = 0;
        	   for(Vertex v: keysOfShadowTriangles[counter].getVertices()){
        		   String strX = String.valueOf(Math.round(v.getX()*1000)/1000.0) ; 
        		   String strY = String.valueOf(Math.round(v.getY()*1000)/1000.0); 
        		   String strZ = String.valueOf(Math.round(v.getZ()*1000)/1000.0);
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
	
	private static ArrayList<ShadowTriangle> getShadowTriangleListForSolarCalculationYear(Building building,Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.set(GregorianCalendar.MONTH, 0);
		ArrayList<ShadowTriangle> listWinter = getShadowTriangleListForSolarCalculation(building, gc.getTime());
		gc.set(GregorianCalendar.MONTH, 4);
		ArrayList<ShadowTriangle> listSpringAndAutumn = getShadowTriangleListForSolarCalculation(building, gc.getTime());
		gc.set(GregorianCalendar.MONTH, 6);
		ArrayList<ShadowTriangle> listSummer = getShadowTriangleListForSolarCalculation(building, gc.getTime());
		ArrayList<ShadowTriangle> listYear = new ArrayList <ShadowTriangle>();
		for(int i = 0; i < 90; i++){ //3Monate Dez = Jan = 31, Feb=28 => 31*2 + 28 = 90 
			listYear.addAll(listWinter);
		}
		for(int i = 0; i < 183; i++){ //6Monate Mar =31  Apr = 30, Mai=31, Nov = 30, Okt = 31, Sept = 30 => 31*3 + 3 *30 = 183 
			listYear.addAll(listSpringAndAutumn);
		}
		for(int i = 0; i < 92; i++){ //3Monate Jun =30 Jul = 31, Aug=31 => 92
			listYear.addAll(listSummer);
		}
		return listYear;
	}
	
	private static ArrayList<ShadowTriangle> getShadowTriangleListForSolarCalculation(Building building, Date date) {
		ArrayList<ShadowTriangle> listShadowTriangles = new ArrayList<ShadowTriangle> ();
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
