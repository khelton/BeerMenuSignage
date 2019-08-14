package item.edit;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.exceptions.CJCommunicationsException;

import image.EditImageController;
import image.ImageSourceType;
import image.ImageType;
import item.price.EditPriceController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import menulayouts.grid2x10.Item2X10Controller;
import menulayouts.grid4x5.Item4X5Controller;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPriceType;
import types.PriceSchedule;


public class EditBeerController {
	
	public BeerMenuItem beerItem;
	
	@FXML
	public TextField beerId;
	@FXML
	public TextField beerName;
	@FXML
	public ColorPicker beerNameColor;
	@FXML
	public TextField company;
	//@FXML
	//public TextField companyLocation;
	@FXML
	public TextField notes;
	@FXML
	public ColorPicker notesColor;
	@FXML
	public TextField style;
	@FXML
	public TextField abv;
	@FXML
	public TextField ibu;
	//@FXML
	//public TextField srmText;
	@FXML
	public ColorPicker srmColor;
	
	//@FXML
	//public Button selectLogoButton;
	//@FXML
	//public Label logoPath;
	
	@FXML
	public Button pricesButton;
	
	@FXML
	public Button imageButton;
	
	@FXML
	public Button saveButton;
	@FXML
	public Button saveAndCloseButton;
	
	@FXML
	public GridPane previewPane;
	
	public Item2X10Controller previewItemController;
	
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
		beerNameColor.setValue(Color.web(b.beerNameColor));
		company.setText(b.company);
		//companyLocation.setText(b.location);
		notes.setText(b.notes);
		notesColor.setValue(Color.web(b.notesColor));
		style.setText(b.style);
		abv.setText(""+ b.abv);
		ibu.setText(""+ b.ibu);
		srmColor.setValue(Color.web(b.beerPourColor));

