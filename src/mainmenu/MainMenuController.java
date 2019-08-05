package mainmenu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.exceptions.CJCommunicationsException;

import item.edit.EditBeerController;
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
import menulayouts.grid4x5.Item4X5Controller;
import menulayouts.grid4x5.Menu4X5Controller;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;


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
		ArrayList<ItemPriceType> priceTypeList = new ArrayList<ItemPriceType>();
		
		try {
			conn = sql.connect();
			String queryString  = "SELECT * FROM beer ORDER BY company ASC, beer_name ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				beerList.add(new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), 
						rs.getString("beer_name_color"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu"), rs.getDouble("srm"), 
						rs.getString("beer_pour_color")));
			});
			
			queryString  = "SELECT * FROM price_type WHERE active = 1 ORDER BY id DESC;";
			sql.runQuery(conn, queryString, (rs) -> {
				priceTypeList.add(new ItemPriceType(rs.getInt("id"), rs.getString("name")));
			});

			queryString  = "SELECT price.*, price_type.name as 'price_type_name', "
					+ "`schedule`.time_start as s_start, `schedule`.time_end as s_end, `schedule`.days_string as s_days "
					+ "FROM price "
					+ "LEFT JOIN price_type ON price_type.id = price.price_type_id "
					+ "LEFT JOIN schedule ON schedule.id = price_type.schedule_id "
					+ "WHERE price.active = 1 ORDER BY beer_id DESC, rank DESC, price.id DESC;";
			sql.runQuery(conn, queryString, (rs) -> {
				ItemPrice p = new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getInt("rank"), rs.getDouble("price"), rs.getInt("size"), 
						new ItemPriceType(rs.getInt("price_type_id"), rs.getString("price_type_name")), rs.getInt("enabled"));
				p.scheduleDays = rs.getString("s_days");
				p.scheduleTimeStart = rs.getString("s_start");
				p.scheduleTimeEnd = rs.getString("s_end");
				priceList.add(p);
			});
			
			queryString  = "SELECT b.id, b.beer_name, b.beer_name_color, b.company, b.notes, b.style, b.abv, b.ibu, "
					+ "b.srm, b.beer_pour_color "
					//+ "p1.price AS price1, p1.size AS size1, p2.price AS price2, p2.size AS size2 "
					+ "FROM active_beers "
					+ "LEFT JOIN beer b ON b.id = active_beers.beer_id "
					//+ "LEFT JOIN price p1 ON p1.id = active_beers.price_1_id "
					//+ "LEFT JOIN price p2 ON p2.id = active_beers.price_2_id "
					+ "WHERE 1 "
					+ "ORDER BY active_beers.id ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				activeBeerList.add(new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), 
						rs.getString("beer_name_color"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu"), rs.getDouble("srm"), 
						rs.getString("beer_pour_color")));
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
		ArrayList<ItemPrice> activeBeersPriceList =   (ArrayList<ItemPrice>) priceList.clone();
		if (beerList != null && beerList.size() > 0) {
			for(BeerMenuItem b : beerList) {
				for (int i = priceList.size() - 1; i >= 0; i--) {
					if (priceList.get(i).beerId == b.id) {
						b.addPrice(priceList.get(i));
						priceList.remove(i);
					}
				}
			}
			allBeersListView.setItems(beerList);
			/*
			for(BeerMenuItem b : beerList) {
				//b.resolvePrices();
			}
			allBeersListView.setItems(beerList);
			for(BeerMenuItem b : beerList) {
				for (int i = priceList.size() - 1; i >= 0; i--) {
					if (priceList.get(i).beerId == b.id) {
						b.addPrice(priceList.get(i));
						priceList.remove(i);
					}
				}
			}*/
		}
		
		if (activeBeerList != null && activeBeerList.size() > 0) {
			for(BeerMenuItem b : activeBeerList) {
				for (int i = activeBeersPriceList.size() - 1; i >= 0; i--) {
					if (activeBeersPriceList.get(i).beerId == b.id) {
						b.addPrice(activeBeersPriceList.get(i));
						activeBeersPriceList.remove(i);
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
			int priceId1, priceId2;
			if (newBeer.priceList.get(0).size < newBeer.priceList.get(1).size) {
				priceId1 = newBeer.priceList.get(0).id;
				priceId2 = newBeer.priceList.get(1).id;
			} else {
				priceId1 = newBeer.priceList.get(1).id;
				priceId2 = newBeer.priceList.get(0).id;
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
			menuLoader.setLocation(getClass().getResource("/menulayouts/grid4x5/Menu.fxml"));
			GridPane menuLayout = menuLoader.load();
			Menu4X5Controller menuController = menuLoader.getController();
			menuController.setLayout(activeBeersListView.getItems());
			Scene scene2 = new Scene(menuLayout);
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage2 = new Stage();
			stage2.setScene(scene2);
			stage2.setFullScreen(true);
			
			stage2.show();
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
			beerLoader.setLocation(getClass().getResource("/menulayouts/grid4x5/Item.fxml"));
			VBox beerLayout = beerLoader.load();
			Item4X5Controller beerItemController = beerLoader.getController();
			beerLayout.setUserData(beerItemController);
			
			editBeerController.previewPane.add(beerLayout, 0, 0);
			editBeerController.previewItemController = beerItemController;
			
			editBeerWindow.setUserData(editBeerController);
			
			Scene scene = new Scene(editBeerWindow);
			
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
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
	
	
	
	/*
	public void fillGridPane(GridPane grid) {
		if (this.activeBeersListView == null)
			return;
		ObservableList<BeerMenuItem> activeBeerList = this.activeBeersListView.getItems();
		int gridRows = grid.getRowConstraints().size();
		int gridCols = grid.getColumnConstraints().size();
		int i = 1;
		for (int y = 0 ; y  < gridRows; y++) {
			for (int x = 0 ; x  < gridCols; x++) {
				BeerMenuItem item = null;
				if (activeBeerList.size() < i) {
					item = new BeerMenuItem();
				} else if (activeBeerList.get(i-1) == null) {
					item = new BeerMenuItem();
				} else {
					item = activeBeerList.get(i-1);
				}
				VBox beerLayout = null;
				try {
					FXMLLoader beerLoader = new FXMLLoader();
					beerLoader.setLocation(getClass().getResource("/menulayouts/grid4x5/Item.fxml"));
					beerLayout = beerLoader.load();
					Item4X5Controller controller = beerLoader.getController();
					beerLayout.setUserData(controller);
					//fillBeerLayout(beerLayout, controller, item, i);
					controller.beerItem = item;
					item.beerNumber = i;
					controller.setLayout(item);
				} catch (IOException e) {
					e.printStackTrace();
				}
				i++;
				if (beerLayout != null)
					grid.add(beerLayout, x, y);
			}
		}
	}*/
	
}