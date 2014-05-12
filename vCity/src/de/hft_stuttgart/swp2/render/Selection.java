package de.hft_stuttgart.swp2.render;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

public class Selection implements FocusListener{
	
	public JTextComponent textKomponente;
	
	public Selection(JTextComponent text) {
		textKomponente = text;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		textKomponente.selectAll();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}
}