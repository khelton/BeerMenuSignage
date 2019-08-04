package menulayouts;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import types.BeerMenuItem;
import types.ItemPrice;


public class BeerItemLayoutController {
	
	@FXML
	public Label beerNumber;
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
	
	
	
	public void fillBeerLayout(BeerMenuItem item) {
		DecimalFormat df = new DecimalFormat("0.00");
		beerItem = item;
		beerNumber.setText("" + item.beerNumber + ".");
		beerName.setText(item.beerName);
		company.setText(item.company);
		notes.setText(item.notes);
		beerStyle.setText(item.style);
		abv.setText("" + item.abv);
		
		ArrayList<ItemPrice> validPrices = item.getPrices();
		System.out.println(item.priceList.size());
		System.out.println(validPrices.size());
		if (validPrices.size() > 0) {
			price1.setText(""+ df.format(validPrices.get(0).price));
			ounce1.setText("/"+ validPrices.get(0).size);
		} else {
			price1.setText("NA");
			ounce1.setText("/NA");
		}
		
		if (validPrices.size() > 1) {
			price2.setText(""+ df.format(validPrices.get(1).price));
			ounce2.setText("/"+ validPrices.get(1).size);
		} else {
			price2.setText("NA");
			ounce2.setText("/NA");
		}
		
	}
	
}
