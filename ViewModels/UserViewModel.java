
public class UserViewModel {
	public int Id ;
	public String firstName ;
	public String surName;
	public double money;
	
	
	public UserViewModel(String firstname, String surname, double money,int ID){
		this.firstName = firstname;
		this.surName = surname;
		this.money = money;
		this.Id = ID;
	}
	
}
