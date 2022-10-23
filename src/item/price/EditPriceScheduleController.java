package item.price;

import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import mysql.FieldChecker;
import mysql.MySqlManager;
import types.PriceSchedule;


public class EditPriceScheduleController {
	
	public PriceSchedule schedule;
	public ArrayList<PriceSchedule> scheduleList;
	//private String errorMessage;

	
	@FXML
	public ListView<VBox> scheduleListView;
	
	@FXML
	public Button addButton;
	@FXML
	public Button deletePriceButton;
	@FXML
	public Button saveButton;

	
	
	//Required constructor that is empty
	public EditPriceScheduleController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (scheduleList != null) {
			setLayoutFields(scheduleList);
		}
	}
	public void setLayoutFields(ArrayList<PriceSchedule> scheduleList) {
		fillListView(scheduleList);
	}
	
	public void fillListView(ArrayList<PriceSchedule> scheduleList) {
		ArrayList<VBox> layoutList = new ArrayList<VBox>();
		for (int i = 0 ; i < scheduleList.size() ; i++) {
			VBox layout = null;
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/item/price/PriceScheduleListViewLayout.fxml"));
				layout = loader.load();
				PriceScheduleListViewController controller = loader.getController();
				layout.setUserData(controller);
				controller.setLayoutFields(scheduleList.get(i));
				layoutList.add(layout);
			} catch (IOException e) {
				System.out.println("Error loading item price layout");
				e.printStackTrace();
			}
		}
		ObservableList<VBox> oList = FXCollections.observableList(layoutList);
		scheduleListView.setItems(oList);
	}
	
	public void saveButtonClicked() {
		try {
			saveRecords();
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

	@FXML
	public void deleteButtonClicked() {
		if (scheduleListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		VBox item = scheduleListView.getSelectionModel().getSelectedItem();
		PriceScheduleListViewController itemController = (PriceScheduleListViewController) item.getUserData();
		if (itemController.schedule.id != 0) {
			// we only have to remove it from the database if it has previously been saved in the db
			String sqlQuery = "UPDATE schedule SET active = 0 WHERE id = " +  itemController.schedule.id + ";";
			boolean deleteSuccess = updateScheduleRecord(sqlQuery);
			if (!deleteSuccess) {
				return; // don't remove the item price because it didn't get removed in the db
			}
		}
		scheduleList.remove(itemController.schedule);
		scheduleListView.getItems().remove(item);
		
	}
	
	public PriceSchedule addNew() {
		PriceSchedule schedule = null;
		VBox layout = null;
		if (scheduleList == null || scheduleList.size() == 0) {
			return null;
		}
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/item/price/PriceScheduleListViewLayout.fxml"));
			layout = loader.load();
			PriceScheduleListViewController controller = loader.getController();
			layout.setUserData(controller);
			schedule = new PriceSchedule(0, "", "00:00:00", "23:59:59", "1111111");
			scheduleList.add(schedule);
			controller.setLayoutFields(schedule);
			scheduleListView.getItems().add(layout);
		} catch (IOException e) {
			System.out.println("Error loading item price layout");
		}
		return schedule;
	}
	
	public boolean saveRecords() {
		//errorMessage = "";
		FieldChecker fc = new FieldChecker();
		ObservableList<VBox> vBoxList =  scheduleListView.getItems();
		scheduleList.clear();
		for ( int i = 0 ; i < vBoxList.size() ; i++ ) {
		//for (VBox view : pricesListView.getItems()) {
			PriceScheduleListViewController itemController = (PriceScheduleListViewController) vBoxList.get(i).getUserData();
			itemController.schedule.name = fc.nameCheck(itemController.name);
			itemController.schedule.startTime = fc.timeCheck(itemController.startTime);
			itemController.schedule.endTime = fc.timeCheck(itemController.endTime);
			itemController.schedule.daysEnabledString = fc.getDays(itemController);
			if (fc.errorMessage.length() > 0) {
				Alert alert = new Alert(AlertType.ERROR, fc.errorMessage);
				alert.showAndWait();
				return false;
			}
			//itemController.rank.setText("" + itemController.itemPrice.rank);
			scheduleList.add(itemController.schedule);
		}
		MySqlManager sql = new MySqlManager();
		try {
			Connection conn = sql.connect();
			//SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME; 
			for (PriceSchedule s : scheduleList) {
				//System.out.println(s.startTime.toString() + " " + s.endTime.toString());
				String sqlQuery = "";
				if (s.id == 0) {
					sqlQuery = "INSERT INTO schedule (name, time_start, time_end, days_string, active) VALUES "
							+ "('" + s.name + "', '" + s.startTime.format(formatter) + "', '" + s.endTime.format(formatter)  
							+ "', '" + s.daysEnabledString + "', '1');";
				} else {
					sqlQuery = "UPDATE schedule SET name = '" + s.name + "', time_start = '" + s.startTime.format(formatter)
							+ "', time_end = '" + s.endTime.format(formatter) + "', days_string = '" + s.daysEnabledString + "' "
							+ "WHERE id = '" +  s.id + "';";
				}
				sql.runInsertQuery(conn, sqlQuery);
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, 
					"Error modifying / entering records, please try again");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	public boolean updateScheduleRecord(String sqlQuery) {
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
