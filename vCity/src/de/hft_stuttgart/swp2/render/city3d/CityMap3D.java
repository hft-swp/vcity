package de.hft_stuttgart.swp2.render.city3d;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.Calendar;
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
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;
import de.hft_stuttgart.swp2.parser.Parser;
import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.PanelSettings;
import de.hft_stuttgart.swp2.render.threads.StartShadowCalculationRunnable;

/**
 * 
 * 
 * 
 * Die Funktion glDepthFunc legt fest, wann ein Fragment den Tiefentest im
 * Tiefenpuffer besteht. Der Parameter func legt die Tiefenvergleichsfunktion
 * fest. Die Tiefenvergleichsfunktion ist eine Bedingung, die erfüllt sein muss,
 * damit das entsprechende Pixel/Fragment gezeichnet wird. Folgende Funktionen
 * existieren: GL_NEVER (Neue Fragmente bestehen niemals den Vergleich) GL_LESS
 * (Neue Fragmente bestehen den Vergleich, wenn sie einen geringeren Tiefenwert
 * haben) und GL_EQUAL, GL_LEQUAL, GL_GREATER, GL_NOTEQUAL, GL_GEQUAL und
 * GL_ALWAYS. Voreingestellt ist GL_LESS
 * 
 * 
 * @author 21ruma1bif
 * 
 */
