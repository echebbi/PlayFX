package fr.kazejiyu.playfx;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import fr.kazejiyu.playfx.exceptions.UnloadedActException;
import fr.kazejiyu.playfx.injection.internal.InjectedControllerFactory;
import javafx.animation.Animation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A set of {@link Act}s. <br>
 * <br>
 * This class is intended to ease the use of multiple FXML scenes and to smooth the transitions between them.
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
		this.stage = requireNonNull(stage);
		this.factory = new InjectedControllerFactory(instanciator);
	}
	
	/** Convenience method that calls {@code stage.setTitle(title); } */
	public void setTitle(String title) { 
		stage.setTitle(title); 
	}
	
	/** Convenience method that calls {@code stage.show();} */
	public void show() { 
		stage.show(); 
	}
	
	/** @return play's current stage */
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
	 * 			The name of the act to free.
	 */
	public Play removeScene(String name) {
		acts.remove(name);
		scenes.remove(name);
		
		return this;
	}
	
	/**
	 * Sets the current scene of the play. <br>
	 * <br>
	 * Before being set, a scene must be {@link #prepare(String, URL) prepared}.
	 * 
	 * @param name
	 * 			The name of the act to show.
	 * 
	 * @return a reference to self, enabling method chaining
	 * 
	 * @throws UnloadedActException if the act has not been loaded
	 * 
	 * @see #prepare(String, URL)
	 * @see #setScene(String, Animation)
	 * @see {@link #setScene(String, BiFunction)}
	 */
	public Play setScene(String name) {
		if( ! scenes.containsKey(name) )
			throw new UnloadedActException(name);
		
		Scene scene = scenes.get(name);
		stage.setScene(scene);
		
		return this;
	}
	
	/**
	 * Sets the current scene of the play using an animated transition. <br>
	 * <br>
	 * Before being set, a scene must be {@link #prepare(String, URL) prepared}.
	 * 
	 * @param name
	 * 			The name of the act to show.
	 * @param transition
	 * 			The transition to play.
	 *  
	 * @return a reference to self, enabling method chaining
	 * 
	 * @throws UnloadedActException if the act has not been loaded
	 * 
	 * @see #prepare(String, URL)
	 * @see #setScene(String)
	 * @see #setScene(String, BiFunction)
	 */
	public Play setScene(String name, Animation transition) {
		return setScene(name, (stag,scene) -> transition);
	}
	
	/**
	 * Sets the current scene of the play using an animated transition. <br>
	 * <br>
	 * Before being set, a scene must be {@link #prepare(String, URL) prepared}.
	 * 
	 * @param name
	 * 			The name of the act to show.
	 * @param transition
	 * 			Returns transition to play. Takes the current stage and the 
	 * 			next scene as arguments.
	 *  
	 * @return a reference to self, enabling method chaining
	 * 
	 * @throws UnloadedActException if the act has not been loaded
	 * 
	 * @see #prepare(String, URL)
	 * @see #setScene(String)
	 * @see #setScene(String, Animation)
	 */
	public Play setScene(String name, BiFunction <Stage,Scene,Animation> transition) {
		if( ! scenes.containsKey(name) )
			throw new UnloadedActException(name);
		
		Scene nextScene = scenes.get(name);
		Animation anim = transition.apply(stage, nextScene);
		
		anim.setOnFinished(e -> setScene(name));
		anim.play();
		
		return this;
	}
}
