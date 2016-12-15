import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;

import net.proteanit.sql.DbUtils;

import org.sqlite.core.DB;

import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Choice;
import java.awt.Color;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PersonalFrame extends JFrame {

	private JPanel contentPane;
	public static UserViewModel user;
	public static BoughtProductViewModel currentProduct;
	private sqliteQueryClass queryExecuter = new sqliteQueryClass();
	private JTable productsTable;
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JComboBox comboBox;
	private JTextField amountTextField;
	public static double totalPrice = 0;
	JLabel totalPriceLabel;
	JLabel moneyLabel;
	private JTable boughtItemsTable;

	JButton btnCancelOrder ;
	private JTable medicineTable;
	JTabbedPane tabbedPane;
	private JTextField medicineAmount;
	JLabel totalMedicinePrice;
	 
	private String category;
	private JTable currentTable;
	private JTextField currentQuantity;
	JLabel currentTotalPriceLabel;
	
	private JTable requestTable;
	JTextArea sendRequstArea;
	JButton sendRequest;
	String request = "";
	JTextArea bookArea;
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PersonalFrame frame = new PersonalFrame(user);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void showBook(){
		JFileChooser chooser = new JFileChooser();
		String filename = "";
		chooser.showOpenDialog(null);
		File f = chooser.getSelectedFile();
		if(f != null){
			 filename = f.getAbsolutePath();
			 try {
					FileReader reader = new FileReader(filename);
					BufferedReader br = new BufferedReader(reader);
					bookArea.read(br,null);
					br.close();
					bookArea.requestFocus();
					
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "There was an error with reading the file");
				}
		}
	}
	
	public void setCurrentTabVariables(){
		System.out.println("TABBED CLICKED AND CHANGED");
		if(medicineTable.isShowing()){ //you are in Medicine Tab
			category = "Medicine";
			currentTable = medicineTable;
			currentTotalPriceLabel = totalMedicinePrice;
			currentQuantity = medicineAmount;
		}else if(btnCancelOrder.isShowing()) {//you are in the Bought Items Tab
			currentTable = boughtItemsTable;
		}else if(sendRequest.isShowing()) {//you are in the Bought Items Tab
			currentTable = requestTable;
		}else{						//you are in the products tab
			currentTable = productsTable;
			currentTotalPriceLabel = totalPriceLabel;
			currentQuantity = amountTextField;
		}
		
	}
	
	public void GetProdutsData(JTable table){
		try {
			ResultSet rs;
			if(medicineTable.isShowing()){
				rs  = queryExecuter.GetProdutsData(category);
			}else if(boughtItemsTable.isShowing()) {
				rs  = queryExecuter.GetBoughtProdutsData(user.Id);
			}else if(requestTable.isShowing()) {
				rs  = queryExecuter.GetRequest(user.Id);
			}else{
				category = comboBox.getSelectedItem().toString();
				rs  = queryExecuter.GetProdutsData(category);
			}
			System.out.println(table.getName());
			table.setModel(DbUtils.resultSetToTableModel(rs));
			rs.close();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error when getting products data");		
		}
	}
	
	public void showCostAmount(JLabel cost, JTextField quantity){
		quantity.setText("0"); 
		if(currentProduct == null){
			 cost.setText("0.0 BGN");
		}else if(currentProduct.price == 0){
			cost.setText("FREE");
		}else{
			 cost.setText("0.0 BGN"); 
		}
	}
	
	public void SelectItem(){
		try{
			currentTable.setSelectionBackground(new Color(255,255,0));
			int row = currentTable.getSelectedRow();
			int productId = (int) currentTable.getModel().getValueAt(row, 0) ;
			String productName = (String) currentTable.getModel().getValueAt(row, 1);
			int productQuantity = (Integer) currentTable.getModel().getValueAt(row, 2);
			double productPrice = (double) currentTable.getModel().getValueAt(row, 3);
			String productCategory = (String) currentTable.getModel().getValueAt(row, 4);
			
			currentProduct = new BoughtProductViewModel(productId,productName,productQuantity,productPrice,productCategory);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error at taking Product information");		
		}
	}
		
	public String SelectRequest(){
		try{
			currentTable.setSelectionBackground(new Color(255,255,0));
			int row = currentTable.getSelectedRow();
			String request = (String) currentTable.getModel().getValueAt(row, 0);
			return request;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error at taking Product information");		
		}
		return "";
	}
	
	public String[] SetComboboxValues(boolean FoodIsSelected){
		String[] categoryes;
		if(FoodIsSelected){
			 categoryes = new String[]{"Fruit","Vegetables","Meat"};
		}else{
			 categoryes = new String[]{"Water","Alcohol","Milk"};
		}
		return categoryes;
	}

	public void changeQuantity(int quantitySold){
		try {
			queryExecuter.changeQuantity(currentProduct.id,currentProduct.quantity - quantitySold);
		}catch(Exception ex){
			System.out.println(ex.getStackTrace());
		}
	}
	
	public void decreaseUserMoney(){
		try {
			user.money = user.money - totalPrice;
			queryExecuter.decreaseUserMoney(user.Id, user.money);
		}catch(Exception ex){
			System.out.println(ex.getStackTrace());
		}
	}
	
	public void AddRequest(String request ) {
		try {			
			queryExecuter.AddRequest(request, user.Id);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Error at taking request information");	
		}
	}
	
	public void addBoughtProduct(int quantitySold) throws Exception{
		try {			
			queryExecuter.addBoughtProduct(quantitySold, user.Id, currentProduct);
		}catch(Exception ex){
			 throw ex;
		}
	}
	
	public void buyItem(JTextField texfield){
		try{
			int quantityBought = Integer.parseInt(texfield.getText()); //amountTextField
			if(quantityBought <= 0){
				JOptionPane.showMessageDialog(null, "Wrong quantaty");	
				totalPrice = 0;
			}else if(currentProduct.quantity < quantityBought){
				JOptionPane.showMessageDialog(null, "Not enough product quantity " + currentProduct.name);	
			}else if(user.money - totalPrice < 0){
				JOptionPane.showMessageDialog(null, "Not enough money to buy " + currentProduct.name);	
			}else{
				changeQuantity(quantityBought);
				addBoughtProduct(quantityBought);
				decreaseUserMoney();
				moneyLabel.setText(Math.round(user.money *100)/100.0+ ""); //moneyLabel
				currentProduct = null;
			}
			
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(null, "Quantity must be only Integer numbers " );
		}catch(NullPointerException ex){
			JOptionPane.showMessageDialog(null, "No logged user or not selected product" );
		}catch(Exception ex){
			System.out.println(ex);
		}		
	}
	
	public void showTotalAmount(JTextField texfield,JLabel money){
		try
		{
			if (texfield.getText() != "") 
			{
				int quantaty = Integer.parseInt(texfield.getText());
				if(currentProduct.price != 0){
					PersonalFrame.totalPrice = (Math.round(currentProduct.price*100)* quantaty)/100.0;
					money.setText(PersonalFrame.totalPrice + " BGN"); 
				}else{
					PersonalFrame.totalPrice = 0;
					money.setText("FREE"); 
				}
			}else{
				money.setText("0.0 BGN"); 
			}
		}catch(Exception ex){
			 
			money.setText("0.0 BGN"); 
		}		
	}
	
	public void returnItem(){
		try {			
			if(user == null || currentProduct == null){
				throw new Exception();
			}
			double moneyToReturn = currentProduct.price*currentProduct.quantity;
			queryExecuter.updateItemQuantity(currentProduct.id,currentProduct.quantity);
			queryExecuter.returnPersonMoney(moneyToReturn, user.Id);
			queryExecuter.deleteBoughtProduct(currentProduct,user.Id);
			user.money += moneyToReturn;
			moneyLabel.setText(Math.round(user.money *100)/100.0+ "");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Coudn't return the item" );
		}
	}
	
	public void cancelRequest(String request){
		try {			
			if(user == null || request == "" ){
				throw new Exception();
			}
			queryExecuter.deleteRequest(user.Id,request);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Coudn't cancel request");
		}
	}
	
	/**
	 * Create the frame.
	 */	
	
	public PersonalFrame(UserViewModel user) { 
		this.user = user;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 401);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addMouseListener(new MouseAdapter() { 
			@Override
			public void mouseClicked(MouseEvent e) { 
				setCurrentTabVariables();
				GetProdutsData(currentTable);
				showCostAmount(currentTotalPriceLabel,currentQuantity);
			}
		});
		tabbedPane.setBounds(0, 0, 565, 362);
		contentPane.add(tabbedPane);
		
		JPanel productsPanel = new JPanel();
		tabbedPane.addTab("Products", null, productsPanel, null);
		productsPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(136, 0, 425, 333);
		productsPanel.add(scrollPane);
		
		productsTable = new javax.swing.JTable(){
		    public boolean isCellEditable(int rowIndex, int colIndex) {
		        return false;   //Disallow the editing of any cell
		    }
		};
		
		productsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				SelectItem();
				showCostAmount(currentTotalPriceLabel,currentQuantity);
			}
		});
		scrollPane.setViewportView(productsTable);
		
		JRadioButton rdbtnFood = new JRadioButton("Food");
		rdbtnFood.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setModel(new DefaultComboBoxModel(SetComboboxValues(true)));
				GetProdutsData(currentTable);
			}
		});
		buttonGroup.add(rdbtnFood);
		rdbtnFood.setBounds(69, 98, 61, 32);
		productsPanel.add(rdbtnFood);
		rdbtnFood.setSelected(true);
		
		JRadioButton rdbtnDrinks = new JRadioButton("Drinks");
		rdbtnDrinks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setModel(new DefaultComboBoxModel(SetComboboxValues(false)));
				GetProdutsData(currentTable);
			}
		});
		buttonGroup.add(rdbtnDrinks);
		rdbtnDrinks.setBounds(6, 94, 61, 41);
		productsPanel.add(rdbtnDrinks);
		
	    comboBox = new JComboBox();
	    comboBox.addActionListener(new ActionListener() { 
	    	public void actionPerformed(ActionEvent e) {
	    		GetProdutsData(currentTable);
	    	}
	    });
		comboBox.setModel(new DefaultComboBoxModel(SetComboboxValues(true))); 
		comboBox.setBounds(10, 11, 119, 25);
		productsPanel.add(comboBox);
		
		JButton btnBuy = new JButton("BUY");
		btnBuy.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				buyItem(currentQuantity);
				GetProdutsData(currentTable);
			}
		});
		
		btnBuy.setBounds(21, 281, 95, 41);
		productsPanel.add(btnBuy);
		
		amountTextField = new JTextField();
		amountTextField.addKeyListener(new KeyAdapter() { 
			@Override
			public void keyReleased(KeyEvent e) {
				showTotalAmount(currentQuantity,currentTotalPriceLabel);
			}
		});
		amountTextField.setBounds(21, 250, 95, 20);
		productsPanel.add(amountTextField);
		amountTextField.setColumns(10);
		
		totalPriceLabel = new JLabel("");
		totalPriceLabel.setBounds(21, 215, 95, 25);
		productsPanel.add(totalPriceLabel);
		
		JPanel medicinePanel = new JPanel();
		tabbedPane.addTab("Medicine", null, medicinePanel, null);
		medicinePanel.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(138, 0, 423, 333);
		medicinePanel.add(scrollPane_2);
		
		medicineTable = new JTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		medicineTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SelectItem();
				showCostAmount(currentTotalPriceLabel,currentQuantity);
			}
		});
		scrollPane_2.setViewportView(medicineTable);
		
		JButton medicineBuyButton = new JButton("BUY");
		medicineBuyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buyItem(currentQuantity); 
				GetProdutsData(currentTable); 
			}
		});
		medicineBuyButton.setBounds(20, 281, 95, 41);
		medicinePanel.add(medicineBuyButton);
		
		medicineAmount = new JTextField();
		medicineAmount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				showTotalAmount(currentQuantity,currentTotalPriceLabel);
			}
		});
		medicineAmount.setColumns(10);
		medicineAmount.setBounds(20, 250, 95, 20);
		medicinePanel.add(medicineAmount);
		
		totalMedicinePrice = new JLabel("");
		totalMedicinePrice.setBounds(20, 211, 95, 25);
		medicinePanel.add(totalMedicinePrice);
		
		JPanel furniturePanel = new JPanel();
		tabbedPane.addTab("Books", null, furniturePanel, null);
		furniturePanel.setLayout(null);
		
		JButton btnReadFile = new JButton("Read file");
		btnReadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showBook();
				
			}
		});
		btnReadFile.setBounds(0, 140, 89, 23);
		furniturePanel.add(btnReadFile);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(96, 11, 454, 312);
		furniturePanel.add(scrollPane_5);
		
		bookArea = new JTextArea();
		bookArea.setEditable(false);
		scrollPane_5.setViewportView(bookArea);
		
		JPanel servicesPanel = new JPanel();
		tabbedPane.addTab("Services", null, servicesPanel, null);
		servicesPanel.setLayout(null);
		
		sendRequest = new JButton("Send Request");
		sendRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sendRequstArea.getText() != null){
					String request  = sendRequstArea.getText();
					AddRequest(request);
					GetProdutsData(currentTable);
					sendRequstArea.setText("");
				}
				
			}
		});
		sendRequest.setBounds(10, 56, 125, 42);
		servicesPanel.add(sendRequest);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(145, 11, 391, 127);
		servicesPanel.add(scrollPane_3);
		
		sendRequstArea = new JTextArea();
		scrollPane_3.setViewportView(sendRequstArea);
		sendRequstArea.setLineWrap(true);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(144, 176, 395, 147);
		servicesPanel.add(scrollPane_4);
		
		requestTable = new JTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		requestTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 request = SelectRequest();
			}
		});
		scrollPane_4.setViewportView(requestTable);
		
		JButton cancelRequest = new JButton("Cancel Request");
		cancelRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelRequest(request);
				GetProdutsData(currentTable);
			}
		});
		cancelRequest.setBounds(10, 221, 125, 42);
		servicesPanel.add(cancelRequest);
		
		JPanel boughtItemsPanel = new JPanel();
		tabbedPane.addTab("Bought Items", null, boughtItemsPanel, null);
		boughtItemsPanel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(137, 0, 424, 333);
		boughtItemsPanel.add(scrollPane_1);
		
		boughtItemsTable = new JTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		boughtItemsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SelectItem();
			}
		});
		scrollPane_1.setViewportView(boughtItemsTable);
		
		btnCancelOrder = new JButton("Cancel Order");
		btnCancelOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returnItem();
				GetProdutsData(currentTable);
			}
		});
		btnCancelOrder.setBounds(10, 175, 100, 43);
		boughtItemsPanel.add(btnCancelOrder);
		
		JLabel lblNewLabel = new JLabel("Money:");
		lblNewLabel.setBounds(589, 298, 46, 25);
		contentPane.add(lblNewLabel);
		
		moneyLabel = new JLabel((Math.round(user.money *100)/100.0) + "lv");
		moneyLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
		moneyLabel.setBounds(574, 306, 110, 55);
		contentPane.add(moneyLabel);
		
		JButton btnPolice = new JButton("POLICE");
		btnPolice.setBounds(568, 74, 106, 33);
		contentPane.add(btnPolice);
		
		JButton btnAmbulance = new JButton("AMBULANCE");
		btnAmbulance.setBounds(568, 128, 106, 33);
		contentPane.add(btnAmbulance);
		
		JButton btnFire = new JButton("FIRE ");
		btnFire.setBounds(568, 187, 106, 33);
		contentPane.add(btnFire);
		
		JLabel fullName = new JLabel(PersonalFrame.user.firstName + " " + PersonalFrame.user.surName);
		fullName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		fullName.setBounds(568, 38, 106, 25);
		contentPane.add(fullName);
		
		setCurrentTabVariables();
		GetProdutsData(currentTable);
	}
}
