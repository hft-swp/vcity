package de.hft_stuttgart.swp2.render.options;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.Thread.State;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;

import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.FileChooserGmlFileView;
import de.hft_stuttgart.swp2.render.options.FileChooserGmlFilter;
import de.hft_stuttgart.swp2.render.options.FileChooserImagePreview;
import de.hft_stuttgart.swp2.render.threads.StartParserRunnable;

public class PanelSettings extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8682939653068331145L;
	private GridBagConstraints constraints = new GridBagConstraints();
	private JPanel panelDatabase = new JPanel();
	private String[] strChooseSource = { "Filesystem", "Datenbank"};
	private final JComboBox<String> cmbChooseSource = new JComboBox<String>(strChooseSource);
	private Thread threadStartParsing;
	
	//Elements of panelFile
	private JPanel panelFile;
	private JPanel optionPanel;
	private JPanel panelShadowOptions;
	private JButton btnFileChooser;
	JTextField txtHours = new JTextField("12");
	JTextField txtMin = new JTextField("00");

	
	String strBtnFileChooser = "GML-Datei auswählen";
	JFileChooser fc;
	File gmlFile;
	Date userDate;
	String strPathContent = "Pfad zur GML Datei";
	JLabel lblPath;
	JCheckBox cbGUI = new JCheckBox("Stadt 3D gerendert anzeigen");
	JCheckBox cbVolume = new JCheckBox("Volumen berechnen");
	JCheckBox cbShadow = new JCheckBox("Schatten berechnen");
	JXDatePicker jxDatePicker = new JXDatePicker(new Date());
	private JButton btnStart;

	public Date getTime(){
		if(userDate != null){
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(userDate);
			try{
				int hours = getHours();
				int minutes = getMinutes();
				gc.set(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH), 
						gc.get(GregorianCalendar.DAY_OF_MONTH), hours, minutes);
				return gc.getTime();
			}catch(Exception e1){
				gc.set(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH), 
						gc.get(GregorianCalendar.DAY_OF_MONTH), 12, 0, 0);
			};
		}
		return new Date();
	}


	private int getMinutes() {
		return Integer.parseInt(txtMin.getText());
	}

	private int getHours() {
		return Integer.parseInt(txtHours.getText());
	}

	public JButton getBtnStartParse() {
		return btnStart;
	}

	public PanelSettings(){
		this.setLayout(new GridBagLayout());
		setContent();
	}

	private void setContent() {

		JLabel lblChooseSource = new JLabel("Quelle wählen");
		constraints.insets = new Insets(10, 10, 0, 0);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;// components
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1.0;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.gridwidth = 1;
		lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		//constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblChooseSource, constraints);
		//lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		//constraints.weighty = 0;   //request any extra vertical space
		constraints.gridx = 1; // column 0
		constraints.gridwidth = 1;
		constraints.ipady = 0;
		constraints.insets = new Insets(10, 10, 0, 0);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 0; // row 0
		//constraints.fill = GridBagConstraints.NONE;
		//constraints.anchor = GridBagConstraints.LINE_END;
		cmbChooseSource.setSelectedIndex(0);
		cmbChooseSource.addActionListener(chooseSourceAction());
		this.add(cmbChooseSource, constraints);
		

		generatePanelFile();
		generatePanelDataBase();
		constraints.insets = new Insets(10, 10, 0, 0);
		//constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		//constraints.weighty = 0.1;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		if(cmbChooseSource.getSelectedItem().equals("Filesystem")){
			this.add(panelFile, constraints);	
		}else if(cmbChooseSource.getSelectedItem().equals("Datenbank")){
			this.add(panelDatabase, constraints);
		}else{
			this.add(panelFile, constraints);
		}


		
		constraints.gridx = 0; // column 0
		constraints.gridy = 3; // row 0
		constraints.gridwidth = 2;
		TitledBorder titledBorderOption;
		titledBorderOption = BorderFactory.createTitledBorder("Optionen");

		constraints.gridx = 0; // column 0
		constraints.gridy = 4; // row 0
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		setJPanelOptions();
		optionPanel.setBorder(titledBorderOption);
		this.add(optionPanel, constraints);
		
	}

	private void generatePanelDataBase() {
		// TODO Auto-generated method stub
		
	}


	private JPanel setJPanelOptions() {
		optionPanel = new JPanel();
		optionPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1.0;// components
		constraints.ipady = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1.0;   //request any extra vertical space
		
		CheckBoxListener cbListener = new CheckBoxListener();
		cbGUI.setSelected(true);
		cbGUI.addItemListener(cbListener);
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		//constraints.fill = GridBagConstraints.HORIZONTAL;
		optionPanel.add(cbGUI,
				constraints);
		
		cbVolume.setSelected(true);
		cbVolume.addItemListener(cbListener);
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		optionPanel.add(cbVolume,
				constraints);
		
		cbShadow.setSelected(false);
		cbShadow.addItemListener(cbListener);
		constraints.gridx = 0; // column 0
		constraints.gridy = 2; // row 0
		optionPanel.add(cbShadow,
				constraints);
		
		generatePanelShadowOptions();
		constraints.gridx = 0; // column 0
		constraints.gridy = 3; // row 0
		optionPanel.add(panelShadowOptions,
				constraints);
		if(!cbShadow.isSelected()){
			panelShadowOptions.setVisible(false);
		}
		
		return optionPanel;
	}

	private void generatePanelShadowOptions() {
		panelShadowOptions = new JPanel();
		TitledBorder titledBorderShadow;
		titledBorderShadow = BorderFactory.createTitledBorder("Schatten-Optionen");
		panelShadowOptions.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1.0;// components
		constraints.ipady = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 1.0;   //request any extra vertical space
		jxDatePicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userDate = jxDatePicker.getDate();
			}
		});
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		//constraints.fill = GridBagConstraints.HORIZONTAL;
		panelShadowOptions.add(jxDatePicker,
				constraints);
		JPanel panelTime = new JPanel();
		panelTime.setLayout(new GridLayout(2,2));
		JLabel lblHours = new JLabel("Stunden");
		JLabel lblMin = new JLabel("Minuten");
		
		panelTime.add(lblHours);
		panelTime.add(lblMin);

		txtHours.addFocusListener(new Selection(txtHours));
		txtMin.addFocusListener(new Selection(txtMin));
		panelTime.add(txtHours);
		panelTime.add(txtMin);
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		panelShadowOptions.add(panelTime, constraints);
		panelShadowOptions.setBorder(titledBorderShadow);
	}

	private ActionListener actionStartParsing() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					if(!gmlFile.getPath().isEmpty()){
						lblPath.setForeground(Color.black);
						Runnable StartParserRunnable = new StartParserRunnable(gmlFile.getPath());
						threadStartParsing = new Thread(StartParserRunnable);
						threadStartParsing.start();
						State s = threadStartParsing.getState();
						if(threadStartParsing.getState()==Thread.State.TERMINATED){ 
							try {
								threadStartParsing.join();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}else{
						lblPath.setText("Kein gültiger Pfad");
						lblPath.setForeground(Color.RED);
					}
				}catch(Exception e1){
					lblPath.setText("Kein gültiger Pfad");
					lblPath.setForeground(Color.RED);
				}
			}
		};
	}

	private ActionListener chooseSourceAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cmbChooseSource.getSelectedIndex() == 0){
					panelDatabase.setVisible(false);
					panelFile.setVisible(true);
				}else if(cmbChooseSource.getSelectedIndex() == 1){
					panelFile.setVisible(false);
					panelDatabase.setVisible(true);
				}else{
					panelDatabase.setVisible(false);
					panelFile.setVisible(true);	
				}
			}
		};
	}

	private void generatePanelFile() {
		panelFile = new JPanel();
		panelFile.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.weightx = 1.0;// components
		constraints.weighty = 1.0;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		//constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		btnFileChooser = new JButton(strBtnFileChooser);
		btnFileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFileChooser();
			}
		});
		panelFile.add(btnFileChooser, constraints);
		
		constraints.insets = new Insets(5, 0, 5, 0);
		constraints.gridx = 0;
		constraints.gridy = 1;
		lblPath = new JLabel(strPathContent);
		panelFile.add(lblPath, constraints);
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 2; // row 0
		constraints.fill = GridBagConstraints.HORIZONTAL;
		btnStart = new JButton("Start");
		btnStart.addActionListener(actionStartParsing());
		panelFile.add(btnStart, constraints);
	}

	
	private void setFileChooser() {
		// Set up the file chooser.
		if (fc == null) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File("."));
			// Add a custom file filter and disable the default
			// (Accept All) file filter.
			// fc.addChoosableFileFilter(null);
			fc.setFileFilter(new FileChooserGmlFilter());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setDialogTitle("Bitte wählen Sie eine GML-Datei aus");
			// Add custom icons for file types.
			fc.setFileView(new FileChooserGmlFileView());
			// Add the preview pane.
			fc.setAccessory(new FileChooserImagePreview(fc));
		}

		// Show it.
		int returnVal = fc.showDialog(this, "auswählen");

		// Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gmlFile = fc.getSelectedFile();
		}

		final String[] okFileExtensions = new String[] { "gml" };
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gmlFile = fc.getSelectedFile();
			Boolean flag = false;
			for (String extension : okFileExtensions) {
				if (gmlFile.getName().toLowerCase().endsWith(extension)) {
					lblPath.setText(gmlFile.getPath());
					lblPath.setForeground(Color.black);
					flag = true;
				}
			}
			if (!flag) {
				JOptionPane.showMessageDialog(null,
						"Please choose a gml file only.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		// Reset the file chooser for the next time it's shown.
		fc.setSelectedFile(null);
	}
	
	
	class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getSource();
            if (source == cbGUI) {
            	if(cbGUI.isSelected()){
            		if (Main.getCityMap3D() == null){
            			Main.createGUI();
            		}
            	}else{
            		if(Main.getCityMap3D() != null){
                		Main.getCityMap3D().dispose();
                		Main.setCityMap3DToNull();
            		}
            	}
            }else if (source == cbVolume) {
            	if(cbVolume.isSelected()){
                	Main.getCityMap3D().setIsStartCalculation(true);
            	}
            }else if (source == cbShadow) {
            	if(cbShadow.isSelected()){
            		panelShadowOptions.setVisible(true);
            		Main.getCityMap3D().setIsStartCalculation(true);
            	}else{
            		panelShadowOptions.setVisible(false);
            	}
            	panelShadowOptions.revalidate();
            	optionPanel.revalidate();
            	optionPanel.repaint();
            } 

        }
	}

}
