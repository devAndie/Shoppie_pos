package models;

public class Product {
	int ID;
	String Name, Desc;
	
	
	public Product(int iD, String name, String desc) {
		super();
		ID = iD;
		Name = name;
		Desc = desc;
	}


	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public String getDesc() {
		return Desc;
	}


	public void setDesc(String desc) {
		Desc = desc;
	}

	
}
