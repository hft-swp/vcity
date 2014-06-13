package de.hft_stuttgart.swp2.render.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;


import de.hft_stuttgart.swp2.parser.ParserException;
import de.hft_stuttgart.swp2.parser.ParserExport;
import de.hft_stuttgart.swp2.render.Main;


public class PanelExport extends JPanel {

	private JComboBox<String> cmbFormat;
	private static JButton btnSave = new JButton("Speichern");
	private String programInfo = "";

	private class ExportFormatFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			// Also shows subdirectories
			if (f.isFile()) {
				return true;
			} else {
				return false;
			}
		}
		public String getDescription() {
			return (String) cmbFormat.getSelectedItem();
		}
	}
	private static final long serialVersionUID = 7774736827577623532L;

	public PanelExport() {
		JLabel lblFormat = new JLabel("Format");
		String[] types = { "xml", "csv", "cgml" };
		cmbFormat = new JComboBox<String>(types);
		// JTextField txtFileName = new JTextField("");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooserExport = new JFileChooser(new File(System
						.getProperty("user.home")));

				// creation of file filter for CSV/XML/CGML files
				FileFilter filter = new ExportFormatFilter();
				// Add filter to JFileChooser
				chooserExport.setAcceptAllFileFilterUsed(false);
				chooserExport.setFileFilter(filter);

				int rueckgabeWert = chooserExport.showDialog(null, "Speichern");

				// query whether button "Oeffnen" was clicked or not
				if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
					// output chosen file
					String filePath = chooserExport.getSelectedFile().getPath()
							+ "." + (String) cmbFormat.getSelectedItem();
					File exportFile = new File(filePath);
					if (!exportFile.exists()) {
						try {
							// build file on hard drive
							boolean isCreated = exportFile.createNewFile();
							// check whether file was builded or not
							if (isCreated) {
								ParserExport parserExport = new ParserExport();
								String exportEnding = (String) cmbFormat
										.getSelectedItem();
								try {
									if (exportEnding.equals("xml")) {
										parserExport.exportToXml(filePath);
									} else if (exportEnding.equals("csv")) {
										parserExport.exportToCsv(filePath);
									} else if (exportEnding.equals("cgml")) {
										parserExport.exportToCGML(filePath);
									}
									programInfo = filePath
											+ Main.newline + "wurde erfolgreich erstellt";
								} catch (ParserException pex) {
									programInfo = "Fehler beim Exportieren - Parserfehler: \n" + pex.getMessage();
								}

							} else {
								programInfo = filePath
										+ Main.newline +  "wurde nicht erfolgreich erstellt";
							}
						} catch (IOException ex) {
							// failure
							programInfo = filePath
									+ Main.newline + "wurde nicht erfolgreich erstellt";
							ex.printStackTrace();
						}
					}else{
						programInfo = filePath +"Diese Datei existiert schon und " +
								"wurde deshalb nicht ueberschrieben.";
					}
					PanelInformation.addNewTopic("Exportieren von Parser-Daten");
					PanelInformation.addProgrammInfo(programInfo + Main.newline);
				}
			}
		});

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START; // bottom of space
		constraints.gridx = 0;
		constraints.insets = new Insets(0, 5, 2, 0); // top padding
		constraints.gridy = 0;
		constraints.ipady = 10; // make this component tall
		this.add(lblFormat, constraints);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.ipady = 0;
		this.add(cmbFormat, constraints);
		//
		constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.ipady = 40; //make this component tall
		constraints.weightx = 0.0;
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 1;
		this.add(btnSave, constraints);

		JLabel label = new JLabel("");
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0; // reset to default
		constraints.weighty = 1.0; // request any extra vertical space
		constraints.anchor = GridBagConstraints.PAGE_END; // bottom of space
		constraints.insets = new Insets(10, 0, 0, 0); // top padding
		constraints.gridx = 1; // aligned with button 2
		constraints.gridwidth = 2; // 2 columns wide
		constraints.gridy = 2; // third row
		this.add(label, constraints);

		TitledBorder titledBorderGraphicOption;
		titledBorderGraphicOption = BorderFactory.createTitledBorder("Export");
		this.setBorder(titledBorderGraphicOption);

	}
}