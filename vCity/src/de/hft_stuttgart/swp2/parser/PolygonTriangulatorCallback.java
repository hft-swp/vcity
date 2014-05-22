package de.hft_stuttgart.swp2.parser;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

/**
 * JOGL Tessellator callback
 * 
 * @author 02grst1bif
 */
public class PolygonTriangulatorCallback extends GLUtessellatorCallbackAdapter {

	private GLU glu = null;
	private PolygonTriangulatorCollector collector = null;

	public PolygonTriangulatorCallback(GLU glu, PolygonTriangulatorCollector collector) {
		super();
		this.glu = glu;
		this.collector = collector;
	}

	public void begin(int type) {
		collector.begin(type);
	}

	public void end() {
		collector.end();
	}

	public void error(int errnum) throws RuntimeException {
		String estring = glu.gluErrorString(errnum);
		try {
			throw new ParserException("Tessellation Error: " + estring);
		} catch (ParserException e) {
			throw new RuntimeException("Something really got bad here...");
		}
	}

	public void vertex(Object vertexData) {
		double[] coords = (double[]) vertexData;
		collector.collect(coords);
	}

	public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
		outData[0] = coords;
	}

}
