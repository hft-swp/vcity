package de.hft_stuttgart.swp2.render.options;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.render.Main;

public class PanelCityInfo extends JPanel{
	private static final long serialVersionUID = -5994955918983304356L;
	private static JTextArea txtaCityInfo = new JTextArea(10, 10);
	private static JScrollPane jspPanelShadowOptions;
	
	public PanelCityInfo() {
		// TODO Auto-generated constructor stub
		this.setLayout(new BorderLayout());
		txtaCityInfo.setEditable(false);
		jspPanelShadowOptions = new JScrollPane(txtaCityInfo);
		this.add(jspPanelShadowOptions, BorderLayout.CENTER);
	}
	
	/**
	 * update the text in the city info panel
	 */
	public static void updateCityInfo() {
		String text = "anz. Gebäude: " + City.getInstance().getBuildings().size();
		if (City.getInstance().getTotalVolume() > 0.0){
			text += String.format(Main.newline + "Stadtvolumen: %.3f m²", City.getInstance().getTotalVolume());
		}
		if (City.getInstance().getTotalShadowTrianglesCount() > 0){
			text += Main.newline + "anz. Schattendreiecke: " + City.getInstance().getTotalShadowTrianglesCount();
		}
		txtaCityInfo.setText(text);		
	}
}
