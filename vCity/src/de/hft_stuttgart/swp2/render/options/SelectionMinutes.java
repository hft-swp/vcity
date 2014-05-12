package de.hft_stuttgart.swp2.render.options;

import java.awt.event.FocusEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.text.JTextComponent;

import de.hft_stuttgart.swp2.render.Main;

public class SelectionMinutes extends Selection{

	private JTextComponent textKomponente;
	
	public SelectionMinutes(JTextComponent text) {
		super(text);
		this.textKomponente = text;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		textKomponente.selectAll();
		try{
			Main.getOptionGUI().setTime();
			//int  = Integer.parseInt(textKomponente.getText());
		}catch(Exception e){
			//TODO Ausgabe Fehler
			Calendar cal = new GregorianCalendar();
			cal.setTime(Main.getCurrentTime());
			Main.getOptionGUI().setTime();
			//Main.getCityMap3D().month = cal.get(Calendar.);
		}

	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}

}
