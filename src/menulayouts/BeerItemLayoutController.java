package menulayouts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import types.BeerMenuItem;


public class BeerItemLayoutController {
	
	@FXML
	public Label beerName;
	@FXML
	public Label company;
	@FXML
	public Label notes;
	@FXML
	public Label beerStyle;
	@FXML
	public Label abv;
	@FXML
	public Label price1;
	@FXML
	public Label ounce1;
	@FXML
	public Label price2;
	@FXML
	public Label ounce2;
	
	public BeerMenuItem beerItem;
	
	
	
	//Required constructor that is empty
	public BeerItemLayoutController() {}
	
	@FXML
	public void initialize() {
	}
	
	
	public void fillBeerLayout(BeerMenuItem item, GridPane pane) {
		beerItem = item;
		beerName.setText(item.beerName);
		company.setText(item.company);
		notes.setText(item.notes);
		beerStyle.setText(item.style);
		abv.setText(item.abv);
		price1.setText(item.price1);
		ounce1.setText("/" + item.price1Size);
		price2.setText(item.price2);
		ounce2.setText("/" + item.price2Size);
		
		pane.setUserData(this);
	}
	
}
