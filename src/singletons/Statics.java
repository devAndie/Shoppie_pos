package singletons;

public class Statics {
	static String Name = "Instalink Solutions (K) LTD ";              
	
	static String Dealership = "Dealers in: All types of glasses, "
			+ "Aluminium & Office partitioning, Perspex, Putty, "
			+ "Sign Boards \nAnd All Construction Works ";
	
	static String Address = "A.N. House(Jainsala Hotel Building) Ngombeni Lane, "
			+ "Off Jainsala Rd - Nairobi \n";
	static String Contact = "e-mail: instalinksolutions@gmail.com \n"
			+ "Tel: 0721 996 637 ";
	
//	static String dir = "C:\\Users\\user\\Desktop\\POS\\";
//	public static String img = "C:\\Users\\user\\Desktop\\POS\\src\\Instalink bg.png";

	static String dir = "C:\\Users\\Andru\\Desktop\\Dru Inc\\pos\\docs\\";
	public static String img = "src\\document\\Instalink bg.png";

	static String ReceiptsDir = dir;
	static String InvoicesDir = dir;
	static String QuotesDir = dir;
	static String DeliveryDir = dir;
	
	
	public static String CONNECTION = "jdbc:mysql://localhost/hardware? user=sqluser&password=sqluserpw";
	public static int USERID = 1;
	
	
	public static String getReceiptsDir() {
		return ReceiptsDir;
	}
	public static String getInvoicesDir() {
		return InvoicesDir;
	}
	public static String getQuotesDir() {
		return QuotesDir;
	}
	
	public static String getDeliveryDir() {
		return DeliveryDir;
	}
	
	public static String getName() {
		return Name;
	}
	
	public static String getDealership() {
		return Dealership;
	}
	
	public static String getAddress() {
		return Address;
	}
	
	public static String getContact() {
		return Contact;
	}
	
	public static String getCONNECTION() {
		return CONNECTION;
	}

	public static int getUSERID() {
		return USERID;
	}
	public static void setUSERID(int uSERID) {
		USERID = uSERID;
	}

	
	
	
}
