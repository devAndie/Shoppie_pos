package api;

public class ConvertMeasure {

	public static double inchesToFeet(double inch) {
		double feet = 0;
		
		if (inch > 0 && inch <= 12) { feet = 1; }
		else if (inch > 12 && inch <= 18) { feet = 1.5; }
		else if (inch > 18 && inch <= 24) { feet = 2; }
		else if (inch > 24 && inch <= 30) { feet = 2.5; }
		else if (inch > 30 && inch <= 36) { feet = 3; }
		else if (inch > 36 && inch <= 42) { feet = 3.5; }
		else if (inch > 42 && inch <= 48) { feet = 4; }
		else if (inch > 48 && inch <= 54) { feet = 4.5; }
		else if (inch > 54 && inch <= 60) { feet = 5; }
		else if (inch > 60 && inch <= 65) { feet = 5.5; }
		else if (inch > 65 && inch <= 72) { feet = 6; }
		else if (inch > 72 && inch <= 84) { feet = 7; }
		else if (inch > 84 && inch <= 96) { feet = 8; }

		return feet; 
		
	}
	
	
	public static double mmToFeet(double mm) {
		double feet = 0;
		
		if (mm >0 && mm <= 305) { feet = 1; }
		else if (mm >305 && mm <= 457 ) { feet = 1.5; }
		else if (mm >457 && mm <= 610 ) { feet = 2; }
		else if (mm >610 && mm <= 762 ) { feet = 2.5; }
		else if (mm >762 && mm <= 915 ) { feet = 3; }
		else if (mm >915 && mm <= 1067 ) { feet = 3.5; }
		else if (mm >1067 && mm <= 1220 ) { feet = 4; }
		else if (mm >1220 && mm <= 1372 ) { feet = 4.5; }
		else if (mm >1372 && mm <= 1525 ) { feet = 5; }
		else if (mm >1525 && mm <= 1650 ) { feet = 5.5; }
		else if (mm >1650 && mm <= 1830 ) { feet = 6; }
		else if (mm >1830 && mm <= 2134 ) { feet = 7; }
		else if (mm >2134 && mm <= 2440 ) { feet = 8; }		
		
		return feet;
	}
	
	
}
