package image;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import types.ItemPrice;
import types.ItemPriceType;


public class ImageItemListViewLayoutController {
	
	public ItemImage itemImage;
	public ObservableList<ImageType> imageTypeList;
	public ObservableList<ImageSourceType> imageSourceTypeList;

	
	@FXML
	public Label rank;
	
	@FXML
	public TextField imageSrc;
	@FXML
	public ComboBox<ImageType> imageTypeDropdown;
	@FXML
	public ComboBox<ImageSourceType> imageSourceTypeDropdown;
	@FXML
	public CheckBox enabled;
	
	
	//Required constructor that is empty
	public ImageItemListViewLayoutController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (itemImage != null && imageTypeList != null && imageSourceTypeList != null) {
			setLayoutFields(itemImage, imageTypeList, imageSourceTypeList);
		}
	}
	public void setLayoutFields(ItemImage i, ObservableList<ImageType> iTypeList, ObservableList<ImageSourceType> iSourceTypeList) {
		this.itemImage = i;
		this.imageTypeList = iTypeList;
		this.imageSourceTypeList = iSourceTypeList;
		rank.setText("" + i.rank);
		imageSrc.setText(""+ i.imageSrc);
		imageTypeDropdown.setItems(iTypeList);
		for (ImageType type : iTypeList) {
			if (i.imageType.id == type.id) {
				imageTypeDropdown.setValue(type);
			}
		}
		imageSourceTypeDropdown.setItems(iSourceTypeList);
		for (ImageSourceType type : iSourceTypeList) {
			if (i.imageSourceType.id == type.id) {
				imageSourceTypeDropdown.setValue(type);
			}
		}
		enabled.setSelected((i.enabled == 1));
	}
	
}
