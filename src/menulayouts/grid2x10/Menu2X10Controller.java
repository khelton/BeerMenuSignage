package menulayouts.grid2x10;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.util.Calendar;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import types.BeerMenuItem;


public class Menu2X10Controller {
	
	@FXML
	public GridPane layoutGrid;
	
	Timeline checkPriceInterval = null;
	LocalTime lastCheckedTime = null;
	
	//Required constructor that is empty
	public Menu2X10Controller() {}
	
	@FXML
	public void initialize() {
	}
	
	public void setLayout(ObservableList<BeerMenuItem> activeBeerList) {
		
		//if (this.activeBeersListView == null)
		//	return;
		//ObservableList<BeerMenuItem> activeBeerList = this.activeBeersListView.getItems();
		int gridRows = layoutGrid.getRowConstraints().size();
		int gridCols = layoutGrid.getColumnConstraints().size();
		int cellWidth = (int) (layoutGrid.getWidth() / gridCols);
		int cellHeight = (int) (layoutGrid.getHeight() / gridRows);
		for (ColumnConstraints cc : layoutGrid.getColumnConstraints()) {
			cc.setMinWidth(960);
			cc.setMaxWidth(960);
		}
		for (RowConstraints rc : layoutGrid.getRowConstraints()) {
			rc.setMinHeight(108);
			rc.setMaxHeight(108);
		}
		int i = 1;
		for (int x = 0 ; x  < gridCols; x++) {
			for (int y = 0 ; y  < gridRows; y++) {
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
					beerLoader.setLocation(getClass().getResource("/menulayouts/grid2x10/Item.fxml"));
					beerLayout = beerLoader.load();
					Item2X10Controller controller = beerLoader.getController();
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
		lastCheckedTime = LocalTime.now();
		checkPriceInterval = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

		    @Override
		    public void handle(ActionEvent event) {
		    	LocalTime now = LocalTime.now();
		    	if(now.getMinute() != lastCheckedTime.getMinute()) {
		    		System.out.println("Updating Prices");
		    		updatePrices();
		    		lastCheckedTime = now;
		    	}
		    }
		}));
		checkPriceInterval.setCycleCount(Timeline.INDEFINITE);
		checkPriceInterval.play();
	}
	
	public void updatePrices() {
		ObservableList<Node> gridChildren = layoutGrid.getChildren();
		for (Node n : gridChildren) {
			Item2X10Controller c = (Item2X10Controller)n.getUserData();
			c.setPrices(c.beerItem);
		}
	}
	
	public void stopPriceIntervalTimer() {
		if (checkPriceInterval != null) {
			checkPriceInterval.stop();
			checkPriceInterval = null;
		}
	}
}
