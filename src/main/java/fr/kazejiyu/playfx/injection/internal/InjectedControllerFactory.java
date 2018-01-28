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
package fr.kazejiyu.playfx.injection.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.kazejiyu.playfx.Play;
import fr.kazejiyu.playfx.configuration.SerializedProperties;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

/**
 * A custom controller factory that handles dependency injection. <br>
 * <br>
 * Instances of this class are supposed to be given as argument to {@link FXMLLoader#setControllerFactory(Callback)}.
 * 
 * @author Emmanuel CHEBBI
 */
public class InjectedControllerFactory implements Callback<Class<?>, Object> {
	
	private static final String CONFIG_FILE = "config.properties"; 
	
	private final Injector injector;
	/** Creates instances upon fields' name */
	private final Function<String, Object> instanciator;
	
	private static final Logger LOGGER = Logger.getLogger(Play.class.getName());
	
	public InjectedControllerFactory(Function <String,Object> instanciator) {
		this.instanciator = requireNonNull(instanciator);
		this.injector = new Injector(this.instanciator);
	}

	@Override
	public Object call(Class<?> clazz) {
		try {
			Object instance = clazz.getDeclaredConstructor().newInstance();
			SerializedProperties properties = loadPropertiesFor(clazz);
			
			return injector.injectFields(instance, properties);
			
		} catch (NoSuchMethodException | IllegalAccessException | SecurityException e) {
			LOGGER.log(Level.SEVERE, "Cannot access default constructor of {0} : {1}", new Object[] {clazz, e});
		} catch (IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			LOGGER.log(Level.SEVERE, "Failed to inject instance of {0} : {1}", new Object[] {clazz, e});
		}

		return null;
	}
	
	/** @return the properties stored in controller's config file */
	private SerializedProperties loadPropertiesFor(Class <?> controller) {
		SerializedProperties prop = new SerializedProperties();
		
		try( InputStream is = controller.getResourceAsStream(CONFIG_FILE) ) {
			if( is != null ) {
				prop = new SerializedProperties(is);
				prop.load();
			}
			
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to load the configuration file \"{0}\" : {1} ", new Object[] {CONFIG_FILE, e});
		}
		
		return prop;
	}

}
