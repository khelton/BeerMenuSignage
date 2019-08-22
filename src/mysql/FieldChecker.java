package mysql;

import java.time.LocalTime;

import item.price.PriceScheduleListViewController;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

public class FieldChecker {
	
	public String errorMessage;
	
	public FieldChecker() {
		errorMessage = "";
	}
	

	public static String getHexColor(ColorPicker color) {
		String colorHex = Integer.toHexString(color.getValue().hashCode());
		while (colorHex.length() < 8) {
			colorHex = "0" + colorHex;
		}
		return "#" + colorHex;	
	}
	
	public int idCheck(TextField beerId) {
		int retVal = 0;
		if (beerId.getText().trim().length() == 0)
			return retVal;
		/*
		if (beerItem != null) {
			return beerItem.id;
		}*/
		try {
			retVal = Integer.valueOf(beerId.getText());
		} catch (Exception e) {
			// do nothing, this means we need to add the beer to the db
		}
		return retVal;
	}
	
	public double abvCheck(TextField abvText) {
		double retVal = 0;
		try {
			retVal = Double.valueOf(abvText.getText());
		} catch (Exception e) {
			errorMessage += "\nNot a valid decimal in ABV field";
		}
		return retVal;
	}
	
	public int ibuCheck(TextField ibuText) {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(ibuText.getText());
		} catch (Exception e) {
			// do nothing, this is not required
			//errorMessage += "\nNot a valid number in IBU field";
		}
		return retVal;
	}

	public int srmCheck(TextField srmText) {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(srmText.getText());
		} catch (Exception e) {
			// do nothing, this is not required
			//errorMessage += "\nNot a valid number in IBU field";
		}
		return retVal;
	}
	
	
	public double priceCheck(TextField price) {
		double retVal = 0;
		try {
			retVal = Double.valueOf(price.getText());
		} catch (Exception e) {
			errorMessage = "decimal in a price or number in size is not formatted correctly";
		}
		return retVal;
	}
	public int sizeCheck(TextField size) {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(size.getText());
		} catch (Exception e) {
			errorMessage = "decimal in a price or number in size is not formatted correctly";
		}
		return retVal;
	}
	public double priceCheck(TextField price, int priceNumber) {
		double retVal = 0;
		try {
			retVal = Double.valueOf(price.getText());
		} catch (Exception e) {
			errorMessage += "\nNot a valid decimal in a Price " + priceNumber + " field";
		}
		return retVal;
	}
	
	public int sizeCheck(TextField size, int sizeNumber) {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(size.getText());
		} catch (Exception e) {
			errorMessage += "\nNot a valid number in a Size " + sizeNumber + " field";
		}
		return retVal;
	}
	
	public boolean beerNameCheck(TextField beerName) {
		if (beerName.getText().trim().length() == 0) {
			errorMessage += "\nThe beer must have a name";
			return false;
		}
		return true;
	}
	
	public String imageSrcCheck(TextField tb) {
		String retVal = "";
		if (tb.getText().trim().length() == 0) {
			errorMessage += "\nThe image source is empty";
			return retVal;
		}
		return tb.getText().trim().replace("'", "''");
	}
	
	public LocalTime timeCheck(TextField text) {
		LocalTime time = null;
		String s = text.getText().trim().replace("'", "\\'");
		if (s.length() == 0) {
			errorMessage += "Time is empty";
			return time;
		}
		try {
			time = LocalTime.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage += "\nTime couldn't be parsed";
		}
		
		return time;
	}
	
	public String getDays(PriceScheduleListViewController itemController) {
		String retVal = "";
		retVal += (itemController.sun.isSelected()) ? "1" : "0";
		retVal += (itemController.mon.isSelected()) ? "1" : "0";
		retVal += (itemController.tue.isSelected()) ? "1" : "0";
		retVal += (itemController.wed.isSelected()) ? "1" : "0";
		retVal += (itemController.thu.isSelected()) ? "1" : "0";
		retVal += (itemController.fri.isSelected()) ? "1" : "0";
		retVal += (itemController.sat.isSelected()) ? "1" : "0";
		
		return retVal;
	}
	
	public String nameCheck(TextField text) {
		String retVal = text.getText().trim().replace("'", "\\'");
		if (retVal.length() == 0)
			errorMessage += "name is empty";
		
		return retVal;
	}
	
	
	
	public String fixText(TextField text) {
		return text.getText().replace("'", "\\'");
	}
}
