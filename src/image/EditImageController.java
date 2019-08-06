package image;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import mysql.MySqlManager;
import types.BeerMenuItem;
import types.ItemPrice;
import types.ItemPriceType;


public class EditImageController {
	
	public BeerMenuItem beerItem;
	public ArrayList<ItemImage> imageList;
	public ObservableList<ImageType> imageTypeList;
	public ObservableList<ImageSourceType> imageSourceTypeList;
	private String errorMessage;

	
	@FXML
	public ListView<VBox> imageListView;
	
	@FXML
	public Button addImageButton;
	@FXML
	public Button deleteImageButton;
	@FXML
	public Button saveButton;

	
	
	//Required constructor that is empty
	public EditImageController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (imageList != null) {
			setLayoutFields(imageList);
		}
	}
	public void setLayoutFields(ArrayList<ItemImage> imageList) {
		fillListView(imageList);
	}
	
	public void fillListView(ArrayList<ItemImage> imageList) {
		ArrayList<VBox> imageLayoutList = new ArrayList<VBox>();
		for (int i = 0 ; i < imageList.size() ; i++) {
			VBox itemImageLayout = null;
			try {
				FXMLLoader itemImageLoader = new FXMLLoader();
				itemImageLoader.setLocation(getClass().getResource("/image/ImageItemListViewLayout.fxml"));
				itemImageLayout = itemImageLoader.load();
				ImageItemListViewLayoutController controller = itemImageLoader.getController();
				itemImageLayout.setUserData(controller);
				controller.setLayoutFields(imageList.get(i), imageTypeList, imageSourceTypeList);
				imageLayoutList.add(itemImageLayout);
			} catch (IOException e) {
				System.out.println("Error loading item price layout");
				e.printStackTrace();
			}
		}
		ObservableList<VBox> oList = FXCollections.observableList(imageLayoutList);
		imageListView.setItems(oList);
	}
	
	public void saveButtonClicked() {
		try {
			saveImageRecords();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addButtonClicked() {
		try {
			addNewImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void moveUpButtonClicked() {
		int selectedIndex = imageListView.getSelectionModel().getSelectedIndex();
		if (selectedIndex == 0) {
			return; // we can't move the price up, its the first one already
		}
		ObservableList<VBox> allImageLayouts = imageListView.getItems();
		VBox imageBox1 = allImageLayouts.get(selectedIndex);
		VBox imageBox2 = allImageLayouts.get(selectedIndex - 1);
		allImageLayouts.set(selectedIndex - 1, imageBox1);
		allImageLayouts.set(selectedIndex, imageBox2);
	}
	@FXML
	public void moveDownButtonClicked() {
		int selectedIndex = imageListView.getSelectionModel().getSelectedIndex();
		int count = imageListView.getItems().size();
		if (selectedIndex == count - 1) {
			return; // we can't move the price down, its the last one already
		}
		ObservableList<VBox> allPriceLayouts = imageListView.getItems();
		VBox priceBox1 = allPriceLayouts.get(selectedIndex);
		VBox priceBox2 = allPriceLayouts.get(selectedIndex - 1);
		allPriceLayouts.set(selectedIndex + 1, priceBox1);
		allPriceLayouts.set(selectedIndex, priceBox2);
	}
	@FXML
	public void deleteButtonClicked() {
		if (imageListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		VBox imageBox = imageListView.getSelectionModel().getSelectedItem();
		ImageItemListViewLayoutController itemController = (ImageItemListViewLayoutController) imageBox.getUserData();
		if (itemController.itemImage.id != 0) {
			// we only have to remove it from the database if it has previously been saved in the db
			String sqlQuery = "UPDATE image SET active = 0 WHERE id = " +  itemController.itemImage.id + ";";
			boolean deleteSuccess = updateImageRecord(sqlQuery);
			if (!deleteSuccess) {
				return; // don't remove the item price because it didn't get removed in the db
			}
		}
		imageList.remove(itemController.itemImage);
		imageListView.getItems().remove(imageBox);
		
	}
	
	public ItemImage addNewImage() {
		ItemImage image = null;
		VBox itemPriceLayout = null;
		if (imageTypeList == null || imageTypeList.size() == 0) {
			return null;
		}
		if (imageSourceTypeList == null || imageSourceTypeList.size() == 0) {
			return null;
		}
		ImageType type = imageTypeList.get(0);
		for (ImageType t : imageTypeList) {
			if (t.name.contentEquals("Logo")) {
				type = t;
				break;
			}
		}
		ImageSourceType sourcetype = imageSourceTypeList.get(0);
		for (ImageSourceType t : imageSourceTypeList) {
			if (t.name.contentEquals("Logo")) {
				sourcetype = t;
				break;
			}
		}
		try {
			FXMLLoader itemPriceLoader = new FXMLLoader();
			itemPriceLoader.setLocation(getClass().getResource("/image/ImageItemListViewLayout.fxml"));
			itemPriceLayout = itemPriceLoader.load();
			ImageItemListViewLayoutController controller = itemPriceLoader.getController();
			itemPriceLayout.setUserData(controller);
			image = new ItemImage(0, beerItem.id, imageList.size() + 1, "", type, sourcetype, 1);
			imageList.add(image);
			controller.setLayoutFields(image, imageTypeList, imageSourceTypeList);
			imageListView.getItems().add(itemPriceLayout);
		} catch (IOException e) {
			System.out.println("Error loading item price layout");
		}
		return image;
	}
	
	public boolean saveImageRecords() {
		errorMessage = "";
		ObservableList<VBox> vBoxList =  imageListView.getItems();
		imageList.clear();
		for ( int i = 0 ; i < vBoxList.size() ; i++ ) {
		//for (VBox view : pricesListView.getItems()) {
			ImageItemListViewLayoutController itemController = (ImageItemListViewLayoutController) vBoxList.get(i).getUserData();
			itemController.itemImage.imageSrc = checkImageSrc(itemController.imageSrc);
			itemController.itemImage.imageType = itemController.imageTypeDropdown.getValue();
			itemController.itemImage.imageSourceType = itemController.imageSourceTypeDropdown.getValue();
			itemController.itemImage.rank = i + 1;
			itemController.itemImage.enabled = (itemController.enabled.isSelected()) ? 1 : 0 ;
			if (errorMessage.length() > 0) {
				Alert alert = new Alert(AlertType.ERROR, errorMessage);
				alert.showAndWait();
				return false;
			}
			itemController.rank.setText("" + itemController.itemImage.rank);
			imageList.add(itemController.itemImage);
		}
		MySqlManager sql = new MySqlManager();
		try {
			Connection conn = sql.connect();
			for (ItemImage p : imageList) {
				String sqlQuery = "";
				if (p.id == 0) {
					sqlQuery = "INSERT INTO image (beer_id, image_src, image_type_id, image_source_type_id, rank, enabled) VALUES "
							+ "('" + beerItem.id + "', '" + p.imageSrc + "', '" + p.imageType.id + "', '" 
							+ p.imageSourceType.id + "', '" + p.rank + "', '" + p.enabled + "');";
				} else {
					sqlQuery = "UPDATE image SET beer_id = " + beerItem.id + ", image_src = " + p.imageSrc + ", image_type_id = " 
							+ p.imageType.id + ", image_source_type_id = " + p.imageSourceType.id+ ", `rank` = " + p.rank + ", enabled = " + p.enabled + " "
							+ "WHERE id = " +  p.id + ";";
				}
				sql.runInsertQuery(conn, sqlQuery);
			}
			conn.close();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, 
					"Error modifying / entering records, please try again");
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	public boolean updateImageRecord(String sqlQuery) {
		MySqlManager sql = new MySqlManager();
		try {
			Connection conn = sql.connect();
			sql.runInsertQuery(conn, sqlQuery);
			conn.close();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, 
					"Error modifying / entering records, please try again");
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	public String checkImageSrc(TextField tb) {
		String retVal = "";
		if (tb.getText().trim().length() == 0) {
			errorMessage += "\nThe image source is empty";
			return retVal;
		}
		return tb.getText().trim().replace("'", "''");
	}
	
	public double priceCheck(TextField price) {
		double retVal = 0;
		try {
			retVal = Double.valueOf(price.getText());
		} catch (Exception e) {
			errorMessage = "decimal in a price or number in size is not formatted correctly";
		}
		return retVal;
	}
	public int sizeCheck(TextField size) {
		int retVal = 0;
		try {
			retVal = Integer.valueOf(size.getText());
		} catch (Exception e) {
			errorMessage = "decimal in a price or number in size is not formatted correctly";
		}
		return retVal;
	}
	
	
}
