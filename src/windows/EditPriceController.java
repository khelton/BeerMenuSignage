package windows;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;


public class EditPriceController {
	
	public BeerMenuItem beerItem;
	public ArrayList<ItemPrice> priceList;
	public ObservableList<ItemPriceType> priceTypeList;
	private String errorMessage;

	
	@FXML
	public ListView<VBox> pricesListView;
	
	@FXML
	public Button addPriceButton;
	@FXML
	public Button deletePriceButton;
	@FXML
	public Button saveButton;

	
	
	//Required constructor that is empty
	public EditPriceController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (priceList != null) {
			setLayoutFields(priceList);
		}
	}
	public void setLayoutFields(ArrayList<ItemPrice> priceList) {
		fillListView(priceList);
	}
	
	public void fillListView(ArrayList<ItemPrice> priceList) {
		ArrayList<VBox> priceLayoutList = new ArrayList<VBox>();
		for (int i = 0 ; i < priceList.size() ; i++) {
			VBox itemPriceLayout = null;
			try {
				FXMLLoader itemPriceLoader = new FXMLLoader();
				itemPriceLoader.setLocation(getClass().getResource("/windows/EditPriceItem.fxml"));
				itemPriceLayout = itemPriceLoader.load();
				EditPriceItemController controller = itemPriceLoader.getController();
				itemPriceLayout.setUserData(controller);
				controller.setLayoutFields(priceList.get(i), priceTypeList);
				priceLayoutList.add(itemPriceLayout);
			} catch (IOException e) {
				System.out.println("Error loading item price layout");
				e.printStackTrace();
			}
		}
		ObservableList<VBox> oList = FXCollections.observableList(priceLayoutList);
		pricesListView.setItems(oList);
	}
	
	public void saveButtonClicked() {
		try {
			savePriceRecords();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPriceButtonClicked() {
		try {
			addNewPrice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ItemPrice addNewPrice() {
		ItemPrice price = null;
		VBox itemPriceLayout = null;
		if (priceTypeList == null || priceTypeList.size() == 0) {
			return null;
		}
		ItemPriceType type = priceTypeList.get(0);
		for (ItemPriceType t : priceTypeList) {
			if (t.name.contentEquals("Normal")) {
				type = t;
				break;
			}
		}
		try {
			FXMLLoader itemPriceLoader = new FXMLLoader();
			itemPriceLoader.setLocation(getClass().getResource("/windows/EditPriceItem.fxml"));
			itemPriceLayout = itemPriceLoader.load();
			EditPriceItemController controller = itemPriceLoader.getController();
			itemPriceLayout.setUserData(controller);
			price = new ItemPrice(0, beerItem.id, priceList.size(), 0.00, 16, type, 1);
			priceList.add(price);
			controller.setLayoutFields(price, priceTypeList);
			pricesListView.getItems().add(itemPriceLayout);
		} catch (IOException e) {
			System.out.println("Error loading item price layout");
		}
		return price;
	}
	
	//TODO BUG price, size, type, rank, and enabled aren't saving correctly need to set them in price list
	public boolean savePriceRecords() {
		errorMessage = "";
		for (VBox view : pricesListView.getItems()) {
			EditPriceItemController itemController = (EditPriceItemController) view.getUserData();
			priceCheck(itemController.price);
			if (errorMessage.length() > 0) {
				Alert alert = new Alert(AlertType.ERROR, errorMessage);
				alert.showAndWait();
				return false;
			}
		}
		MySqlManager sql = new MySqlManager();
		try {
			Connection conn = sql.connect();
			for (ItemPrice p : priceList) {
				String sqlQuery = "";
				if (p.id == 0) {
					sqlQuery = "INSERT INTO price (beer_id, price, size, price_type_id, rank, enabled) VALUES "
							+ "('" + beerItem.id + "', '" + p.price + "', '" + p.size + "', '" + p.priceType.id + "', '" + p.rank + "', '" + p.enabled + "');";
				} else {
					sqlQuery = "UPDATE price SET price = " + p.price + ", size = " + p.size + ", `rank` = " + p.rank + ", enabled = " + p.enabled + " "
							+ "WHERE id = " +  p.id + ";";
				}
				sql.runInsertQuery(conn, sqlQuery);
			}
			conn.close();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, 
					"Error modifying / entering records, please try again");
			alert.showAndWait();
			return false;
		}
		return true;
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
	
	
}
