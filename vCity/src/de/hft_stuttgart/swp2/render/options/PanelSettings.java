package de.hft_stuttgart.swp2.render.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

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
	private JButton btnFileChooser;
	String strBtnFileChooser = "GML-Datei auswählen";
	JFileChooser fc;
	File gmlFile;
	String strPathContent = "Pfad zur GML Datei";
	JLabel lblPath;
	JCheckBox cbGUI = new JCheckBox("Stadt 3D gerendert anzeigen");
	JCheckBox cbVolume = new JCheckBox("Volumen berechnen");
	JCheckBox cbShadow = new JCheckBox("Schatten berechnen");
	JXDatePicker jxDatePicker = new JXDatePicker(new Date());
	private JButton btnStartParse;


	public PanelSettings(){
		this.setLayout(new GridBagLayout());
		setContent();
	}

	private void setContent() {

		JLabel lblChooseSource = new JLabel("Quelle wählen");
		constraints.insets = new Insets(2, 10, 0, 0);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;// components
		constraints.ipady = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1.0;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.PAGE_START;
		lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		//constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblChooseSource, constraints);
		//lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		//constraints.weighty = 0;   //request any extra vertical space
		constraints.gridx = 1; // column 0
		constraints.gridwidth = 1;
		constraints.insets = new Insets(8, 10, 0, 0);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 0; // row 0
		//constraints.fill = GridBagConstraints.NONE;
		//constraints.anchor = GridBagConstraints.LINE_END;
		cmbChooseSource.setSelectedIndex(0);
		cmbChooseSource.addActionListener(chooseSourceAction());
		this.add(cmbChooseSource, constraints);
		

		generatePanelFile();
		constraints.insets = new Insets(10, 10, 0, 0);
		//constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		//constraints.weighty = 0.1;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(panelFile, constraints);
		
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 2; // row 0
		btnStartParse = new JButton("Start Parser");
		btnStartParse.addActionListener(actionStartParsing());
		this.add(btnStartParse, constraints);
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 3; // row 0
		constraints.gridwidth = 2;
		this.add(new JSeparator(JSeparator.HORIZONTAL),
				constraints);
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 4; // row 0
		constraints.gridwidth = 2;
		
		this.add(cbGUI,
				constraints);
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 5; // row 0
		constraints.gridwidth = 2;
		
		this.add(cbVolume,
				constraints);
		
		constraints.gridx = 0; // column 0
		constraints.gridy = 6; // row 0
		constraints.gridwidth = 2;
		
		this.add(cbShadow,
				constraints);
	}

	private ActionListener actionStartParsing() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runnable StartParserRunnable = new StartParserRunnable(gmlFile.getPath());
				threadStartParsing = new Thread(StartParserRunnable);
				threadStartParsing.start();
				try {
					threadStartParsing.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//Main.startParser(gmlFile.getPath());
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
		constraints.weightx = 0.5;// components
		constraints.weighty = 0.0;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.anchor = GridBagConstraints.PAGE_START;
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

}
