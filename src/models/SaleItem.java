package models;

public class SaleItem {

	String Code, Name, Desc, Unit,  Dimens, Quantity, Sellp, Total;
	
	public SaleItem(String code, String name, String desc, String unit, String quant, 
			String dimens, String sellp, String itemSubTotal) {
		Code = code;
		Name = name;
		Desc = desc;
		Unit = unit;
		Quantity = quant;
		Dimens = dimens;
		Sellp = sellp;
		Total = itemSubTotal;
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

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getDimens() {
		return Dimens;
	}

	public void setDimens(String dimens) {
		Dimens = dimens;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String  code) {
		Code = code;
	}

	public String getQuantity() {
		return Quantity;
	}

	public void setQuantity(String quantity) {
		Quantity = quantity;
	}

	public String getSellp() {
		return Sellp;
	}

	public void setSellp(String sellp) {
		Sellp = sellp;
	}

	public String getTotal() {
		return Total;
	}

	public void setTotal(String total) {
		Total = total;
	}
	
	
	
	
}
