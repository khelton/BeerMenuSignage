package windows;

import java.sql.Connection;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;


public class EditBeerController {
	
	public BeerMenuItem beerItem;
	
	@FXML
	public TextField beerId;
	@FXML
	public TextField beerName;
	@FXML
	public TextField company;
	@FXML
	public TextField notes;
	@FXML
	public TextField style;
	@FXML
	public TextField abv;
	@FXML
	public TextField ibu;
	@FXML
	public TextField price1;
	@FXML
	public TextField price1Size;
	@FXML
	public TextField price2;
	@FXML
	public TextField price2Size;
	
	@FXML
	public Button saveButton;
	
	private String errorMessage;
	
	//Required constructor that is empty
	public EditBeerController() {}
	
	@FXML
	public void initialize() {
	}
	
	public void saveButton() {
		if(saveRecord())
			closeWindow();
	}
	
	private void closeWindow() {
		Stage stage = (Stage) saveButton.getScene().getWindow();
	    stage.close();
	}
	
	public boolean saveRecord() {
		errorMessage = "";
		
		beerItem = new BeerMenuItem(idCheck(), fixText(beerName), fixText(company),
				fixText(notes), fixText(style), abvCheck(), ibuCheck(), 
				priceCheck(price1, 1), sizeCheck(price1Size, 1), 
				priceCheck(price2, 2), sizeCheck(price2Size, 2));
		
		if (errorMessage.trim().length() > 0) {
			errorMessage += "\n\nPlease fix these errors and hit save again";
			Alert alert = new Alert(AlertType.ERROR, errorMessage);
			alert.showAndWait();
			return false;
		}
		
		// Save the beer in the database
		MySqlManager sql = new MySqlManager();
		if (beerItem.id == 0) {
			//insert a new Item
			try {
				Connection conn = sql.connect();
				int beerItemId = 0;
				String sqlQuery = "INSERT INTO beer "
						+ "(beer_name, company, notes, style, abv, ibu) VALUES "
						+ "('" + beerItem.beerName + "', '" + beerItem.company + "', '" + beerItem.notes + "',"
						+ " '" + beerItem.style + "', " + beerItem.abv + ", " + beerItem.ibu + ");";
				beerItemId = sql.runInsertQuery(conn, sqlQuery);
				
				sqlQuery = "INSERT INTO price "
						+ "(beer_id, price, size) VALUES "
						+ "('" + beerItemId + "', '" + beerItem.price1 + "', '" + beerItem.price1Size + "), "
						+ "('" + beerItemId + "', '" + beerItem.price2 + "', '" + beerItem.price2Size + ");";
				sql.runInsertQuery(conn, sqlQuery);
				
				beerItem.id = beerItemId;
				conn.close();
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR, 
						"Error inserting record, please try again");
				alert.showAndWait();
				return false;
			}
		} else {
			try {
				Connection conn = sql.connect();
			
				//save current beer
				int beerItemId = 0;
				String sqlQuery = "UPDATE beer SET beer_name = '" + beerItem.beerName + "', "
						+ "beer_name = '" + beerItem.beerName + "', "
						+ "beer_name = '" + beerItem.company + "', "
						+ "beer_name = '" + beerItem.notes + "', "
						+ "beer_name = '" + beerItem.style + "', "
						+ "beer_name = " + beerItem.abv + "', "
						+ "beer_name = " + beerItem.ibu + " WHERE id = " + beerItem.id + ";";
				beerItemId = sql.runInsertQuery(conn, sqlQuery);
				
				int priceId = 0;
				ArrayList<ItemPrice> tempPriceList = new ArrayList<ItemPrice>();
				sqlQuery = "SELECT id FROM price WHERE beer_id = " + beerItem.id + " AND size = " + beerItem.price1Size + ";";
				sql.runQuery(conn, sqlQuery, (rs) -> {
					tempPriceList.add(new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getDouble("price"), rs.getInt("size")));
				});
				if (priceId > 0) {
					sqlQuery = "UPDATE price SET price = " + beerItem.price1 + ", size = " + beerItem.price1Size + " "
							+ "WHERE id = " +  priceId + ";";
				} else {
					sqlQuery = "INSERT INTO price (beer_id, price, size) VALUES "
							+ "('" + beerItemId + "', '" + beerItem.price1 + "', '" + beerItem.price1Size + ");";
				}
				sql.runInsertQuery(conn, sqlQuery);
				tempPriceList.remove(0);
				
				sqlQuery = "SELECT * FROM price WHERE beer_id = " + beerItem.id + " AND size = " + beerItem.price2Size + ";";
				sql.runQuery(conn, sqlQuery, (rs) -> {
					tempPriceList.add(new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getDouble("price"), rs.getInt("size")));
				});
				priceId = tempPriceList.get(0).id;
				if (priceId > 0) {
					sqlQuery = "UPDATE price SET price = " + beerItem.price2 + ", size = " + beerItem.price2Size + " "
							+ "WHERE id = " +  priceId + ";";
				} else {
					sqlQuery = "INSERT INTO price (beer_id, price, size) VALUES "
							+ "('" + beerItemId + "', '" + beerItem.price2 + "', '" + beerItem.price2Size + ");";
				}
				sql.runInsertQuery(conn, sqlQuery);
				
				//use price list, update prices
				
				conn.close();
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR, 
						"Error modifying record, please try again");
				alert.showAndWait();
				return false;
			}

		}
		return true;
	}
	
	
	
	public int idCheck() {
		int retVal = 0;
		if (beerId.getText().trim().length() == 0)
			return retVal;
		try {
			retVal = Integer.valueOf(abv.getText());
		} catch (Exception e) {
			// do nothing, this means we need to add the 
		}
		return retVal;
	}
	public double abvCheck() {
		double retVal = 0;
		try {
			retVal = Double.valueOf(abv.getText());
		} catch (Exception e) {
			errorMessage += "Not a valid decimal in ABV field";
		}
		return retVal;
	}
	public int ibuCheck() {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(ibu.getText());
		} catch (Exception e) {
			// do nothing, this is not required
			//errorMessage += "\nNot a valid number in IBU field";
		}
		return retVal;
	}
	public double priceCheck(TextField price, int priceNumber) {
		double retVal = 0;
		try {
			retVal = Double.valueOf(price.getText());
		} catch (Exception e) {
			errorMessage += "\nNot a valid decimal in Price " + priceNumber + " field";
		}
		return retVal;
	}
	public int sizeCheck(TextField size, int sizeNumber) {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(size.getText());
		} catch (Exception e) {
			errorMessage += "\nNot a valid number in Size " + sizeNumber + " field";
		}
		return retVal;
	}
	public String fixText(TextField text) {
		return text.getText().replace("'", "\'");
	}
	
	
	
	
	
}
