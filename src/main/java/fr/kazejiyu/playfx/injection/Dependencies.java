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
	
	/**
	 * Makes {@code value} available as injected value.
	 * <br><br>
	 * {@code value} will be injected into fields annotated with {@link Inject}
	 * and called {@code name}.
	 * 
	 * @param name
	 * 			The name of the fields to inject.
	 * @param value
	 * 			The value to be injected.
	 */
	public void registerName(final String name, final Object value) {
		valuesPerName.put(name, value);
	}
	
	/**
	 * Makes {@code value} available as injected value.
	 * <br><br>
	 * {@code value} will be injected into fields of the same type.
	 * <br><br>
	 * This convenience method is a shortcut for {@code registerClass(value.getClass(), value);}
	 * 
	 * @param value
	 * 			The value to be injected.
	 */
	public void registerClass(final Object value) {
		registerClass(value.getClass(), value);
	}
	
	/**
	 * Makes {@code value} available as injected value.
	 * <br><br>
	 * {@code value} will be injected into fields which type is {@code clazz}.
	 * 
	 * @param clazz
	 * 			The type of the fields to inject.
	 * @param value
	 * 			The value to be injected.
	 */
	public void registerClass(final Class<?> clazz, final Object value) {
		if( ! clazz.isInstance(value) )
			throw new ClassCastException(value + " is not a subclass of " + clazz);
		
		valuesPerClass.put(clazz, value);
	}
	
	/**
	 * Makes {@code value} available as injected value.
	 * <br><br>
	 * {@code value} will be injected into fields called {@code name} or which type 
	 * match {@code value}'s one.
	 * <br><br>
	 * This convenience method is a shortcut for {@code registerNameAndClass(name, value.getClass(), value);}
	 * 
	 * @param name
	 * 			The name of the fields to inject.
	 * @param value
	 * 			The value to inject.
	 */
	public void registerNameAndClass(final String name, final Object value) {
		registerNameAndClass(name, value.getClass(), value);
	}
	
	/**
	 * Makes {@code value} available as injected value.
	 * <br><br>
	 * {@code value} will be injected into fields called {@code name} or which type 
	 * is {@code clazz}.
	 * 
	 * @param name
	 * 			The name of the fields to inject.
	 * @param clazz
	 * 			The type of the fields to inject.
	 * @param value
	 * 			The value to inject.
	 */
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
