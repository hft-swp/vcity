package de.hft_stuttgart.swp2.render.city3d;



import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;
import de.hft_stuttgart.swp2.render.Main;

public class GLBuildingEntity extends GLEntity {
	private boolean isShowGrid = true;
	private boolean isShowVolumeAmount = true;
	private boolean isVolumeCalc;
	private boolean isShadowCalc;
	public boolean isShowGrid() {
		return isShowGrid;
	}

	public void setShowGrid(boolean isShowGrid) {
		this.isShowGrid = isShowGrid;
	}

	public boolean isShowVolumeAmount() {
		return isShowVolumeAmount;
	}

	public void setShowVolumeAmount(boolean isShowVolumeAmount) {
		this.isShowVolumeAmount = isShowVolumeAmount;
	}

	public boolean isVolumeCalc() {
		return isVolumeCalc;
	}

	public void setVolumeCalc(boolean isVolumeCalc) {
		this.isVolumeCalc = isVolumeCalc;
	}

	public boolean isShadowCalc() {
		return isShadowCalc;
	}

	public void setShadowCalc(boolean isShadowCalc) {
		this.isShadowCalc = isShadowCalc;
	}

	public boolean isPolygon() {
		return isPolygon;
	}

	public void setPolygon(boolean isPolygon) {
		this.isPolygon = isPolygon;
	}

	private boolean isPolygon;
	

	public GLBuildingEntity(GL2 gl, GLU glu, GLUT glut, Building building,
			 boolean isVolumeCalc, boolean isShadowCalc, boolean isPolygon, 
			 boolean isShowGrid, boolean isShowVolumeAmount) {
		super(gl, glu, glut, building);
		this.isVolumeCalc = isVolumeCalc;
		this.isShadowCalc = isShadowCalc;
		this.isPolygon = isPolygon;
		this.isShowGrid = isShowGrid;
		this.isShowVolumeAmount = isShowVolumeAmount;
		
	}
	
	private void drawGrid(GL2 gl, Triangle triangle) {
		if (isShowGrid) {
			gl.glColor3f(0, 0, 0);
			gl.glBegin(GL2.GL_LINE_LOOP);
			for (Vertex v : triangle.getVertices()) {
				gl.glVertex3fv(v.getCoordinates(), 0);
			}
			gl.glEnd();
		}
	}

	private void drawGrid(GL2 gl, ShadowTriangle triangle) {
		if(isShowGrid) {
			gl.glColor3f(0, 0, 0);
			gl.glBegin(GL2.GL_LINE_LOOP);
//				gl.glBegin(GL2.GL_TRIANGLES);
			for (Vertex v : triangle.getVertices()) {
				gl.glVertex3fv(v.getCoordinates(), 0);
			}
			gl.glEnd();
		}
	}
	
