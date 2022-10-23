package item.price;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import types.PriceSchedule;


public class PriceScheduleListViewController {
	
	public PriceSchedule schedule;

	
	@FXML
	public TextField name;
	@FXML
	public TextField startTime;
	@FXML
	public TextField endTime;
	@FXML
	public ComboBox<PriceSchedule> priceScheduleDropdown;
	@FXML
	public CheckBox sun;
	@FXML
	public CheckBox mon;
	@FXML
	public CheckBox tue;
	@FXML
	public CheckBox wed;
	@FXML
	public CheckBox thu;
	@FXML
	public CheckBox fri;
	@FXML
	public CheckBox sat;
	
	
	//Required constructor that is empty
	public PriceScheduleListViewController() {}
	
	@FXML
	public void initialize() {

	}
	
	public void setLayoutFields() {
		if (schedule != null) {
			setLayoutFields(schedule);
		}
	}
	public void setLayoutFields(PriceSchedule s) {
		this.schedule = s;
		name.setText(""+ s.name);
		
		setTimes(s);
		setDays(s);
	}
	
	public void setTimes(PriceSchedule s) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME; 
		//SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		startTime.setText(s.startTime.format(formatter));
		endTime.setText(s.endTime.format(formatter));
	}
	
	public void setDays(PriceSchedule s) {
		if (s.getDaysEnabledString().length() != 7) {
			return;
		}
		sun.setSelected((s.getDaysEnabledString().charAt(0) == '1'));
		mon.setSelected((s.getDaysEnabledString().charAt(1) == '1'));
		tue.setSelected((s.getDaysEnabledString().charAt(2) == '1'));
		wed.setSelected((s.getDaysEnabledString().charAt(3) == '1'));
		thu.setSelected((s.getDaysEnabledString().charAt(4) == '1'));
		fri.setSelected((s.getDaysEnabledString().charAt(5) == '1'));
		sat.setSelected((s.getDaysEnabledString().charAt(6) == '1'));
	}
	
}
