package de.hft_stuttgart.swp2.render.options;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.hft_stuttgart.swp2.render.Main;

public class PanelNavigation extends JPanel implements ItemListener, ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2465204467327908028L;
	private JButton btnUp = new JButton("Up");
	private JButton btnDown = new JButton("Down");
	private JButton btnLeft = new JButton("Left");
	private JButton btnRight = new JButton("Right");
	private JSlider jSliderZoom = new JSlider(JSlider.HORIZONTAL, 20, 100, 60);
	
	public PanelNavigation() {
		this.setLayout(new GridLayout(3, 3));
		this.add(new JLabel());
		this.add(btnUp);
		btnUp.addItemListener(this);
		this.add(new JLabel());
		this.add(btnLeft);
		// Die Abstände zwischen den 
		// Teilmarkierungen werden festgelegt
		jSliderZoom.addChangeListener(this);
		jSliderZoom.setMajorTickSpacing(40);
		jSliderZoom.setMinorTickSpacing(20);
	    // create a new hashtable
	    Dictionary <Integer, JLabel> dict = new Hashtable <Integer, JLabel>();
	    // add elements in the hashtable
	    dict.put(20, new JLabel("20"));
	    dict.put(60, new JLabel("50"));
	    dict.put(100, new JLabel("100m"));
		jSliderZoom.setLabelTable(dict);
		// Standardmarkierungen werden erzeugt 
		//jSliderZoom.createStandardLabels(1);
		// Zeichnen der Markierungen wird aktiviert
		jSliderZoom.setPaintTicks(true);
		// Zeichnen der Labels wird aktiviert
		jSliderZoom.setPaintLabels(true);
		this.add(jSliderZoom);
		this.add(btnRight);
		this.add(new JLabel());
		this.add(btnDown);
		this.add(new JLabel());
		addActionListeners();
	}
		
	private void addActionListeners(){
		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCameraUp();
			}
		});
		btnLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCameraLeft();
			}
		});
		btnRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCameraRight();
			}
		});
		btnDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCameraDown();
			}
		});
	}
	
	private void setCameraUp() {
		Main.getCityMap3D().camera.strafeForward(3d);
	}
	
	private void setCameraDown() {
		Main.getCityMap3D().camera.strafeBackwards(3d);
	}
	
	private void setCameraRight() {
		Main.getCityMap3D().camera.strafeRight(3d);
	}
	
	private void setCameraLeft() {
		Main.getCityMap3D().camera.strafeLeft(3d);
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		System.out.println(ItemEvent.SELECTED);
		Object source = e.getSource();
		if(source == btnUp){
			System.out.println(ItemEvent.SELECTED);
			setCameraUp();
		}
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if(source == jSliderZoom){
			int currentCameraValue = (int)Main.getCityMap3D().camera.positionY ;
			int currentSliderValue = jSliderZoom.getValue();
			int diff;
			if(currentSliderValue < currentCameraValue){
				diff = currentCameraValue - currentSliderValue;
				Main.getCityMap3D().camera.moveForward(diff);
			}else if(currentCameraValue < currentSliderValue){
				diff = currentSliderValue - currentCameraValue;
				Main.getCityMap3D().camera.moveBackwards(diff);
			}
		}
	}



}