		/*
		ArrayList<ItemPrice> validPrices = b.getPrices();
		if (validPrices.size() > 0) {
			price1.setText(""+ validPrices.get(0).price);
			price1Size.setText(""+ validPrices.get(0).size);
			if (validPrices.size() > 1) {
				price2.setText(""+ validPrices.get(1).price);
				price2Size.setText(""+ validPrices.get(1).size);
			}
		}*/
	}
	public void updatePreview() {
		if (beerItem != null && previewItemController != null) {
			previewItemController.setLayout(beerItem);
		}
	}
	
	@FXML
	public void saveButtonClicked() {
		try {
			saveRecord();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void openPricesWindow() {
		if (beerItem == null || beerItem.id == 0) {
			Alert alert = new Alert(AlertType.ERROR, 
					"Please save the beer first.\n\n"
					+ "The beer needs to have a proper id assigned from\n"
					+ "the database before prices can be saved correctly ");
			alert.showAndWait();
			return;
		}
		try {
			EditPriceController editPriceController = launchEditPriceWindow();
			editPriceController.priceList = beerItem.priceList;
			editPriceController.beerItem = beerItem;
			ObservableList<ItemPriceType> priceTypeList = FXCollections.observableArrayList();
			//TODO put this in its own file. it can be done statically
			MySqlManager sql = new MySqlManager();
			Connection conn = null;
			try {
				conn = sql.connect();
				/*String queryString  = "SELECT * FROM price_type WHERE active = 1 ORDER BY id DESC;";
				sql.runQuery(conn, queryString, (rs) -> {
					priceTypeList.add(new ItemPriceType(rs.getInt("id"), rs.getString("name")));
				});*/
				String queryString  = "SELECT price_type.*, "
						+ "s.name as 's_name', s.days_string as 's_days', "
						+ "s.time_start as 's_start', s.time_end as 's_end' "
						+ "FROM price_type "
						+ "LEFT JOIN schedule s ON s.id = price_type.schedule_id "
						+ "WHERE price_type.active = 1 ORDER BY id DESC;";
				sql.runQuery(conn, queryString, (rs) -> {
					PriceSchedule s  = new PriceSchedule(rs.getInt("schedule_id"), rs.getString("s_name"), 
							rs.getString("s_start"), rs.getString("s_end"), rs.getString("s_days"));
					ItemPriceType pt = new ItemPriceType(rs.getInt("id"), rs.getString("name"), s, rs.getInt("enabled"));
					priceTypeList.add(pt);
				});
				conn.close();
			} catch (CJCommunicationsException e1) {
				e1.printStackTrace();
				System.out.println("not connected, aborting");
				return;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
				
			editPriceController.priceTypeList = priceTypeList;
			editPriceController.setLayoutFields();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	
	
	private EditPriceController launchEditPriceWindow() {
		EditPriceController returnPriceController = null;
		try {
			FXMLLoader editPriceLoader = new FXMLLoader();
			editPriceLoader.setLocation(getClass().getResource("/item/price/EditPrice.fxml"));
			VBox editPriceWindow = editPriceLoader.load();
			EditPriceController editPriceController = editPriceLoader.getController();
			
			editPriceWindow.setUserData(editPriceController);
			
			Scene scene = new Scene(editPriceWindow);
			
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Edit Prices");
			
			stage.setOnCloseRequest( event ->
		    {
		    	beerItem.priceList = editPriceController.priceList;
		    	//beerItem.priceList = ((EditPriceController)stage.getScene().getUserData()).priceList;
		    	stage.close();
		        //refresh prices
		    	try {
		    		previewItemController.setPrices(beerItem);
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    });
			returnPriceController = editPriceController;
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return returnPriceController;
	}
	
	@FXML
	public void openImageWindow() {
		if (beerItem == null || beerItem.id == 0) {
			Alert alert = new Alert(AlertType.ERROR, 
					"Please save the beer first.\n\n"
					+ "The beer needs to have a proper id assigned from\n"
					+ "the database before images can be saved correctly ");
			alert.showAndWait();
			return;
		}
		try {
			EditImageController editImageController = launchEditImageWindow();
			editImageController.imageList = beerItem.imageList;
			editImageController.beerItem = beerItem;
			ObservableList<ImageType> imageTypeList = FXCollections.observableArrayList();
			ObservableList<ImageSourceType> imageSourceTypeList = FXCollections.observableArrayList();
			//TODO put this in its own file. it can be done statically
			MySqlManager sql = new MySqlManager();
			Connection conn = null;
			try {
				conn = sql.connect();
				String queryString  = "SELECT * FROM image_type WHERE active = 1 ORDER BY id DESC;";
				sql.runQuery(conn, queryString, (rs) -> {
					imageTypeList.add(new ImageType(rs.getInt("id"), rs.getString("name")));
				});
				queryString  = "SELECT * FROM image_source_type WHERE active = 1 ORDER BY id DESC;";
				sql.runQuery(conn, queryString, (rs) -> {
					imageSourceTypeList.add(new ImageSourceType(rs.getInt("id"), rs.getString("name")));
				});
				conn.close();
			} catch (CJCommunicationsException e1) {
				e1.printStackTrace();
				System.out.println("not connected, aborting");
				return;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
				
			editImageController.imageTypeList = imageTypeList;
			editImageController.imageSourceTypeList = imageSourceTypeList;
			editImageController.setLayoutFields();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	
	private EditImageController launchEditImageWindow() {
		EditImageController editImageController = null;
		try {
			FXMLLoader editImageLoader = new FXMLLoader();
			editImageLoader.setLocation(getClass().getResource("/image/EditImage.fxml"));
			VBox editImageWindow = editImageLoader.load();
			editImageController = editImageLoader.getController();
			
			editImageWindow.setUserData(editImageController);
			
			Scene scene = new Scene(editImageWindow);
			
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Edit Images");
			
			stage.setOnCloseRequest( event ->
		    {
		    	stage.close();
		        //refresh all beers list
		    	try {
		    		previewItemController.setImage(beerItem);
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    });

			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return editImageController;
	}
	
	public void saveAndCloseButtonClicked() {
		boolean saved = false;
		try {
			saved = saveRecord();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(saved) {
			closeWindow();
		} 
	}
	
	private void closeWindow() {
		Stage stage = (Stage) saveButton.getScene().getWindow();
	    //stage.close();
	    stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
	}
	
	public boolean saveRecord() {
		errorMessage = "";
		
		checkBeerName();
		String beerNameColorHex = "#" + Integer.toHexString(beerNameColor.getValue().hashCode());
		String beerPourColorHex = "#" + Integer.toHexString(srmColor.getValue().hashCode());
		String beerNotesColorHex = "#" + Integer.toHexString(notesColor.getValue().hashCode());
		beerItem = new BeerMenuItem(idCheck(), fixText(beerName), beerNameColorHex, fixText(company),
				fixText(notes), fixText(style), abvCheck(), ibuCheck(), 0, beerPourColorHex);
		beerItem.notesColor = beerNotesColorHex;
				//priceCheck(price1, 1), sizeCheck(price1Size, 1), 
				//priceCheck(price2, 2), sizeCheck(price2Size, 2));
		
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
						+ "(beer_name, beer_name_color, company, notes, style, abv, ibu, srm, beer_pour_color, notes_color) VALUES "
						+ "('" + beerItem.beerName + "', '" + beerItem.beerNameColor + "', '" + beerItem.company + "', '" + beerItem.notes + "',"
						+ " '" + beerItem.style + "', '" + beerItem.abv + "', '" + beerItem.ibu + "', '" + beerItem.srm 
						+ "', '" + beerItem.beerPourColor + "', '" + beerItem.notesColor + "');";
				beerItemId = sql.runInsertQuery(conn, sqlQuery);
				
				beerItem.id = beerItemId;
				beerId.setText("" + beerItemId);
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
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
						+ "beer_name_color = '" + beerItem.beerNameColor + "', "
						+ "company = '" + beerItem.company + "', "
						//+ "location = '" + beerItem.location + "', "
						+ "notes = '" + beerItem.notes + "', "
						+ "notes_color = '" + beerItem.notesColor + "', "
						+ "style = '" + beerItem.style + "', "
						+ "abv = '" + beerItem.abv + "', "
						+ "ibu = '" + beerItem.ibu + "', "
						+ "srm = '" + beerItem.srm + "', "
						+ "beer_pour_color = '" + beerItem.beerPourColor + "' WHERE id = " + beerItem.id + ";";
				sql.runInsertQuery(conn, sqlQuery);
				
				//int priceId = 0;
				//ArrayList<ItemPrice> tempPriceList = new ArrayList<ItemPrice>();
				/*
				for (ItemPrice p : beerItem.priceList) {
					ItemPrice tempPrice = null;
					sqlQuery = "SELECT * FROM price WHERE beer_id = " + beerItem.id + " AND price_type_id = " + p.priceTypeId 
							+ " AND size = " + p.size +";";
					sql.runQuery(conn, sqlQuery, (rs) -> {
						 tempPrice = new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getDouble("price"), rs.getInt("size"), 
								rs.getInt("price_type_id"), rs.getString("price_type_name"));
					});
					//priceId = tempPriceList.get(0).id;
					if (tempPrice != null) {
						sqlQuery = "UPDATE price SET price = " + p.price + ", size = " + p.size + ", `rank` = " + p.rank + ", enabled = " + p.enabled + " "
								+ "WHERE id = " +  p.id + ";";
					} else {
						sqlQuery = "INSERT INTO price (beer_id, price, size, price_type_id, rank, enabled) VALUES "
								+ "('" + beerItem.id + "', '" + p.price + "', '" + p.size + "', '" + p.priceType.id + "', '" + p.rank + "', '" + p.enabled + "');";
					}
					sql.runInsertQuery(conn, sqlQuery);
				}*/
				/*
				tempPriceList.remove(0);
				
				sqlQuery = "SELECT * FROM price WHERE beer_id = " + beerItem.id + " AND size = " + beerItem.price2Size + ";";
				sql.runQuery(conn, sqlQuery, (rs) -> {
					tempPriceList.add(new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getDouble("price"), rs.getInt("size"), 
							rs.getInt("price_type_id"), rs.getString("price_type_name")));
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
				*/
				
				//use price list, update prices
				
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
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
	/*
	 * implement srmCheck
	 */
	/*
	public int srmCheck() {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(srmText.getText());
		} catch (Exception e) {
			// do nothing, this is not required
			//errorMessage += "\nNot a valid number in IBU field";
		}
		return retVal;
	}*/
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
	public void beerNameColorChanged() {
		//System.out.println("Changed beer name color");
		this.previewItemController.beerName.setTextFill(this.beerNameColor.getValue());
		this.previewItemController.beerNumber.setTextFill(this.beerNameColor.getValue());
	}
	
	@FXML
	public void notesColorChanged() {
		//System.out.println("Changed beer name color");
		this.previewItemController.notes.setTextFill(this.notesColor.getValue());
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
			
			/*
			 * TODO implement location
			if (e.getSource().equals(location)) {
				previewItemController.location.setText(location.getText());
			}*/
			
			if (e.getSource().equals(notes)) {
				previewItemController.notes.setText(notes.getText());
			}
			
			if (e.getSource().equals(style)) {
				previewItemController.beerStyle.setText(style.getText());
			}
			
			if (e.getSource().equals(abv)) {
				previewItemController.abv.setText(abv.getText());
			}
			/*
			 * implement srmText
			if (e.getSource().equals(srmText)) {
				previewItemController.srm.setText(srmText.getText());
			}*/
			
		}
		
	}
	
	
	
}
