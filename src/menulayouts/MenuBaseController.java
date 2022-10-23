package menulayouts;

import java.io.IOException;
import java.time.LocalTime;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sidefeature.SideFeatureController;
import types.BeerMenuItem;

public abstract class MenuBaseController {
	
	@FXML
	public GridPane layoutGrid;
	@FXML
	public HBox contentHBox;
	
	// price update time variables
	Timeline checkPriceInterval = null;
	LocalTime lastCheckedTime = null;
	
	public boolean showSideFeatures = true;
	
	
	
	public abstract String getItemFxmlLoaderString();
	
	public abstract ObservableList<Node> getItemLayouts();
	
	public abstract void fillLayout(ObservableList<BeerMenuItem> beerItems);
	
	
	public void updatePrices() {
		ObservableList<Node> gridChildren = getItemLayouts();
		for (Node n : gridChildren) {
			ItemBaseController c = (ItemBaseController)n.getUserData();
			c.setPrices(c.beerItem);
		}
	}
	
	public void setupPriceIntervalTimer() {
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
	
	public void stopPriceIntervalTimer() {
		if (checkPriceInterval != null) {
			checkPriceInterval.stop();
			checkPriceInterval = null;
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

}
