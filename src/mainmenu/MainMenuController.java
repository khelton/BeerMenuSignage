package mainmenu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.exceptions.CJCommunicationsException;

import image.ImageSourceType;
import image.ImageType;
import image.ItemImage;
import item.edit.EditBeerController;
import item.price.EditPriceTypeController;
import item.price.PriceTypeListViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import menulayouts.grid2x10.Item2X10Controller;
import menulayouts.grid2x10.Menu2X10Controller;
import menulayouts.grid4x5.Item4X5Controller;
import menulayouts.grid4x5.Menu4X5Controller;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;
import types.PriceSchedule;


public class MainMenuController {
	
	public String dbName = "churchill";
	
	@FXML
	public TextField dbHost;
	@FXML
	public TextField dbPort;
	
	@FXML
	public TextField dbUsername;
	@FXML
	private TextField dbPassword;
	
	@FXML
	public CheckBox allCaps;
	
	@FXML
	public ListView<BeerMenuItem> activeBeersListView;
	@FXML
	public ListView<BeerMenuItem> allBeersListView;
	
	
	
	
	//Required constructor that is empty
	public MainMenuController() {}
	
	@FXML
	public void initialize() {
		//dbHost.setText("192.168.2.246");
		//MySqlManager.dbHost = "192.168.2.246";
		//fillBeerLists();
		MySqlManager.dbHost = dbHost.getText().trim();
		MySqlManager.dbPort = dbPort.getText().trim();
		MySqlManager.dbName = "churchill";
		
		allCaps.setSelected(false);
	}
	
	@FXML
	private void refreshConnection() {
		MySqlManager.dbHost = dbHost.getText().trim();
		MySqlManager.dbPort = dbPort.getText().trim();
		if (dbUsername.getText().trim().length() > 0)
			MySqlManager.setUsername(dbUsername.getText().trim());
		if (dbPassword.getText().trim().length() > 0)
			MySqlManager.setPassword(dbPassword.getText().trim());
		fillBeerListBoxes();
	}
	
	public void fillBeerListBoxes() {
		MySqlManager sql = new MySqlManager();
		Connection conn = null;
		ObservableList<BeerMenuItem> beerList = FXCollections.observableArrayList();
		ObservableList<BeerMenuItem> activeBeerList = FXCollections.observableArrayList();
		ArrayList<ItemPrice> priceList = new ArrayList<ItemPrice>();
		//ArrayList<ItemPriceType> priceTypeList = new ArrayList<ItemPriceType>();
		ArrayList<ItemImage> imageList = new ArrayList<ItemImage>();
		
		try {
			conn = sql.connect();
			String queryString  = "SELECT * FROM beer ORDER BY company ASC, beer_name ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				BeerMenuItem b = new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), 
						rs.getString("beer_name_color"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu"), rs.getDouble("srm"), 
						rs.getString("beer_pour_color"));
				b.notesColor = rs.getString("notes_color");
				beerList.add(b);
			});
			/*
			queryString  = "SELECT price_type.*, "
					+ "`schedule`.name as s_name, `schedule`.days_string as s_days "
					+ "`schedule`.time_start as s_start, `schedule`.time_end as s_end "
					+ "FROM price_type "
					+ "LEFT JOIN schedule ON schedule.id = price_type.schedule_id "
					+ "WHERE active = 1 ORDER BY id DESC;";
			sql.runQuery(conn, queryString, (rs) -> {
				PriceSchedule s  = new PriceSchedule(rs.getInt("schedule_id"), rs.getString("s_name"), 
						rs.getString("s_start"), rs.getString("s_end"), rs.getString("s_days"));
				ItemPriceType pt = new ItemPriceType(rs.getInt("id"), rs.getString("name"), s, rs.getInt("enabled"));
				priceTypeList.add(pt);
			});*/

