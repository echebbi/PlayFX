package fr.kazejiyu.playfx;

import javafx.scene.Scene;

/**
 * Represents an act, which can be associated to JavaFX's controllers. 
 * <br><br>
 * This interface is intended to be implemented by controllers loaded by {@link Play}.
 * 
 * @author Emmanuel CHEBBI
 */
public interface Act {

	/**
	 * Prepare an {@code Act} to be made on stage. 
	 * <br><br>
	 * This method is called after the creation of the scene associated to the act.
	 * 
	 * @param piece
	 * 			The piece that contains the act. 
	 * @param scene
	 * 			The scene associated to the act.
	 */
	abstract void prepare(Play piece, Scene scene);
	
}
