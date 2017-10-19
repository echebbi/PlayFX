package fr.kazejiyu.playfx;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import fr.kazejiyu.playfx.exceptions.UnloadedActException;
import fr.kazejiyu.playfx.injection.InjectedControllerFactory;
import javafx.animation.Animation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Represents a set of {@link Act}s.
 * <br><br>
 * This class is intented to ease the use of multiple FXML scenes and to smooth the transitions between them.
 * 
 * @author Emmanuel CHEBBI
 */
public final class Play {
	
	/** The root of all scenes/acts */
	private final Stage stage;
	
	/** Creates injected controllers  */
	private final InjectedControllerFactory factory;
	
	/** Application's states */
	private final Map <String, Object> acts = new HashMap<>();
	private final Map <String, Scene> scenes = new HashMap<>();
	
	/**
	 * Creates a new piece that will be directed on the given stage.
	 * 
	 * @param stage
	 * 			The primary stage of the application.
	 */
	public Play(Stage stage) {
		this(stage, name -> null);
	}

	/**
	 * Creates a new piece that will be directed on the given stage.
	 * 
	 * @param stage
	 * 			The primary stage of the application.
	 * @param dependencies
	 * 			Defines the values available to be injected into controllers.
	 */
	public Play(Stage stage, Function <String,Object> instanciator) {
		this.stage = Objects.requireNonNull(stage);
		this.factory = new InjectedControllerFactory(instanciator);
	}
	
	/** Convenience method that calls {@code stage.setTitle(title); } */
	public void setTitle(String title)	{ stage.setTitle(title); }
	
	/** Convenience method that calls {@code stage.show();} */
	public void start()	{ stage.show(); }
	
	/** Returns the stage of the piece */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Loads an act.
	 * 
	 * @param name
	 * 			The name given to the loaded act. Must be unique.
	 * @param location
	 * 			The location of the .fxml file that describes the scene. 
	 * 
	 * @return the loaded act
	 * 
	 * @throws IOException if {@code FXMLLoader} fails to load {@code location}.
	 */
	public <T> T prepare(String name, URL location) throws IOException {
		FXMLLoader loader = new FXMLLoader(location);
		loader.setControllerFactory(factory);

		Parent root = loader.load();
		Scene scene = new Scene(root);
		T act = loader.getController();
		
		// TODO Replace instanceof by dynamic dispatch ?
		if( act instanceof Act )
			((Act) act).prepare(this, scene);
		
		acts.put(name, act);
		scenes.put(name, scene);
		
		return act;
	}
	
	/**
	 * Frees an act from memory.
	 * 
	 * @param name
	 * 			The name of the act to free
	 */
	public Play removeScene(String name) {
		acts.remove(name);
		scenes.remove(name);
		
		return this;
	}
	
	/**
	 * Sets the current scene of the piece.
	 * 
	 * @param name
	 * 			The name of the act to show.
	 * 
	 * @throws UnloadedActException if the act has not been loaded
	 */
	public Play setScene(String name) {
		if( ! scenes.containsKey(name) )
			throw new UnloadedActException(name);
		
		Scene scene = scenes.get(name);
		stage.setScene(scene);
		
		return this;
	}
	
	public Play makeOnStage(String name, Animation animation) {
		return makeOnStage(name, (stag,scene) -> animation);
	}
	
	public Play makeOnStage(String name, BiFunction <Stage,Scene,Animation> animation) {
		if( ! scenes.containsKey(name) )
			throw new UnloadedActException(name);
		
		Scene nextScene = scenes.get(name);
		Animation anim = animation.apply(stage, nextScene);
		
		anim.setOnFinished(e -> setScene(name));
		anim.play();
		
		return this;
	}
}
