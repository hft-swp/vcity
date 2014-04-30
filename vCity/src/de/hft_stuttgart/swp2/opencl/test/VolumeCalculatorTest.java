package de.hft_stuttgart.swp2.opencl.test;

import org.junit.Assert;
import org.junit.Test;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.opencl.VolumeCalculatorInterface;
import de.hft_stuttgart.swp2.opencl.VolumeCalculatorJavaBackend;
import de.hft_stuttgart.swp2.opencl.VolumeCalculatorOpenClBackend;
import de.hft_stuttgart.swp2.opencl.VolumeTest;

public class VolumeCalculatorTest {

	@Test
	public void testCalculateVolumeOpenCl() throws OpenClException {
		VolumeTest.testCity1();
		VolumeTest.testCity2();
		VolumeCalculatorInterface vc = new VolumeCalculatorOpenClBackend();
		vc.calculateVolume();
		double actualVolume = City.getInstance().getBuildings().get(0).getVolume();
		Assert.assertEquals("Volume of Building 1 is wrong", 100D, actualVolume, 0.001);
		actualVolume = City.getInstance().getBuildings().get(1).getVolume();
		Assert.assertEquals("Volume of Building 2 is wrong", 8D, actualVolume, 0.001);
	}
	@Test
	public void testCalculateVolumeJava() throws OpenClException {
		VolumeTest.testCity1();
		VolumeTest.testCity2();
		VolumeCalculatorInterface vc = new VolumeCalculatorJavaBackend();
		vc.calculateVolume();
		double actualVolume = City.getInstance().getBuildings().get(0).getVolume();
		Assert.assertEquals("Volume of Building 1 is wrong", 100D, actualVolume, 0.001);
		actualVolume = City.getInstance().getBuildings().get(1).getVolume();
		Assert.assertEquals("Volume of Building 2 is wrong", 8D, actualVolume, 0.001);
	}

}
