package fr.kazejiyu.piecefx;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.kazejiyu.piecefx.exceptions.UnloadedActException;
import fr.kazejiyu.piecefx.injection.Dependencies;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Represents a set of {@link Act}s.
 * <br><br>
 * This class is intented to ease the use of multiple FXML scenes and to smooth the transitions between them.
 * 
 * @author Emmanuel CHEBBI
 */
public final class Piece {
	
	private final Stage stage;
	private final Dependencies dependencies;
	private final Map <String, Act> acts = new HashMap<>();
	private final Map <String, Scene> scenes = new HashMap<>();

	/**
	 * Creates a new piece that will be directed on the given stage.
	 * 
	 * @param stage
	 */
	public Piece(Stage stage, Dependencies dependencies) {
		this.stage = stage;
		this.dependencies = dependencies;
	}
	
	/** Convenience method that calls {@code stage.setTitle(title); } */
	public void setTitle(String title)	{ stage.setTitle(title); }
	
	/** Convenience method that calls {@code stage.show();} */
	public void start()	{ stage.show(); }

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
	public <T extends Act> T directAct(String name, URL location) throws IOException {
		FXMLLoader loader = new FXMLLoader(location);
		loader.setControllerFactory( new Callback<Class<?>, Object>() {
			
			@Override
			public Object call(Class<?> param) {
				System.out.println("Factory called for " + param);
				
				try {
					Object instance = param.newInstance();
					return dependencies.injectFields(instance);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				
				return null;
			}
		});

		Parent root = loader.load();
		Scene scene = new Scene(root);
		T act = loader.getController();
		
		act.prepare(this, scene);
		
		acts.put(name, act);
		scenes.put(name, scene);
		
		return act;
	}
	
	public void abandonAct(String name) {
		acts.remove(name);
		scenes.remove(name);
	}
	
	/**
	 * Sets the current scene of the piece.
	 * 
	 * @param name
	 * 			The name of the act to show.
	 * 
	 * @throws UnloadedActException if the act has not been loaded
	 */
	public void makeOnStage(String name) {
		if( ! scenes.containsKey(name) )
			throw new UnloadedActException(name);
		
		Scene scene = scenes.get(name);
		stage.setScene(scene);
	}
}
