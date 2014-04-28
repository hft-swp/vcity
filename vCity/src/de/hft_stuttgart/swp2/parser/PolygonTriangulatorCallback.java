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
//		String[] typename = new String[] { "invalid type 0", "invalid type 1", "GL_LINE_LOOP", "invalid type 3", "GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN" };

		collector.begin(type);
	}
	
	
	public void end() {
		collector.end();
	}
	
	
	public void error(int errnum) throws RuntimeException {
		String estring = glu.gluErrorString(errnum);
		throw new RuntimeException("Tessellation Error: " + estring);
	}
	
	
	public void vertex(Object vertexData) {
		double[] coords = (double[]) vertexData;
		collector.collect(coords);
		
//		System.out.println("Dreieck Punkt (" + coords[0] + ", " + coords[1] + ", " + coords[2] + ")");
	}
	
}
