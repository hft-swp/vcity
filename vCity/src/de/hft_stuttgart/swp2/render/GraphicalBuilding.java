package de.hft_stuttgart.swp2.render;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.Vertex;

public class GraphicalBuilding {
	
	/**
	 * @param drawable
	 * @param gl
	 * @param size
	 * @param ursprungX
	 * @param ursprungY
	 * @param ursprungZ
	 */
	public GraphicalBuilding(GLAutoDrawable drawable, GL2 gl, Building building){
		//Hintergrundfarbe
		//gl.glClearColor(0f, 0f, 0f, 0f);
		
		//Zeichnet die Rückseiten nicht mehr, da man
		//sonst bei einem 3d Objekt hindurchschauen koennte
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_CULL_FACE);
		
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(1, 0, 0); //rot
		//Hinten 
		
		List<Vertex> vertices = building.getPolygon();
		for (Vertex vertex: vertices){
			gl.glVertex3f(vertex.getX(),vertex.getY(),vertex.getZ());
		}

		gl.glEnd();
		gl.glColor3f(0.25f, 0.25f, 0.25f);
	}

}
