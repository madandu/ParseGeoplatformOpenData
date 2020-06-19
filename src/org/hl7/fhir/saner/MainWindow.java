package org.hl7.fhir.saner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.hl7.fhir.saner.data.Context;
import org.hl7.fhir.saner.data.Context.Type;
import org.hl7.fhir.saner.data.Model;

/**
 * @author Madan Upadhyay
 * @email: madandu@gmail.com
 */
public class MainWindow extends JFrame implements ActionListener  {

	private static JComboBox<String> lstType;
	private static JComboBox<String> lstSource;
	private static JButton btnProc;;
	private static JFrame win;
	private static	String[][] aSources;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					win = new MainWindow();
					win.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
    public void actionPerformed(ActionEvent e) {
      
    	Object obj = e.getSource();
	    
        // JButton clicked
        if (obj instanceof JButton) {
            // Process button clicked.
            Context ctx = null;
			final String sSource = lstSource.getSelectedItem().toString();
			
			switch (lstType.getSelectedItem().toString()) {
			case "JSON":
				// Open and parse json file @ sourcePath on local file-system
				ctx = new Context(sSource, Context.Type.JSON);
				break;					
				// Get  and parse data from the REST url
			case "REST":
				ctx = new Context(sSource, Context.Type.REST);
				break;
			case "FHIR":
				// Get  and parse data from the FHIR server.
				ctx = new Context(sSource, Context.Type.FHIR);
				break;
			default:
				// Default
				ctx = new Context(sSource, Context.Type.JSON);
			}
			new ParserWorker(new Model(ctx)).execute();
		}
        else if (obj instanceof JMenuItem)
        {
        	//Exit window
			System.exit(0);
        }
        // List selected.
        else if (obj instanceof JComboBox)
        {
        	lstSource.setModel(new DefaultComboBoxModel<>(aSources[lstType.getSelectedIndex()]));
        }
	}
   
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		this.setTitle("Application to get and parse hospital-records");
		this.setMaximumSize(new Dimension(800, 320));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());  

		initializeMenus();
		initializeLists();
	
		btnProc = new JButton("Process-data");
		btnProc.setBackground(SystemColor.inactiveCaption);
		btnProc.setBounds(10, 20, 25, 40);
		btnProc.addActionListener(this);

		this.getContentPane().add(btnProc);
		// win.getContentPane().add(BorderLayout.SOUTH, jtaStatus);
		showWelcome();
		
		this.setSize(800, 240);
		this.setLocationRelativeTo(null);
		this.pack();
	}
	
	private void showWelcome()
	{
		JOptionPane.showMessageDialog(win,
				"Welcome to the Saner data-parser app." 
				+"\n\rPlease select data-type and data-source to find"
				+"\n\rcritical-resources in hospitals-records of Hifld-opendata.");
	}
	
	private void initializeMenus()
	{
		JMenuBar jmbMain = new JMenuBar();
		JMenu jmnOptions = new JMenu("Options");
		JMenuItem jmiExit = new JMenuItem("Exit");
		jmiExit.setHorizontalAlignment(SwingConstants.LEADING);
		jmnOptions.add(jmiExit);
		jmbMain.add(jmnOptions);
		jmiExit.addActionListener(this);
		this.setJMenuBar(jmbMain);
	}
	
	private void initializeLists()
	{
		JPanel pnlType = new JPanel();
		pnlType.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		pnlType.setBounds(15, 25, 100, 50);
		
		final String[] aTypes = {"JSON", "REST", "FHIR"};
		final DefaultComboBoxModel<String> tModel = new DefaultComboBoxModel<>(aTypes);
		
		lstType = new JComboBox<String>();
		lstSource = new JComboBox<String>();
		lstType.setModel(tModel);
		lstType.setSize(10, 20);
		
		String JSON[]= {"C:\\Users\\madan\\git\\ParseGeoplatformOpenData\\data\\hifld-geoplatform.opendata.arcgis.com.api.json"};
		String FHIR[] = { "http://nprogram.azurewebsites.net/Patient/1/?_format=json",
				"http://nprogram.azurewebsites.net/DiagnosticReport/1?_format=json",
				"http://fhir-dstu2-nprogram.azurewebsites.net/DiagnosticReport/10" };
		String REST[] = {"https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson"};
		aSources = new String[][]{JSON,REST,FHIR};
		
		lstType.addActionListener(this);		
		final DefaultComboBoxModel<String> sModel = new DefaultComboBoxModel<>(aSources[0]);
		lstSource.setModel(sModel);

		JLabel lblType = new JLabel("Data-type:");
		pnlType.add(lblType);
		pnlType.add(lstType);

		JPanel pnlSource = new JPanel();
		pnlSource.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		pnlSource.setBounds(15, 25, 300, 200);
		JLabel lblDS = new JLabel("Data-source:");
		pnlSource.add(lblDS);
		pnlSource.add(lstSource);

		this.getContentPane().add(pnlType);
		this.getContentPane().add(pnlSource);
	}
}
