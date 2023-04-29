package models;

public class QuoteModel {

	String QId,  CName, CContact, Project, Date, Total, Teller, Invoiced;

	public QuoteModel(String qId, String cName, String cContact, String project, 
			 String total, String date, String teller, String invoiced) {
		QId = qId;
		CName = cName;
		CContact = cContact;
		Project = project;
		Date = date;
		Total = total;
		Teller = teller;
		Invoiced = invoiced;
	}

	public String getQId() {
		return QId;
	}

	public void setQId(String qId) {
		QId = qId;
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

	public String getProject() {
		return Project;
	}

	public void setProject(String project) {
		Project = project;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getTotal() {
		return Total;
	}

	public void setTotal(String total) {
		Total = total;
	}

	public String getTeller() {
		return Teller;
	}

	public void setTeller(String teller) {
		Teller = teller;
	}

	public String getInvoiced() {
		return Invoiced;
	}

	public void setInvoiced(String invoiced) {
		Invoiced = invoiced;
	}
	
	
}
