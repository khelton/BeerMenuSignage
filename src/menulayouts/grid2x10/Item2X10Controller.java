package menulayouts.grid2x10;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import image.ItemImage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import menulayouts.IDisplayMenuItem;
import types.BeerMenuItem;
import types.IMenuItem;
import types.ItemPrice;


public class Item2X10Controller implements IDisplayMenuItem {
	
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
	@FXML
	public ImageView logo;
	@FXML
	public VBox logoWrapper;
	
	public BeerMenuItem beerItem;
	
	
	
	//Required constructor that is empty
	public Item2X10Controller() {}
	
	@FXML
	public void initialize() {
		
	}
	
	
	
	public void setLayout(IMenuItem item) {
		if (item == null) {
			return;
		}
		
		BeerMenuItem bItem = (BeerMenuItem)item;
		
		beerItem = bItem;
		beerNumber.setText("" + bItem.beerNumber + ".");
		beerName.setText(bItem.beerName);
		beerNumber.setTextFill(Color.web(bItem.beerNameColor));
		beerName.setTextFill(Color.web(bItem.beerNameColor));
		company.setText(bItem.company);
		notes.setText(bItem.notes);
		notes.setTextFill(Color.web(bItem.notesColor));
		beerStyle.setText(bItem.style);
		abv.setText("" + bItem.abv);
		
		setPrices(bItem);

		setImage(bItem);
		
	}
	
	public void setPrices(IMenuItem item) {
		if (item == null) {
			return;
		}
		
		BeerMenuItem bItem = (BeerMenuItem)item;
		
		ArrayList<ItemPrice> validPrices = bItem.getPrices();
		DecimalFormat df = new DecimalFormat("0.00");
		if (validPrices.size() > 0) {
			price1.setText(""+ df.format(validPrices.get(0).price));
			ounce1.setText(""+ validPrices.get(0).size);
		} else {
			price1.setText("NA");
			ounce1.setText("NA");
		}
		
		if (validPrices.size() > 1) {
			price2.setText(""+ df.format(validPrices.get(1).price));
			ounce2.setText(""+ validPrices.get(1).size);
		} else {
			price2.setText("NA");
			ounce2.setText("NA");
		}
	}
	
	public void setImage(IMenuItem item) {
		if (item == null) {
			return;
		}
		
		BeerMenuItem bItem = (BeerMenuItem)item;
		
		ItemImage tempImage = null;
		if (bItem.imageList != null) {
			if (bItem.imageList.size() > 0) {
				System.out.println("ImageFound " + bItem.beerName);
				for (int i = 0 ; i < bItem.imageList.size() ; i++) {
					System.out.println("Imagetype " + bItem.imageList.get(i).imageType.name);
					if (bItem.imageList.get(i).isValid() && bItem.imageList.get(i).imageType.name.toLowerCase().equals("beer logo")) {
						tempImage = bItem.imageList.get(i);
						break;
					}
				}
				if (tempImage != null ) {
					final ItemImage logoImage = tempImage;
					Thread t = new Thread(() -> {
						double iwidth = logoWrapper.getPrefWidth();
						double iheight = logoWrapper.getPrefHeight();
						File file = new File(logoImage.imageSrc);
						Image image = new Image(file.toURI().toString(), iwidth, iheight, true, false, true);
						logo.setPreserveRatio(true);
						logo.setImage(image);
						logo.maxWidth(iwidth);
						logo.prefWidth(iwidth);
						logo.minWidth(iwidth);
						logoWrapper.maxWidth(iwidth);
						logoWrapper.prefWidth(iwidth);
						logoWrapper.minWidth(iwidth);
						logoWrapper.setStyle("-fx-background-color: #FFFFFF");
					});
					t.start();
				} else {
					logoWrapper.setStyle("-fx-background-color: #555555");
				}
			} 
		} 
	}

	
}
