package models;

public class Customer {
	String ID, Name, Contact;

	public Customer(String iD, String name, String contact) {
		ID = iD;
		Name = name;
		Contact = contact;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getContact() {
		return Contact;
	}

	public void setContact(String contact) {
		Contact = contact;
	}
	
	

}
