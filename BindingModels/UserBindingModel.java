import java.io.IOException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonParser;

@XmlRootElement()
//@XmlType(name = "")
public class UserBindingModel {
	@XmlAttribute()
	public int id;
	@XmlAttribute()
	public String name;
	@XmlAttribute()
	public String surName;
	@XmlAttribute()
	public String userName;
	@XmlAttribute()
	public String password;
	@XmlAttribute()
	public String city;
	@XmlAttribute()
	public boolean isAdmin;
	@XmlAttribute()
	public double money;
	
	public UserBindingModel(){
		
	}
	
	public UserBindingModel(
			int id, 
			String name, 
			String surName, 
			String userName,
			String password,
			String city,
			boolean admin,
			double money){
		this.id = id;
		this.name = name;
		this.surName = surName;
		this.userName = userName;
		this.password = password;
		this.city = city;
		this.isAdmin = admin;
		this.money = money;
	}

}

