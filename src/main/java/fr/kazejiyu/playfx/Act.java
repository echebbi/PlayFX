/*
 * 		Copyright 2018 Emmanuel CHEBBI
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.kazejiyu.playfx;

import javafx.scene.Scene;

/**
 * Represents an act, which can be associated to JavaFX's controllers. <br>
 * <br>
 * This interface is intended to be implemented by controllers loaded by {@link Play}.
 * 
 * @author Emmanuel CHEBBI
 * 
 * TODO Discuss the utility of the Act interface
 */
public interface Act {

	/**
	 * Prepare an {@code Act} to be made on stage. <br>
	 * <br>
	 * This method is called after the creation of the scene associated to the act.
	 * 
	 * @param play
	 * 			The play containing the act. 
	 * @param scene
	 * 			The scene associated with the act.
	 */
	abstract void prepare(Play play, Scene scene);
	
}
