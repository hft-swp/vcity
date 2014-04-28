package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;

import org.citygml4j.geometry.Matrix;

import de.hft_stuttgart.swp2.model.Vertex;

public class PolygonTranslate {

	/**
	 * Translate the Coordinates to point of Origin
	 * @param poly List of Vertices
	 * @param reference Smallest Vertex
	 * @return translated Polygonlist
	 * @author 02sial1bif, 02grst1bif
	 */
	public static ArrayList<Vertex> translateToOrigin(ArrayList<Vertex> poly,double[] reference) {

		Matrix translate;
		ArrayList<Vertex> polynew = new ArrayList<>();

		for (int i = 0; i < poly.size(); i++) {

			double x = poly.get(i).getX();
			double y = poly.get(i).getY();
			double z = poly.get(i).getZ();

			translate = new Matrix(new double[][] {
					{ 1, 0, 0, (reference[0]) * -1 },
					{ 0, 1, 0, (reference[1]) * -1 },
					{ 0, 0, 1, (reference[2]) * -1 }, { 0, 0, 0, 1 } });

			double[] vals = new double[] { x, y, z, 1 };
			Matrix v = new Matrix(vals, 1);
			Matrix trans = translate;
			double[] newVals = trans.times(v.transpose()).toColumnPackedArray();

			polynew.add(new Vertex((float) newVals[0], (float) newVals[1], (float) newVals[2]));

		}

		return polynew;

	}
}