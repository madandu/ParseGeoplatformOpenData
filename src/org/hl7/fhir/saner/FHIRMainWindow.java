package org.hl7.fhir.saner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.hl7.fhir.saner.data.Context;
import org.hl7.fhir.saner.data.Context.Type;
import org.hl7.fhir.saner.data.Model;


/**
 * @author Madan Upadhyay
 * @email: madandu@gmail.com
 */
public class FHIRMainWindow {

	private static JComboBox<String> cmbType;
	private static JComboBox<String> cmbSource;
	private static JButton btnProc;;
	private static JFrame win;
	private static String sourcePath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					win = new JFrame("Application to get and parse hospital-records");
					FHIRMainWindow window = new FHIRMainWindow();
					window.win.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FHIRMainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		win.setMaximumSize(new Dimension(800, 240));
		win.setSize(800, 240);
		win.setResizable(false);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.getContentPane().setLayout(new FlowLayout());

		JMenuBar jmbMain = new JMenuBar();
		JMenu jmnOptions = new JMenu("Options");
		JMenuItem jmiExit = new JMenuItem("Exit");
		jmiExit.setHorizontalAlignment(SwingConstants.LEADING);
		jmnOptions.add(jmiExit);
		jmbMain.add(jmnOptions);
		jmiExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		win.setJMenuBar(jmbMain);

		JPanel pnlType = new JPanel();
		pnlType.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlType.setBackground(SystemColor.inactiveCaption);
		pnlType.setBounds(15, 15, 100, 50);
		String TYPES[] = { "JSON", "REST", "FHIR" };
		final DefaultComboBoxModel<String> tyModel = new DefaultComboBoxModel<>(TYPES);
		cmbType = new JComboBox<String>();
		cmbType.setModel(tyModel);
		cmbType.setMaximumRowCount(3);
		cmbType.setSize(10, 20);
		cmbType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				switch (cmbType.getSelectedIndex()) {
				

				default:
					break;
				}
				if (cmbType.getSelectedItem() == Type.JSON.toString()) {
				}
				// TODO re-populate cmbSource
			}
		});

		JLabel lblType = new JLabel("Data-type:");
		pnlType.add(lblType);
		pnlType.add(cmbType);

		String JSON[]= {"C:\\Users\\madan\\git\\ParseGeoplatformOpenData\\data\\hifld-geoplatform.opendata.arcgis.com.api.json"};
		String FHIR[] = { "http://nprogram.azurewebsites.net/Patient/1/?_format=json",
				"http://nprogram.azurewebsites.net/DiagnosticReport/1?_format=json",
				"http://fhir-dstu2-nprogram.azurewebsites.net/DiagnosticReport/10" };
		String REST[] = {"https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson"};
		
		final DefaultComboBoxModel<String> dsModel = new DefaultComboBoxModel<>(REST);
		cmbSource = new JComboBox<String>();
		cmbSource.setModel(dsModel);
		cmbSource.setMaximumRowCount(5);
		cmbSource.setSize(20, 100);
		cmbSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbSource.getSelectedIndex() == 1) {
				}
				// TODO register-selection
			}
		});

		JPanel pnlSource = new JPanel();
		pnlSource.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlSource.setBackground(SystemColor.inactiveCaption);
		pnlSource.setForeground(SystemColor.activeCaption);
		pnlSource.setBounds(15, 15, 300, 200);
		JLabel lblDS = new JLabel("Data-source:");
		pnlSource.add(lblDS);
		pnlSource.add(cmbSource);

		win.getContentPane().add(BorderLayout.NORTH, pnlType);
		win.getContentPane().add(BorderLayout.NORTH, pnlSource);

		btnProc = new JButton("Process-data");
		btnProc.setBackground(SystemColor.inactiveCaption);
		btnProc.setBounds(10, 10, 10, 30);
		btnProc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// openAndAnalyzeJSONFile();
				Context ctx = null;
				final String sSource = cmbSource.getSelectedItem().toString();
				final String sType =cmbType.getSelectedItem().toString();
				
				switch (sType) {
				case "JSON":
					// Open and parse json file @ sourcePath on local file-system
					ctx = new Context(sSource, Type.JSON);
					break;					
					// Get  and parse data from the REST url
				case "REST":
					ctx = new Context(sSource, Type.REST);
					break;
				case "FHIR":
					// Get  and parse data from the FHIR server.
					ctx = new Context(sSource, Type.FHIR);
					break;
				default:
					// Default
					ctx = new Context(sSource, Type.JSON);
				}
				new ParserWorker(new Model(ctx)).execute();
			}
		});

		win.getContentPane().add(BorderLayout.CENTER, btnProc);
		// win.getContentPane().add(BorderLayout.SOUTH, jtaStatus);

		JOptionPane.showMessageDialog(win,
				"Welcome to the Saner data-parser app." 
				+"\n\rPlease select data-type and data-source to find"
				+"\n\rcritical-resources in hospitals-records of Hifld-opendata.");
		
		win.pack();
		win.setVisible(true);
	}
}
