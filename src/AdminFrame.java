import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.proteanit.sql.DbUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class AdminFrame extends JFrame {

	private sqliteQueryClass queryExecuter = new sqliteQueryClass();
	private JPanel contentPane;
	private JTable peopleTable;
	
	public UserBindingModel selectedUser;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	JRadioButton rdbtnJson;
	private JTextField Id;
	private JTextField Name;
	private JTextField SurName;
	private JTextField Username;
	private JTextField Password;
	private JTextField City;
	private JTextField IsAdmin;
	private JTextField Money;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminFrame frame = new AdminFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void GetPeopleData(){
		try {
			ResultSet rs;
			rs = queryExecuter.GetPeopleData();
			peopleTable.setModel(DbUtils.resultSetToTableModel(rs));
			rs.close();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error when getting People data");		
		}
	}
	
	public void SelectPerson(){
		try{
			peopleTable.setSelectionBackground(new Color(255,255,0));
			int row = peopleTable.getSelectedRow();
			int Id = (int) peopleTable.getModel().getValueAt(row, 0) ;
			String name = (String) peopleTable.getModel().getValueAt(row, 1);
			String surName = (String) peopleTable.getModel().getValueAt(row, 2);
			String userName = (String) peopleTable.getModel().getValueAt(row, 3);
			String password = (String) peopleTable.getModel().getValueAt(row, 4);
			String city = (String) peopleTable.getModel().getValueAt(row, 5);
			boolean isAdmin = Boolean.parseBoolean((String) peopleTable.getModel().getValueAt(row, 6)) ;
			double money = (double) peopleTable.getModel().getValueAt(row, 7);
			selectedUser = new UserBindingModel(Id,name,surName,userName,password,city,isAdmin,money);
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error at taking Product information");		
		}
	}

	public void exportData(){
		if(selectedUser == null){
			JOptionPane.showMessageDialog(null, "Please select a user");	
		}else{
			
			if(rdbtnJson.isSelected()){
				//Turn object to  string XML format
				GsonBuilder builder = new GsonBuilder();
		        Gson gson = builder.create();
		        String jsonData = "";
		        jsonData = gson.toJson(selectedUser);
		        
		        //Create or Update the file 
		        CreateOrUpdateFile(selectedUser.name + ".json",jsonData);					
			}else{
				try {
					//Turn object to  string XML format
					java.io.StringWriter sw = new StringWriter();
					JAXBContext jc = JAXBContext.newInstance(UserBindingModel.class); 
					Marshaller m = jc.createMarshaller();
					String xmlData = "";
					m.marshal( selectedUser, sw);
					xmlData = sw.toString(); 
					
					//Create or Update the file 
					CreateOrUpdateFile(selectedUser.name + ".xml",xmlData);
					
				} catch (JAXBException e) {
					System.out.println("there was a problem with reading/writing from the file");
				}
			}
		}
	}
	
	public void CreateOrUpdateFile(String fileName,String data){
		//Create or Update the file 
		boolean fileIsCreated = false;
        File file = new File(fileName);
		try {
			fileIsCreated = file.createNewFile();
			if(!fileIsCreated){
				System.out.println("File Already exists and has been updated");
			}else {
				System.out.println("File Has been created");
			}
		} catch (IOException ex) {
			System.out.println("There was a problem with creating the file");
			ex.printStackTrace();
		}
		
		//Write the data into the file
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file));){					
			writer.write(data);
			writer.flush();					
		}catch(FileNotFoundException ex){
			System.out.println("File can't be found");
		}catch(IOException ex){
			System.out.println("there was a problem with reading/writing from the file");
		}	
	}
	
	public void DeleteUser() {
		try {
			if(selectedUser != null){
				queryExecuter.DeleteUser(selectedUser.id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void AddUser() {
		try {
			int id = Integer.parseInt(Id.getText());
			String name = Name.getText();
			String surName = SurName.getText();
			String username = Username.getText();
			String password = Password.getText();
			String city = City.getText();
			String isadmin =  IsAdmin.getText();//Boolean.parseBoolean(
			double money = Double.parseDouble(Money.getText());
			
			queryExecuter.AddUser(id,name,surName,username,password,city,isadmin,money);
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Wrong Id,IsAdmin or Money Field");	
		}catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Wrong type of Infromation");	
		}
	}
	
	public void importData(){
		JFileChooser chooser = new JFileChooser();
		String filename = "";
		chooser.showOpenDialog(null);
		File f = chooser.getSelectedFile();
		if(f != null){
			 filename = f.getAbsolutePath();
			 //here if for xml and json
			 try {
					FileReader reader = new FileReader(filename);
					BufferedReader br = new BufferedReader(reader);
					String line = br.readLine();
					String information = line;
					while(line != null){
						line = br.readLine();
						information += line;
					}
					br.close();
					//new String(Files.readAllBytes(path)) //reads the file
					if(rdbtnJson.isSelected()){
						 GsonBuilder builder = new GsonBuilder();
					        Gson gson = builder.create();
					        UserBindingModel createdUser = null ;
					        createdUser = gson.fromJson(information, (java.lang.reflect.Type) createdUser);
					        System.out.println(createdUser.name);
					}else{
						 try {
							 String xml = information;
							 JAXBContext jaxbContext = JAXBContext.newInstance(UserBindingModel.class);
							 Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

							 Source source = new StreamSource(xml);
							 //JAXBElement<UserBindingModel> person =  (JAXBElement<UserBindingModel>) unmarshaller.unmarshal(source);
							 JAXBElement<UserBindingModel> person = unmarshaller.unmarshal(source,
									   UserBindingModel.class);
							 
							 UserBindingModel foo = person.getValue();
							 System.out.println("First Name:- " +  foo.name);
							/* 
							   UserBindingModel person = (UserBindingModel)je.getValue();
							   */
							  } catch (JAXBException e) {
							   e.printStackTrace();
							  }
					}
					
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "There was an error with reading the file");
				}
		}
	
	}
	/**
	 * Create the frame.
	 */
	public AdminFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 401);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu(" File ");
		menuBar.add(fileMenu);
		
		JMenuItem exportPerson = new JMenuItem(" Export ");
		fileMenu.add(exportPerson);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mntmImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		fileMenu.add(mntmImport);
		
		JMenu ExitMenu = new JMenu(" Exit ");
		menuBar.add(ExitMenu);
		
		JMenuItem mntmLogout = new JMenuItem("logout");
		ExitMenu.add(mntmLogout);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		ExitMenu.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 560, 362);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("People", null, panel, null);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(130, 0, 425, 323);
		panel.add(scrollPane);
		
		peopleTable = new javax.swing.JTable(){
		    public boolean isCellEditable(int rowIndex, int colIndex) {
		        return false;   //Disallow the editing of any cell
		    }
		};
		peopleTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SelectPerson();
			}
		});
		scrollPane.setViewportView(peopleTable);
		
		JButton btnExportInformation = new JButton("Export");
		btnExportInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportData();
			}
		});
		btnExportInformation.setBounds(10, 122, 110, 23);
		panel.add(btnExportInformation);
		
		rdbtnJson = new JRadioButton("JSON");
		rdbtnJson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		rdbtnJson.setSelected(true);
		buttonGroup.add(rdbtnJson);
		rdbtnJson.setBounds(10, 38, 109, 23);
		panel.add(rdbtnJson);
		
		JRadioButton rdbtnXml = new JRadioButton("XML");
		rdbtnXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		buttonGroup.add(rdbtnXml);
		rdbtnXml.setBounds(10, 64, 109, 23);
		panel.add(rdbtnXml);
		
		JButton button = new JButton("Import");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importData();
			}
		});
		button.setBounds(10, 167, 110, 23);
		panel.add(button);
		
		JButton delBtn = new JButton("DELETE");
		delBtn.setBounds(10, 229, 110, 29);
		panel.add(delBtn);
		
		JButton addBtn = new JButton("ADD");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddUser();
				GetPeopleData();
			}
		});
		addBtn.setBounds(10, 269, 110, 29);
		panel.add(addBtn);
		delBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteUser();
				GetPeopleData();
			}
		});
		
		Id = new JTextField();
		Id.setText("Id");
		Id.setBounds(563, 24, 86, 20);
		contentPane.add(Id);
		Id.setColumns(10);
		
		Name = new JTextField();
		Name.setText("Name");
		Name.setColumns(10);
		Name.setBounds(563, 66, 86, 20);
		contentPane.add(Name);
		
		SurName = new JTextField();
		SurName.setText("SurName");
		SurName.setColumns(10);
		SurName.setBounds(563, 108, 86, 20);
		contentPane.add(SurName);
		
		Username = new JTextField();
		Username.setText("Username");
		Username.setColumns(10);
		Username.setBounds(563, 150, 86, 20);
		contentPane.add(Username);
		
		Password = new JTextField();
		Password.setText("Password");
		Password.setColumns(10);
		Password.setBounds(563, 193, 86, 20);
		contentPane.add(Password);
		
		City = new JTextField();
		City.setText("City");
		City.setColumns(10);
		City.setBounds(563, 224, 86, 20);
		contentPane.add(City);
		
		IsAdmin = new JTextField();
		IsAdmin.setText("IsAdmin");
		IsAdmin.setColumns(10);
		IsAdmin.setBounds(563, 266, 86, 20);
		contentPane.add(IsAdmin);
		
		Money = new JTextField();
		Money.setText("Money");
		Money.setColumns(10);
		Money.setBounds(563, 310, 86, 20);
		contentPane.add(Money);
		GetPeopleData();
	}
}
