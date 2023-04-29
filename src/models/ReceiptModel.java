package models;

public class ReceiptModel {

	String RId, CName, CContact, Total, Date, Teller;

	public ReceiptModel(String rId, String cName, String cContact, 
			String total, String teller, String date) {
		RId = rId;
		CName = cName;
		CContact = cContact;
		Total = total;
		Date = date;
		Teller = teller;
		
	}

	public String getRId() {
		return RId;
	}

	public void setRId(String rId) {
		RId = rId;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getCContact() {
		return CContact;
	}

	public void setCContact(String cContact) {
		CContact = cContact;
	}

	public String getTotal() {
		return Total;
	}

	public void setTotal(String total) {
		Total = total;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getTeller() {
		return Teller;
	}

	public void setTeller(String teller) {
		Teller = teller;
	}
	
	
	
}
