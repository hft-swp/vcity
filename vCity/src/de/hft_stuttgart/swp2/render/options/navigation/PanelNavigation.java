package de.hft_stuttgart.swp2.render.options.navigation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import de.hft_stuttgart.swp2.render.options.navigation.ML;

import de.hft_stuttgart.swp2.render.Main;

public class PanelNavigation extends JPanel implements  ChangeListener {
	private void addActionListeners() {
		this.btnUP.addMouseListener(new ML(Direction.up, this.btnUP));
		this.btnDown.addMouseListener(new ML(Direction.down, this.btnDown));
		this.btnLeft.addMouseListener(new ML(Direction.left, this.btnLeft));
		this.btnRight.addMouseListener(new ML(Direction.right, this.btnRight));
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2465204467327908028L;
	private JLabel btnUP = new JLabel();
	private JLabel btnDown = new JLabel();
	private JLabel btnLeft = new JLabel();
	private JLabel btnRight = new JLabel();
	private JSlider jSliderZoom = new JSlider(JSlider.HORIZONTAL, 1, 200, 100);
//	private ActionThread at;

	/**
	 * Constructor of the navigation part of the GUI.
	 * Initializes and formats the arrow-buttons and zoom-slider.
	 */
	public PanelNavigation() {
//		at = new ActionThread();
//		at.start();
		this.setMaximumSize(new Dimension(225, 225));
		this.setMinimumSize(new Dimension(225, 225));

		
		this.setLayout(new BorderLayout());
		
		
		JPanel panelNaviElements = new JPanel();
		panelNaviElements.setLayout(new GridLayout(3,3));
		panelNaviElements.add(new JLabel());
		this.btnUP.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\up.png"));
		panelNaviElements.add(btnUP);
		panelNaviElements.add(new JLabel());
		this.btnLeft.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\left.png"));
		panelNaviElements.add(btnLeft);
		panelNaviElements.add(new JLabel());
		this.btnRight.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\right.png"));
		panelNaviElements.add(btnRight);
		panelNaviElements.add(new JLabel());
		this.btnDown.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\down.png"));
		panelNaviElements.add(btnDown);
		panelNaviElements.add(new JLabel());
		addActionListeners();
		
		this.add(panelNaviElements, BorderLayout.CENTER);
		// Die Abstände zwischen den
		// Teilmarkierungen werden festgelegt
		jSliderZoom.addChangeListener(this);
		jSliderZoom.setMajorTickSpacing(50);
		jSliderZoom.setMinorTickSpacing(25);
		// create a new hashtable
		Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
		// add elements in the hashtable
		dict.put(1, new JLabel("1"));
		dict.put(100, new JLabel("100"));
		dict.put(200, new JLabel("200m"));
		jSliderZoom.setLabelTable(dict);
		// Standardmarkierungen werden erzeugt
		// jSliderZoom.createStandardLabels(1);
		// Zeichnen der Markierungen wird aktiviert
		jSliderZoom.setPaintTicks(true);
		// Zeichnen der Labels wird aktiviert
		jSliderZoom.setPaintLabels(true);
		jSliderZoom.setOrientation(JSlider.VERTICAL);
		this.add(jSliderZoom, BorderLayout.EAST);
		
	}

	/**
	 * Method for the zoom-function via the slider.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == jSliderZoom) {
//			int currentCameraValue = (int) Main.getCityMap3D().camera.positionY;
			int currentSliderValue = jSliderZoom.getValue();
//			if (currentSliderValue < currentCameraValue) {
//				diff = currentCameraValue - currentSliderValue;
//				Main.getCityMap3D().camera.moveForward(diff);
				Main.getCityMap3D().camera.slider(currentSliderValue);
//			} else if (currentCameraValue < currentSliderValue) {
//				diff = currentSliderValue - currentCameraValue;
//				Main.getCityMap3D().camera.moveBackwards(diff);
//			}
		}
	}
}
