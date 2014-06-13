package de.hft_stuttgart.swp2.render.options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;

import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.Selection;
import de.hft_stuttgart.swp2.render.city3d.CityMap3D;
import de.hft_stuttgart.swp2.render.threads.StartParserRunnable;

public class PanelSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8682939653068331145L;
	private final int DEFAULT_VALUE_HOURS = 12;
	private final int DEFAULT_VALUE_MINUTES = 0;
	private static final String strRecalculate = "Schatten neu berechnen";

	public static String getStrRecalculate() {
		return strRecalculate;
	}

	private GridBagConstraints constraints = new GridBagConstraints();
	private JPanel panelDatabase = new JPanel();
	private String[] strChooseSource = { "Filesystem", "Datenbank" };
	private final JComboBox<String> cmbChooseSource = new JComboBox<String>(
			strChooseSource);

	private Runnable StartParserRunnable;

	// Elements of panelFile
	private JPanel panelFile;
	private JPanel optionPanel;
	private JPanel panelShadowOptions;
	private JScrollPane jspPanelShadowOptions;
	JPanel panelTriangleChoice = new JPanel();

	private JButton btnFileChooser;
	final JButton btnInformation = new JButton("< Programminformationen");
	final JButton btnExport = new JButton("Export >");
	JTextField txtHours = new JTextField(String.valueOf(DEFAULT_VALUE_HOURS));
	JTextField txtMin = new JTextField(getMinutesToText(DEFAULT_VALUE_MINUTES));
	JTextField txtSplitAzimuth = new JTextField();
	JTextField txtSplitHeight = new JTextField();
	private Selection selectionHours = new Selection(txtHours);
	private Selection selectionMin = new Selection(txtMin);
	private Selection selectionAzimuth = new Selection(txtSplitAzimuth);
	private Selection selectionHeight = new Selection(txtSplitHeight);
	JLabel lblPrecision = new JLabel("Genauigkeit");
	private JComboBox<ShadowPrecision> cmbShadowPrecision;
	int tempAzimuth = Main.getSplitAzimuth();
	int tempHeight = Main.getSplitHeight();

	private final ButtonGroup groupFormView = new ButtonGroup();
	String strBtnFileChooser = "GML-Datei auswählen";
	JFileChooser fc;
	private String path;

	File gmlFile;
	Date userDate;
	String strPathContent = "Pfad zur GML Datei";
	JLabel lblPath;
	JCheckBox cbGUI = new JCheckBox("Stadt 3D gerendert anzeigen");
	JCheckBox cbVolume = new JCheckBox("Volumen berechnen");
	JCheckBox cbShadow = new JCheckBox("Schatten berechnen");
	JRadioButton jrbVolumeView = new JRadioButton("in Volumen-Ansicht wechseln");
	JRadioButton jrbShadowView = new JRadioButton(
			"in Schatten-Ansicht wechseln");

	public boolean isVolumeViewSelected() {
		if (jrbVolumeView.isSelected()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isShadowViewSelected() {
		if (jrbShadowView.isSelected()) {
			return true;
		} else {
			return false;
		}
	}

	public void setSelectVolumeView(boolean select) {
		groupFormView.setSelected(jrbVolumeView.getModel(), select);
	}

	public void setSelectShadowView(boolean select) {
		groupFormView.setSelected(jrbShadowView.getModel(), select);
	}

	public void setVolumeViewEnabled(boolean enabled) {
		jrbVolumeView.setEnabled(enabled);
	}

	public void setShadowViewEnabled(boolean enabled) {
		jrbShadowView.setEnabled(enabled);
	}

	public boolean isCbShadowIsSelected() {
		return cbShadow.isSelected();
	}

	JCheckBox cbShowGrid = new JCheckBox("Raster anzeigen");
	JCheckBox cbVolumeAmount = new JCheckBox("Volumen über Gebäude anzeigen");
	JXDatePicker jxDatePicker = new JXDatePicker(new Date());
	GregorianCalendar gc = new GregorianCalendar();
	private JButton btnStart;
	private JButton btnRecalculateShadow;
	public int hours = 12;
	public int minutes = 0;

	public void setHours(int hours) {
		this.hours = hours;
		setTime(userDate, hours, minutes);
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
		setTime(userDate, hours, minutes);
	}

	/**
	 * 
	 * @param userDate
	 * @param hours
	 * @param minutes
	 */
	public void setTime(Date userDate, int hours, int minutes) {
		this.userDate = userDate;
		this.hours = hours;
		this.minutes = minutes;
		if (userDate != null) {
			updateTime(userDate, hours, minutes);
		} else {
			setDefaultTime();
			updateTime(userDate, hours, minutes);
		}
	}

	private void updateTime(Date userDate, int hours, int minutes) {
		gc.setTime(userDate);
		try {
			gc.set(gc.get(GregorianCalendar.YEAR),
					gc.get(GregorianCalendar.MONTH),
					gc.get(GregorianCalendar.DAY_OF_MONTH), hours, minutes);
			updateTimeUI();
		} catch (Exception e1) {
			gc.set(gc.get(GregorianCalendar.YEAR),
					gc.get(GregorianCalendar.MONTH),
					gc.get(GregorianCalendar.DAY_OF_MONTH), 12, 0, 0);
			updateTimeUI();
		}
		int day = gc.get(Calendar.DAY_OF_MONTH);
		int month = gc.get(Calendar.MONTH);
		Main.getCityMap3D().initialSunPosition(month, day);
	}

	private String getHoursToText(int hours) {
		if (hours <= 24) {
			return String.valueOf(hours);
		} else {
			return String.valueOf(0);
		}
	}

	private String getMinutesToText(int minutes) {
		for (int i = 0; i < 10; i++) {
			if (minutes == i) {
				return "0" + String.valueOf(i);
			}
		}
		return String.valueOf(minutes);
	}

	public void setMinutesInitialNull() {
		txtMin.setText(getMinutesToText(gc.get(GregorianCalendar.MINUTE)));
	}

	private void updateTimeUI() {
		try {
			txtHours.setText(String.valueOf(gc
					.get(GregorianCalendar.HOUR_OF_DAY)));
			txtMin.setText(getMinutesToText(gc.get(GregorianCalendar.MINUTE)));
			jxDatePicker.setDate(gc.getTime());
		} catch (Exception e) {
			jxDatePicker.setDate(gc.getTime());
		}

	}

	public GregorianCalendar getTime() {
		if (userDate != null) {
			gc.setTime(userDate);
			try {
				gc.set(gc.get(GregorianCalendar.YEAR),
						gc.get(GregorianCalendar.MONTH),
						gc.get(GregorianCalendar.DAY_OF_MONTH), hours, minutes);
				return gc;
			} catch (Exception e1) {
				gc.set(gc.get(GregorianCalendar.YEAR),
						gc.get(GregorianCalendar.MONTH),
						gc.get(GregorianCalendar.DAY_OF_MONTH), 12, 0, 0);
			}
			;
			return gc;
		} else {
			setDefaultTime();
			return gc;
		}

	}

	private void setDefaultTime() {
		Date now = new Date();
		gc.setTime(now);
		minutes = 0;
		hours = 12;
		gc.set(gc.get(Calendar.YEAR), gc.get(GregorianCalendar.MONTH),
				gc.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		userDate = gc.getTime();

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

	public PanelSettings() {
		this.setLayout(new GridBagLayout());
		setContent();
	}

	private void setContent() {
		constraints.insets = new Insets(0, 10, 0, 0);
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1.0;// components
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 0.3; // request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.gridwidth = 2;
		JPanel panelParser = generatePanelParser();
		this.add(panelParser, constraints);

		TitledBorder titledBorderOption;
		titledBorderOption = BorderFactory.createTitledBorder("Optionen");
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		constraints.weighty = 0.7; // request any extra vertical space
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.PAGE_END; // bottom of space
		constraints.fill = GridBagConstraints.BOTH;
		setJPanelOptions();
		optionPanel.setBorder(titledBorderOption);
		this.add(optionPanel, constraints);

	}

	private JPanel generatePanelParser() {
		JPanel panelParser = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		JLabel lblChooseSource = new JLabel("Quelle wählen");
		constraints.insets = new Insets(0, 10, 0, 0);
		constraints.ipady = 15;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;// components
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1.0; // request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.gridwidth = 1;
		lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		panelParser.add(lblChooseSource, constraints);
		// lblChooseSource.setHorizontalAlignment(SwingConstants.LEFT);
		// constraints.weighty = 0; //request any extra vertical space
		constraints.gridx = 1; // column 0
		constraints.gridwidth = 1;
		constraints.ipady = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridy = 0; // row 0
		// constraints.fill = GridBagConstraints.NONE;
		// constraints.anchor = GridBagConstraints.LINE_END;
		cmbChooseSource.setSelectedIndex(0);
		cmbChooseSource.addActionListener(chooseSourceAction());
		panelParser.add(cmbChooseSource, constraints);

		generatePanelFile();
		generatePanelDataBase();
		// constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		// constraints.weighty = 0.1; //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		constraints.ipady = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		if (cmbChooseSource.getSelectedItem().equals("Filesystem")) {
			panelParser.add(panelFile, constraints);
		} else if (cmbChooseSource.getSelectedItem().equals("Datenbank")) {
			panelParser.add(panelDatabase, constraints);
		} else {
			panelParser.add(panelFile, constraints);
		}
		return panelParser;
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
		constraints.weighty = 1.0; // request any extra vertical space

		TitledBorder titledBorderGraphicOption;
		titledBorderGraphicOption = BorderFactory
				.createTitledBorder("Grafische Optionen");
		JPanel panelGraphic = new JPanel();
		panelGraphic.setBorder(titledBorderGraphicOption);
		panelGraphic.setLayout(new GridLayout(5, 1));
		panelGraphic.add(cbGUI);
		panelGraphic.add(cbShowGrid);
		panelGraphic.add(cbVolumeAmount);
		panelGraphic.add(jrbVolumeView);
		panelGraphic.add(jrbShadowView);

		groupFormView.add(jrbVolumeView);
		groupFormView.add(jrbShadowView);
		jrbVolumeView.setEnabled(false);
		jrbShadowView.setEnabled(false);

		CheckBoxListener cbListener = new CheckBoxListener();
		cbGUI.setSelected(true);
		cbGUI.addItemListener(cbListener);
		cbShowGrid.setSelected(true);
		cbShowGrid.addItemListener(cbListener);
		cbVolumeAmount.setSelected(true);
		cbVolumeAmount.addItemListener(cbListener);
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.weighty = 0.2;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		optionPanel.add(panelGraphic, constraints);

		cbVolume.setSelected(true);
		cbVolume.addItemListener(cbListener);
		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		constraints.weighty = 0.1;
		optionPanel.add(cbVolume, constraints);

		cbShadow.setSelected(false);
		cbShadow.addItemListener(cbListener);
		constraints.gridx = 0; // column 0
		constraints.gridy = 2; // row 0
		constraints.weighty = 0.1;
		optionPanel.add(cbShadow, constraints);

		generatePanelShadowOptions();
		constraints.gridx = 0; // column 0
		constraints.gridy = 3; // row 0
		constraints.ipady = 80;
		constraints.weighty = 4.0; // request any extra vertical space
		panelShadowOptions.setPreferredSize(new Dimension(200, 300));
		jspPanelShadowOptions = new JScrollPane(panelShadowOptions);
		jspPanelShadowOptions.setMinimumSize(new Dimension(150, 100));
		jspPanelShadowOptions.setWheelScrollingEnabled(true);
		jspPanelShadowOptions.getVerticalScrollBar().setUnitIncrement(14); 
		
		optionPanel.add(jspPanelShadowOptions, constraints);
		if (!cbShadow.isSelected()) {
			panelShadowOptions.setVisible(false);
			jspPanelShadowOptions.setVisible(false);
		}

		return optionPanel;
	}

	public void setCbVolumeAmount(Boolean isVisible) {
		cbVolumeAmount.setSelected(isVisible);
		Main.getCityMap3D().setShowVolumeAmount(isVisible);
	}

	private void generatePanelShadowOptions() {
		panelShadowOptions = new JPanel();
		TitledBorder titledBorderShadow;
		titledBorderShadow = BorderFactory
				.createTitledBorder("Schatten-Optionen");
		panelShadowOptions.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1.0;// components
		constraints.ipady = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1.0; // request any extra vertical space
		jxDatePicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userDate = jxDatePicker.getDate();
				hours = getHours();
				minutes = getMinutes();
				setTime(userDate, hours, minutes);
			}
		});

		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		panelShadowOptions.add(jxDatePicker, constraints);
		JPanel panelTime = new JPanel();
		panelTime.setLayout(new GridLayout(4, 2));
		JLabel lblHours = new JLabel("Stunden");
		JLabel lblMin = new JLabel("Minuten");

		panelTime.add(lblHours);
		panelTime.add(lblMin);
		txtHours.addFocusListener(selectionHours);

		txtHours.addKeyListener(getKeyListenerHours());
		txtMin.addFocusListener(selectionMin);
		txtMin.addKeyListener(getKeyListenerMinutes());
		panelTime.add(txtHours);
		panelTime.add(txtMin);

		panelTime.add(new JLabel("Unterteilung in:"));
		panelTime.add(new JLabel());
		final JRadioButton jrbPolygon = new JRadioButton("Polygone");
		JRadioButton jrbTriangle = new JRadioButton("Dreiecke");
		final ButtonGroup groupForms = new ButtonGroup();
		groupForms.add(jrbPolygon);
		groupForms.add(jrbTriangle);
		groupForms.setSelected(jrbPolygon.getModel(), true);

		ActionListener l = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (groupForms.getSelection() == jrbPolygon.getModel()) {
					Main.getCityMap3D().drawPolygons = true;
				} else {
					Main.getCityMap3D().drawPolygons = false;
				}
			}
		};
		jrbPolygon.addActionListener(l);
		jrbTriangle.addActionListener(l);

		panelTime.add(jrbTriangle);
		panelTime.add(jrbPolygon);

		constraints.gridx = 0; // column 0
		constraints.gridy = 1; // row 0
		panelShadowOptions.add(panelTime, constraints);

		panelTriangleChoice.setLayout(new GridLayout(1, 2));
		panelTriangleChoice.setVisible(true);
		cmbShadowPrecision = new JComboBox<ShadowPrecision>(
				ShadowPrecision.values());
		// groupForms.isSelected(jrbTriangle.getModel());
		panelTriangleChoice.add(lblPrecision);
		panelTriangleChoice.add(cmbShadowPrecision);
		constraints.gridx = 0; // column 0
		constraints.gridy = 2; // row 0
		panelShadowOptions.add(panelTriangleChoice, constraints);

		constraints.gridx = 0; // column 0
		constraints.gridy = 3; // row 0
		JPanel panelAzimuthAndHeight = new JPanel();
		panelAzimuthAndHeight.setLayout(new GridLayout(2, 2));
		JLabel lblSplitAzimuth = new JLabel("Azimuthwinkel");
		JLabel lblSplitHeight = new JLabel("Hoehenwinkel");
		panelAzimuthAndHeight.add(lblSplitAzimuth);
		panelAzimuthAndHeight.add(lblSplitHeight);
		txtSplitAzimuth.addFocusListener(selectionAzimuth);
		txtSplitHeight.addFocusListener(selectionHeight);

		txtSplitAzimuth.setText(String.valueOf(Main.getSplitAzimuth()));
		txtSplitHeight.setText(String.valueOf(Main.getSplitHeight()));

		panelAzimuthAndHeight.add(txtSplitAzimuth);
		panelAzimuthAndHeight.add(txtSplitHeight);
		panelShadowOptions.add(panelAzimuthAndHeight, constraints);

		btnRecalculateShadow = new JButton("Schatten berechnen");

		btnRecalculateShadow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// if (City.getInstance().getBuildings().size() == 0) {
				// return;
				// }
				recalculateShadow();
			}
		});

		panelShadowOptions.setBorder(titledBorderShadow);

		constraints.gridx = 0; // column 0
		constraints.gridy = 4; // row 0
		panelShadowOptions.add(btnRecalculateShadow, constraints);
	}

	public JButton getBtnRecalculateShadow() {
		return btnRecalculateShadow;
	}

	private void recalculateShadow() {
		if (!Main.isParserSuccess()) {
			startParser(true);
		}
		ShadowPrecision prec = (ShadowPrecision) cmbShadowPrecision
				.getSelectedItem();
		Main.getCityMap3D().setStartShadowCalculationRunnable(prec,
				Integer.parseInt(txtSplitAzimuth.getText()),
				Integer.parseInt(txtSplitHeight.getText()));
		Main.setSplitHeight(Integer.parseInt(txtSplitHeight.getText()));
		Main.setSplitAzimuth(Integer.parseInt(txtSplitAzimuth.getText()));
		if (Main.getCityMap3D().isFirstTimeShadowCalc()) {
			Main.executor.execute(Main.startShadowCalculationRunnable);
		} else {
			Main.getCityMap3D().calculation();
		}
		// Main.executor.execute(Main.startShadowCalculationRunnable);
		Main.getCityMap3D().setRecalculateShadow(true);
		setSelectShadowView(true);
		if (Main.isParserSuccess()) {
			btnRecalculateShadow.setText("Neu rechnen");
		}
	}

	private KeyListener getKeyListenerMinutes() {
		return new KeyListener() {
			public boolean verifyMinutes() {
				String regEx = "0*(([0-5][0-9])|([0-9]))";
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern.matcher(selectionMin.textKomponente
						.getText());
				return matcher.matches();
			}

			private void setMinutes() {
				int minutes;
				try {
					if (verifyMinutes()) {
						minutes = Integer.parseInt(selectionMin.textKomponente
								.getText());
						Main.getOptionGUI().setMinutes(minutes);
					}
				} catch (Exception e) {
					// TODO Ausgabe Fehler
					Main.getOptionGUI().setMinutes(0);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					setMinutes();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
					setMinutes();
					setMinutesInitialNull();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
	}

	@SuppressWarnings("unused")
	private KeyListener getKeyListenerAzimuth() {
		return new KeyListener() {
			public boolean verifyAzimuth() {
				String regEx = "[1-9](([0-9])*)";
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern
						.matcher(selectionAzimuth.textKomponente.getText());
				return matcher.matches();
			}

			private void setAzimuth() {
				try {
					if (verifyAzimuth()) {
						tempAzimuth = Integer
								.parseInt(selectionAzimuth.textKomponente
										.getText());
						// Main.getCityMap3D().setStartShadowCalculationRunnable(defaultShadowPrecision,
						// splitAzimuth, splitHeight)
					}
				} catch (Exception e) {
					// TODO Ausgabe Fehler
					Main.getOptionGUI().setMinutes(0);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					setAzimuth();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
					setAzimuth();
					setMinutesInitialNull();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
	}

	private KeyListener getKeyListenerHours() {
		return new KeyListener() {
			public boolean verifyHours() {
				String regEx = "0*(([0-1][0-9])|([2][0-4])|([0-9]))";
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern.matcher(selectionHours.textKomponente
						.getText());
				// if(matcher.matches()){
				// txtHours.setForeground(Color.BLACK);
				// }else{
				// txtHours.setForeground(Color.RED);
				// }
				return matcher.matches();
			}

			private void setHour() {
				int hour;
				if (!selectionHours.textKomponente.getText().isEmpty()) {
					hour = Integer.parseInt(selectionHours.textKomponente
							.getText());
				} else {
					hour = 0;
				}

				try {
					if (verifyHours()) {
						Main.getOptionGUI().setHours(hour);
					} else {
						txtHours.setText(getHoursToText(hour));
					}
				} catch (Exception e) {
					// TODO Ausgabe Fehler
					Main.getOptionGUI().setHours(12);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					setHour();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
					setHour();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
	}

	private ActionListener actionStartParsing() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startParser(false);
			}
		};
	}

	private ActionListener chooseSourceAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbChooseSource.getSelectedIndex() == 0) {
					panelDatabase.setVisible(false);
					panelFile.setVisible(true);
				} else if (cmbChooseSource.getSelectedIndex() == 1) {
					panelFile.setVisible(false);
					panelDatabase.setVisible(true);
				} else {
					panelDatabase.setVisible(false);
					panelFile.setVisible(true);
				}
			}
		};
	}

	private void startParser(boolean isShadowCalculationButtonPressed) {
		try {
			if (!gmlFile.getPath().isEmpty()) {
				lblPath.setForeground(Color.black);
				StartParserRunnable = new StartParserRunnable(gmlFile.getPath());
				// threadStartParsing = new Thread(StartParserRunnable);
				// threadStartParsing.start();
				Main.executor.execute(StartParserRunnable);
				path = gmlFile.getPath();
			} else {
				lblPath.setText("Kein gültiger Pfad");
				lblPath.setForeground(Color.RED);
			}
		} catch (Exception e1) {
			lblPath.setText("Kein gültiger Pfad");
			lblPath.setForeground(Color.RED);
		}
		Main.getCityMap3D().setStartShadowCalculationRunnable(
				(ShadowPrecision) cmbShadowPrecision.getSelectedItem(),
				Integer.parseInt(txtSplitAzimuth.getText()),
				Integer.parseInt(txtSplitHeight.getText()));
	}

	private void generatePanelFile() {
		panelFile = new JPanel();
		panelFile.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1.0;// components
		constraints.weighty = 1.0; // request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		// constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.BOTH;
		btnFileChooser = new JButton(strBtnFileChooser);
		btnFileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFileChooser();
			}
		});
		panelFile.add(btnFileChooser, constraints);

		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.gridx = 0;
		constraints.gridy = 1;
		lblPath = new JLabel(strPathContent);
		panelFile.add(lblPath, constraints);

		constraints.gridx = 0; // column 0
		constraints.gridy = 2; // row 0
		constraints.fill = GridBagConstraints.BOTH;

		btnStart = new JButton("Start");
		btnStart.addActionListener(actionStartParsing());
		panelFile.add(btnStart, constraints);

		constraints.gridx = 0; // column 0
		constraints.gridy = 3; // row 0
		constraints.insets = new Insets(10, 0, 0, 0);

		btnExport.setEnabled(false);
		btnExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point point = Main.getOptionGUI().getLocation();
				if (!Main.getOptionGUI().isPanelExportVisible()) {

					Main.getOptionGUI().setLocation(point.x - 135, point.y);
					openPanelExport();
					if (Main.getOptionGUI().isPanelInformationVisible()) {
						Main.getOptionGUI().setLocation(point.x + 350-135, point.y);
					}
					closePanelInformation();
				} else {
					Main.getOptionGUI().setLocation(point.x + 135, point.y);
					closePanelExport();
				}

			}
		});
		panelFile.add(btnExport, constraints);

		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.gridx = 0; // column 0
		constraints.gridy = 4; // row 0
		btnInformation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point point = Main.getOptionGUI().getLocation();
				if (!Main.getOptionGUI().isPanelInformationVisible()) {

					Main.getOptionGUI().setLocation(point.x - 350, point.y);
					openPanelInformation();
					if (Main.getOptionGUI().isPanelExportVisible()) {
						Main.getOptionGUI().setLocation(point.x + 135-350, point.y);
					}
					closePanelExport();

				} else {
					Main.getOptionGUI().setLocation(point.x + 350, point.y);
					closePanelInformation();
				}
			}
		});
		panelFile.add(btnInformation, constraints);
	}

	public void setBtnExportEnabled(boolean enabled) {
		btnExport.setEnabled(enabled);
	}

	private void closePanelExport() {
		Main.getOptionGUI().setPanelExportVisible(false);
		Main.getOptionGUI().removePanelExport();
		btnExport.setText("< Export");
	}

	private void openPanelExport() {
		Main.getOptionGUI().addPanelExport();
		Main.getOptionGUI().setPanelExportVisible(true);
		btnExport.setText("> Export");
	}

	private void closePanelInformation() {
		Main.getOptionGUI().setPanelInformationVisible(false);
		Main.getOptionGUI().removePanelInformation();
		btnInformation.setText("< Programminformationen");
	}

	private void openPanelInformation() {
		Main.getOptionGUI().addPanelInformation();
		Main.getOptionGUI().setPanelInformationVisible(true);
		btnInformation.setText("> Programminformationen");
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

	private class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getSource();
			if (source == cbGUI) {
				if (cbGUI.isSelected()) {
					if (Main.getCityMap3D() == null) {
						Main.createGUI();
					}
				} else {
					if (Main.getCityMap3D() != null) {
						Main.getCityMap3D().dispose();
						Main.setCityMap3DToNull();
					}
				}
			} else if (source == cbShadow) {
				if (cbShadow.isSelected()) {
					panelShadowOptions.setVisible(true);
					jspPanelShadowOptions.setVisible(true);
					Main.getOptionGUI().refresh();
				} else {
					panelShadowOptions.setVisible(false);
					jspPanelShadowOptions.setVisible(false);
					Main.getOptionGUI().refresh();
				}
			} else if (source == cbVolume) {
				if (cbVolume.isSelected()) {
					if (!Main.getCityMap3D().isFirstTimeVolumeCalc()) {
						if (Main.isParserSuccess()) {
							Main.executor
									.execute(Main.startVolumeCalculationRunnable);
						}
					}
				}
			} else if (source == cbVolumeAmount) {
				if (cbVolumeAmount.isSelected()
						&& Main.getCityMap3D().isFirstTimeVolumeCalc()) {
					Main.getCityMap3D().setShowVolumeAmount(true);
				} else {
					Main.getCityMap3D().setShowVolumeAmount(false);
				}
			} else if (source == cbShowGrid) {
				if (cbShowGrid.isSelected()) {
					if (Main.isParserSuccess()) {
						Main.getCityMap3D().setShowGrid(true);
					}
				} else {
					Main.getCityMap3D().setShowGrid(false);
				}
			}
		}
	}

	public String getPath() {
		return path;
	}

}
