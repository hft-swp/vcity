package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

import org.citygml4j.geometry.Matrix;

import de.hft_stuttgart.swp2.model.Vertex;

public class PolygonTranslate {

	/**
	 * @param poly
	 * @param smallestValue
	 * @return translated Polygonlist
	 */
	//Translate the Coordinates to point of Origin
	static ArrayList<Vertex> translateToOrigin(ArrayList<Vertex> poly) {

		float smallestx;
		float smallesty;
		float smallestz;
		
		
		Matrix translate;
		ArrayList<Vertex> polynew = new ArrayList<>();

		smallestx = poly.get(0).getX();
		smallesty = poly.get(0).getY();
		smallestz = poly.get(0).getZ();
		
		for (int i = 0; i < poly.size(); i++) {
  
			float x = poly.get(i).getX();
			float y = poly.get(i).getY();
			float z = poly.get(i).getZ();
			
			
			
			if(x<smallestx){
				smallestx=x;
			}
			if(y<smallesty){
				smallesty=y;
			}
			if(z<smallestz){
				smallestz=z;
			}
			
			translate = new Matrix(new double[][] {
					{ 1, 0, 0, (smallestx) * -1 },
					{ 0, 1, 0, (smallesty) * -1 },
					{ 0, 0, 1, (smallestz) * -1 },
					{ 0, 0, 0, 1 } });
			
			double[] vals = new double[] { x, y, z, 1 };
			Matrix v = new Matrix(vals, 1);
			Matrix trans = translate;
			double[] newVals = trans.times(v.transpose()).toColumnPackedArray();

			polynew.add(new Vertex((float) newVals[0], (float) newVals[1],
					(float) newVals[2]));
			
		}
		System.out.println("Kleinster x,y,z wert:");
		System.out.println(smallestx+ " " + smallesty +" " + smallestz);
		return polynew;

	}
}