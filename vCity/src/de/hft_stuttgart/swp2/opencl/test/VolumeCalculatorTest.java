package de.hft_stuttgart.swp2.opencl.test;

import org.junit.Assert;
import org.junit.Test;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.opencl.VolumeCalculator;
import de.hft_stuttgart.swp2.opencl.VolumeTest;

public class VolumeCalculatorTest {

	@Test
	public void testCalculateVolume() throws OpenClException {
		VolumeTest.testCity1();
		VolumeTest.testCity2();
		VolumeCalculator vc = new VolumeCalculator();
		vc.calculateVolume();
		double actualVolume = City.getInstance().getBuildings().get(0).getVolume();
		Assert.assertEquals("Volume of Building 1 is wrong", 100D, actualVolume, 0.001);
		actualVolume = City.getInstance().getBuildings().get(1).getVolume();
		Assert.assertEquals("Volume of Building 2 is wrong", 8D, actualVolume, 0.001);
	}

}
