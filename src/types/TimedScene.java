package types;

import javafx.animation.Timeline;
import javafx.scene.Scene;

/**
 * A class to hold Scenes and Timelines for the scene
 * 
 * @author khelton
 *
 */
public class TimedScene {
	
	public Timeline timeline;
	public Scene scene;

	public TimedScene() {
		timeline = new Timeline();
		scene = new Scene(null);
	}
	
	public TimedScene(Timeline timeline, Scene scene) {
		this.timeline = timeline;
		this.scene = scene;
	}
	
	public void startTimeline() {
		
	}
	
	public void stopTimeline() {
		
	}
	
	public void pauseTimeline() {
		
	}
}
