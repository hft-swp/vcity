package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

/**
 * This class implements the CalculatorInterface if the context creation of the
 * openCL fails it falls back to a java implementation which is slow.
 * 
 * @author group 3/4
 *
 */
public class CalculatorImpl implements CalculatorInterface {

	@Override
	public void calculateVolume() throws OpenClException {
		VolumeCalculatorInterface vc;
		try {
			vc = new VolumeCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using Java Backend");
			vc = new VolumeCalculatorJavaBackend();
		}
		vc.calculateVolume();
	}

	@Override
	public void calculateShadow(ShadowPrecision precision)
			throws OpenClException {
		ShadowCalculatorInterface backend;
		try {
			backend = new ShadowCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using Java Backend");
			backend = new ShadowCalculatorJavaBackend();
		}
		backend.calculateShadow(precision);
	}

	@Override
	public void calculateShadow(ShadowPrecision precision, int splitAzimuth,
			int splitHeight) throws OpenClException {
		ShadowCalculatorInterface backend;
		try {
			backend = new ShadowCalculatorOpenClBackend();
		} catch (OpenClException e) {
			System.out.println("Using Java Backend");
			backend = new ShadowCalculatorJavaBackend();
		}
		backend.calculateShadow(precision, splitAzimuth, splitHeight);
		
	}

	@Override
	public void calculateArea() {
		City city = City.getInstance();
		for (Building building : city.getBuildings()) {
			for (BoundarySurface surface : building.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					double area = 0;
					for (Triangle t : p.getTriangles()) {
						float a = getDistance(t.getVertices()[0], t.getVertices()[1]);
						float b = getDistance(t.getVertices()[0], t.getVertices()[2]);
						float c = getDistance(t.getVertices()[1], t.getVertices()[2]);
						float s = (a + b + c) / 2f;
						area += Math.sqrt(s * (s - a) * (s - b) * (s - c));
					}
					p.setArea(area);
				}
			}
		}
		
	}
	
	protected static float getDistance(Vertex v0, Vertex v1) {
		float x = v1.getX() - v0.getX();
		float y = v1.getY() - v0.getY();
		float z = v1.getZ() - v0.getZ();
		return (float) Math.sqrt(x * x + y * y + z * z);
	}


}
