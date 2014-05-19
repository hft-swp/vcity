package de.hft_stuttgart.swp2.opencl;

public enum ShadowPrecision {
	/** Area maximum is 5 */
	VERY_LOW(5, "Very low"),
	/** Area maximum is 2.5 */
	LOW(2.5f, "Low"),
	/** Area maximum is 1.25 */
	MID(1.25f, "Mid"),
	/** Area maximum is 0.75 */
	HIGH(0.75f, "High"),
	/** Area maximum is 0.375 */
	ULTRA(0.375f, "Ultra"),
	/** Area maximum is 0.1 */
	HYPER(0.1f, "Hyper"),
	/** Area maximum is 0.01 */
	AWESOME(0.01f, "Awesome");
	
	
	private float area;
	private String name;
	
	private ShadowPrecision(float area, String name) {
		this.area = area;
	}
	
	/**
	 * 
	 * @return the area
	 */
	public float getArea() {
		return area;
	}
	
	/**
	 * 
	 * @return a readable expression of the enum
	 */
	public String getName() {
		return name;
	}

}