			queryString  = "SELECT price.*, pt.id as 'pt_id', pt.name as 'pt_name', pt.enabled as 'pt_enabled', "
					+ "s.id as 's_id', s.name as 's_name', s.days_string as 's_days', "
					+ "s.time_start as 's_start', s.time_end as 's_end' "
					+ "FROM price "
					+ "LEFT JOIN price_type pt ON pt.id = price.price_type_id "
					+ "LEFT JOIN schedule s ON s.id = pt.schedule_id "
					+ "WHERE price.active = 1 ORDER BY beer_id DESC, rank DESC, price.id DESC;";
			sql.runQuery(conn, queryString, (rs) -> {
				PriceSchedule s  = new PriceSchedule(rs.getInt("s_id"), rs.getString("s_name"), 
						rs.getString("s_start"), rs.getString("s_end"), rs.getString("s_days"));
				ItemPriceType pt = new ItemPriceType(rs.getInt("pt_id"), rs.getString("pt_name"),
						s, rs.getInt("pt_enabled"));
				ItemPrice p = new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getInt("rank"), rs.getDouble("price"), rs.getInt("size"), 
						pt, rs.getInt("enabled"));
				priceList.add(p);
			});
			
			queryString  = "SELECT image.*, it.name as 'it_name', "
					+ "ist.name as 'ist_name' "
					+ "FROM image "
					+ "LEFT JOIN image_type it ON it.id = image.image_type_id "
					+ "LEFT JOIN image_source_type ist ON ist.id = image.image_source_type_id "
					+ "WHERE image.active = 1 ORDER BY beer_id DESC, rank DESC, image.id DESC;";
			sql.runQuery(conn, queryString, (rs) -> {
				ImageType it = new ImageType(rs.getInt("image_type_id"), rs.getString("it_name"));
				ImageSourceType ist = new ImageSourceType(rs.getInt("image_source_type_id"), rs.getString("ist_name"));
				ItemImage im = new ItemImage(rs.getInt("id"), rs.getInt("beer_id"), rs.getInt("rank"), rs.getString("image_src"), 
						it, ist, rs.getInt("enabled"));
				imageList.add(im);
			});
			
			queryString  = "SELECT b.id, b.beer_name, b.beer_name_color, b.company, b.notes, b.style, b.abv, b.ibu, "
					+ "b.srm, b.beer_pour_color, b.notes_color "
					+ "FROM active_beers "
					+ "LEFT JOIN beer b ON b.id = active_beers.beer_id "
					+ "WHERE 1 "
					+ "ORDER BY active_beers.id ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				BeerMenuItem b = new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), 
						rs.getString("beer_name_color"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu"), rs.getDouble("srm"), 
						rs.getString("beer_pour_color"));
				b.notesColor = rs.getString("notes_color");
				activeBeerList.add(b);
			});
		
			conn.close();
		} catch (CJCommunicationsException e1) {
			e1.printStackTrace();
			System.out.println("not connected, aborting");
			return;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<ItemPrice> activeBeersPriceList = (ArrayList<ItemPrice>) priceList.clone();
		@SuppressWarnings("unchecked")
		ArrayList<ItemImage> activeBeersImageList = (ArrayList<ItemImage>) imageList.clone();
		
		if (beerList != null && beerList.size() > 0) {
			for(BeerMenuItem b : beerList) {
				for (int i = priceList.size() - 1; i >= 0; i--) {
					if (priceList.get(i).beerId == b.id) {
						b.addPrice(priceList.get(i));
						priceList.remove(i);
					}
				}
				for (int i = imageList.size() - 1; i >= 0; i--) {
					if (imageList.get(i).beerId == b.id) {
						b.addImage(imageList.get(i));
						imageList.remove(i);
					}
				}
			}
			allBeersListView.setItems(beerList);
		}
		
		if (activeBeerList != null && activeBeerList.size() > 0) {
			for(BeerMenuItem b : activeBeerList) {
				for (int i = activeBeersPriceList.size() - 1; i >= 0; i--) {
					if (activeBeersPriceList.get(i).beerId == b.id) {
						b.addPrice(activeBeersPriceList.get(i));
						activeBeersPriceList.remove(i);
					}
				}
				for (int i = activeBeersImageList.size() - 1; i >= 0; i--) {
					if (activeBeersImageList.get(i).beerId == b.id) {
						b.addImage(activeBeersImageList.get(i));
						activeBeersImageList.remove(i);
					}
				}
			}
			activeBeersListView.setItems(activeBeerList);
		}
		
	}
	
	@FXML
	private void swapSelected() {
		if (allBeersListView.getSelectionModel().getSelectedItem() == null ||
				activeBeersListView.getSelectionModel().getSelectedItem() == null) {
			Alert alert = new Alert(AlertType.ERROR, 
					"A beer from both columns must be selected");
			alert.showAndWait();
			return;
		}
		
		BeerMenuItem newBeer = allBeersListView.getSelectionModel().getSelectedItem();
		BeerMenuItem currentActiveBeer  = activeBeersListView.getSelectionModel().getSelectedItem();
		
		int currentActiveBeerIndex = 0;
		ObservableList<BeerMenuItem> items = activeBeersListView.getItems();
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).equals(currentActiveBeer)) {
				System.out.println("beer found");
				items.remove(i);
				items.add(i, newBeer);
				activeBeersListView.setItems(items);
				activeBeersListView.getSelectionModel().select(i);
				currentActiveBeerIndex = i;
				break;
			}
		}

		// update the active beer in the database
		try {
			MySqlManager sql = new MySqlManager();
			Connection conn = sql.connect();
			int priceId1 = 0;
			int priceId2 = 0;
			// This needs to be taken out, prices don't work like they used to
			if (newBeer.priceList != null && newBeer.priceList.size() > 1) {
				if (newBeer.priceList.get(0).size < newBeer.priceList.get(1).size) {
					priceId1 = 0;
					priceId2 = 0;
				} else {
					priceId1 = 0;
					priceId2 = 0;
				}
			}
			//save current beer
			String sqlQuery = "UPDATE active_beers SET beer_id = " + newBeer.id + ", "
					+ "price_1_id = " + priceId1 + ", "
					+ "price_2_id = " + priceId2 + " WHERE id = " + (currentActiveBeerIndex + 1) + ";";
			sql.runInsertQuery(conn, sqlQuery);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	
	@FXML
	private void launchFullScreenMenu() {
		try {
			if (activeBeersListView == null)
				return;
			FXMLLoader menuLoader = new FXMLLoader();
			menuLoader.setLocation(getClass().getResource("/menulayouts/grid2x10/Menu.fxml"));
			GridPane menuLayout = menuLoader.load();
			Menu2X10Controller menuController = menuLoader.getController();
			menuController.setLayout(activeBeersListView.getItems());
			Scene scene2 = new Scene(menuLayout);
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage2 = new Stage();
			stage2.setScene(scene2);
			stage2.setTitle("Fullscreen Menu");
			stage2.setFullScreen(true);
			stage2.setOnCloseRequest( event ->
		    {
		    	menuController.stopPriceIntervalTimer();
		    	stage2.close();
		        //refresh all beers list
		    	try {
		    		refreshConnection();
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    });
			
			stage2.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	@FXML
	private void editPriceTypesButton() {
		try {
			EditPriceTypeController controller = launchEditPriceTypeWindow();
			//editBeerController.beerItem = new BeerMenuItem();
			ArrayList<ItemPriceType> priceTypeList = new ArrayList<ItemPriceType>();
			ObservableList<PriceSchedule> scheduleList = FXCollections.observableArrayList();
			MySqlManager sql = new MySqlManager();
			Connection conn = null;
			try {
				conn = sql.connect();
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
				queryString  = "SELECT * FROM schedule WHERE active = 1 ORDER BY name ASC;";
				sql.runQuery(conn, queryString, (rs) -> {
					PriceSchedule s  = new PriceSchedule(rs.getInt("id"), rs.getString("name"), 
							rs.getString("time_start"), rs.getString("time_end"), rs.getString("days_string"));
					scheduleList.add(s);
				});
				conn.close();
			} catch (CJCommunicationsException e1) {
				e1.printStackTrace();
				System.out.println("not connected, aborting");
				return;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			controller.priceTypeList = priceTypeList;
			controller.scheduleList = scheduleList;
			controller.setLayoutFields();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@FXML
	private void addBeerButton() {
		try {
			EditBeerController editBeerController = launchEditBeerWindow();
			editBeerController.beerItem = new BeerMenuItem();
			editBeerController.setLayoutFields();
			editBeerController.updatePreview();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void editBeerButton() {
		try {
			EditBeerController editBeerController = launchEditBeerWindow();
			if (allBeersListView.getSelectionModel().getSelectedItem() != null &&
					editBeerController != null) {
				BeerMenuItem b = allBeersListView.getSelectionModel().getSelectedItem();
				editBeerController.beerItem = b;
				editBeerController.setLayoutFields();
				editBeerController.updatePreview();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	

	/* TODO implement button and view to select/save pricetypes
	@FXML
	private void editPriceTypesButton()
		// Open saved images window
	}*/
	
	/* TODO implement button and view to select/save schedules
	@FXML
	private void editSchedulesButton()
		// Open saved images window
	}*/
	
	private EditBeerController launchEditBeerWindow() {
		EditBeerController editBeerController = null;
		try {
			FXMLLoader editBeerLoader = new FXMLLoader();
			editBeerLoader.setLocation(getClass().getResource("/item/edit/EditBeer.fxml"));
			VBox editBeerWindow = editBeerLoader.load();
			editBeerController = editBeerLoader.getController();
			
			FXMLLoader beerLoader = new FXMLLoader();
			beerLoader.setLocation(getClass().getResource("/menulayouts/grid2x10/Item.fxml"));
			VBox beerLayout = beerLoader.load();
			Item2X10Controller beerItemController = beerLoader.getController();
			beerLayout.setUserData(beerItemController);
			
			editBeerController.previewPane.add(beerLayout, 0, 0);
			editBeerController.previewItemController = beerItemController;
			
			editBeerWindow.setUserData(editBeerController);
			
			Scene scene = new Scene(editBeerWindow);
			
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Edit Beer");
			stage.setOnCloseRequest( event ->
		    {
		    	stage.close();
		        //refresh all beers list
		    	try {
		    		refreshConnection();
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    });

			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return editBeerController;
	}
	
	private EditPriceTypeController launchEditPriceTypeWindow() {
		EditPriceTypeController controller = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/item/price/EditPriceType.fxml"));
			VBox vBox = loader.load();
			controller = loader.getController();
			
			vBox.setUserData(controller);
			
			Scene scene = new Scene(vBox);
			
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Edit Price Types");
			stage.setOnCloseRequest( event ->
		    {
		    	stage.close();
		        //refresh all beers list
		    	try {
		    		refreshConnection();
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    });

			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return controller;
	}
	

	
}
