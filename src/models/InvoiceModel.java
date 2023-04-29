package models;

public class InvoiceModel {

	String CName, CContact, Teller, Date, InvTotal, InvNo, Balance;

	public InvoiceModel(String invNo, String cName, String cContact,
			String invTotal, String balance, String teller,  String date) {
		
		InvNo = invNo;
		CName = cName;
		CContact = cContact;
		Teller = teller;
		Date = date;
		InvTotal = invTotal;
		
		Balance = balance;
		
	}

	public String getcName() {
		return CName;
	}

	public void setcName(String cName) {
		CName = cName;
	}

	public String getcContact() {
		return CContact;
	}

	public void setcContact(String cContact) {
		CContact = cContact;
	}

	public String getTeller() {
		return Teller;
	}

	public void setTeller(String teller) {
		Teller = teller;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getInvTotal() {
		return InvTotal;
	}

	public void setInvTotal(String invTotal) {
		InvTotal = invTotal;
	}

	public String getInvNo() {
		return InvNo;
	}

	public void setInvNo(String invNo) {
		InvNo = invNo;
	}

	public String getBalance() {
		return Balance;
	}

	public void setBalance(String balance) {
		Balance = balance;
	}
	
	
}