	private void drawBuildingVolumeAmount(GL2 gl, Building building) {
		float minZ = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		float maxZ = Float.MIN_VALUE;
		if(building.getVolume() == 0.0 && !isVolumeCalc){
			//TODO Fehlermeldung
//			System.out.println("Um das Volumen der Gebaeude anzeigen zu lassen " +
//					"muss es erst berechnet werden.");
		}
		if (building.getVolume() > 0) {
			for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = building.getBoundarySurfaces().get(i);
				for (int j = 0; j < surface.getPolygons().size(); ++j) {
					Polygon p = surface.getPolygons().get(j);
					for (Triangle triangle : p.getTriangles()) {
						Vertex[] v = triangle.getVertices();
						for (int k = 0; k < 3; k++) {
							if (minX > v[k].getX()) {
								minX = v[k].getX();
							}
							if (minY > v[k].getY()) {
								minY = v[k].getY();
							}
							if (minZ > v[k].getZ()) {
								minZ = v[k].getZ();
							}
							if (maxX < v[k].getX()) {
								maxX = v[k].getX();
							}
							if (maxY < v[k].getY()) {
								maxY = v[k].getY();
							}
							if (maxZ < v[k].getZ()) {
								maxZ = v[k].getZ();
							}
						}
					}
				}
			}
			//---------------
			
			float averageValueX;
			if(minX <= 0 && maxX >= 0){
				averageValueX = ((maxX + Math.abs(minX))/2f) + minX;
			}else if(maxX <= 0 && minX <= 0){
				averageValueX = ((Math.abs(minX) - Math.abs(maxX))/2f) * (-1f) + maxX;
			}else if(maxX >= 0 && minX >= 0){
				averageValueX = ((maxX - minX)/2f) + minX;
			}else{
				averageValueX = ((Math.abs(maxX) - Math.abs(minX))/2f) + minX;
			}
			

			float averageValueZ = (Math.abs(minZ) + Math.abs(maxZ)/2f)+minZ;
			if(minZ <= 0 && maxZ >= 0){
				averageValueZ = ((maxZ + Math.abs(minZ))/2f) + minZ;
			}else if(maxZ <= 0 && minZ <= 0){
				averageValueZ = ((Math.abs(minZ) - Math.abs(maxZ))/2f) * (-1f) + maxZ;
			}else if(maxZ >= 0 && minZ >= 0){
				averageValueZ = ((Math.abs(maxZ) - Math.abs(minZ))/2f) + minZ;
			}else{
				averageValueZ = ((Math.abs(maxZ) - Math.abs(minZ))/2f) + minZ;
			}
			gl.glColor3f(1, 1, 1);
			gl.glRasterPos3f(averageValueX, maxY + 1f, averageValueZ);
			glut.glutBitmapString(7, String.valueOf(Math.round(building.getVolume())));
		}
	}



	private void drawShadowBuildingsWithVolume(GL2 gl, Building building) {
		drawShadowBuildings(gl, building);
	}


	private void drawVolumeBuildings(GL2 gl, Building building) {
		for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
			BoundarySurface surface = building.getBoundarySurfaces().get(i);
			for (int j = 0; j < surface.getPolygons().size(); ++j) {
				Polygon p = surface.getPolygons().get(j);
				for (Triangle t : p.getTriangles()) {
					gl.glBegin(GL2.GL_TRIANGLES);
					gl.glColor3f(0, 1, 0);
					for (Vertex v : t.getVertices()) {
						// green
						gl.glColor3f(0, 1, 0);
						gl.glVertex3fv(v.getCoordinates(), 0);
					}
					gl.glEnd();
					drawGrid(gl, t);
				}
			}
			
		}
	}
	

	private void drawShadowBuildings(GL2 gl, Building building) {
		try {
			int ray = Main.getCityMap3D().ray;
			SunPositionCalculator sunPos = Main.getCityMap3D().getSunPos();
			SunPositionCalculator[][] sunPositions = Main.getCityMap3D().getSunPositions();
			int month = Main.getCityMap3D().month;
			
			for (int i = 0; i < building.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = building.getBoundarySurfaces().get(i);
				for (int j = 0; j < surface.getPolygons().size(); ++j) {
					Polygon polygon = surface.getPolygons().get(j);
					Double grey = 0.0;
					if (ray != -1) {
						grey = 1.0 - polygon.getPercentageShadow()[ray];
					}
					for (ShadowTriangle t : polygon.getShadowTriangles()) {
						gl.glBegin(GL2.GL_TRIANGLES);
						gl.glColor3d(grey, grey, grey);
						if (ray != -1 && !t.getShadowSet().get(ray)) {
							gl.glColor3d(0.0, grey, 0.0);
						}
						
						for (Vertex v : t.getVertices()) {
							gl.glVertex3fv(v.getCoordinates(), 0);
						}
						gl.glEnd();
						drawGrid(gl,t);
						gl.glColor3f(255f, 0, 255f);
						gl.glBegin(GL2.GL_LINE_LOOP);
						for (int sunPositionIdx = 0; sunPositionIdx < sunPositions[month].length; sunPositionIdx++) {
							gl.glVertex3d(sunPositions[month][sunPositionIdx].getX(), sunPositions[month][sunPositionIdx].getY(), sunPositions[month][sunPositionIdx].getZ());
						}
						gl.glEnd();
						
						gl.glBegin(GL2.GL_LINES);
						gl.glVertex3d(sunPos.getX(), sunPos.getY(), sunPos.getZ());
						gl.glVertex3d(0, 0, 0);
						gl.glEnd();
					}

				}
			}
		} catch (Exception e) {
			//System.out.println("Initialisierung der Schattenberechnung");
		}

	}

	public void _draw() {
		if(isVolumeCalc && isShadowCalc){
			Main.getCityMap3D().setSunPosition(Main.getTimeForSunPosition());
			drawShadowBuildingsWithVolume(gl, building);
			if(isShowVolumeAmount){
				drawBuildingVolumeAmount(gl, building);
			}
		}else{
			if (isVolumeCalc) {
				drawVolumeBuildings(gl, building);
				if(isShowVolumeAmount){
					drawBuildingVolumeAmount(gl, building);
				}
			} else if (isShadowCalc) {
				Main.getCityMap3D().setSunPosition(Main.getTimeForSunPosition());
				drawShadowBuildings(gl, building);
				if(isShowVolumeAmount){
					drawBuildingVolumeAmount(gl, building);
				}
			}else{
				drawVolumeBuildings(gl, building);
				if(isShowVolumeAmount){
					drawBuildingVolumeAmount(gl, building);
				}
			}
		}
	}
}
