import java.awt.EventQueue;

import javax.swing.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginFrame {

	private JFrame frame;
	boolean isShown = true;
	private sqliteQueryClass queryExecuter = new sqliteQueryClass();
	private JTextField UsernameText;
	private JTextField passwordShow;
	private JPasswordField passwordHide;
	
	/**
	 * Launch the application.
	 */
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public LoginFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 399);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(187, 96, 108, 36);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Passwordl");
		lblNewLabel_1.setBounds(187, 158, 108, 36);
		frame.getContentPane().add(lblNewLabel_1);
		
		UsernameText = new JTextField();
		UsernameText.setBounds(394, 95, 114, 39);
		frame.getContentPane().add(UsernameText);
		UsernameText.setColumns(10);
		
		passwordShow = new JTextField();
		passwordShow.setBounds(394, 165, 114, 39);
		frame.getContentPane().add(passwordShow);
		passwordShow.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String username = UsernameText.getText();
					String password = "";
					if(isShown){
						password = passwordShow.getText();
					}else{
						password =  passwordHide.getText();
					}
					
					ResultSet rs = queryExecuter.checkLoginInformation(username,password);
					
					List<String> row = new ArrayList<String>();
					int elements = rs.getMetaData().getColumnCount();
					int count  = 0;
					while(rs.next())
					{
						count++;
					    for (int i=0; i < elements ; i++)
					    {
					       row.add(rs.getString(i + 1));
					    }
					}
					
					if(count == 0){
						JOptionPane.showMessageDialog(null, "No such User");
					}else if (count == 1){
						JOptionPane.showMessageDialog(null, "logged Successfully");
						frame.dispose();
						if(row.get(6).equals("true")){
							AdminFrame adminFrame = new AdminFrame();
							adminFrame.setVisible(true);
						}else{
							UserViewModel user = new UserViewModel(
									row.get(1),
									row.get(2),
									Double.parseDouble(row.get(7)),
									Integer.parseInt(row.get(0)));
							PersonalFrame personalFrame = new PersonalFrame(user);
							personalFrame.setVisible(true);
						}
						
					}else if (count > 1){
						JOptionPane.showMessageDialog(null, "Duplicate User");
					}
					
					rs.close();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex);		
				}
			}
		});
		btnLogin.setBounds(266, 234, 124, 45);
		frame.getContentPane().add(btnLogin);
		
		passwordHide = new JPasswordField();
		passwordHide.setEditable(false);
		passwordHide.setEnabled(false);
		passwordHide.setBounds(394, 165, 114, 38);
		frame.getContentPane().add(passwordHide);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Hide my password");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isShown = !isShown;
				if(isShown){
					passwordHide.setEnabled(false);
					passwordHide.setEditable(false);
					passwordShow.setEnabled(true);
					passwordShow.setEditable(true);				
					passwordHide.setVisible(false);
					passwordShow.setVisible(true);
				}else{
					passwordShow.setEnabled(false);
					passwordShow.setEditable(false);
					passwordHide.setEnabled(true);
					passwordHide.setEditable(true);
					passwordHide.setText( passwordShow.getText());
					passwordShow.setVisible(false);
					passwordHide.setVisible(true);
				}
			
			}
		});

		rdbtnNewRadioButton.setBounds(553, 165, 144, 23);
		frame.getContentPane().add(rdbtnNewRadioButton);
	}
}
