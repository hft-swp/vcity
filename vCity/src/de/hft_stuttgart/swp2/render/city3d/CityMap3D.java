package de.hft_stuttgart.swp2.render.city3d;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;
import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.threads.StartShadowCalculationRunnable;
import de.hft_stuttgart.swp2.render.threads.StartVolumeCalculationRunnable;

public class CityMap3D extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 6681486095144440340L;
	private static final int FPS = 60;

	private GLU glu;
	private GLUT glut = new GLUT();
	public Camera camera;
	public Robot robot;
	public int halfScreenHeight;
	public int halfScreenWidth;
	private float r = 10000;
	public boolean enableDrawCenters = false;
	private boolean isShadowCalc = false;
	private boolean isVolumeCalc = true;
	private int minGroundSize = 0;
	private int maxGroundSize = 10000;
	ShadowPrecision defaultShadowPrecision = ShadowPrecision.VERY_LOW;

	private FPSAnimator animator;

	Runnable startVolumeCalculationRunnable = 
			new StartVolumeCalculationRunnable();
	Runnable startShadowCalculationRunnable = 
			new StartShadowCalculationRunnable(defaultShadowPrecision);

	private SunPositionCalculator[][] sunPositions;
	//Sunposition vars
	
	public Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	public SunPositionCalculator sunPos;
	private boolean isShowVolumeAmount = true;
	public int month = 0;

	public int ray = 0;

	private static final boolean showGrid = true;
	
	public ArrayList<Building> findBugBuildings(){
		ArrayList<Building> listeBuilding = new ArrayList <Building>();
		for (Building b : City.getInstance().getBuildings()) {
				int triangleIndex = 0;
				float minZ = Float.MAX_VALUE;
				float minY = Float.MAX_VALUE;
				for (Triangle triangle : b.getTriangles()){
					Vertex [] v= triangle.getVertices();
					for(int j=0; j<3;j++){
						System.out.println("Point " + String.valueOf(triangleIndex) + String.valueOf(j) +  
								" x Wert: "+ v[j].getX() + " y Wert: "+ v[j].getY() + 
								" z Wert: "+ v[j].getZ());
						if(minZ > v[j].getZ()){
							minZ = v[j].getZ();
						}
						if(minY > v[j].getY()){
							minY = v[j].getY();
						}
					}
					triangleIndex ++;
				}
				if(minY < 0 || minZ <0){
					listeBuilding.add(b);
				}
		}
		return listeBuilding;
	}
	
	private Boolean isStartCalculation = true;
	public void setIsStartCalculation(Boolean isStartCalculation) {
		this.isStartCalculation = isStartCalculation;
	}




	public CityMap3D(int width, int height){
		super("vCity - 3D Stadtansicht");
		this.setSize(width, height);
		this.requestFocus();
		setGround(minGroundSize, maxGroundSize);
	
		//
		try {
			robot = new Robot();
		} catch (AWTException e) {
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set up the drawing canvas
		GLCanvas canvas = new GLCanvas();
		canvas.setPreferredSize(new Dimension(width, height));
		getContentPane().add(canvas);

		// draw the scene at FPS fps
		animator = new FPSAnimator(canvas, FPS);
		animator.start();
		
		addListeners(canvas);
		pack();
		this.setLocationRelativeTo(null);
		halfScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
		halfScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
		robot.mouseMove(halfScreenWidth, halfScreenHeight);
	}

	private void initialSunPosition() {
		sunPositions = new SunPositionCalculator[12][24];
		for (int j = 1; j < 13; ++j) {
			for (int i = 0; i < sunPositions[j - 1].length; i++) {
				utcCal.set(2014, j, 1, i, 0, 0);
				sunPositions[j - 1][i] = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
			}
		}
		setSunPosition(Main.getTimeForSunPosition());
	}

	
	public void setSunPosition(Date date) {	
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		utcCal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
				calendar.get(Calendar.MINUTE), 0);
		month = calendar.get(Calendar.MONTH);
		sunPos = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
		ray = sunPos.getSunPosition();
	}
	

	private void addListeners(GLCanvas canvas) {
		canvas.addGLEventListener(this);
		KeyEventListener keyEvent = new KeyEventListener(this);
		this.addKeyListener(keyEvent);
		canvas.addKeyListener(keyEvent);
		MouseEventListener mouseEvent = new MouseEventListener(this);
		canvas.addMouseListener(mouseEvent);
		canvas.addMouseMotionListener(mouseEvent);
		canvas.addMouseWheelListener(mouseEvent);
	}

	
	private void calculation(){
		if(Main.isParserSuccess()){
			if (isVolumeCalc) {
				Main.executor.execute(startVolumeCalculationRunnable);

			}
			if (isShadowCalc) {
				initialSunPosition();
				setSunPosition(Main.getTimeForSunPosition());
				Main.executor.execute(startShadowCalculationRunnable);
			}
		}

	}

	public static float[] RGBToOpenGL(int r1, int g1, int b1) {
		float[] tmp = new float[3];
		tmp[0] = r1 / 255f;
		tmp[1] = g1 / 255f;
		tmp[2] = b1 / 255f;
		return tmp;
	}

	private void setGround(int minSize, int maxSize) {
		Vertex v0 = new Vertex(minSize, 0, minSize);
		Vertex v1 = new Vertex(minSize, 0, maxSize);
		Vertex v2 = new Vertex(maxSize, 0, maxSize);
		Vertex v3 = new Vertex(maxSize, 0, minSize);
		Triangle t1 = new Triangle(v0, v1, v2);
		Triangle t2 = new Triangle(v0, v2, v3);

		Building b = new Building();
		b.addTriangle(t1);
		b.addTriangle(t2);
		City.getInstance().addBuilding(b);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
//		info();
		isShadowCalc = Main.isCalculateShadow();
		isVolumeCalc = Main.isCalculateVolume();

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		// apply camera modifications
		camera.lookAt();
		// drawing building 0
		gl.glColor3f(1f, 1f, 1f);
		
		//TODO Wenn sich der Pfad ändert alles neuberechnen, wenn nicht city Objekte speichern
		if(isStartCalculation){
			calculation();
			isStartCalculation = false;
		}
		if (City.getInstance().getBuildings() != null) {
			if(isVolumeCalc && isShadowCalc){
				setSunPosition(Main.getTimeForSunPosition());
				//System.out.println(Main.getTimeForSunPosition());
				for (Building b : City.getInstance().getBuildings()) {
					drawShadowBuildingsWithVolume(gl, b);
					if(isShowVolumeAmount){
						drawBuildingVolumeAmount(gl, b);
					}
				}
			}else{
				if (isVolumeCalc) {
					for (Building b : City.getInstance().getBuildings()) {
						drawVolumeBuildings(gl, b);
						if(isShowVolumeAmount){
							drawBuildingVolumeAmount(gl, b);
						}
					}
				} else if (isShadowCalc) {
					setSunPosition(Main.getTimeForSunPosition());
					for (Building b : City.getInstance().getBuildings()) {
						drawShadowBuildings(gl, b);
						if(isShowVolumeAmount){
							drawBuildingVolumeAmount(gl, b);
						}
					}
				}else{
					for (Building b : City.getInstance().getBuildings()) {
						drawVolumeBuildings(gl, b);
						if(isShowVolumeAmount){
							drawBuildingVolumeAmount(gl, b);
						}
					}
				}
			}
		}

		drawHemisphere(gl);
		drawAxis(gl);
//		setGround(minGroundSize, maxGroundSize);
		if (enableDrawCenters) {
			drawCentersOfHemisphere(gl);
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
			System.out.println("Um das Volumen der Gebaeude anzeigen zu lassen " +
					"muss es erst berechnet werden.");
		}
		if(building.getVolume() > 0){
			for (Triangle triangle : building.getTriangles()){
				Vertex [] v= triangle.getVertices();
				for(int j=0; j<3;j++){
					if(minX > v[j].getX()){
						minX = v[j].getX();
					}
					if(minY > v[j].getY()){
						minY = v[j].getY();
					}
					if(minZ > v[j].getZ()){
						minZ = v[j].getZ();
					}
					if(maxX < v[j].getX()){
						maxX = v[j].getX();
					}
					if(maxY < v[j].getY()){
						maxY = v[j].getY();
					}
					if(maxZ < v[j].getZ()){
						maxZ = v[j].getZ();
					}
				}
			}
			
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
		for (Triangle t : building.getTriangles()) {
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
		// TODO Texturen Volumen
	}

	private void drawShadowBuildings(GL2 gl, Building building) {
		try {
			for (ShadowTriangle t : building.getShadowTriangles()) {
				gl.glBegin(GL2.GL_TRIANGLES);
				gl.glColor3f(1, 0, 0);
				if (ray != -1 && !t.getShadowSet().get(ray)) {
					gl.glColor3f(0, 1, 0);
				}
				for (Vertex v : t.getVertices()) {
					gl.glVertex3fv(v.getCoordinates(), 0);
				}
				gl.glEnd();
				if (showGrid) {
					drawGrid(gl, t);
				}

				setSunPosition(Main.getTimeForSunPosition());
				gl.glColor3f(255f, 0, 255f);
				gl.glBegin(GL2.GL_LINE_LOOP);
				for (int i = 0; i < sunPositions[month].length; i++) {
					gl.glVertex3d(sunPositions[month][i].getX(),
							sunPositions[month][i].getY(),
							sunPositions[month][i].getZ());
				}
				gl.glEnd();

				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3d(sunPos.getX(), sunPos.getY(), sunPos.getZ());
				gl.glVertex3d(0, 0, 0);
				gl.glEnd();
			}
		} catch (Exception e) {
			System.out.println("Schattenberechnung funktioniert nicht");
			drawVolumeBuildings(gl, building);
		}

	}

	private void drawGrid(GL2 gl, Triangle triangle) {
		if (showGrid) {
			gl.glColor3f(0, 0, 0);
			gl.glBegin(GL2.GL_LINE_LOOP);
			for (Vertex v : triangle.getVertices()) {
				gl.glVertex3fv(v.getCoordinates(), 0);
			}
			gl.glEnd();
		}
	}

	private void drawGrid(GL2 gl, ShadowTriangle triangle) {
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		for (Vertex v : triangle.getVertices()) {
			gl.glVertex3fv(v.getCoordinates(), 0);
		}
		gl.glEnd();
	}



	private void drawCentersOfHemisphere(GL2 gl) {
		if (ray == -1) {
			return;
		}
		gl.glColor3f(1f, 1f, 0);
		float dv = (float) (Math.PI / 12);
		float dh = (float) (2 * Math.PI / 12);
		// for (int i = 0; i < 1; i++) {
		// for (int j = 0; j < 1; j++) {
		float v = dv * (ray / 12) + dv / 2;
		float h = dh * ((ray % 12) - 6) + dh / 2;
		double sinH = Math.sin(h);
		double sinV = Math.sin(v);
		double cosH = Math.cos(h);
		double cosV = Math.cos(v);

		double posX = cosV * sinH * r;
		double posY = sinV * r;
		double posZ = cosV * cosH * r;
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(0d, 0d, 0d);
		gl.glVertex3d(posX, posY, posZ);
		gl.glEnd();
		// }
		// }
	}

	private void drawHemisphere(GL2 gl) {
		gl.glColor3f(1, 1, 1);
		float dv = (float) (Math.PI / 12);
		float dh = (float) (2 * Math.PI / 12);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				float v = dv * i;
				float h = dh * (j - 6);
				float newV = v + dv;
				float newH = h + dh;
				double sinH = Math.sin(h);
				double sinV = Math.sin(v);
				double cosH = Math.cos(h);
				double cosV = Math.cos(v);
				double sinNewV = Math.sin(newV);
				double sinNewH = Math.sin(newH);
				double cosNewV = Math.cos(newV);
				double cosNewH = Math.cos(newH);

				double posX = cosV * sinH * r;
				double posY = sinV * r;
				double posZ = cosV * cosH * r;

				double newVertPosX = cosNewV * sinH * r;
				double newVertPosY = sinNewV * r;
				double newVertPosZ = cosNewV * cosH * r;

				double newHorPosX = cosV * sinNewH * r;
				double newHorPosY = sinV * r;
				double newHorPosZ = cosV * cosNewH * r;

				double newPosX = cosNewV * sinNewH * r;
				double newPosY = sinNewV * r;
				double newPosZ = cosNewV * cosNewH * r;

				gl.glBegin(GL2.GL_LINE_LOOP);
				{
					gl.glVertex3d(posX, posY, posZ);
					gl.glVertex3d(newVertPosX, newVertPosY, newVertPosZ);
					gl.glVertex3d(newPosX, newPosY, newPosZ);
					gl.glVertex3d(newHorPosX, newHorPosY, newHorPosZ);
				}
				gl.glEnd();
			}
		}
	}

	private void drawAxis(GL2 gl) {
		gl.glColor3f(0f, 0f, 1f);
		// x Axis in blue
		gl.glEnable(GL.GL_TRUE);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glBegin(GL2.GL_LINES);

		gl.glLineWidth(5.0f);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(10000f, 0f, 0f);
		}
		gl.glEnd();
		// y Axis in green
		gl.glColor3f(0f, 1f, 0f);
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0, 10000f, 0f);
		}
		gl.glEnd();

		// z Axis in red
		gl.glColor3f(1f, 0f, 0f);
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0f, 0f, 10000f);
		}
		gl.glEnd();

		gl.glColor3f(1f, 1f, 1f);
		gl.glDisable(GL.GL_TRUE);
		gl.glDisable(GL.GL_LINE_SMOOTH);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		camera = new Camera(glu);
		camera.turnRight(-1.2);
		camera.turnDown(0.3);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();

		if (height == 0) {
			height = 1;
		}

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity(); // reset projection matrix
		camera.setPerspective(width, height);
		// Enable the model-view transform
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void stopAnimator() {
		if (animator != null) {
			animator.pause();
		}
	}

	public void startAnimator() {
		if (animator != null) {
			animator.resume();
		}
	}
	
	
	public void info(){
		System.out.println("Horizontal angle: "+ camera.horizontalAngle);
		System.out.println("Vertical angle: "+camera.verticalAngle);
		System.out.println("Pos x: "+camera.positionX);
		System.out.println("Pos y: "+camera.positionY);
		System.out.println("Pos z: "+camera.positionZ);
	}

}
