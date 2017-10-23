package fr.kazejiyu.playfx.injection;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.kazejiyu.playfx.Play;
import fr.kazejiyu.playfx.configuration.SerializedProperties;

/**
 * Injects fields annotated with {@link Inject}.
 * 
 * @author Emmanuel CHEBBI 
 */
class Injector {
	
	private final Function<String, Object> instanciator;
	
	private static final Logger LOGGER = Logger.getLogger(Play.class.getName());

	protected Injector(Function <String,Object> instanciator) {
		this.instanciator = instanciator;
	}
	
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
