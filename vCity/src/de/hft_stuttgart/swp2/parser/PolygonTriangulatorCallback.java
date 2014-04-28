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
	
	public PolygonTriangulatorCallback(GLU glu) {
		super();
		this.glu = glu;
	}
	
	
	public void begin(int type) {
		String[] typename = new String[] { "invalid type 0", "invalid type 1", "GL_LINE_LOOP", "invalid type 3", "GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN" };

		System.out.println("begin type " + typename[type]);
	}
	
	
	public void end() {
		System.out.println("end");
	}
	
	
	public void error(int errnum) throws RuntimeException {
		String estring = glu.gluErrorString(errnum);
		throw new RuntimeException("Tessellation Error: " + estring); //$NON-NLS-1$
	}
	
	
	public void vertex(Object vertexData) {
		double[] coords = (double[]) vertexData;
		System.out.println("Dreieck Punkt (" + coords[0] + ", " + coords[1] + ", " + coords[2] + ")");
	}
	
}