public class CityMap3D extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 6681486095144440340L;
	private static final int FPS = 40;
	private GL2 gl;
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
	private boolean isShowGrid = true, isStartCalculation = true;
	
	//if the checkbox for shadow isSelected and 
	//if is isShadowCalcViaCheckBoxLock=false
	//then it will be calculated immediately with
	//the shadow default settings, else not,
	//then it would be calculated, only when 
	//the button for recalculate shadow would be pressed
	private boolean isShadowCalcViaCheckBoxLock = true; 
	private boolean isRecalculateShadow = false; 

	public void setRecalculateShadow(boolean isRecalculateShadow) {
		this.isRecalculateShadow = isRecalculateShadow;
	}

	private int splitAzimuth = Main.getSplitAzimuth();
	private int splitHeight = Main.getSplitHeight();
	private boolean isPerformance = true;
	private FPSAnimator animator;

	private SunPositionCalculator[] sunPositions;
	// Sunposition vars
	public Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	private SunPositionCalculator sunPos;
	private boolean isShowVolumeAmount = true;
	public int month = 0;
	private boolean isPolygon = false;
	private boolean isCalculating = false;
	GLBuildingEntity[] glBuildings;
	private boolean isFirstTimeShadowCalc = false;
	
	public boolean isShowVolumeAmount() {
		return isShowVolumeAmount;
	}

	public void setShowVolumeAmount(boolean isShowVolumeAmount) {
		this.isShowVolumeAmount = isShowVolumeAmount;
	}




	public boolean isFirstTimeShadowCalc() {
		return isFirstTimeShadowCalc;
	}

	private boolean isFirstTimeVolumeCalc = false;
	private boolean isVolumeChange = true;
	private boolean isShadowChange = true;
	public boolean drawPolygons = true;
	
	
	public boolean isFirstTimeVolumeCalc() {
		return isFirstTimeVolumeCalc;
	}


	public SunPositionCalculator[] getSunPositions() {
		return sunPositions;
	}

	public SunPositionCalculator getSunPos() {
		return sunPos;
	}

	public int ray = 0;




	public void setIsStartCalculation(Boolean isStartCalculation) {
		this.isStartCalculation = isStartCalculation;
	}
	
	public void setIsFirstTimeShadowCalculation(boolean isFirstTimeShadowCalc) {
		this.isFirstTimeShadowCalc= isFirstTimeShadowCalc;
	}

	public void setShowGrid(boolean isShowGrid) {
		this.isShowGrid = isShowGrid;
	}
	
	public boolean isShowGrid() {
		return isShowGrid;
	}

	
	public CityMap3D(int width, int height) {
		super("vCity - 3D Stadtansicht");
		this.setSize(width, height);
		this.requestFocus();
		// setGround(minGroundSize, maxGroundSize);

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

	public void initialSunPosition(int month, int day) {
		sunPositions = new SunPositionCalculator[24];
		for (int j = 0; j < 24; ++j) {
			utcCal.set(2014, month, day, j, 0, 0);
			sunPositions[j] = new SunPositionCalculator(utcCal.getTime(),
					Parser.getInstance());
		}
		setSunPosition(Main.getTimeForSunPosition());
	}

	public void setSunPosition(GregorianCalendar cal) {
		Calendar calendar = cal;
		month = calendar.get(Calendar.MONTH);
		sunPos = new SunPositionCalculator(calendar.getTime(),
				Parser.getInstance());
		ray = sunPos.getSunPosition(splitAzimuth, splitHeight);
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

	/**
	 * Starts the threads and checks whether a new calculation is needed
	 * isVolumeCalc: Gives the information of the checkboxes in the
	 * PanelSettings if it is selected or not isFirstTimeVolumeCalc: gives the
	 * information if the volume calculation was started or not isVolumeChange:
	 * gives the information if the value of isVolumeCalc has changed or not
	 * isStartCalculation: gives the information if the calculation starts for
	 * the first time or several times with the same parsed city
	 */
	public void calculation() {
		isCalculating = true;
		if (Main.isParserSuccess()) {
			if (isVolumeCalc && isFirstTimeVolumeCalc
					&& (isVolumeChange || isStartCalculation)) {
				Main.executor.execute(Main.startVolumeCalculationRunnable);
			}
			if (isShadowCalc && isFirstTimeShadowCalc
					&& (isShadowChange || isStartCalculation)) {
				int day = Main.getTimeForSunPosition().get(Calendar.DAY_OF_MONTH);
				int month = Main.getTimeForSunPosition().get(Calendar.MONTH);
				initialSunPosition(month, day);
				setSunPosition(Main.getTimeForSunPosition());
				Main.executor.execute(Main.startShadowCalculationRunnable);
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

	@Override
	public void display(GLAutoDrawable drawable) {
		splitAzimuth = Main.getSplitAzimuth();
		splitHeight = Main.getSplitHeight();
		gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		// apply camera modifications
		camera.lookAt();
		// drawing building 0
		gl.glColor3f(1f, 1f, 1f);

		// TODO Wenn sich der Pfad ändert alles neuberechnen, wenn nicht city
		// Objekte speichern
		if (isStartCalculation) {
			StartShadowCalculationRunnable.setShadowCalculated(false);
			if (Main.isCalculateShadow()) {
				isFirstTimeShadowCalc = true;
			} else {
				isFirstTimeShadowCalc = false;
			}
			if (Main.isCalculateVolume()) {
				isFirstTimeVolumeCalc = true;
			} else {
				isFirstTimeVolumeCalc = false;
			}
			calculation();
			// Berechnung der GL-Buildings
			if (City.getInstance().getBuildings() != null) {
				glBuildings = new GLBuildingEntity[City.getInstance()
						.getBuildings().size()];
				GLBuildingEntity glBuilding;
				int buildingCounter = 0;
				for (Building building : City.getInstance().getBuildings()) {
					glBuilding = new GLBuildingEntity(gl, glu, glut, building,
							isVolumeCalc, isShadowCalc, isPolygon, isShowGrid,
							isShowVolumeAmount);
					glBuildings[buildingCounter] = glBuilding;
					buildingCounter++;
				}
			}
			isStartCalculation = false;
		}
		changeView();
		if (glBuildings != null) {
			boolean isViewShadow = Main.getOptionGUI().isShadowViewSelected();
			boolean isViewVolume = Main.getOptionGUI().isVolumeViewSelected();
			// DRAW BUILDINGS
			if (!isChangeValue(isShadowCalc, isVolumeCalc,
					Main.getOptionGUI().isCalculateShadow(), 
					Main.getOptionGUI().isCalculateVolume())
					&& !isRecalculateShadow) {
				for (GLBuildingEntity glBuilding : glBuildings) {
					glBuilding.setShadowCalc(isViewShadow);
					glBuilding.setVolumeCalc(isViewVolume);
					glBuilding.draw();
				}
			} else {
				isShadowCalc = Main.getOptionGUI().isCalculateShadow();
				isVolumeCalc = Main.getOptionGUI().isCalculateVolume();
				isRecalculateShadow = false;
				if (isFirstTimeShadowCalc == false && isShadowCalc) {
					// notice the query: if(isFirstTimeVolumeCalc == false &&
					// isVolumeCalc)
					// is not needed because you can't press so fast the two
					// checkboxes
					// as the display-Method runs
					isFirstTimeShadowCalc = true;
					calculation();
				} else if (isFirstTimeVolumeCalc == false && isVolumeCalc) {
					isFirstTimeVolumeCalc = true;
					calculation();
				}
				//View boolean values
				for (GLBuildingEntity glBuilding : glBuildings) {
					glBuilding.setShadowCalc(isViewShadow);
					glBuilding.setVolumeCalc(isViewVolume);
					glBuilding.draw();
				}
			}

		}

		drawHemisphere(gl);
		drawAxis(gl);
		drawSkyModel(gl);
		// setGround(minGroundSize, maxGroundSize);
		if (enableDrawCenters) {
			drawCentersOfHemisphere(gl);
		}
		
		isShadowCalc = Main.getOptionGUI().isCalculateShadow();
		isVolumeCalc = Main.getOptionGUI().isCalculateVolume();
		isCalculating = false; //Variable must stand on the end
	}

	private void drawSkyModel(GL2 gl2) {
		if (sunPositions == null) {
			return;
		}
		gl.glColor3f(1f, 0, 1f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		for (int sunPositionIdx = 0; sunPositionIdx < sunPositions.length; sunPositionIdx++) {
			gl.glVertex3d(sunPositions[sunPositionIdx].getX(),
					sunPositions[sunPositionIdx].getY(),
					sunPositions[sunPositionIdx].getZ());
		}
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(sunPos.getX(), sunPos.getY(),
				sunPos.getZ());
		gl.glVertex3d(0, 0, 0);
		gl.glEnd();		
	}

	private boolean isChangeValue(boolean isShadowCalcOld,
			boolean isVolumeCalcOld, Boolean calculateShadow,
			Boolean calculateVolume) {
		
		if (isShadowCalcOld == calculateShadow) {
			isShadowChange = false;
			if (isVolumeCalcOld == calculateVolume) {
				isVolumeChange = false;
				return false;
			} else {
				isVolumeChange = true;
				return true;
			}
		} else {
			if(isShadowCalcViaCheckBoxLock){
				isShadowChange = true;
				if(isFirstTimeShadowCalc){
					return true;
				}else{
					return false;
				}
			}else{
				isShadowChange = true;
				return true;
			}
		}
	}

	private void drawCentersOfHemisphere(GL2 gl) {
		if (ray == -1) {
			return;
		}
		gl.glColor3f(1f, 1f, 0);
		float dv = (float) (Math.PI / splitHeight / 2);
		float dh = (float) (2 * Math.PI / splitAzimuth);
		float v = dv * (ray / splitAzimuth) + dv / 2;
		float h = dh * ((ray % splitAzimuth) - (splitAzimuth / 2f)) + dh / 2;
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

	}

	private void drawHemisphere(GL2 gl) {
		gl.glColor3f(1, 1, 1);
		float dv = (float) (Math.PI / splitHeight / 2);
		float dh = (float) (2 * Math.PI / splitAzimuth);
		for (int i = 0; i < splitHeight; i++) {
			for (int j = 0; j < splitAzimuth; j++) {
				float v = dv * i;
				float h = dh * (j - (splitAzimuth / 2f));
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
		gl.glClearColor(0.3333f, 0.3961f, 0.4941f, 0.0f);
		gl.glClearDepth(1.0f);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		if (isPerformance) {
			gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		} else {
			gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		}

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
	
	public void changeView(){
		if(isFirstTimeShadowCalc && isFirstTimeVolumeCalc && Main.isParserSuccess()){
			if(StartShadowCalculationRunnable.isShadowCalculated()){
				Main.getOptionGUI().setShadowViewEnabled(true);
			}
			Main.getOptionGUI().setVolumeViewEnabled(true);
			if(isCalculating){
				Main.getOptionGUI().getBtnRecalculateShadow().setText(PanelSettings.getStrRecalculate());
				Main.getOptionGUI().setSelectShadowView(true);
			}
		}else{
			if(isFirstTimeShadowCalc && Main.isParserSuccess()){
				Main.getOptionGUI().setShadowViewEnabled(true);
				if(isCalculating){
					Main.getOptionGUI().setSelectShadowView(true);
				}
			}else{
				Main.getOptionGUI().setShadowViewEnabled(false);
			}
			if(isFirstTimeVolumeCalc && Main.isParserSuccess()){
				Main.getOptionGUI().setVolumeViewEnabled(true);
				if(isCalculating){
					Main.getOptionGUI().setSelectVolumeView(true);
				}
			}else{
				Main.getOptionGUI().setVolumeViewEnabled(false);
			}
		}

	}

	public void setStartShadowCalculationRunnable(
			ShadowPrecision defaultShadowPrecision, int splitAzimuth,
			int splitHeight) {
		Main.startShadowCalculationRunnable = new StartShadowCalculationRunnable(
				defaultShadowPrecision, splitAzimuth, splitHeight);
	}

	public void info() {
		System.out.println("Horizontal angle: " + camera.horizontalAngle);
		System.out.println("Vertical angle: " + camera.verticalAngle);
		System.out.println("Pos x: " + camera.positionX);
		System.out.println("Pos y: " + camera.positionY);
		System.out.println("Pos z: " + camera.positionZ);
	}

}
