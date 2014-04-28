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
	private int type = 0;
	private PolygonTriangulatorCollector collector = null;
	
	public PolygonTriangulatorCallback(GLU glu) {
		super();
		this.glu = glu;
		collector = new PolygonTriangulatorCollector();
	}
	
	
	public void begin(int type) {
		this.type = type;
		String[] typename = new String[] { "invalid type 0", "invalid type 1", "GL_LINE_LOOP", "invalid type 3", "GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN" };

		System.out.println("begin type " + typename[type]);
		collector.begin(type);
	}
	
	
	public void end() {
		System.out.println("end");
		collector.end();
	}
	
	
	public void error(int errnum) throws RuntimeException {
		String estring = glu.gluErrorString(errnum);
		throw new RuntimeException("Tessellation Error: " + estring); //$NON-NLS-1$
	}
	
	
	public void vertex(Object vertexData) {
		double[] coords = (double[]) vertexData;
		
		// TODO we need to perform various operations to each type
		
		collector.collect(coords);
		
		System.out.println("Dreieck Punkt (" + coords[0] + ", " + coords[1] + ", " + coords[2] + ")");
		
		// TODO place the triangles somewhere
	}
	
}
