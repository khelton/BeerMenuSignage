package application;
	
import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;
import mainmenu.MainMenuController;
import menulayouts.grid4x5.Item4X5Controller;
import types.BeerMenuItem;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	//public int beerNumber;
	public ArrayList<Item4X5Controller> beerControllerList;
	public ArrayList<VBox> beerLayoutList;
	public ArrayList<BeerMenuItem> beerItemList;
	

	public String dbName = "churchill";
	
	
	
	@Override
	public void start(Stage primaryStage) {
		//this.beerNumber = 1;
		beerControllerList = new ArrayList<Item4X5Controller>();
		beerLayoutList = new ArrayList<VBox>();
		beerItemList = new ArrayList<BeerMenuItem>();
		try {
			VBox rootLayout = null;
			FXMLLoader rootLoader = new FXMLLoader();
			rootLoader.setLocation(getClass().getResource("/mainmenu/MainMenu.fxml"));
			rootLayout = rootLoader.load();
			MainMenuController controller = rootLoader.getController();
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Main Menu");
			primaryStage.show();
			controller.fillBeerListBoxes();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
