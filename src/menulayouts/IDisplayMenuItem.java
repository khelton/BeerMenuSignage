package menulayouts;

import types.IMenuItem;

public interface IDisplayMenuItem {

	// should have in FXML so I am requiring it for layouts
	void initialize();
	
	void setLayout(IMenuItem item);
		
}
