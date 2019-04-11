package windows;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.mysql.cj.exceptions.CJCommunicationsException;
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
import menulayouts.BeerItemLayoutController;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;


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
		fillBeerLists();
	}
	
	public void fillBeerLists() {
		MySqlManager sql = new MySqlManager();
		Connection conn = null;
		ObservableList<BeerMenuItem> beerList = FXCollections.observableArrayList();
		ObservableList<BeerMenuItem> activeBeerList = FXCollections.observableArrayList();
		ArrayList<ItemPrice> priceList = new ArrayList<ItemPrice>();
		//ObservableList<BeerMenuItem> beerObservableList = (ObservableList<BeerMenuItem>) beerList;
		
		try {
			conn = sql.connect();
			String queryString  = "SELECT * FROM beer ORDER BY company ASC, beer_name ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				beerList.add(new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu")));
			});

			queryString  = "SELECT * FROM price WHERE active = 1 ORDER BY beer_id DESC;";
			sql.runQuery(conn, queryString, (rs) -> {
				priceList.add(new ItemPrice(rs.getInt("id"), rs.getInt("beer_id"), rs.getDouble("price"), rs.getInt("size")));
			});
			
			queryString  = "SELECT b.id, b.beer_name, b.company, b.notes, b.style, b.abv, b.ibu, "
					+ "p1.price AS price1, p1.size AS size1, p2.price AS price2, p2.size AS size2 "
					+ "FROM active_beers "
					+ "LEFT JOIN beer b ON b.id = active_beers.beer_id "
					+ "LEFT JOIN price p1 ON p1.id = active_beers.price_1_id "
					+ "LEFT JOIN price p2 ON p2.id = active_beers.price_2_id "
					+ "WHERE 1 "
					+ "ORDER BY active_beers.id ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				activeBeerList.add(new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu"), 
						rs.getDouble("price1"), rs.getInt("size1"), rs.getDouble("price2"), rs.getInt("size2")));
			});
		
			conn.close();
		} catch (CJCommunicationsException e1) {
			e1.printStackTrace();
			System.out.println("not connected, aborting");
			return;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if (beerList != null && beerList.size() > 0) {
			for(BeerMenuItem b : beerList) {
				for (int i = priceList.size() - 1; i >= 0; i--) {
					if (priceList.get(i).beerId == b.id) {
						b.addPrice(priceList.get(i));
						priceList.remove(i);
					}
				}
			}
			for(BeerMenuItem b : beerList) {
				b.resolvePrices();
			}
			allBeersListView.setItems(beerList);
			for(BeerMenuItem b : beerList) {
				for (int i = priceList.size() - 1; i >= 0; i--) {
					if (priceList.get(i).beerId == b.id) {
						b.addPrice(priceList.get(i));
						priceList.remove(i);
					}
				}
			}
		}
		
		if (activeBeerList != null) {
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
			GridPane grid = (GridPane)FXMLLoader.load(getClass().getResource("MenuFullScreen.fxml"));
			fillGridPane(grid);
			Scene scene2 = new Scene(grid);
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
			launchEditBeerWindow();
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
	
	private EditBeerController launchEditBeerWindow() {
		EditBeerController editBeerController = null;
		try {
			FXMLLoader editBeerLoader = new FXMLLoader();
			editBeerLoader.setLocation(getClass().getResource("EditBeer.fxml"));
			VBox editBeerWindow = editBeerLoader.load();
			editBeerController = editBeerLoader.getController();
			
			FXMLLoader beerLoader = new FXMLLoader();
			beerLoader.setLocation(getClass().getResource("/menulayouts/BeerItemLayout.fxml"));
			VBox beerLayout = beerLoader.load();
			BeerItemLayoutController beerItemController = beerLoader.getController();
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
					beerLoader.setLocation(getClass().getResource("/menulayouts/BeerItemLayout.fxml"));
					beerLayout = beerLoader.load();
					BeerItemLayoutController controller = beerLoader.getController();
					beerLayout.setUserData(controller);
					//beerLayoutList.add(beerLayout);
					//beerControllerList.add(controller);
					fillBeerLayout(beerLayout, controller, item, i);
				} catch (IOException e) {
					e.printStackTrace();
				}
				i++;
				if (beerLayout != null)
					grid.add(beerLayout, x, y);
			}
		}
	}
	
	
	public void fillBeerLayout(VBox beerLayout, BeerItemLayoutController controller, BeerMenuItem item, int beerNumber) {
		controller.beerItem = item;
		item.beerNumber = beerNumber;
		DecimalFormat df = new DecimalFormat("0.00");
		controller.beerNumber.setText("" + item.beerNumber);
		controller.beerName.setText((allCaps.isSelected()) ? item.beerName.toUpperCase() : item.beerName);
		controller.company.setText(item.company);
		controller.notes.setText(item.notes);
		controller.beerStyle.setText(item.style);
		controller.abv.setText("" + item.abv);
		controller.price1.setText((item.price1 == -1) ? "NA" : df.format(item.price1));
		controller.ounce1.setText("/" + item.price1Size);
		controller.price2.setText((item.price2 == -1) ? "NA" : df.format(item.price2));
		controller.ounce2.setText("/" + item.price2Size);
		
		//beerLayout.setUserData(controller);
		
	}
}
