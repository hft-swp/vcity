package de.hft_stuttgart.swp2.render;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class WelcomeFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3409563953493985031L;
	
	JLabel lblWelcome = new JLabel("");
	
	public WelcomeFrame() {
		this.setLayout(new GridLayout(4,1));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}
