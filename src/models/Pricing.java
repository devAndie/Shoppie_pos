package models;

public class Pricing {

	String Unit;
	float Price;
	int PrID, Pid;
	
	public Pricing(int prID, String unit, float price, int pid) {
		Unit = unit;
		Price = price;
		PrID = prID;
		Pid = pid;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public float getPrice() {
		return Price;
	}

	public void setPrice(float price) {
		Price = price;
	}

	public int getPrID() {
		return PrID;
	}

	public void setPrID(int prID) {
		PrID = prID;
	}

	public int getPid() {
		return Pid;
	}

	public void setPid(int pid) {
		Pid = pid;
	}
	
}
