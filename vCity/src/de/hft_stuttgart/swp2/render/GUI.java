package de.hft_stuttgart.swp2.render;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI extends JFrame{

	/**
	 * 
	 */
	JPanel panelContent;
	JLabel lblPath;
	JLabel lblResolution;
	JButton btnFileChooser;
	String strPathContent = "Sie haben noch keinen Pfad zu einer GML-Datei ausgewählt.";
	String strBtnFileChooser = "Bitte wählen Sie hier Ihren Pfad zu einer GML-Datei aus";
	String strLblResolution = "Auflösung";
	JFileChooser fc;
	File gmlFile;
	JColorChooser tcc;
	Color colorBuilding = new Color(255,0,0);
	Color colorShadow;

	private static final long serialVersionUID = -2135256125525996134L;

	public GUI() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 400));
		this.setTitle("3D City Volume Calculating Tool");
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		setPanelContent();
		this.add(panelContent, BorderLayout.NORTH);
		this.pack();
	}

	private void setPanelContent() {
		GridBagConstraints constraints = new GridBagConstraints();
		panelContent = new JPanel();
		panelContent.setLayout(new GridBagLayout());
		
		constraints.insets = new Insets(20, 2, 5, 2); 												// components
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		btnFileChooser = new JButton(strBtnFileChooser);
		btnFileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFileChooser();
			}
		});
		panelContent.add(btnFileChooser, constraints);
		
		
		constraints.insets = new Insets(5, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 1;
		lblPath = new JLabel(strPathContent);
		panelContent.add(lblPath, constraints);
		
		
		constraints.insets = new Insets(50, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		lblResolution = new JLabel(strLblResolution);
		panelContent.add(lblResolution, constraints);
		constraints.insets = new Insets(62, 10, 0, 10);
		panelContent.add(new JSeparator(JSeparator.HORIZONTAL), constraints);
		constraints.insets = new Insets(0, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 3;
		JPanel panelResolution = new JPanel(new GridLayout(2,3));
		JLabel lblHoehe = new JLabel("Höhe");
		JLabel lblBreite = new JLabel("Breite");
		final JTextField txtHeight = new JTextField("768");
		final JTextField txtWidth = new JTextField("1024");
		txtHeight.addFocusListener(new Selection(txtHeight));
		txtWidth.addFocusListener(new Selection(txtWidth));
		panelResolution.add(lblBreite);
		panelResolution.add(new JLabel());
		panelResolution.add(lblHoehe);
		panelResolution.add(txtWidth);
		JLabel lblX = new JLabel("x");
		lblX.setHorizontalAlignment(JLabel.CENTER);
		panelResolution.add(lblX);
		panelResolution.add(txtHeight);
		panelContent.add(panelResolution, constraints);
		
		constraints.insets = new Insets(50, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 4;
		JLabel lbl3DRendering = new JLabel("3D - Einstellung");
		panelContent.add(lbl3DRendering, constraints);
		constraints.insets = new Insets(62, 10, 0, 10);
		panelContent.add(new JSeparator(JSeparator.HORIZONTAL), constraints);

		constraints.insets = new Insets(5, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.weightx = 2;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		JLabel lblStartCoordinate = new JLabel("Startkoordinate");
		JLabel lblY = new JLabel("y");
		JLabel lblZ = new JLabel("z");
		JLabel lblX2  = new JLabel("x");
		lblY.setHorizontalAlignment(JLabel.CENTER);
		lblZ.setHorizontalAlignment(JLabel.CENTER);
		lblX2.setHorizontalAlignment(JLabel.CENTER);
		JTextField txtX = new JTextField("0");
		JTextField txtY = new JTextField("0");
		JTextField txtZ = new JTextField("0");
		txtX.addFocusListener(new Selection(txtX));
		txtY.addFocusListener(new Selection(txtY));
		txtZ.addFocusListener(new Selection(txtZ));
		JPanel panelKoordinaten = new JPanel(new GridLayout(2,3));
		panelKoordinaten.add(lblX2);
		panelKoordinaten.add(lblY);
		panelKoordinaten.add(lblZ);
		panelKoordinaten.add(txtX);
		panelKoordinaten.add(txtY);
		panelKoordinaten.add(txtZ);
		panelContent.add(lblStartCoordinate, constraints);
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.gridy = 6;
		panelContent.add(panelKoordinaten, constraints);
		
		constraints.insets = new Insets(5, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.weightx = 1;
		String[] cameraStrings = { "Zentralprojektion", "Orthogonalprojektion"};
		JComboBox<String> cmbCameraPersective = new JComboBox<String>(cameraStrings);
		String strCamera = "Kameraperspektive";
		JLabel lblCamera = new JLabel(strCamera);
		panelContent.add(lblCamera, constraints);
		constraints.insets = new Insets(5, 150, 0, 10);
		panelContent.add(cmbCameraPersective, constraints);
		
		
		constraints.insets = new Insets(25, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.weightx = 1;
		JLabel lblColor = new JLabel("Farbeinstellungen");
		panelContent.add(lblColor, constraints);
		constraints.insets = new Insets(40, 10, 0, 10);
		panelContent.add(new JSeparator(JSeparator.HORIZONTAL), constraints);
		
		constraints.insets = new Insets(5, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.weightx = 1;
		JLabel lblBuilding= new JLabel("Gebäude");
		panelContent.add(lblBuilding, constraints);
		
		JPanel panelRGB = new JPanel(new GridLayout(2,4));
		JLabel lblR = new JLabel("R");
		JLabel lblG = new JLabel("G");
		JLabel lblB = new JLabel("B");
		JTextField txtR = new JTextField("255");
		JTextField txtG = new JTextField("0");
		JTextField txtB = new JTextField("0");
		JButton btnColorChooserBuilding = new JButton("Farbe auswählen");
		panelContent.add(btnColorChooserBuilding, constraints);
		panelRGB.add(lblR);
		panelRGB.add(lblG);
		panelRGB.add(lblB);
		panelRGB.add(new JPanel());
		panelRGB.add(txtR);
		panelRGB.add(txtG);
		panelRGB.add(txtB);
		panelRGB.add(btnColorChooserBuilding);
		constraints.insets = new Insets(5, 70, 0, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panelContent.add(panelRGB, constraints);
		btnColorChooserBuilding.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorBuilding = JColorChooser.showDialog(null, "Select a Background Color",
						colorBuilding);
				if (colorBuilding != null) {
					System.out.println(colorBuilding);
				}
			}
		});
		
		
		constraints.insets = new Insets(5, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.weightx = 1;
		JLabel lblShadow = new JLabel("Schatten");
		panelContent.add(lblShadow, constraints);
		
		JPanel panelRGB2 = new JPanel(new GridLayout(2,4));
		JLabel lblR2 = new JLabel("R");
		JLabel lblG2 = new JLabel("G");
		JLabel lblB2 = new JLabel("B");
		JTextField txtR2 = new JTextField("255");
		JTextField txtG2= new JTextField("0");
		JTextField txtB2 = new JTextField("0");
		JButton btnColorChooserBuilding2 = new JButton("Farbe auswählen");
		panelContent.add(btnColorChooserBuilding2, constraints);
		panelRGB2.add(lblR2);
		panelRGB2.add(lblG2);
		panelRGB2.add(lblB2);
		panelRGB2.add(new JPanel());
		panelRGB2.add(txtR2);
		panelRGB2.add(txtG2);
		panelRGB2.add(txtB2);
		panelRGB2.add(btnColorChooserBuilding2);
		constraints.insets = new Insets(5, 70, 0, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panelContent.add(panelRGB2, constraints);
		btnColorChooserBuilding2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorBuilding = JColorChooser.showDialog(null, "Select a Background Color",
						colorBuilding);
				if (colorBuilding != null) {
					System.out.println(colorBuilding);
				}
			}
		});
		
		
		constraints.insets = new Insets(35, 10, 0, 10);
		constraints.gridx = 0;
		constraints.gridy = 11;
		constraints.weightx = 1;
		JPanel panelCalculate = new JPanel(new GridLayout(1,2));
		JButton btnRender3D = new JButton("3D Rendering starten");
		btnRender3D.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.execute3DRendering(Integer.parseInt(txtWidth.getText()), 
						Integer.parseInt(txtHeight.getText()));
			}
		});
		panelCalculate.add(btnRender3D);
		JButton btnCalculateVolume = new JButton("Volumenberechnung");
		panelCalculate.add(btnCalculateVolume);
		panelContent.add(panelCalculate, constraints);

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
		int returnVal = fc.showDialog(GUI.this, "auswählen");

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
					Main.startParser(gmlFile.getPath());
					lblPath.setText(gmlFile.getPath());
					flag = true;
				}
			}
			if (!flag) {
				JOptionPane.showMessageDialog(null,
						"Please choose a txt file only.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// Reset the file chooser for the next time it's shown.
		fc.setSelectedFile(null);
	}

}
