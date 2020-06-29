package org.hl7.fhir.saner;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.hl7.fhir.saner.data.Context;
import org.hl7.fhir.saner.data.Model;
import org.hl7.fhir.saner.parser.HIFLDOpenDataParser;

/**
 * @author Madan Upadhyay
 * @email: madandu@gmail.com
 */
public class MainWindow extends JFrame implements ActionListener  {

	/**
	 * Default version-ID
	 */
	private static final long serialVersionUID = 1L;
	
	private static JComboBox<String> lstType;
	private static JComboBox<String> lstSource;
	private static JButton btnProc;;
	private static	String[][] aSources;
	private static	GridBagConstraints gbc;
	private static JFrame win;

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
					gbc = new GridBagConstraints();
					win = new MainWindow();
					win.setAlwaysOnTop(true);
					win.setLocationByPlatform(true);
					win.setLocationRelativeTo(null);
					win.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setTitle("Analyze data source for critical-resources in hospital.");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		 
		initializeMenus();
		initializeLists();
	
		btnProc = new JButton("Analyze-data");
		btnProc.addActionListener(this);
		gbc.fill = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		this.getContentPane().add(btnProc, gbc);
		
		this.pack();
		
		showWelcome();
	}
	
	private void showWelcome() {
		JOptionPane.showMessageDialog(win,
				"Welcome to the Saner data-analysis App." 
				+"\n\rPlease select Hi-fld-opendata type & source"
				+"\n to find critical-resources in USA-hospitals.");
	}
	
	private void initializeMenus() {
		JMenuBar jmbMain = new JMenuBar();
		JMenu jmnOptions = new JMenu("Options");
		JMenuItem jmiExit = new JMenuItem("Exit");
		jmnOptions.add(jmiExit);
		jmbMain.add(jmnOptions);
		jmiExit.addActionListener(this);
		this.setJMenuBar(jmbMain);
	}
	
	private void initializeLists() {		
		JLabel lblType = new JLabel("Data-type: ", JLabel.TRAILING);
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 1;
		gbc.insets = new Insets(15, 10, 10, 0);
		this.getContentPane().add(lblType, gbc);
		
		List<Context.Type> types = Arrays.asList(Context.Type.values());			
		final String[] aTypes = {types.get(0).name(),  types.get(1).name()}; //, "FHIR"
		final DefaultComboBoxModel<String> tModel = new DefaultComboBoxModel<>(aTypes);
		lstType = new JComboBox<String>();
		lstType.setModel(tModel);
		lstType.addActionListener(this);		
	    gbc.gridx = 1;
	    gbc.gridy = 0;
	    gbc.gridwidth = 2;
		gbc.insets = new Insets(15, 2, 10, 10);
		this.getContentPane().add(lstType, gbc); 

		JLabel lblSource = new JLabel("Data-source: ", JLabel.TRAILING);
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 10, 10, 0);
		this.getContentPane().add(lblSource, gbc);
	    
		String JSON[]= this.getDataList();
		String REST[] = {"https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson"};	
		aSources = new String[][]{JSON, REST}; //, FHIR	
		final DefaultComboBoxModel<String> sModel = new DefaultComboBoxModel<>(aSources[0]);
		
		lstSource = new JComboBox<String>();
		lstSource.setModel(sModel);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 2, 10, 10);
		this.getContentPane().add(lstSource, gbc);
	}
	
	private String[] getDataList() {
		String[] rlist = null;
		String dataPath = System.getProperty("user.dir") + File.separator+"data";
		try {
			 	File f = new File(dataPath);
			 	String[] list = f.list((d, s) -> {
			 		return s.toLowerCase().endsWith(".json");
			 		});	
			 		
			 		rlist = new String[list.length];
			 		for (int i = 0; i < list.length; i++) {
			 			rlist[i] = dataPath  + File.separator+ list[i];
			 		}
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
			return rlist;
	}
	
	  public void actionPerformed(ActionEvent e) {
	    	Object obj = null;
			MainWindow.win.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    	
			try {
	    		 obj = e.getSource();
	  
	    		 if (obj instanceof JButton) {   // Handle Process-button click
	    				Context.Type dType = Context.Type.valueOf(lstType.getSelectedItem().toString());
	    				String dSource = lstSource.getSelectedItem().toString();
	    		
    					final Context ctx = new Context(dSource, dType);
	    				final Model model = new Model(ctx);
	    				// Start parsing...
	    				 new ParserWorker(model).run();
	    			}
	    	        else if (obj instanceof JComboBox)    // Handle data-type selection.
	    	        	lstSource.setModel(new DefaultComboBoxModel<>(aSources[lstType.getSelectedIndex()]));
	    	        else if (obj instanceof JMenuItem) // Handle 'Exit' menu-click.
	    				System.exit(0);  //Exit window
	    		 
			} finally {			
				win.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
				}
		}
	
	/* A worker class to do data-parsing as background job.
	 * */		
	public class ParserWorker extends SwingWorker<String, Integer> {
		private Model model; 
		
		public ParserWorker(Model data){
		this.model = data;
		}
		
		/**
		 * Load and parse HIFLD open-data of USA based hospitals in background.
		 */
		 @Override
		protected String doInBackground() {
			String result = "";
					try {
						final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(this.model);
						MainWindow.win.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						result = hldParser.parseData();
					} finally {
						MainWindow.win.setCursor(Cursor.getDefaultCursor());
						JOptionPane.showMessageDialog(MainWindow.win, result);
					}
					return result;
		} 
	}
}
