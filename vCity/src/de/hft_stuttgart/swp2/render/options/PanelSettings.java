package de.hft_stuttgart.swp2.render.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.hft_stuttgart.swp2.render.options.FileChooserGmlFileView;
import de.hft_stuttgart.swp2.render.options.FileChooserGmlFilter;
import de.hft_stuttgart.swp2.render.options.FileChooserImagePreview;
import de.hft_stuttgart.swp2.render.threads.StartParserRunnable;

public class PanelSettings extends JPanel implements Refreshable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8682939653068331145L;
	private GridBagConstraints constraints = new GridBagConstraints();
	private JPanel panelDatabase = new JPanel();
	private String[] strChooseSource = { "Filesystem", "Datenbank"};
	private final JComboBox<String> cmbChooseSource = new JComboBox<String>(strChooseSource);
	private Thread parser;
	
	//Elements of panelFile
	private JPanel panelFile;
	private JButton btnFileChooser;
	String strBtnFileChooser = "GML-Datei auswählen";
	JFileChooser fc;
	File gmlFile;
	String strPathContent = "Sie haben noch keinen Pfad zu einer GML-Datei ausgewählt.";
	JLabel lblPath;
	



	public PanelSettings(){
		this.setLayout(new GridBagLayout());
		setContent();
	}

	private void setContent() {

		JLabel lblChooseSource = new JLabel("Quelle wählen");

		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 0.5;// components
		constraints.ipady = 10;
		constraints.weighty = 0;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.PAGE_START;
		//constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblChooseSource, constraints);
		//lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		constraints.ipady = 0;
		constraints.weightx = 0.5;// components
		//constraints.weighty = 0;   //request any extra vertical space
		constraints.gridx = 2; // column 0
		constraints.gridwidth = 1;
		constraints.gridy = 0; // row 0
		constraints.fill = GridBagConstraints.NONE;
		//constraints.anchor = GridBagConstraints.LINE_END;
		cmbChooseSource.setSelectedIndex(0);
		cmbChooseSource.addActionListener(chooseSourceAction());
		this.add(cmbChooseSource, constraints);
		

		generatePanelFile();
		constraints.insets = new Insets(10, 0, 0, 0);
		//constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 2;
		constraints.weightx = 0.5;// components
		//constraints.weighty = 0.1;   //request any extra vertical space
		constraints.gridx = 1; // column 0
		constraints.gridy = 1; // row 0
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(panelFile, constraints);
		
	}

	private ActionListener chooseSourceAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cmbChooseSource.getSelectedIndex() == 0){
					panelDatabase.setVisible(false);
					panelFile.setVisible(true);
					refresh();
				}else if(cmbChooseSource.getSelectedIndex() == 1){
					panelFile.setVisible(false);
					panelDatabase.setVisible(true);
					refresh();
				}else{
					panelDatabase.setVisible(false);
					panelFile.setVisible(true);	
					refresh();
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

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
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
					parser = new Thread(new StartParserRunnable(gmlFile.getPath()));
					parser.start();
					lblPath.setText(gmlFile.getPath());
					//Main.startParser(gmlFile.getPath());
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
