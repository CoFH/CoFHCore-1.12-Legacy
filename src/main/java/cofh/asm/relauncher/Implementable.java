package cofh.asm.relauncher;

import java.lang.annotation.*;

/**
 * Adds an interface to the class when the interface is present at runtime for soft dependencies.
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target ({ ElementType.TYPE })
public @interface Implementable {

	String[] value();

	/**
	 * The *only* side on which these interfaces will be implemented if present (NONE == BOTH)
	 */
	CoFHSide side() default CoFHSide.NONE;
}
