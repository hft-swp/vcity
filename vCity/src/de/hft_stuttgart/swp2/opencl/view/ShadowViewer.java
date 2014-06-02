package de.hft_stuttgart.swp2.opencl.view;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.TimeZone;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jocl.CLException;

import com.jogamp.opengl.util.FPSAnimator;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.opencl.CalculatorImpl;
import de.hft_stuttgart.swp2.opencl.CalculatorInterface;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;
import de.hft_stuttgart.swp2.parser.Parser;
import de.hft_stuttgart.swp2.parser.ParserInterface;

public class ShadowViewer extends JFrame implements GLEventListener,
		KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 6681486095144440340L;
	private static final int FPS = 60;
	
	private GLU glu;
	private Camera camera;
	private Robot robot;
	private int halfScreenHeight;
	private int halfScreenWidth;
	private float r = 10000;
	private boolean enableDrawCenters = false;
	
	private Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	private SunPositionCalculator[][] sunPositions;
	private SunPositionCalculator sunPos;
	private int hour = 6;
	private int month = 0;
	
	private int ray = 0;
	
	private int splitAzimuth = 32;
	private int splitHeight = 16;
	
	private static final boolean showGrid = true;


	public static void main(String[] args) throws OpenClException {
//		CL.setLogLevel(CL.LogLevel.LOG_DEBUGTRACE);
		try {
			final ShadowViewer view = new ShadowViewer();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					view.setVisible(true);
				}
			});
		} catch (CLException e){
			e.printStackTrace();
			System.out.println(e.getStatus());
		}
	}

	public ShadowViewer() throws OpenClException {
		super("Shadow view");
		
		// test values
//		VolumeTest.testCity1();
//		VolumeTest.testCity2();
//		VolumeTest.testCity2();
//		VolumeTest.testCity2();
//		City.getInstance().getBuildings().get(1).translate(0, 0, 5);
//		City.getInstance().getBuildings().get(2).translate(5, 0, 5);
//		City.getInstance().getBuildings().get(2).scale(1, 5, 1);
//		
//		int size = 20;
//		Vertex v0 = new Vertex(-size, 0, -size);
//		Vertex v1 = new Vertex(-size, 0, size);
//		Vertex v2 = new Vertex(size, 0, size);
//		Vertex v3 = new Vertex(size, 0, -size);
//		
//		Triangle t1 = new Triangle(v0, v1, v2);
//		Triangle t2 = new Triangle(v0, v2, v3);
//		
//		Building b = new Building();
//		BoundarySurface surface = new BoundarySurface("sur");
//		Polygon p = new Polygon("poly");
//		surface.addPolygon(p);
//		p.addTriangle(t1);
//		p.addTriangle(t2);
//		b.addBoundarySurface(surface);
//		City.getInstance().addBuilding(b);
		
//		for (int i = 0; i < 5; i++) {
//			for (int j = 0; j < 5; j++) {
//				VolumeTest.testCity2();
//				Building b = City.getInstance().getBuildings().get(i * 5 + j);
//				b.translate(3 * i, 0, 3 * j);
//			}
//		}
//		
//		for (Building b2 : City.getInstance().getBuildings()) {
//			for (BoundarySurface surface2 : b2.getBoundarySurfaces()) {
//				for (Polygon p2 : surface2.getPolygons()) {
//					for (Triangle t : p2.getTriangles()) {
//						Vertex d0 = ShadowCalculatorInterface.vertexDiff(t.getVertices()[1], t.getVertices()[0]);
//						Vertex d1 = ShadowCalculatorInterface.vertexDiff(t.getVertices()[2], t.getVertices()[0]);
//						t.setNormalVector(cross(d0, d1));
//					}
//				}
//			}
//		}
		
		ParserInterface parser = Parser.getInstance();
		try {
			parser.parse("Gruenbuehl_LOD2.gml");
//			parser.parse("einHaus.gml");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println(Parser.getInstance().getEPSG());

		sunPositions = new SunPositionCalculator[12][24];
		for (int j = 1; j < 13; ++j) {
			for (int i = 0; i < sunPositions[j - 1].length; i++) {
				utcCal.set(2014, j, 1, i, 0, 0);
				sunPositions[j - 1][i] = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
			}
		}
		utcCal.set(2014, month + 1, 1, hour, 0, 0);
		sunPos = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
		
		CalculatorInterface calc = new CalculatorImpl();
//		ShadowCalculatorJavaBackend calc = new ShadowCalculatorJavaBackend();
		System.out.println("Starting shadow calculation...");
		long start = System.currentTimeMillis(); 
		calc.calculateShadow(ShadowPrecision.VERY_LOW, splitAzimuth, splitHeight); //VERY_LOW(5f), LOW(2.5f), MID(1.25f), HIGH(0.75f), ULTRA(0.375f), HYPER(0.1f), AWESOME(0.01f)
		long end = System.currentTimeMillis();
		long milli = end - start;
		
		System.out.printf("\n\n"
				+ "calculate shadow took : %7d milliseconds %s\n"
				+ "average per building  : %5.2f milliseconds (%d buildings)\n", milli, milliseconds2string(milli), (double)milli/City.getInstance().getBuildings().size(), City.getInstance().getBuildings().size());
		
		
		halfScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
		halfScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width / 2;

		try {
			robot = new Robot();
		} catch (AWTException e) {
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set up the drawing canvas
		GLCanvas canvas = new GLCanvas();
		canvas.setPreferredSize(new Dimension(1024, 768));
		getContentPane().add(canvas);
		
		// draw the scene at FPS fps
		FPSAnimator animator = new FPSAnimator(canvas, FPS);
		animator.start();
		
		canvas.addGLEventListener(this);
		this.addKeyListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		pack();
		this.setLocationRelativeTo(null);
		robot.mouseMove(halfScreenWidth, halfScreenHeight);
	}

	private Vertex cross(Vertex v0, Vertex v1) {
		float x = v0.getY() * v1.getZ() - v0.getZ() * v1.getY();
		float y = v0.getZ() * v1.getX() - v0.getX() * v1.getZ();
		float z = v0.getX() * v1.getY() - v0.getY() * v1.getX();
		return new Vertex(x, y, z);
	}

	private String milliseconds2string(long milli) {
		String mytime = "( ";
		if(milli > 3600000) {
			mytime += String.format("%d hours ",(int) ((milli / (1000*60*60)) % 24));
		}
		if(milli > 60000) {
			mytime += String.format("%d minutes ", (int) ((milli / (1000*60)) % 60));
		}
		if(milli > 1000) {
			mytime += String.format("%d seconds ", (int) (milli / 1000) % 60);
		}
		mytime +=  String.format("%d milliseconds )", (int) (milli % 1000));
		return mytime;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		// apply camera modifications
		camera.lookAt();
		// drawing buildings
		gl.glColor3f(1f, 1f, 1f);
		for (Building b : City.getInstance().getBuildings()) {
			for (int i = 0; i < b.getBoundarySurfaces().size(); ++i) {
				BoundarySurface surface = b.getBoundarySurfaces().get(i);
				for (int j = 0; j < surface.getPolygons().size(); ++j) {
					Polygon p = surface.getPolygons().get(j);
					double grey = 0.1;
					if (ray != -1) {
						grey = 1.0 - p.getPercentageShadow()[ray];
					}
//					System.out.println("polygon: " + j + " grey: " +grey);
					for (Triangle t : p.getTriangles()) {
//						gl.glBegin(GL2.GL_LINE_LOOP);
						gl.glBegin(GL2.GL_TRIANGLES);
						gl.glColor3d(grey, grey, grey);
//						if (ray != -1 && !t.getShadowSet().get(ray)) {
//							gl.glColor3d(0.0, grey, 0.0);
//						}
						
						for (Vertex v : t.getVertices()) {
							gl.glVertex3fv(v.getCoordinates(), 0);
						}
						gl.glEnd();
//						if(showGrid) {
//							gl.glColor3f(0, 0, 0);
//							gl.glBegin(GL2.GL_LINE_LOOP);
//			//				gl.glBegin(GL2.GL_TRIANGLES);
//							for (Vertex v : t.getVertices()) {
//								gl.glVertex3fv(v.getCoordinates(), 0);
//							}
//							gl.glEnd();
//						}
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
		}
		
		drawHemisphere(gl);
		drawAxis(gl);
		
		if (enableDrawCenters) {
			drawCentersOfHemisphere(gl);
		}
		
	}
	
	private void drawCentersOfHemisphere(GL2 gl) {
		if (ray == -1) {
			return;
		}
		gl.glColor3f(1f, 1f, 0);
		float dv = (float) (Math.PI / splitHeight / 2);
		float dh = (float) (2 * Math.PI / splitAzimuth);
//		for (int i = 0; i < 1; i++) {
//			for (int j = 0; j < 1; j++) {
				float v = dv * (ray / splitAzimuth) + dv / 2;
				float h = dh * ((ray % splitAzimuth) - (splitAzimuth/2f)) + dh / 2;
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
//			}
//		}
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
				
				gl.glBegin(GL2.GL_LINE_LOOP); {
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
		gl.glBegin(GL2.GL_LINES); {
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(10000f, 0f, 0f);
		}
		gl.glEnd();
		// y Axis in green
		gl.glColor3f(0f, 1f, 0f);
		gl.glBegin(GL2.GL_LINES); {
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0, 10000f, 0f);
		}
		gl.glEnd();
		
		// z Axis in red
		gl.glColor3f(1f, 0f, 0f);
		gl.glBegin(GL2.GL_LINES); {
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0f, 0f, 10000f);
		}
		gl.glEnd();
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
		gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
		gl.glClearDepth(1.0f);
//		gl.glCullFace(GL.GL_FRONT_AND_BACK);
		gl.glEnable(GL.GL_CULL_FACE);
		
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

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			camera.moveForward(0.5d);
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			camera.moveBackwards(0.5d);
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			camera.strafeLeft(0.5d);
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			camera.strafeRight(0.5d);
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		if (e.getKeyCode() == KeyEvent.VK_O) {
			ray++;
			ray = ray % (splitAzimuth * splitHeight);
		}
		if (e.getKeyCode() == KeyEvent.VK_L) {
			ray--;
			if (ray < 0) {
				ray = (splitAzimuth * splitHeight) - 1;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			enableDrawCenters = !enableDrawCenters;
		}
		if (e.getKeyCode() == KeyEvent.VK_I) {
			month++;
			if (month > 11) {
				month = 11;
			}
			utcCal.set(2014, month + 1, 1, hour, 0, 0);
			sunPos = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
			ray = sunPos.getSunPosition(splitAzimuth, splitHeight);
		}
		if (e.getKeyCode() == KeyEvent.VK_K) {
			month--;
			if (month < 0) {
				month = 0;
			}
			utcCal.set(2014, month + 1, 1, hour, 0, 0);
			sunPos = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
			ray = sunPos.getSunPosition(splitAzimuth, splitHeight);
		}
		if (e.getKeyCode() == KeyEvent.VK_U) {
			hour++;
			if (hour > 23) {
				hour = 0;
			}
			utcCal.set(2014, month + 1, 1, hour, 0, 0);
			sunPos = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
			ray = sunPos.getSunPosition(splitAzimuth, splitHeight);

		}
		if (e.getKeyCode() == KeyEvent.VK_J) {
			hour--;
			if (hour < 0) {
				hour = 23;
			}
			utcCal.set(2014, month + 1, 1, hour, 0, 0);
			sunPos = new SunPositionCalculator(utcCal.getTime(), 11.6, 48.1);
			ray = sunPos.getSunPosition(splitAzimuth, splitHeight);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		halfScreenWidth = e.getXOnScreen();
		halfScreenHeight = e.getYOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (camera == null) {
			return;
		}
		int dx = e.getXOnScreen() - halfScreenWidth;
		int dy = e.getYOnScreen() - halfScreenHeight;
		robot.mouseMove(halfScreenWidth, halfScreenHeight);
		camera.turnLeft(dx * 2 * Math.PI / 360 / 8);
		camera.turnDown(dy * 2 * Math.PI / 360 / 8);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
//		if (e.getButton() == 2) {
//			System.out.println("scroll");
//		}
	}

}
