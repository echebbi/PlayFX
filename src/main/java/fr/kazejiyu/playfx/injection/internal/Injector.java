package fr.kazejiyu.playfx.injection.internal;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.kazejiyu.playfx.Play;
import fr.kazejiyu.playfx.configuration.SerializedProperties;
import fr.kazejiyu.playfx.injection.Inject;

/**
 * Injects fields annotated with {@link Inject}.
 * 
 * @author Emmanuel CHEBBI 
 */
class Injector {
	
	/** Creates instances upon fields' name */
	private final Function<String, Object> instanciator;
	
	private static final Logger LOGGER = Logger.getLogger(Play.class.getName());

	protected Injector(Function <String,Object> instanciator) {
		this.instanciator = instanciator;
	}
	
	/**
	 * Injects {@code instance}'s fields that are annotated with {@link Inject @Inject}. <br>
	 * <br>
	 * This method attempts, in order, to inject a field with a value taken from:
	 * <ol>
	 * 	<li>given {@code properties} (typically extracted from controller's conf file),</li>
	 * 	<li>system properties (correspond to command line arguments)</li>
	 * 	<li>the instanciator given to the instance's constructor.</li>
	 * </ol>
	 * 
	 * If a field cannot be injected successfully, a warning is printed on {@link Play}'s logger.
	 * 
	 * @param instance
	 * 			The instance to inject.
	 * @param properties
	 * 			Controller's properties. 
	 * @param <T>
	 * 			Runtime type of {@code instance}.
	 * 
	 * @return given {@code instance} which fields have been injected.
	 * 
	 * @throws NullPointerException if any of the arguments is null
	 */
	protected <T extends Object> T injectFields(final T instance, final SerializedProperties properties) {
		Class <?> clazz = instance.getClass();
		
		for( final Field field : clazz.getDeclaredFields() ) {
			if( field.isAnnotationPresent(Inject.class) ) {
				boolean injectionSucceeded = tryToInjectFieldWithConfigurationFile(instance, field, properties);

				if( ! injectionSucceeded )
					injectionSucceeded = tryToInjectFieldWithProperties(instance, field);
				
				if( ! injectionSucceeded )
					injectionSucceeded = tryToInjectFieldWithInstanciator(instance, field);
				
				if( ! injectionSucceeded )
					LOGGER.log(Level.WARNING, "Unable to inject the field : {0}", field);
			}
		}
		
		return instance;
	}
	
	private boolean tryToInjectFieldWithConfigurationFile(Object instance, Field field, SerializedProperties properties) {
		return tryToInject(instance, field, name -> properties.get(name).orElse(null));
	}
	
	private boolean tryToInjectFieldWithProperties(Object instance, Field field) {
		return tryToInject(instance, field, System::getProperty);
	}

	private boolean tryToInjectFieldWithInstanciator(Object instance, Field field) {
		return tryToInject(instance, field, instanciator);
	}
	
	private boolean tryToInject(Object instance, Field field, Function <String,Object> valueToInject) {
		Inject inject = field.getAnnotation(Inject.class);
		String name = inject.name().isEmpty() ? field.getName() : inject.name();
		
		Object value = valueToInject.apply(name);
		
		// Injecting null would erase possible default values
		if( value == null )
			return false;
		
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
				LOGGER.log(Level.SEVERE, "Failed to inject field {0} with : {1}.\nException is ", new Object[] {field, value, e});
				injectionSucceeded.set(false);
			} finally {
				field.setAccessible(isAccessible);
			}
			
			return null;
		});
		
		return injectionSucceeded.get();
	}
}
