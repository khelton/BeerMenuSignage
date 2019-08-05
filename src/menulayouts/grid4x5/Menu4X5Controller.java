package menulayouts.grid4x5;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import types.BeerMenuItem;


public class Menu4X5Controller {
	
	@FXML
	public GridPane layoutGrid;
	
	//Required constructor that is empty
	public Menu4X5Controller() {}
	
	@FXML
	public void initialize() {
	}
	
	public void setLayout(ObservableList<BeerMenuItem> activeBeerList) {
		
		//if (this.activeBeersListView == null)
		//	return;
		//ObservableList<BeerMenuItem> activeBeerList = this.activeBeersListView.getItems();
		int gridRows = layoutGrid.getRowConstraints().size();
		int gridCols = layoutGrid.getColumnConstraints().size();
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
					layoutGrid.add(beerLayout, x, y);
			}
		}
	}
	
}
