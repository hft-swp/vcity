package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class VolumeCalculatorJavaBackend implements VolumeCalculatorInterface {

	@Override
	public void calculateVolume() {
		Double totalVolume = 0.0;
		for(Building b: City.getInstance().getBuildings()) {
			float sum = 0f;
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					for(Triangle t: p.getTriangles()) {
						sum += det(t.getVertices()[0], t.getVertices()[1], t.getVertices()[2]);
					}
					b.setVolume(Math.abs(sum/6));
					totalVolume += Math.abs(sum/6);
				}
			}
		}
		City.getInstance().setTotalVolume(totalVolume);
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
