package de.hft_stuttgart.swp2.render;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

/**
 *  
 * @author Joerg Amelunxen - japr0.wordpress.com
 * @modified Marcel Ruckaberle
 */
public class KoordinatenSystem extends JFrame implements GLEventListener, KeyListener,
MouseListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7982886201689719394L;
	private float rotx;
	private float roty;
	private float rotz;

	private int prevMouseX;
	private int prevMouseY;
	
	private GLU glu;
	//private GLUT glut;
	protected boolean listenersInitialized;
	protected String Name;
    protected int window_x;
    protected int window_y;
    protected GLCanvas canvas;
    protected float transx;
    protected float transy;
    protected float transz;

    KoordinatenSystem(String Name_value, int x, int y){
    	// Setze interne Variablen
    	glu = new GLU();
    	//glut = new GLUT();
    	listenersInitialized = false;
    	Name = Name_value;
    	window_x = x;
    	window_y = y;
    	rotx = 0;
    	roty = 0;
    	rotz = 0;
    	transx = 0;
    	transy = 0;
    	transz = -5;
    	
    	// Starte App
    	// Suche passendens Profil und setze es
    	GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);

        // Erstelle neuen Frame und bette die Zeichenfläche ein
        this.setTitle(Name);
        //Frame frame = new Frame(Name);
        this.setSize(window_x, window_y);
        this.add(canvas);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        // Erstelle einen Window Listener und sorge für korrektes
        // schließen des Programmes
        this.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Setze den Eventlistener
        canvas.addGLEventListener(this);

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        //animator.add(canvas); nicht in JOGL 2.1.5
        animator.start();	
    }

    /**
     * Diese Methode wird einmal pro Frame aufgerufen und kümmert sich um 
     * die Berechnungen und das Zeichnen
     * 
     * @param GLAutoDrawable drawable
     */
    public void display(GLAutoDrawable drawable) {
        onFrameMath(drawable);
        render(drawable);
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		System.out.println("##############< Info >#################");
		System.out.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
		System.out.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
		System.out.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
		System.out.println("##############</Info >#################");

		// Listener nur einmal initialisieren !
		if (!listenersInitialized)
		{
			listenersInitialized = true;
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			canvas.addKeyListener(this);
		}
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL2 gl = drawable.getGL().getGL2();
		// Setze einen passenden Viewport
		gl.glViewport(0, 0, window_x, window_y);
		//Projektionsmatrix 'leeren'
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(30, (float) window_x / window_y, 1, 100);
		// Modelview 'leeren'
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
    }

    /**
     * Diese Methode macht die Berechnungen pro Frame
     * 
     * @param GLAutodrawable drawable
     */
    private void onFrameMath(GLAutoDrawable drawable) {
    	applyMovement(drawable.getGL().getGL2());
    }

    /**
     * Diese Methode zeichnet ein Koordinatensystem von min bis max
     * 
     * @param GL2 gl, float min, float max 
     */
	public void print_koordinates(GL2 gl, float min, float max){
		gl.glBegin(GL.GL_LINES);
		
		// x Achse 
		gl.glVertex3f(min,0f,0f);
		gl.glVertex3f(max,0f,0f);
		
		// y Achse
		gl.glVertex3f(0f,min,0f);
		gl.glVertex3f(0f,max,0f);
		
		// z Achse
		gl.glVertex3f(0f,0f,min);
		gl.glVertex3f(0f,0f,max);
		
		gl.glEnd();
		
		// 1er Teilstriche
		for( float i = min; i < max; i++ ){
			gl.glBegin(GL.GL_LINES);		
			// Striche auf der X - Achse
			gl.glVertex3f(i,	0f,		0.2f);
			gl.glVertex3f(i,	0f,		-0.2f);	
			gl.glVertex3f(i,	0.2f,	0f);
			gl.glVertex3f(i,	-0.2f,	0f);
			
			// Striche auf der Y - Achse
			gl.glVertex3f(0.2f	,i	,	0f);
			gl.glVertex3f(-0.2f	,i	,	0f);	
			gl.glVertex3f(0f	,i	,	0.2f);
			gl.glVertex3f(0f	,i	,	-0.2f);

			// Striche auf der Z - Achse
			gl.glVertex3f(0.2f	,0	,	i);
			gl.glVertex3f(-0.2f	,0	,	i);	
			gl.glVertex3f(0f	,0.2f	,	i);
			gl.glVertex3f(0f	,-0.2f	,	i);
			
			gl.glEnd();
		}
		
		// 0.1er Teilstriche
		for( float i = min; i < max; i+=0.1f ){
			gl.glBegin(GL.GL_LINES);		
			// Striche auf der X - Achse
			gl.glVertex3f(i,	0f,		0.02f);
			gl.glVertex3f(i,	0f,		-0.02f);	
			gl.glVertex3f(i,	0.02f,	0f);
			gl.glVertex3f(i,	-0.02f,	0f);
			
			// Striche auf der Y - Achse
			gl.glVertex3f(0.02f	,i	,	0f);
			gl.glVertex3f(-0.02f,i	,	0f);	
			gl.glVertex3f(0f	,i	,	0.02f);
			gl.glVertex3f(0f	,i	,	-0.02f);
			
			// Striche auf der Z - Achse
			gl.glVertex3f(0.02f	,0	,	i);
			gl.glVertex3f(-0.02f,0	,	i);	
			gl.glVertex3f(0f	,0.02f	,	i);
			gl.glVertex3f(0f	,-0.02f	,	i);
			gl.glEnd();
		}
	}
	
    private void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // Zeichne ein Koordinatensystem
        print_koordinates(gl, -10 , 10);
    }

    /**
     * Diese Methode überprüft die Mausbewegungen ( ziehen der Maus )
     * 
     * @param MouseEvent e
     */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		// aktuelle Koordinaten
		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();

		// Linke MT -> rotieren
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
		{
			float thetaY = 360f * ((float) (x - prevMouseX) / (float) size.width);
			float thetaX = 360f * ((float) (prevMouseY - y) / (float) size.height);
			// Vorgänger setzen
			prevMouseX = x;
			prevMouseY = y;
			// Rotation anwenden
			rotx -= thetaX;
			roty += thetaY;
		}
		
		// Rechte MT -> bewegen
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{
			float thetaX = 2f * ((float) (x - prevMouseX) / (float) size.width); 
			float thetaY = 2f * ((float) (prevMouseY - y) / (float) size.height);
			prevMouseX = x;
			prevMouseY = y;
			transx += thetaX;
			transy += thetaY;
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		//in Richtung Z Achse mit oben und unten Taste gehen
		if (e.getKeyCode() == KeyEvent.VK_UP){
			transz -= 0.2f;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN){
			transz += 0.2f;
		}
		
		//in Richtung Z Achse mit oben und unten Taste gehen
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){
			transx -= 0.2f;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT){
			transx += 0.2f;
		}
		if (e.getKeyCode() == KeyEvent.VK_Y){
			transy -= 0.2f;
		}
		else if (e.getKeyCode() == KeyEvent.VK_A){
			transy += 0.2f;
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	protected void applyMovement(GL2 gl){
		IntBuffer buffer = com.jogamp.common.nio.Buffers.newDirectIntBuffer(1);
		gl.glGetIntegerv(GL2.GL_MATRIX_MODE, buffer);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glTranslatef(transx, transy, transz);
		gl.glRotatef(rotx, 1f, 0f, 0f);
		gl.glRotatef(roty, 0f, 1f, 0f);
		gl.glRotatef(rotz, 0f, 0f, 1f);
		gl.glMatrixMode(buffer.get(0));
		
		resetAllVars();
	}
	
	public void resetAllVars(){
		transx = 0f;
		transy = 0f;
		transz = 0f;
		
		rotx = 0f;
		roty = 0f;
		rotz = 0f;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}