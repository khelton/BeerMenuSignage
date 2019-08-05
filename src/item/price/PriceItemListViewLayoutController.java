package item.price;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import types.ItemPrice;
import types.ItemPriceType;


public class PriceItemListViewLayoutController {
	
	public ItemPrice itemPrice;
	public ObservableList<ItemPriceType> priceTypeList;

	
	@FXML
	public Label rank;
	
	@FXML
	public TextField price;
	@FXML
	public TextField priceSize;
	@FXML
	public ComboBox<ItemPriceType> priceTypeDropdown;
	@FXML
	public CheckBox enabled;
	
	
	//Required constructor that is empty
	public PriceItemListViewLayoutController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (itemPrice != null && priceTypeList != null) {
			setLayoutFields(itemPrice, priceTypeList);
		}
	}
	public void setLayoutFields(ItemPrice p, ObservableList<ItemPriceType> pTypeList) {
		this.itemPrice = p;
		this.priceTypeList = pTypeList;
		rank.setText("" + p.rank);
		price.setText(""+ p.price);
		priceSize.setText(""+ p.size);
		priceTypeDropdown.setItems(pTypeList);
		for (ItemPriceType type : pTypeList) {
			if (p.priceType.id == type.id) {
				priceTypeDropdown.setValue(type);
			}
		}
		enabled.setSelected((p.enabled == 1));
	}
	
}
