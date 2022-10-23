package menulayouts.grid2x10;

import java.io.IOException;
import java.time.LocalTime;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import menulayouts.IMenuInfo;
import menulayouts.ItemBaseController;
import menulayouts.MenuBaseController;
import menulayouts.grid2x10x1price.Item2X10X1PriceController;
import types.BeerMenuItem;


public class Menu2X10Controller extends MenuBaseController implements IMenuInfo {
	
	public static final String fxmlLoaderString = "/menulayouts/grid2x10/Menu.fxml";
	
	public static final String menuName = "Grid 2 X 10";
	public static final String menuDesc = "2 Columns by 10 Rows of beers";
	
	public static String itemFxmlLoaderString = Item2X10X1PriceController.fxmlLoaderString;
	
	// price update time variables
	Timeline checkPriceInterval = null;
	LocalTime lastCheckedTime = null;
	
	//Required constructor that is empty
	public Menu2X10Controller() {}
	
	@FXML
	public void initialize() {
	}
	
	@Override
	public String getName() {
		return menuName;
	}

	@Override
	public String getDescription() {
		return menuDesc;
	}
	
	@Override
	public String getItemFxmlLoaderString() {
		return itemFxmlLoaderString;
	}

	@Override
	public ObservableList<Node> getItemLayouts () {
		return layoutGrid.getChildren();
	}
	
	@Override
	public void fillLayout(ObservableList<BeerMenuItem> activeBeerList) {
		
		//if (this.activeBeersListView == null)
		//	return;
		//ObservableList<BeerMenuItem> activeBeerList = this.activeBeersListView.getItems();
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
					beerLoader.setLocation(getClass().getResource(itemFxmlLoaderString));
					beerLayout = beerLoader.load();
					ItemBaseController controller = beerLoader.getController();
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
		
		setupPriceIntervalTimer();
		
		if (showSideFeatures) {
			Platform.runLater(() -> {
				setupSideFeaturePane();
			});
		}
	}

}
