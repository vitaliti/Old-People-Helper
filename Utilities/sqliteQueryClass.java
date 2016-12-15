import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.proteanit.sql.DbUtils;


public class sqliteQueryClass {

	Connection connection = null;
	
	public sqliteQueryClass(){
		connection = sqliteConnection.dbConnector();
	}
	
	public ResultSet GetPeopleData() throws SQLException{

		String query = "SELECT * FROM People "
				+ "ORDER BY Id ASC;";
		PreparedStatement prs = connection.prepareStatement(query);

		return prs.executeQuery();
	}
	
	public ResultSet checkLoginInformation(String username,String password) throws SQLException{
		String query = "select * from People where Username = ? And password = ?";
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setString(1, username);
		prs.setString(2, password);

		return prs.executeQuery();
	}


	public ResultSet GetProdutsData(String selectedCategory) throws SQLException{

		String query = "SELECT Id,Name,Quantity,Price,Category FROM Products"
				+ " Where Category = '" 
				+  selectedCategory
				+ "' AND Quantity > 0 "
				+ "ORDER BY Price ASC, Name ASC;";
		PreparedStatement prs = connection.prepareStatement(query);

		return prs.executeQuery();
	}
	

	public ResultSet GetBoughtProdutsData(int id) throws SQLException{

		String query = "SELECT ProductId,Name,Quantity,Price,Category FROM PersonProducts" //int string int double string
				+ " Where PersonId = '" 
				+  id
				+ "' AND Quantity > 0 "
				+ "ORDER BY Price ASC, Name ASC;";
		PreparedStatement prs = connection.prepareStatement(query);

		return prs.executeQuery();
	}
	
	public ResultSet GetRequest(int personId) throws SQLException{

		String query = "SELECT Text FROM Requests" 
				+ " Where PersonId = '" 
				+  personId
				+ "';";
		PreparedStatement prs = connection.prepareStatement(query);

		return prs.executeQuery();
	}
	
	public void AddRequest(String request,int personId) throws SQLException{
		String query = "insert into Requests (Text,PersonId)"
				+ "Values (?,?) ";
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setString(1, request);
		prs.setInt(2, personId);
	
		prs.executeUpdate();
	}
	
	public void changeQuantity(int productId,int leftProductQuantity) throws SQLException{

		String query = "Update Products "
				+ " Set quantity = ? "
				+ "where Id = " + productId;
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setInt(1,leftProductQuantity);
		prs.executeUpdate();
		prs.close();
	}
	
	public void decreaseUserMoney(int userId, double money) throws SQLException{
		String query = "Update People "
				+ " Set Money = ? "
				+ "where Id = " + userId;
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setDouble(1,money);
		prs.executeUpdate();
		prs.close();
	
	}
	
	public void updateItemQuantity(int productId,int quantity)throws SQLException{
		String query = "Update Products " 
				+ " Set Quantity = Quantity + ?"
				+ "where Id = " + productId;
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setInt(1,quantity);
		prs.executeUpdate();
		prs.close();
	}
	
	public void returnPersonMoney(double price,int userId)throws SQLException{
		String query = "Update People " 
				+ " Set Money = Money + ?"
				+ "where Id = " + userId;
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setDouble(1,price);
		prs.executeUpdate();
		prs.close();
	}
	
	public void deleteBoughtProduct(BoughtProductViewModel product,int personId)throws SQLException{
		
		String query = "SELECT Id FROM PersonProducts WHERE personId = ? AND ProductId = ? AND Quantity = ? AND Price = ? AND Name= ? AND Category = ? Limit 1;";
		
		PreparedStatement prs = connection.prepareStatement(query);
		
		prs.setInt(1,personId);
		prs.setInt(2,product.id );
		prs.setInt(3,product.quantity);
		prs.setDouble(4,product.price);
		prs.setString(5,product.name);
		prs.setString(6,product.category);		
		
		ResultSet rs =  prs.executeQuery();
		int Id = rs.getInt(1);
		prs.close();
		
		query = " DELETE  FROM PersonProducts "
				+ " WHERE Id = " + Id + " ;" ;
		
		prs = connection.prepareStatement(query);		
		prs.execute();
		prs.close();
	}
	
	public void deleteRequest(int personId,String request)throws SQLException{
			
			String query = "SELECT Id FROM Requests WHERE personId = ? AND Text = ?  Limit 1;";
			
			PreparedStatement prs = connection.prepareStatement(query);
			
			prs.setInt(1,personId);
			prs.setString(2,request);		
			
			ResultSet rs =  prs.executeQuery();
			int Id = rs.getInt(1);
			prs.close();
			
			query = " DELETE  FROM Requests "
					+ " WHERE Id = " + Id + " ;" ;
			
			prs = connection.prepareStatement(query);		
			prs.execute();
			prs.close();
		}
	
	public void addBoughtProduct(int quantitySold,int userId,BoughtProductViewModel currentProduct) throws SQLException{
		String query = "insert into PersonProducts (ProductId,Quantity,Price,PersonId,Name,Category)"
				+ "Values (?,?,?,?,?,?)  ";
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setInt(1, currentProduct.id );
		prs.setInt(2, quantitySold);
		prs.setDouble(3, currentProduct.price);
		prs.setInt(4, userId);
		prs.setString(5, currentProduct.name);
		prs.setString(6, currentProduct.category);
		prs.executeUpdate();
	}
	
	public void DeleteUser(int personId) throws SQLException{
		String query = "DELETE FROM People WHERE id = ?;";
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setInt(1, personId);
	
		prs.executeUpdate();
	}
	
	public void AddUser(int personId,
			String name,
			String surName,
			String username,
			String password,
			String City,
			String isAdmin,
			double money) throws SQLException{
		
		String query = "INSERT INTO main.People (Id,Name,SurName,Username,Password,City,IsAdmin,Money)"
				+ " VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement prs = connection.prepareStatement(query);
		prs.setInt(1, personId);
		prs.setString(2, name);
		prs.setString(3, surName);
		prs.setString(4, username);
		prs.setString(5, password);
		prs.setString(6, City);
		prs.setString(7, isAdmin);
		prs.setDouble(8, money);
		
		
	
		prs.executeUpdate();
	}

	
}
