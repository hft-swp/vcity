package de.hft_stuttgart.swp2.opencl;

public enum ShadowPrecision {
	
	VERY_LOW(5), LOW(2.5f), MID(1.25f), HIGH(0.75f), ULTRA(0.375f);
	
	
	private float area;
	
	private ShadowPrecision(float area) {
		this.area = area;
	}
	
	public float getArea() {
		return area;
	}

}
