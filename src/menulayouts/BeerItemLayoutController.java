package menulayouts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
	
}
