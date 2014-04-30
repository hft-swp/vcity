package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class VolumeCalculatorJavaBackend implements VolumeCalculatorInterface {

	@Override
	public void calculateVolume() {
		for(Building b: City.getInstance().getBuildings()) {
			float sum = 0f;
			for(Triangle t: b.getTriangles()) {
				sum += det(t.getVertices()[0], t.getVertices()[1], t.getVertices()[2]);
			}
			b.setVolume(Math.abs(sum/6));
		}

	}
	private float det(Vertex a, Vertex b, Vertex c) {
		 return	(- c.getX() * b.getY() * a.getZ()
				 + b.getX() * c.getY() * a.getZ()
				 + c.getX() * a.getY() * b.getZ()
				 - a.getX() * c.getY() * b.getZ()
				 - b.getX() * a.getY() * c.getZ()
				 + a.getX() * b.getY() * c.getZ());
	}

}
