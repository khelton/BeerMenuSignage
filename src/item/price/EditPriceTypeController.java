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
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;
import types.PriceSchedule;


public class EditPriceTypeController {
	
	public ItemPriceType priceType;
	public ArrayList<ItemPriceType> priceTypeList;
	public ObservableList<PriceSchedule> scheduleList;
	private String errorMessage;

	
	@FXML
	public ListView<VBox> priceTypeListView;
	
	@FXML
	public Button addPriceButton;
	@FXML
	public Button deletePriceButton;
	@FXML
	public Button saveButton;

	
	
	//Required constructor that is empty
	public EditPriceTypeController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (priceTypeList != null) {
			setLayoutFields(priceTypeList);
		}
	}
	public void setLayoutFields(ArrayList<ItemPriceType> priceTypeList) {
		fillListView(priceTypeList);
	}
	
	public void fillListView(ArrayList<ItemPriceType> priceTypeList) {
		ArrayList<VBox> layoutList = new ArrayList<VBox>();
		for (int i = 0 ; i < priceTypeList.size() ; i++) {
			VBox layout = null;
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/item/price/PriceTypeListViewLayout.fxml"));
				layout = loader.load();
				PriceTypeListViewController controller = loader.getController();
				layout.setUserData(controller);
				controller.setLayoutFields(priceTypeList.get(i), scheduleList);
				layoutList.add(layout);
			} catch (IOException e) {
				System.out.println("Error loading item price layout");
				e.printStackTrace();
			}
		}
		ObservableList<VBox> oList = FXCollections.observableList(layoutList);
		priceTypeListView.setItems(oList);
	}
	
	public void saveButtonClicked() {
		try {
			savePriceTypeRecords();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addButtonClicked() {
		try {
			addNew();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	@FXML
	public void moveUpButtonClicked() {
		int selectedIndex = priceTypeListView.getSelectionModel().getSelectedIndex();
		if (selectedIndex == 0) {
			return; // we can't move the price up, its the first one already
		}
		ObservableList<VBox> allPriceTypeLayouts = priceTypeListView.getItems();
		VBox itemSelected = allPriceTypeLayouts.get(selectedIndex);
		VBox item2 = allPriceTypeLayouts.get(selectedIndex - 1);
		allPriceTypeLayouts.set(selectedIndex - 1, itemSelected);
		allPriceTypeLayouts.set(selectedIndex, item2);
		
		priceTypeListView.getSelectionModel().select(itemSelected);
	}
	@FXML
	public void moveDownButtonClicked() {
		int selectedIndex = priceTypeListView.getSelectionModel().getSelectedIndex();
		int count = priceTypeListView.getItems().size();
		if (selectedIndex == count - 1) {
			return; // we can't move the price down, its the last one already
		}
		ObservableList<VBox> allPriceLayouts = priceTypeListView.getItems();
		VBox itemSelected = allPriceLayouts.get(selectedIndex);
		VBox item2 = allPriceLayouts.get(selectedIndex - 1);
		allPriceLayouts.set(selectedIndex + 1, itemSelected);
		allPriceLayouts.set(selectedIndex, item2);
		
		priceTypeListView.getSelectionModel().select(itemSelected);
	}
	*/
	@FXML
	public void deleteButtonClicked() {
		if (priceTypeListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		VBox item = priceTypeListView.getSelectionModel().getSelectedItem();
		PriceTypeListViewController itemController = (PriceTypeListViewController) item.getUserData();
		if (itemController.priceType.id != 0) {
			// we only have to remove it from the database if it has previously been saved in the db
			String sqlQuery = "UPDATE price_type SET active = 0 WHERE id = " +  itemController.priceType.id + ";";
			boolean deleteSuccess = updatePriceRecord(sqlQuery);
			if (!deleteSuccess) {
				return; // don't remove the item price because it didn't get removed in the db
			}
		}
		priceTypeList.remove(itemController.priceType);
		priceTypeListView.getItems().remove(item);
		
	}
	
	public ItemPriceType addNew() {
		ItemPriceType priceType = null;
		VBox layout = null;
		if (priceTypeList == null || priceTypeList.size() == 0) {
			return null;
		}
		PriceSchedule schedule = scheduleList.get(0);
		for (PriceSchedule s : scheduleList) {
			if (s.name.contentEquals("24/7")) {
				schedule = s;
				break;
			}
		}
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/item/price/PriceTypeListViewLayout.fxml"));
			layout = loader.load();
			PriceTypeListViewController controller = loader.getController();
			layout.setUserData(controller);
			priceType = new ItemPriceType(0, "", schedule, 1);
			priceTypeList.add(priceType);
			controller.setLayoutFields(priceType, scheduleList);
			priceTypeListView.getItems().add(layout);
		} catch (IOException e) {
			System.out.println("Error loading item price layout");
		}
		return priceType;
	}
	
	public boolean savePriceTypeRecords() {
		errorMessage = "";
		ObservableList<VBox> vBoxList =  priceTypeListView.getItems();
		priceTypeList.clear();
		for ( int i = 0 ; i < vBoxList.size() ; i++ ) {
		//for (VBox view : pricesListView.getItems()) {
			PriceTypeListViewController itemController = (PriceTypeListViewController) vBoxList.get(i).getUserData();
			itemController.priceType.name = nameCheck(itemController.name);
			itemController.priceType.schedule = itemController.priceScheduleDropdown.getValue();
			itemController.priceType.enabled = (itemController.enabled.isSelected()) ? 1 : 0 ;
			if (errorMessage.length() > 0) {
				Alert alert = new Alert(AlertType.ERROR, errorMessage);
				alert.showAndWait();
				return false;
			}
			//itemController.rank.setText("" + itemController.itemPrice.rank);
			priceTypeList.add(itemController.priceType);
		}
		MySqlManager sql = new MySqlManager();
		try {
			Connection conn = sql.connect();
			for (ItemPriceType p : priceTypeList) {
				String sqlQuery = "";
				if (p.id == 0) {
					sqlQuery = "INSERT INTO price_type (name, schedule_id, enabled) VALUES "
							+ "('" + p.name + "', '" + p.schedule.id + "', '" + p.enabled + "');";
				} else {
					sqlQuery = "UPDATE price_type SET name = '" + p.name + "', schedule_id = '" + p.schedule.id 
							+ "', enabled = '" + p.enabled + "' "
							+ "WHERE id = '" +  p.id + "';";
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
	
	public String nameCheck(TextField text) {
		String retVal = text.getText().trim().replace("'", "\\'");
		if (retVal.length() == 0)
			errorMessage = "name is empty";
		
		return retVal;
	}	
	
}
