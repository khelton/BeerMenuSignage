package application;
	
import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;
import menulayouts.BeerItemLayoutController;
import types.BeerMenuItem;
import windows.MainMenuController;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	public int beerNumber;
	public ArrayList<BeerItemLayoutController> beerControllerList;
	public ArrayList<VBox> beerLayoutList;
	public ArrayList<BeerMenuItem> beerItemList;
	

	public String dbName = "churchill";
	
	
	
	@Override
	public void start(Stage primaryStage) {
		this.beerNumber = 1;
		beerControllerList = new ArrayList<BeerItemLayoutController>();
		beerLayoutList = new ArrayList<VBox>();
		beerItemList = new ArrayList<BeerMenuItem>();
		try {
			VBox rootLayout = null;
			FXMLLoader rootLoader = new FXMLLoader();
			rootLoader.setLocation(getClass().getResource("/windows/MainMenu.fxml"));
			rootLayout = rootLoader.load();
			MainMenuController controller = rootLoader.getController();
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			controller.fillBeerLists();
			/*
			FXMLLoader beerLoader = new FXMLLoader();
			beerLoader.setLocation(getClass().getResource("/menulayouts/BeerItemLayout.fxml"));
			beerLayout = beerLoader.load();
			BeerItemLayoutController controller = beerLoader.getController();
			beerLayout.setUserData(controller);
			*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
