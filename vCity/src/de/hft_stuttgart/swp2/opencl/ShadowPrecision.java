package de.hft_stuttgart.swp2.opencl;

public enum ShadowPrecision {
	/** Area maximum is 5 */
	VERY_LOW(5),
	/** Area maximum is 2.5 */
	LOW(2.5f),
	/** Area maximum is 1.25 */
	MID(1.25f),
	/** Area maximum is 0.75 */
	HIGH(0.75f),
	/** Area maximum is 0.375 */
	ULTRA(0.375f),
	/** Area maximum is 0.1 */
	HYPER(0.1f),
	/** Area maximum is 0.01 */
	AWESOME(0.01f);
	
	
	private float area;
	
	private ShadowPrecision(float area) {
		this.area = area;
	}
	
	public float getArea() {
		return area;
	}

}
