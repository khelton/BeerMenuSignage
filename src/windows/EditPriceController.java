package windows;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;


public class EditPriceController {
	
	public BeerMenuItem beerItem;
	public ArrayList<ItemPrice> priceList;
	public ObservableList<ItemPriceType> priceTypeList;

	
	@FXML
	public ListView<VBox> pricesListView;
	
	@FXML
	public Button addPriceButton;
	@FXML
	public Button deletePriceButton;
	@FXML
	public Button saveButton;

	
	
	//Required constructor that is empty
	public EditPriceController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (priceList != null) {
			setLayoutFields(priceList);
		}
	}
	public void setLayoutFields(ArrayList<ItemPrice> priceList) {
		fillListView(priceList);
	}
	
	public void fillListView(ArrayList<ItemPrice> priceList) {
		ArrayList<VBox> priceLayoutList = new ArrayList<VBox>();
		for (int i = 0 ; i < priceList.size() ; i++) {
			VBox itemPriceLayout = null;
			try {
				FXMLLoader itemPriceLoader = new FXMLLoader();
				itemPriceLoader.setLocation(getClass().getResource("/windows/EditPriceItem.fxml"));
				itemPriceLayout = itemPriceLoader.load();
				EditPriceItemController controller = itemPriceLoader.getController();
				itemPriceLayout.setUserData(controller);
				controller.setLayoutFields(priceList.get(i), priceTypeList);
				priceLayoutList.add(itemPriceLayout);
			} catch (IOException e) {
				System.out.println("Error loading item price layout");
			}
		}
		ObservableList<VBox> oList = FXCollections.observableList(priceLayoutList);
		pricesListView.setItems(oList);
	}
	
	public void saveButtonClicked() {
		try {
			savePriceRecords();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPriceButtonClicked() {
		try {
			addNewPrice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ItemPrice addNewPrice() {
		ItemPrice price = null;
		VBox itemPriceLayout = null;
		if (priceTypeList == null || priceTypeList.size() == 0) {
			return null;
		}
		ItemPriceType type = priceTypeList.get(0);
		for (ItemPriceType t : priceTypeList) {
			if (t.name.contentEquals("Normal")) {
				type = t;
				break;
			}
		}
		try {
			FXMLLoader itemPriceLoader = new FXMLLoader();
			itemPriceLoader.setLocation(getClass().getResource("/windows/EditPriceItem.fxml"));
			itemPriceLayout = itemPriceLoader.load();
			EditPriceItemController controller = itemPriceLoader.getController();
			itemPriceLayout.setUserData(controller);
			price = new ItemPrice(0, beerItem.id, priceList.size(), 0.00, 16, type, 1);
			priceList.add(price);
			controller.setLayoutFields(price, priceTypeList);
			pricesListView.getItems().add(itemPriceLayout);
		} catch (IOException e) {
			System.out.println("Error loading item price layout");
		}
		return price;
	}
	
	//TODO add saving price records
	public boolean savePriceRecords() {
		return false;
	}
	
	
	
	
}
