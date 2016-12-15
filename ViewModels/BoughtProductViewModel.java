
public class BoughtProductViewModel {
	public int id;
	public String name;
	public int quantity;
	public double price;
	public String category;
	
	public BoughtProductViewModel(int id, String name, int quantaty, double price, String category){
		this.id = id;
		this.name = name;
		this.quantity = quantaty;
		this.price = price;
		this.category = category;
	}
}
