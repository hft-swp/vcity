package de.hft_stuttgart.swp2.opencl;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class VolumeTest {

	public static void main(String[] args) throws OpenClException {
		for (int i = 0; i < 100000 ; i++) {
			testCity1();
		}
		testCity2();
		CalculatorInterface vc = new CalculatorImpl();
		long start = System.currentTimeMillis(); 
		vc.calculateVolume();
		long end = System.currentTimeMillis();
		System.out.printf("calculate volume took %d milliseconds\n", (end - start));
//		for (Building b : City.getInstance().getBuildings()) {
//			System.out.println(b.getVolume());
//		}
	}
	
	public static void testCity1() {
		City city = City.getInstance();
		Building b = new Building();
		city.addBuilding(b);

		Vertex p0 = new Vertex(0, 0, 0);
		Vertex p1 = new Vertex(2, 0, 0);
		Vertex p2 = new Vertex(2, 0, 4);
		Vertex p3 = new Vertex(1, 0, 6);
		Vertex p4 = new Vertex(0, 0, 4);
		Vertex p5 = new Vertex(0, 10, 0);
		Vertex p6 = new Vertex(2, 10, 0);
		Vertex p7 = new Vertex(2, 10, 4);
		Vertex p8 = new Vertex(1, 10, 6);
		Vertex p9 = new Vertex(0, 10, 4);
		Triangle t = new Triangle(new Vertex[] {p0, p1, p4});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p2, p4});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p2, p3, p4});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p5, p6});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p6, p2});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p2, p6, p7});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p2, p7, p3});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p8, p4, p3});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p8, p3, p7});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p5, p9, p6});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p9, p7, p6});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p9, p8, p7});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p4, p9});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p4, p8, p9});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p9, p4, p0});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p9, p5});
		b.addTriangle(t);
	}
	
	public static void testCity2() {
		City city = City.getInstance();
		Building b = new Building();
		city.addBuilding(b);

		Vertex p0 = new Vertex(0, 0, 0);
		Vertex p1 = new Vertex(0, 2, 0);
		Vertex p2 = new Vertex(2, 2, 0);
		Vertex p3 = new Vertex(2, 0, 0);
		Vertex p4 = new Vertex(2, 0, 2);
		Vertex p5 = new Vertex(2, 2, 2);
		Vertex p6 = new Vertex(0, 2, 2);
		Vertex p7 = new Vertex(0, 0, 2);
		Triangle t = new Triangle(new Vertex[] {p0, p2, p3});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p1, p2});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p3, p5, p4});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p3, p2, p5});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p5, p2});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p1, p6, p5});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p6, p1});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p6, p0, p7});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p6, p4, p5});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p4, p6, p7});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p4, p7});
		b.addTriangle(t);
		t = new Triangle(new Vertex[] {p0, p3, p4});
		b.addTriangle(t);
	}

}
