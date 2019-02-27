package windows;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
	public TextField dbPassword;
	
	@FXML
	public ListView<BeerMenuItem> activeBeersListView;
	@FXML
	public ListView<BeerMenuItem> allBeersListView;
	
	
	
	
	//Required constructor that is empty
	public MainMenuController() {}
	
	@FXML
	public void initialize() {
		dbHost.setText("192.168.2.246");
		//fillBeerLists();
	}
	
	@FXML
	private void refreshConnection() {
		fillBeerLists();
	}
	
	public void fillBeerLists() {
		MySqlManager sql = new MySqlManager(dbHost.getText() + ":" + dbPort.getText(), "churchill");
		Connection conn = null;
		ObservableList<BeerMenuItem> beerList = FXCollections.observableArrayList();
		ObservableList<BeerMenuItem> activeBeerList = FXCollections.observableArrayList();
		ArrayList<ItemPrice> priceList = new ArrayList<ItemPrice>();
		//ObservableList<BeerMenuItem> beerObservableList = (ObservableList<BeerMenuItem>) beerList;
		
		try {
			conn = sql.connect();
			String queryString  = "SELECT * FROM beer ORDER BY beer_name ASC;";
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
					+ "FROM beer b "
					+ "RIGHT JOIN active_beers ON b.id = active_beers.beer_id "
					+ "LEFT JOIN price p1 ON p1.id = active_beers.price_1_id "
					+ "LEFT JOIN price p2 ON p2.id = active_beers.price_2_id "
					+ "WHERE 1 "
					+ "ORDER BY id ASC;";
			sql.runQuery(conn, queryString, (rs) -> {
				activeBeerList.add(new BeerMenuItem(rs.getInt("id"), rs.getString("beer_name"), rs.getString("company"), rs.getString("notes"), 
						rs.getString("style"), rs.getDouble("abv"), rs.getInt("ibu"), 
						rs.getDouble("price1"), rs.getInt("size1"), rs.getDouble("price2"), rs.getInt("size2")));
			});
		
			conn.close();
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
			VBox layout = (VBox)FXMLLoader.load(getClass().getResource("EditBeer.fxml"));
			Scene scene = new Scene(layout);
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setFullScreen(true);
			
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void editBeerButton() {
		try {
			VBox layout = (VBox)FXMLLoader.load(getClass().getResource("EditBeer.fxml"));
			Scene scene = new Scene(layout);
			//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setFullScreen(true);
			
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
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
				if (activeBeerList.get(i-1) == null)
					return;
				VBox beerLayout = null;
				try {
					FXMLLoader beerLoader = new FXMLLoader();
					beerLoader.setLocation(getClass().getResource("/menulayouts/BeerItemLayout.fxml"));
					beerLayout = beerLoader.load();
					BeerItemLayoutController controller = beerLoader.getController();
					beerLayout.setUserData(controller);
					//beerLayoutList.add(beerLayout);
					//beerControllerList.add(controller);
					fillBeerLayout(beerLayout, controller, activeBeerList.get(i-1));
				} catch (IOException e) {
					e.printStackTrace();
				}
				i++;
				if (beerLayout != null)
					grid.add(beerLayout, x, y);
			}
		}
	}
	
	
	public void fillBeerLayout(VBox beerLayout, BeerItemLayoutController controller, BeerMenuItem item) {
		controller.beerItem = item;
		controller.beerName.setText(item.beerName);
		controller.company.setText(item.company);
		controller.notes.setText(item.notes);
		controller.beerStyle.setText(item.style);
		controller.abv.setText(item.abv);
		controller.price1.setText(item.price1);
		controller.ounce1.setText("/" + item.price1Size);
		controller.price2.setText(item.price2);
		controller.ounce2.setText("/" + item.price2Size);
		
		beerLayout.setUserData(controller);
		
	}
}
