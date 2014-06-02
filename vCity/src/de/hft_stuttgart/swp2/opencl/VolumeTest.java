package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class VolumeTest {

	public static void main(String[] args) throws OpenClException {
		for (int i = 0; i < 1 ; i++) {
			testCity1();
		}
		testCity2();
		CalculatorInterface vc = new CalculatorImpl();
		long start = System.currentTimeMillis(); 
		vc.calculateVolume();
		vc.calculateArea();
		long end = System.currentTimeMillis();
		System.out.printf("calculate volume took %d milliseconds\n", (end - start));
		for (Building b : City.getInstance().getBuildings()) {
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					System.out.println("Area=" + p.getArea());
				}
			}
			System.out.println(b.getVolume());
		}
	}
	
	public static void testCity1() {
		City city = City.getInstance();
		Building b = new Building();
		city.addBuilding(b);
		
		BoundarySurface surface = new BoundarySurface("sur");
		Polygon p = new Polygon("poly");
		surface.addPolygon(p);
		b.addBoundarySurface(surface);

		Vertex p0 = new Vertex(0, 0, 0);
		Vertex p1 = new Vertex(2, 0, 0);
		Vertex p2 = new Vertex(2, 4, 0);
		Vertex p3 = new Vertex(1, 6, 0);
		Vertex p4 = new Vertex(0, 4, 0);
		Vertex p5 = new Vertex(0, 0, 10);
		Vertex p6 = new Vertex(2, 0, 10);
		Vertex p7 = new Vertex(2, 4, 10);
		Vertex p8 = new Vertex(1, 6, 10);
		Vertex p9 = new Vertex(0, 4, 10);
		Triangle t = new Triangle(new Vertex[] {p0, p1, p4});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p2, p4});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p2, p3, p4});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p5, p6});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p6, p2});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p2, p6, p7});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p2, p7, p3});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p8, p4, p3});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p8, p3, p7});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p5, p9, p6});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p9, p7, p6});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p9, p8, p7});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p4, p9});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p4, p8, p9});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p4, p0});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p9, p5});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p5, p1});
		p.addTriangle(t);
	}
	
	public static void testCity2() {
		City city = City.getInstance();
		Building b = new Building();
		city.addBuilding(b);
		
		BoundarySurface surface = new BoundarySurface("sur");
		Polygon p = new Polygon("poly");
		surface.addPolygon(p);
		b.addBoundarySurface(surface);

		Vertex p0 = new Vertex(0, 0, 0);
		Vertex p1 = new Vertex(0, 2, 0);
		Vertex p2 = new Vertex(2, 2, 0);
		Vertex p3 = new Vertex(2, 0, 0);
		Vertex p4 = new Vertex(2, 0, 2);
		Vertex p5 = new Vertex(2, 2, 2);
		Vertex p6 = new Vertex(0, 2, 2);
		Vertex p7 = new Vertex(0, 0, 2);
		Triangle t = new Triangle(new Vertex[] {p0, p2, p3});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p1, p2});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p3, p5, p4});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p3, p2, p5});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p5, p2});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p6, p5});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p6, p1});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p6, p0, p7});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p6, p4, p5});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p4, p6, p7});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p4, p7});
		p.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p3, p4});
		p.addTriangle(t);
	}

}
