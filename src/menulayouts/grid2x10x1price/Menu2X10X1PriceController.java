package menulayouts.grid2x10x1price;

import java.io.IOException;
import java.time.LocalTime;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import menulayouts.IMenuLayoutChoice;
import sidefeature.SideFeatureController;
import types.BeerMenuItem;

@Deprecated
public class Menu2X10X1PriceController implements IMenuLayoutChoice {
	
	public static final String fxmlLoaderString = "/menulayouts/grid2x10x1price/Menu.fxml";
	
	@FXML
	public GridPane layoutGrid;
	@FXML
	public HBox contentHBox;
	
	Timeline checkPriceInterval = null;
	LocalTime lastCheckedTime = null;
	
	public boolean showSideFeatures = false;
	
	//Required constructor that is empty
	public Menu2X10X1PriceController() {}
	
	@FXML
	public void initialize() {
	}
	
	@Override
	public String getFxmlLayoutChoice() {
		return fxmlLoaderString;
	}
	
	public void setLayout(ObservableList<BeerMenuItem> activeBeerList) {
		
		//if (this.activeBeersListView == null)
		//	return;
		//ObservableList<BeerMenuItem> activeBeerList = this.activeBeersListView.getItems();
		//layoutGrid.setGridLinesVisible(true);
		int gridRows = layoutGrid.getRowConstraints().size();
		int gridCols = layoutGrid.getColumnConstraints().size();
		//int cellWidth = (int) (layoutGrid.getWidth() / gridCols);
		//int cellHeight = (int) (layoutGrid.getHeight() / gridRows);
		for (ColumnConstraints cc : layoutGrid.getColumnConstraints()) {
			cc.setMinWidth(960 - 200);
			cc.setPrefWidth(960);
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
				AnchorPane beerLayout = null;
				try {
					FXMLLoader beerLoader = new FXMLLoader();
					beerLoader.setLocation(getClass().getResource(Item2X10X1PriceController.fxmlLoaderString));
					beerLayout = beerLoader.load();
					Item2X10X1PriceController controller = beerLoader.getController();
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
		//checkPriceInterval.play();
		if (showSideFeatures) {
			Platform.runLater(() -> {
				setupSideFeaturePane();
			});
		}
	}
	
	public void setupSideFeaturePane() {
		AnchorPane sideFeaturePane = null;
		SideFeatureController controller = null;
		Rectangle r = null;
		try {
			FXMLLoader beerLoader = new FXMLLoader();
			beerLoader.setLocation(getClass().getResource(SideFeatureController.fxmlLoaderString));
			sideFeaturePane = beerLoader.load();
			controller = beerLoader.getController();
			sideFeaturePane.setUserData(controller);
			this.contentHBox.getChildren().add(sideFeaturePane);
			r = new Rectangle(400,1080);
			controller.pane.setClip(r);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (sideFeaturePane != null) {
			
			final Rectangle finalRectangle = r;
			final Pane finalPane = controller.pane;
			final GridPane finalContentPane = this.layoutGrid;
			
			//finalContentPane.prefWidthProperty().set(1520);
			//finalContentPane.maxWidthProperty().set(1520);
			finalContentPane.prefWidthProperty().set(finalContentPane.getWidth() - 400);
			finalContentPane.maxWidthProperty().set(finalContentPane.getWidth() - 400);
			Timeline featurePaneInterval = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent event) {
			    	Timeline timeline = new Timeline();
			    	KeyValue kv1 = null;
			    	KeyValue kv2 = null;
			    	KeyValue kv3 = null;
			    	KeyValue kv4 = null;
			    	if (finalRectangle.getWidth() == 0) {
			    		kv1 = new KeyValue(finalRectangle.widthProperty(),400,Interpolator.EASE_BOTH);
			    		kv2 = new KeyValue(finalPane.prefWidthProperty(),400,Interpolator.EASE_BOTH);
			    		kv3 = new KeyValue(finalContentPane.maxWidthProperty(),1520,Interpolator.EASE_BOTH);
			    		kv4 = new KeyValue(finalContentPane.prefWidthProperty(),1520,Interpolator.EASE_BOTH);
			    	} else {
			    		kv1 = new KeyValue(finalRectangle.widthProperty(),0,Interpolator.EASE_BOTH);
				    	kv2 = new KeyValue(finalPane.prefWidthProperty(),0,Interpolator.EASE_BOTH);
				    	kv3 = new KeyValue(finalContentPane.maxWidthProperty(),1920,Interpolator.EASE_BOTH);
				    	kv4 = new KeyValue(finalContentPane.prefWidthProperty(),1920,Interpolator.EASE_BOTH);
			    	}
			    	KeyFrame kf1 = new KeyFrame(Duration.millis(500),kv1, kv2, kv3, kv4);
			    	//KeyFrame kf2 = new KeyFrame(Duration.millis(500),kv2);
			    	timeline.getKeyFrames().add(kf1);
			    	//timeline.getKeyFrames().add(kf2);
			    				    	
			    	timeline.play();
			    	//finalPane.setClip(finalRectangle);
			    }
			}));
			featurePaneInterval.setCycleCount(Timeline.INDEFINITE);
			featurePaneInterval.play();
		}
	}
	
	public void updatePrices() {
		ObservableList<Node> gridChildren = layoutGrid.getChildren();
		for (Node n : gridChildren) {
			Item2X10X1PriceController c = (Item2X10X1PriceController)n.getUserData();
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
