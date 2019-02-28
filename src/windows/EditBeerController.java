package windows;

import java.sql.Connection;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import menulayouts.BeerItemLayoutController;
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
	
	@FXML
	public GridPane previewPane;
	
	public BeerItemLayoutController previewItemController;
	
	private String errorMessage;
	
	//Required constructor that is empty
	public EditBeerController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (beerItem != null) {
			setLayoutFields(beerItem);
		}
	}
	public void setLayoutFields(BeerMenuItem b) {
		beerId.setText(""+ b.id);
		beerName.setText(b.beerName);
		company.setText(b.company);
		notes.setText(b.notes);
		style.setText(b.style);
		abv.setText(""+ b.abv);
		ibu.setText(""+ b.ibu);
		b.resolvePrices();
		price1.setText(""+ b.price1);
		price1Size.setText(""+ b.price1Size);
		price2.setText(""+ b.price2);
		price2Size.setText(""+ b.price2Size);
	}
	public void updatePreview() {
		if (beerItem != null && previewItemController != null) {
			previewItemController.fillBeerLayout(beerItem, previewPane);
		}
	}
	
	public void saveButtonClicked() {
		if(saveRecord())
			closeWindow();
	}
	
	private void closeWindow() {
		Stage stage = (Stage) saveButton.getScene().getWindow();
	    stage.close();
	}
	
	public boolean saveRecord() {
		errorMessage = "";
		
		checkBeerName();
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
						+ "(" + beerItemId + ", " + beerItem.price1 + ", " + beerItem.price1Size + "), "
						+ "(" + beerItemId + ", " + beerItem.price2 + ", " + beerItem.price2Size + ");";
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
				String sqlQuery = "UPDATE beer SET beer_name = '" + beerItem.beerName + "', "
						+ "beer_name = '" + beerItem.beerName + "', "
						+ "company = '" + beerItem.company + "', "
						+ "notes = '" + beerItem.notes + "', "
						+ "style = '" + beerItem.style + "', "
						+ "abv = " + beerItem.abv + ", "
						+ "ibu = " + beerItem.ibu + " WHERE id = " + beerItem.id + ";";
				sql.runInsertQuery(conn, sqlQuery);
				
				int priceId = 0;
				ArrayList<ItemPrice> tempPriceList = new ArrayList<ItemPrice>();
				sqlQuery = "SELECT * FROM price WHERE beer_id = " + beerItem.id + " AND size = " + beerItem.price1Size + ";";
				sql.runQuery(conn, sqlQuery, (rs) -> {
					tempPriceList.add(new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getDouble("price"), rs.getInt("size")));
				});
				priceId = tempPriceList.get(0).id;
				if (priceId > 0) {
					sqlQuery = "UPDATE price SET price = " + beerItem.price1 + ", size = " + beerItem.price1Size + " "
							+ "WHERE id = " +  priceId + ";";
				} else {
					sqlQuery = "INSERT INTO price (beer_id, price, size) VALUES "
							+ "('" + beerItem.id + "', '" + beerItem.price1 + "', '" + beerItem.price1Size + "');";
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
							+ "('" + beerItem.id + "', '" + beerItem.price2 + "', '" + beerItem.price2Size + "');";
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
		if (beerItem != null) {
			return beerItem.id;
		}
		try {
			retVal = Integer.valueOf(beerId.getText());
		} catch (Exception e) {
			// do nothing, this means we need to add the beer to the db
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
	public boolean checkBeerName() {
		if (this.beerName.getText().trim().length() == 0) {
			errorMessage += "\nThe beer must have a name";
			return false;
		}
		return true;
	}
	public String fixText(TextField text) {
		return text.getText().replace("'", "\\'");
	}
	
	@FXML
	public void textFieldChanged(KeyEvent e) {
		if (previewItemController != null) {
			if (e.getSource().equals(beerName)) {
				previewItemController.beerName.setText(beerName.getText());
			}
			
			if (e.getSource().equals(company)) {
				previewItemController.company.setText(company.getText());
			}
			
			if (e.getSource().equals(notes)) {
				previewItemController.notes.setText(notes.getText());
			}
			
			if (e.getSource().equals(style)) {
				previewItemController.beerStyle.setText(style.getText());
			}
			
			if (e.getSource().equals(abv)) {
				previewItemController.abv.setText(abv.getText());
			}
			
			if (e.getSource().equals(price1)) {
				previewItemController.price1.setText(price1.getText());
			}
			
			if (e.getSource().equals(price1Size)) {
				previewItemController.ounce1.setText("/" + price1Size.getText());
			}
			
			if (e.getSource().equals(price2)) {
				previewItemController.price2.setText(price2.getText());
			}
			
			if (e.getSource().equals(price2Size)) {
				previewItemController.ounce2.setText("/" + price2Size.getText());
			}
		}
		
	}
	
	
	
}
