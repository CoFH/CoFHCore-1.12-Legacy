package cofh.asm.relauncher;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds an interface to the class when the interface is present at runtime for soft dependencies.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Implementable {

	public String[] value();

	/**
	 * The *only* side on which these interfaces will be implemented if present. (DEFAULT: Both sides.)
	 */
	public CoFHSide side() default CoFHSide.DEFAULT;

}
