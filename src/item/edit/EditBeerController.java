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
import menulayouts.grid2x10x1price.Item2X10Controller;
import menulayouts.grid4x5.Item4X5Controller;
import mysql.FieldChecker;
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
	
	//private String errorMessage;
	
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
				String queryString  = "SELECT price_type.*, "
						+ "s.name as 's_name', s.days_string as 's_days', "
						+ "s.time_start as 's_start', s.time_end as 's_end' "
						+ "FROM price_type "
						+ "LEFT JOIN schedule s ON s.id = price_type.schedule_id "
						+ "WHERE price_type.active = 1 ORDER BY name ASC;";
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
		//errorMessage = "";
		FieldChecker fc = new FieldChecker();
		fc.beerNameCheck(beerName);
		String beerNameColorHex = FieldChecker.getHexColor(beerNameColor);
		//System.out.println(beerNameColorHex);
		//String beerNameColorHex = "#" + Integer.toHexString(beerNameColor.getValue().hashCode());
		String beerPourColorHex = FieldChecker.getHexColor(srmColor);
		String beerNotesColorHex = FieldChecker.getHexColor(notesColor);
		beerItem = new BeerMenuItem(fc.idCheck(beerId), fc.fixText(beerName), beerNameColorHex, fc.fixText(company),
				fc.fixText(notes), fc.fixText(style), fc.abvCheck(abv), fc.ibuCheck(ibu), 0, beerPourColorHex);
		beerItem.notesColor = beerNotesColorHex;
				//priceCheck(price1, 1), sizeCheck(price1Size, 1), 
				//priceCheck(price2, 2), sizeCheck(price2Size, 2));
		
		if (fc.errorMessage.trim().length() > 0) {
			fc.errorMessage += "\n\nPlease fix these errors and hit save again";
			Alert alert = new Alert(AlertType.ERROR, fc.errorMessage);
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
