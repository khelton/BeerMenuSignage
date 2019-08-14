package item.price;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import types.ItemPrice;
import types.ItemPriceType;
import types.PriceSchedule;


public class PriceTypeListViewController {
	
	public ItemPriceType priceType;
	public ObservableList<PriceSchedule> priceScheduleList;

	
	@FXML
	public TextField name;
	@FXML
	public ComboBox<PriceSchedule> priceScheduleDropdown;
	@FXML
	public CheckBox enabled;
	
	
	//Required constructor that is empty
	public PriceTypeListViewController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (priceType != null && priceScheduleList != null) {
			setLayoutFields(priceType, priceScheduleList);
		}
	}
	public void setLayoutFields(ItemPriceType p, ObservableList<PriceSchedule> pScheduleList) {
		this.priceType = p;
		this.priceScheduleList = pScheduleList;
		name.setText(""+ p.name);
		priceScheduleDropdown.setItems(pScheduleList);
		for (PriceSchedule s : pScheduleList) {
			if (p.schedule.id == s.id) {
				priceScheduleDropdown.setValue(s);
			}
		}
		enabled.setSelected((p.enabled == 1));
	}
	
}
