package de.hft_stuttgart.swp2.render.options.navigation;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
		this.btnUP.addMouseListener(new ML(Direction.up));
		this.btnDown.addMouseListener(new ML(Direction.down));
		this.btnLeft.addMouseListener(new ML(Direction.left));
		this.btnRight.addMouseListener(new ML(Direction.right));
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2465204467327908028L;
	private JLabel btnUP = new JLabel();
	private JLabel btnDown = new JLabel();
	private JLabel btnLeft = new JLabel();
	private JLabel btnRight = new JLabel();
	private JSlider jSliderZoom = new JSlider(JSlider.HORIZONTAL, 20, 100, 60);
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

		this.setLayout(new GridLayout(3, 3));
		this.add(new JLabel());
		this.btnUP.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\up.png"));
		this.add(btnUP);
		this.add(new JLabel());
		this.btnLeft.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\left.png"));
		this.add(btnLeft);
		// Die Abstände zwischen den
		// Teilmarkierungen werden festgelegt
		jSliderZoom.addChangeListener(this);
		jSliderZoom.setMajorTickSpacing(40);
		jSliderZoom.setMinorTickSpacing(20);
		// create a new hashtable
		Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
		// add elements in the hashtable
		dict.put(20, new JLabel("20"));
		dict.put(60, new JLabel("50"));
		dict.put(100, new JLabel("100m"));
		jSliderZoom.setLabelTable(dict);
		// Standardmarkierungen werden erzeugt
		// jSliderZoom.createStandardLabels(1);
		// Zeichnen der Markierungen wird aktiviert
		jSliderZoom.setPaintTicks(true);
		// Zeichnen der Labels wird aktiviert
		jSliderZoom.setPaintLabels(true);
		this.add(jSliderZoom);
		this.btnRight.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\right.png"));
		this.add(btnRight);
		this.add(new JLabel());
		this.btnDown.setIcon(new MyIcon(".\\src\\de\\hft_stuttgart\\swp2\\render\\images\\down.png"));
		this.add(btnDown);
		this.add(new JLabel());
		addActionListeners();
	}

	/**
	 * Method for the zoom-function via the slider.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == jSliderZoom) {
			int currentCameraValue = (int) Main.getCityMap3D().camera.positionY;
			int currentSliderValue = jSliderZoom.getValue();
			int diff;
			if (currentSliderValue < currentCameraValue) {
				diff = currentCameraValue - currentSliderValue;
				Main.getCityMap3D().camera.moveForward(diff);
			} else if (currentCameraValue < currentSliderValue) {
				diff = currentSliderValue - currentCameraValue;
				Main.getCityMap3D().camera.moveBackwards(diff);
			}
		}
	}
}
