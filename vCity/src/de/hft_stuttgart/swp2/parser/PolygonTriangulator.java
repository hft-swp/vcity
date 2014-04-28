package de.hft_stuttgart.swp2.parser;

import java.util.ArrayList;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import de.hft_stuttgart.swp2.model.Vertex;

/**
 * Split Polygon into Triangles
 * @author 02grst1bif
 *
 */
public class PolygonTriangulator {

	
	/**
	 * Triangulate a single polygon
	 * @param poly The Polygon, as list of vertices, to be triangulated
	 * @return List of Triangles
	 */
	protected static ArrayList<Vertex> triangulate(ArrayList<Vertex> poly) {
			
		GLU glu = new GLU();
		PolygonTriangulatorCallback callback = new PolygonTriangulatorCallback(glu);
		GLUtessellator tessellator = GLU.gluNewTess();
		GLU.gluTessCallback(tessellator, GLU.GLU_TESS_VERTEX, callback);
		GLU.gluTessCallback(tessellator, GLU.GLU_TESS_BEGIN, callback);
		GLU.gluTessCallback(tessellator, GLU.GLU_TESS_END, callback);
		GLU.gluTessCallback(tessellator, GLU.GLU_TESS_ERROR, callback);
		
		GLU.gluTessBeginPolygon(tessellator, null);
	    GLU.gluTessBeginContour(tessellator);

	    for (Vertex v : poly) {
	    	double[] gon = new double[]{v.getX(),v.getY(),v.getZ()};
	    	GLU.gluTessVertex(tessellator, gon, 0, gon);
	    }
	    
	    GLU.gluTessEndContour(tessellator);
	    GLU.gluTessEndPolygon(tessellator);
	    		
		
		// TODO <Parser>
		ArrayList<Vertex> triangles = new ArrayList<Vertex>();
		
		return triangles;
	}
	
	
}
