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
package fr.kazejiyu.playfx.exceptions;

import fr.kazejiyu.playfx.Act;

/**
 * Thrown when one attempt to access or use an {@link Act} that
 * does not have been loaded yet.
 * 
 * @author Emmanuel CHEBBI
 */
public class UnloadedActException extends RuntimeException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -4078168305111418174L;

	public UnloadedActException(String name) {
		super("Unable to load " + name + " : the act has not been loaded");
	}

}
