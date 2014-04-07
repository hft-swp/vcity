package de.hft_stuttgart.swp2.render;

import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;


public class CityMap3D extends KoordinatenSystem{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6907395880122581284L;


	CityMap3D(String Name_value, int x, int y) {
		super(Name_value, x, y);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args){
			new CityMap3D("3D City Map",1024,768);  
    }
	
	public void display (GLAutoDrawable drawable){
		super.display(drawable);
		GL2 gl = drawable.getGL().getGL2();
		City city = City.getInstance();
		ArrayList <Building> buildings = city.getBuildings();
		
		//---------------TEST-------------------------------------
		BuildingTest bt = new BuildingTest();
		buildings = bt.buildings;
		//--------------------------------------------------------
		
		GraphicalBuilding [] graphicalBuildings = new GraphicalBuilding [buildings.size()];
		//int randomNumber = (int) Math.random() * 10;
		for (int i = 0; i < buildings.size(); i++){
			graphicalBuildings [i] = new GraphicalBuilding(drawable, gl, buildings.get(i));
		}
	}

}
