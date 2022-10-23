package sidefeature;

import java.io.File;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import mysql.MySqlManager;

public class SideFeatureController {
	
	public static final String fxmlLoaderString = "/sidefeature/SideFeature.fxml";
	
	@FXML
	public AnchorPane anchorPane;
	
	@FXML
	public Pane pane;
	
	@FXML
	public Label titleLabel;
	
	@FXML
	public ImageView image;
	
	@FXML
	public Label description;
	
	
	private ArrayList<SideFeatureItem> sideFeatureList;
	private int currentFeatureIndex = 0;
		
	
	
	
	public SideFeatureController() {
		
	}
	
	@FXML
	public void initialize() {
	}
	
	
	public void getSideFeatureInfo() {
		sideFeatureList = new ArrayList<>();
		MySqlManager sql = new MySqlManager();
		String queryString = "SELECT * FROM side_feature WHERE active = 1";
		
		sql.runQuery(queryString, (rs) -> {
			SideFeatureItem item = new SideFeatureItem(
					rs.getString("title"),
					rs.getString("image_source"),
					rs.getString("description"));
			sideFeatureList.add(item);
		});
	}
	
	public boolean canDisplayFeature() {
		if (sideFeatureList == null)
			return false;
		if (sideFeatureList.size() <=0)
			return false;
		
		return true;
	}
	
	public void setNextFeature() {
		if (!canDisplayFeature())
			return;
		
		currentFeatureIndex++;
		if (currentFeatureIndex >= sideFeatureList.size())
			currentFeatureIndex = 0;
	}
	
	public void setSideFeatureInfo() {
		if (!canDisplayFeature())
			return;
		
		SideFeatureItem item = sideFeatureList.get(currentFeatureIndex);
		titleLabel.setText(item.title);
		description.setText(item.description);
		setImage(item);
	}
	
	
	public void setImage(SideFeatureItem item) {
		if (item == null) {
			return;
		}
		
		// this is done on the current thread because the image needs to be loaded before it appears
		// on the screen
		try {
			File file = item.getImageFile();
			Image itemImage = new Image(file.toURI().toString(), image.getFitWidth(), image.getFitHeight(), true, true, false);
			image.setImage(itemImage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	

}
