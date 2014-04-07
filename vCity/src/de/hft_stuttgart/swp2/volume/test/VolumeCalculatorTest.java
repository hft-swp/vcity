package de.hft_stuttgart.swp2.volume.test;

import org.junit.Assert;
import org.junit.Test;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.volume.VolumeCalculator;
import de.hft_stuttgart.swp2.volume.VolumeTest;

public class VolumeCalculatorTest {

	@Test
	public void testCalculateVolume() {
		VolumeTest.testCity1();
		VolumeTest.testCity2();
		VolumeCalculator.calculateVolume();
		double actualVolume = City.getInstance().getBuildings().get(0).getVolume();
		Assert.assertEquals("Volume of Building 1 is wrong", 100D, actualVolume, 0.001);
		actualVolume = City.getInstance().getBuildings().get(1).getVolume();
		Assert.assertEquals("Volume of Building 2 is wrong", 8D, actualVolume, 0.001);
	}

}
