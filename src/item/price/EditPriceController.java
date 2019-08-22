package item.price;

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
import mysql.FieldChecker;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;


public class EditPriceController {
	
	public BeerMenuItem beerItem;
	public ArrayList<ItemPrice> priceList;
	public ObservableList<ItemPriceType> priceTypeList;
	//private String errorMessage;

	
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
				itemPriceLoader.setLocation(getClass().getResource("/item/price/PriceItemListViewLayout.fxml"));
				itemPriceLayout = itemPriceLoader.load();
				PriceItemListViewLayoutController controller = itemPriceLoader.getController();
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
	
	@FXML
	public void moveUpButtonClicked() {
		int selectedIndex = pricesListView.getSelectionModel().getSelectedIndex();
		if (selectedIndex == 0) {
			return; // we can't move the price up, its the first one already
		}
		ObservableList<VBox> allPriceLayouts = pricesListView.getItems();
		VBox priceBoxSelected = allPriceLayouts.get(selectedIndex);
		VBox priceBox2 = allPriceLayouts.get(selectedIndex - 1);
		allPriceLayouts.set(selectedIndex - 1, priceBoxSelected);
		allPriceLayouts.set(selectedIndex, priceBox2);
		
		pricesListView.getSelectionModel().select(priceBoxSelected);
	}
	@FXML
	public void moveDownButtonClicked() {
		int selectedIndex = pricesListView.getSelectionModel().getSelectedIndex();
		int count = pricesListView.getItems().size();
		if (selectedIndex == count - 1) {
			return; // we can't move the price down, its the last one already
		}
		ObservableList<VBox> allPriceLayouts = pricesListView.getItems();
		VBox priceBoxSelected = allPriceLayouts.get(selectedIndex);
		VBox priceBox2 = allPriceLayouts.get(selectedIndex - 1);
		allPriceLayouts.set(selectedIndex + 1, priceBoxSelected);
		allPriceLayouts.set(selectedIndex, priceBox2);
		
		pricesListView.getSelectionModel().select(priceBoxSelected);
	}
	@FXML
	public void deleteButtonClicked() {
		if (pricesListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		VBox priceBox = pricesListView.getSelectionModel().getSelectedItem();
		PriceItemListViewLayoutController itemController = (PriceItemListViewLayoutController) priceBox.getUserData();
		if (itemController.itemPrice.id != 0) {
			// we only have to remove it from the database if it has previously been saved in the db
			String sqlQuery = "UPDATE price SET active = 0 WHERE id = " +  itemController.itemPrice.id + ";";
			boolean deleteSuccess = updatePriceRecord(sqlQuery);
			if (!deleteSuccess) {
				return; // don't remove the item price because it didn't get removed in the db
			}
		}
		priceList.remove(itemController.itemPrice);
		pricesListView.getItems().remove(priceBox);
		
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
			itemPriceLoader.setLocation(getClass().getResource("/item/price/PriceItemListViewLayout.fxml"));
			itemPriceLayout = itemPriceLoader.load();
			PriceItemListViewLayoutController controller = itemPriceLoader.getController();
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
	
	public boolean savePriceRecords() {
		//errorMessage = "";
		FieldChecker fc = new FieldChecker();
		ObservableList<VBox> vBoxList =  pricesListView.getItems();
		priceList.clear();
		for ( int i = 0 ; i < vBoxList.size() ; i++ ) {
		//for (VBox view : pricesListView.getItems()) {
			PriceItemListViewLayoutController itemController = (PriceItemListViewLayoutController) vBoxList.get(i).getUserData();
			itemController.itemPrice.price = fc.priceCheck(itemController.price);
			itemController.itemPrice.size = fc.sizeCheck(itemController.priceSize);
			itemController.itemPrice.priceType = itemController.priceTypeDropdown.getValue();
			itemController.itemPrice.rank = i + 1;
			itemController.itemPrice.enabled = (itemController.enabled.isSelected()) ? 1 : 0 ;
			if (fc.errorMessage.length() > 0) {
				Alert alert = new Alert(AlertType.ERROR, fc.errorMessage);
				alert.showAndWait();
				return false;
			}
			itemController.rank.setText("" + itemController.itemPrice.rank);
			priceList.add(itemController.itemPrice);
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
					sqlQuery = "UPDATE price SET price = " + p.price + ", size = " + p.size + ", price_type_id = " + p.priceType.id +", `rank` = " + p.rank + ", enabled = " + p.enabled + " "
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
	
	public boolean updatePriceRecord(String sqlQuery) {
		MySqlManager sql = new MySqlManager();
		try {
			Connection conn = sql.connect();
			sql.runInsertQuery(conn, sqlQuery);
			conn.close();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, 
					"Error modifying / entering records, please try again");
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	
}
