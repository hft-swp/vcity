package de.hft_stuttgart.swp2.render.options;

import java.awt.event.FocusEvent;


import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import de.hft_stuttgart.swp2.render.Main;

public class SelectionMinutes extends Selection implements DocumentListener{

	private JTextComponent textKomponente;
	
	public SelectionMinutes(JTextComponent text) {
		super(text);
		this.textKomponente = text;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		textKomponente.selectAll();
		setMinutes();

	}

	private void setMinutes() {
		int minutes;
		try{
			minutes = Integer.parseInt(textKomponente.getText());
			Main.getOptionGUI().setMinutes(minutes);
		}catch(Exception e){
			//TODO Ausgabe Fehler
			Main.getOptionGUI().setMinutes(0);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setMinutes();
		System.out.println(Integer.parseInt(textKomponente.getText()));
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		setMinutes();
	}

}
