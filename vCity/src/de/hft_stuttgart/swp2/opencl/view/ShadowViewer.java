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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;
import de.hft_stuttgart.swp2.opencl.OpenClException;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorInterface;
import de.hft_stuttgart.swp2.opencl.ShadowCalculatorOpenClBackend;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.VolumeTest;

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
	
	private int ray = 0;
	
	private static final boolean showGrid = true;


	public static void main(String[] args) throws OpenClException {
		final ShadowViewer view = new ShadowViewer();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.setVisible(true);
			}
		});
	}

	public ShadowViewer() throws OpenClException {
		super("Shadow view");
		
		// test values
		VolumeTest.testCity2();
		VolumeTest.testCity2();
		VolumeTest.testCity2();
		City.getInstance().getBuildings().get(1).translate(0, 0, 5);
		City.getInstance().getBuildings().get(2).translate(5, 0, 5);
		City.getInstance().getBuildings().get(2).scale(1, 5, 1);
		
		int size = 20;
		Vertex v0 = new Vertex(-size, 0, -size);
		Vertex v1 = new Vertex(-size, 0, size);
		Vertex v2 = new Vertex(size, 0, size);
		Vertex v3 = new Vertex(size, 0, -size);
		
		Triangle t1 = new Triangle(v0, v1, v2);
		Triangle t2 = new Triangle(v0, v2, v3);
		
		Building b = new Building();
		b.addTriangle(t1);
		b.addTriangle(t2);
		
		City.getInstance().addBuilding(b);
	
		ShadowCalculatorInterface backend = new ShadowCalculatorOpenClBackend();
//		ShadowCalculatorInterface backend = new ShadowCalculatorJavaBackend();
		System.out.println("Starting shadow calculation...");
		long start = System.currentTimeMillis(); 
		backend.calculateShadow(ShadowPrecision.LOW);
		long end = System.currentTimeMillis();
		System.out.printf("calculate shadow took %d milliseconds\n", (end - start));
		
		
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

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		// apply camera modifications
		camera.lookAt();
		// drawing building 0
		gl.glColor3f(1f, 1f, 1f);
		for (Building b : City.getInstance().getBuildings()) {
			for (ShadowTriangle t : b.getShadowTriangles()) {
//				gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glBegin(GL2.GL_TRIANGLES);
				if (t.getShadowSet().get(ray)) {
					gl.glColor3f(1, 0, 0);
				} else {
					gl.glColor3f(0, 1, 0);
				}
				for (Vertex v : t.getVertices()) {
					gl.glVertex3fv(v.getCoordinates(), 0);
				}
				gl.glEnd();
				if(showGrid) {
					gl.glColor3f(0, 0, 0);
					gl.glBegin(GL2.GL_LINE_LOOP);
	//				gl.glBegin(GL2.GL_TRIANGLES);
					for (Vertex v : t.getVertices()) {
						gl.glVertex3fv(v.getCoordinates(), 0);
					}
					gl.glEnd();
					
	//				gl.glBegin(GL2.GL_POINTS); {
	//					gl.glVertex3fv(t.getCenter().getCoordinates(), 0);
	//				}
	//				gl.glEnd();
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
		gl.glColor3f(1f, 1f, 0);
		float dv = (float) (Math.PI / 12);
		float dh = (float) (2 * Math.PI / 12);
//		for (int i = 0; i < 1; i++) {
//			for (int j = 0; j < 1; j++) {
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
//			}
//		}
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
			ray = ray % 144;
		}
		if (e.getKeyCode() == KeyEvent.VK_L) {
			ray--;
			if (ray < 0) {
				ray = 143;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			enableDrawCenters = !enableDrawCenters;
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
