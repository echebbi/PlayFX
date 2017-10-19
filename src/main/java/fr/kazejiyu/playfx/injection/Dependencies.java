package fr.kazejiyu.playfx.injection;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.kazejiyu.playfx.Play;
import fr.kazejiyu.playfx.configuration.SerializedProperties;

/**
 * Represents the dependencies to inject into the Acts.
 * 
 * This class is a work in progress and will be refactored.
 * 
 * @author Emmanuel CHEBBI
 */
public class Dependencies {

	private final Map <String,Object> valuesPerName = new HashMap<>();
	private final Map <Class<?>,Object> valuesPerClass = new HashMap<>();
	
	private static final Logger LOGGER = Logger.getLogger(Play.class.getName());
	
	public void registerName(final String name, final Object value) {
		valuesPerName.put(name, value);
	}
	
	// TODO check if getClass() can be used instead of asking user for clazz
	public void registerClass(final Class<?> clazz, final Object value) {
		if( ! clazz.isInstance(value) )
			throw new ClassCastException(value + " is not a subclass of " + clazz);
		
		valuesPerClass.put(clazz, value);
	}
	
	public void registerNameAndClass(final String name, final Class<?> clazz, final Object value) {
		registerClass(clazz, value);
		registerName(name, value);
	}
	
	protected <T extends Object> T injectFields(final T instance, final SerializedProperties properties) {
		Class <?> clazz = instance.getClass();
		
		for( final Field field : clazz.getDeclaredFields() ) {
			if( field.isAnnotationPresent(Inject.class) ) {
				boolean injectionSucceeded = tryToInjectFieldUponName(instance, field, properties);
				
				if( ! injectionSucceeded )
					injectionSucceeded = tryToInjectFieldUponType(instance, field);
				
				if( ! injectionSucceeded )
					LOGGER.log(Level.WARNING, "Unable to inject the field : {0}", field);
			}
		}
		
		return instance;
	}

	private boolean tryToInjectFieldUponName(Object instance, Field field, SerializedProperties properties) {
		Inject inject = field.getAnnotation(Inject.class);
		String name = inject.name().isEmpty() ? field.getName() : inject.name();
		
		// Attempt to load the value from the property file first
		Optional <Object> prop = properties.get(name);
		
		if( prop.isPresent() ) 
			if( injectField(instance, field, prop.get()) )
				return true;
		
		// Then check environment
		if( ! valuesPerName.containsKey(name) )
			return false;
		
		Object value = valuesPerName.get(name);
		return injectField(instance, field, value);
	}
	
	private boolean tryToInjectFieldUponType(Object instance, Field field) {
		Class <?> type = field.getType();
		
		if( ! valuesPerClass.containsKey(type) )
			return false;
		
		Object value = valuesPerClass.get(type);
		return injectField(instance, field, value);
	}

	private boolean injectField(final Object instance, final Field field, Object value) {
		final AtomicBoolean injectionSucceeded = new AtomicBoolean(true);
		
		AccessController.doPrivileged((PrivilegedAction<?>) () -> {
			boolean isAccessible = field.isAccessible();
				
			try {
				field.setAccessible(true);
				field.set(instance, value);
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				injectionSucceeded.set(false);
			} finally {
				field.setAccessible(isAccessible);
			}
			
			return null;
		});
		
		return injectionSucceeded.get();
	}
}
