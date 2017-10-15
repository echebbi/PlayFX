package fr.kazejiyu.piecefx.injection;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

/**
 * Represents the dependencies to inject into the Acts.
 * 
 * This class is a work in progress and will be refactored.
 * 
 * @author Emmanuel CHEBBI
 */
public class Dependencies {

	Map <String,Object> valuesPerName = new HashMap<>();
	Map <Class<?>,Object> valuesPerClass = new HashMap<>();
	
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
	
	public <T extends Object> T injectFields(final T instance) {
		Class <?> clazz = instance.getClass();
		
		for( final Field field : clazz.getDeclaredFields() ) {
			if( field.isAnnotationPresent(Inject.class) ) {
				boolean injectionSucceeded = tryToInjectFieldUponName(instance, field);
				
				if( ! injectionSucceeded )
					injectionSucceeded = tryToInjectFieldUponType(instance, field);
				
				if( ! injectionSucceeded )
					System.err.println("Failed to inject " + field);
			}
		}
		
		return instance;
	}

	private boolean tryToInjectFieldUponName(Object instance, Field field) {
		String name = field.getName();
		
		if( ! valuesPerName.containsKey(name) )
			return false;
		
		Object value = valuesPerName.get(name);
		return injectField(instance, field, value);
	}
	
	private boolean tryToInjectFieldUponType(Object instance, Field field) {
		Class <?> type = field.getType();
		
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
