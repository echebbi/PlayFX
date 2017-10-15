package fr.kazejiyu.playfx.injection;

import java.io.IOException;
import java.util.Objects;

import fr.kazejiyu.playfx.configuration.SerializedProperties;
import javafx.util.Callback;

/**
 * A custom controller factory that handle dependency injection.
 * <br><br>
 * Instances of this class are supposed to be given to {@FXMLLoader.setControllerFactory} method.
 * 
 * @author Emmanuel CHEBBI
 */
public class InjectorFactory implements Callback<Class<?>, Object> {
	
	private static final String CONFIG_FILE = "config.properties"; 
	
	private final Dependencies dependencies;
	
	public InjectorFactory(Dependencies dependencies) {
		this.dependencies = Objects.requireNonNull(dependencies);
	}

	@Override
	public Object call(Class<?> clazz) {
		try {
			Object instance = clazz.newInstance();
			SerializedProperties properties = loadPropertiesFor(clazz);
			
			return dependencies.injectFields(instance, properties);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private SerializedProperties loadPropertiesFor(Class <?> clazz) {
		SerializedProperties prop = new SerializedProperties(clazz.getResourceAsStream(CONFIG_FILE));
		
		try {
			prop.load();
		} catch(IOException e) {
			// property file may be missing
			// TODO only catch FileNotFoundException & add proper handling for other errors
			e.printStackTrace();
		}
		
		return prop;
	}

}
