package de.hft_stuttgart.swp2.render.options;

import java.awt.event.FocusEvent;

import javax.swing.text.JTextComponent;

import de.hft_stuttgart.swp2.render.Main;

public class SelectionHours extends Selection{

	public JTextComponent textKomponente;
	
	public SelectionHours(JTextComponent text) {
		super(text);
		this.textKomponente = text;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		textKomponente.selectAll();
		try{
			Main.getCityMap3D().hour = Integer.parseInt(textKomponente.getText());
			Main.getOptionGUI().setTime();
		}catch(Exception e){
			//TODO Ausgabe Fehler
			Main.getCityMap3D().hour = 12;
			Main.getOptionGUI().setTime();
		}

	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}

}
