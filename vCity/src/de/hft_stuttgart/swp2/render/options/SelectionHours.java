package de.hft_stuttgart.swp2.render.options;

import java.awt.event.FocusEvent;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import de.hft_stuttgart.swp2.render.Main;

public class SelectionHours extends Selection implements DocumentListener{

	public JTextComponent textKomponente;
	
	public SelectionHours(JTextComponent text) {
		super(text);
		this.textKomponente = text;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		textKomponente.selectAll();
		setHour();

	}

	private void setHour() {
		int hour; 
		try{
			hour = Integer.parseInt(textKomponente.getText());
			Main.getOptionGUI().setHours(hour);
		}catch(Exception e){
			//TODO Ausgabe Fehler
			Main.getOptionGUI().setHours(12);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setHour();
		System.out.println(Integer.parseInt(textKomponente.getText()));
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		setHour();
	}

}
