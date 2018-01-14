package fr.kazejiyu.playfx.injection;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Appoints a field to inject.
 * 
 * @author Emmanuel CHEBBI
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Inject {
	String name() default "";
}
