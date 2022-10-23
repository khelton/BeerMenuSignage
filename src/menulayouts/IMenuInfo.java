package menulayouts;

public interface IMenuInfo {
	
	/**
	 * Gets the underlying object as this interface
	 * 
	 * @return the object attached to this IMenuInfo instance
	 */
	default IMenuInfo getMenuObject() {
		return this;
	}
	
	/**
	 * Gets the name of the menu. This should be a human readable name 
	 * so the user can select it from the main menu
	 * 
	 * @return a human readable name for the menu layout
	 */
	String getName();
	
	/**
	 * Gets the description of what the menu will look like. 
	 * This should be human readable and will be used as a 
	 * Tooltip when selecting which menu layout the system will use
	 * 
	 * @return a human readable description for the menu layout
	 */
	String getDescription();
	
	

}
