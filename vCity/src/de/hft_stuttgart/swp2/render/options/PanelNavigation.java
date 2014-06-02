package de.hft_stuttgart.swp2.render.options;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hft_stuttgart.swp2.render.Main;

public class PanelNavigation extends JPanel implements ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2465204467327908028L;
	private JButton btnUp = new JButton("Up");
	private JButton btnDown = new JButton("Down");
	private JButton btnLeft = new JButton("Left");
	private JButton btnRight = new JButton("Right");
	
	
	public PanelNavigation() {
		this.setLayout(new GridLayout(3, 3));
		this.add(new JLabel());
		this.add(btnUp);
		btnUp.addItemListener(this);
		this.add(new JLabel());
		
		this.add(btnLeft);
		this.add(new JLabel());
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



}
