package de.hft_stuttgart.swp2.render.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import de.hft_stuttgart.swp2.render.Main;

public class PanelInformation extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595480661468523713L;
	private static JTextArea txtaInformation = new JTextArea(10, 20);
	private static JScrollPane scrollPane;
	private static JScrollBar scrollBar;
	private static int areaSize = 80; //Estimated


	public PanelInformation() {
//		this.setLayout(new GridLayout(10,20));
//		txtaInformation.setSize(100, 500);
		txtaInformation.setEditable(false);
		this.setPreferredSize(new Dimension(350,500));
		scrollPane = new JScrollPane(txtaInformation);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		TitledBorder titledBorderGraphicOption;
		titledBorderGraphicOption = BorderFactory.createTitledBorder("Programminformationen");
		this.setBorder(titledBorderGraphicOption);
		scrollBar = scrollPane.getVerticalScrollBar();
		scrollBar.setValue( scrollBar.getMaximum() );
	}
	
	/**
	 * All System.outs about the status and information (or errors) of the programm
	 * should stay here.
	 * 
	 * @param info
	 */
	public static void addProgrammInfo(String info){
		for(int i = 1; i < info.length(); i++){
			if((String.valueOf(info.charAt(i)) == Main.newline)){
				i = i + areaSize;
			}
			if(i % areaSize == 0){
				if(!(String.valueOf(info.charAt(i)) == Main.newline)){
					info = info.substring(0, i) + Main.newline + info.substring(i, info.length());
				}

			}
		}
		txtaInformation.append(info + Main.newline);
	}
	
	public static void addNewTopic(String topic){
		String line = "";
		String str = "";
		for(int i = 0; i < areaSize; i++){
			line = line + "-";
		}
		addProgrammInfo(line);
		int diff = areaSize - topic.length();
		int diffHalf = diff/2;
		for(int i = 0; i < diffHalf; i++){
			str = str + " ";
		}
		str = str + " " + topic + " ";
		for(int i = 0; i < diffHalf; i++){
			str = str + " ";
		}
		addProgrammInfo(str);
		addProgrammInfo(line);

	}
}
