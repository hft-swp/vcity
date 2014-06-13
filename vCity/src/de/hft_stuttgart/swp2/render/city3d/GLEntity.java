package de.hft_stuttgart.swp2.render.city3d;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import de.hft_stuttgart.swp2.model.Building;

/**
 * 
 * @author 21ruma1bif
 *
 */
public abstract class GLEntity {

	static int idCounter = 0;
	Building building;
	int id;
	GL2 gl;
	GLU glu;
	GLUT glut;

	public GLEntity(GL2 gl, GLU glu, GLUT glut, Building building) {
		this.gl = gl;
		this.glu = glu;
		this.glut = glut;
		this.building = building;
		this.id = idCounter;
		idCounter++;
	}

	public void draw() {
		gl.glPushName(id);
		_draw();
	}

	public abstract void _draw();
}
