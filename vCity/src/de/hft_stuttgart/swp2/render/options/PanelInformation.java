package de.hft_stuttgart.swp2.render.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import de.hft_stuttgart.swp2.render.Main;


public class PanelInformation extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595480661468523713L;
	private static JTextArea txtaInformation = new JTextArea();
	private static JScrollPane scrollPane;
	private static JScrollBar scrollBar;
	private static int areaSize = 80; //Estimated
	private static DefaultCaret caret;
	private static boolean isFirst = true;

	public PanelInformation() {
//		this.setLayout(new GridLayout(10,20));
//		txtaInformation.setSize(100, 500);
		txtaInformation.setEditable(false);
		scrollPane = new JScrollPane(txtaInformation);
		scrollPane.setPreferredSize(new Dimension(350,500));
//		scrollPane.setPreferredSize(new Dimension(350,500));
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		TitledBorder titledBorderGraphicOption;
		titledBorderGraphicOption = BorderFactory.createTitledBorder("Programminformationen");
		this.setBorder(titledBorderGraphicOption);
		scrollBar = scrollPane.getVerticalScrollBar();
		scrollBar.setValue( scrollBar.getMaximum() );
		caret = (DefaultCaret) txtaInformation.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	/**
	 * All System.outs about the status and information (or errors) of the programm
	 * should stay here.
	 * You have to use it in this order, addNewTopic(String topic) to say what's the main cause
	 * and then you have to fill the content of the topic with this method. 
	 * Normally usage: addNewTopic("Error in gui");
	 * 				   addProgrammInfo(The gui can't closed before...); 
	 * 
	 * @param info, if it's bigger than 80 chars
	 * 			    then it would automatically breaks the line. 
	 */
	public static void addProgrammInfo(String info){
		String [] lines = info.split(Main.newline);
		String [] result = new String[lines.length];
		for(int i = 0; i < lines.length; i++){
			if(lines[i].length() > areaSize){
				int divisor = (lines[i].length()/areaSize) + 1;
				for(int j = 1; j < divisor; j++){
					result[i] = lines[i].substring(areaSize*j - areaSize, areaSize*j) + Main.newline;
				}
				result[i] += lines[i].substring((areaSize*(divisor)) - areaSize, lines[i].length()) + Main.newline;
			}else{
				result[i] = lines[i] + Main.newline;
			}
		}
		String str = "";
		for(int i = 0; i < lines.length; i++){
			str += result[i];
		}
		txtaInformation.append(str);
		txtaInformation.validate();
		scrollBar.setValue(scrollBar.getMinimum());
		SwingUtilities.invokeLater(new ScrollDownTask());
	}
	
    private static class ScrollDownTask implements Runnable {
        public void run() {
        	scrollBar.setValue(scrollBar.getMaximum());
        }
    }
	public static void addNewTopic(String topic){
		String line = "";
		String str = "";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
		String strDate = sdf.format(date);
		for(int i = 0; i < areaSize; i++){
			line = line + "-";
		}
		if(isFirst == false){
			txtaInformation.append(Main.newline+Main.newline + line);
		}else{
			txtaInformation.append(line);
			isFirst = false;
		}
		addProgrammInfo(line);
		int diff = areaSize - (topic.length() + strDate.length() + 3);
		int diffHalf = diff/2;
		for(int i = 0; i < diffHalf; i++){
			str = str + " ";
		}
		str = str + " " + topic +" (" + strDate +  ")" + " ";
		for(int i = 0; i < diffHalf; i++){
			str = str + " ";
		}
		addProgrammInfo(str);
		txtaInformation.append(line);
		addProgrammInfo(line);
		txtaInformation.validate();
		scrollBar.setValue(scrollBar.getMinimum());
		SwingUtilities.invokeLater(new ScrollDownTask());
	}
}
