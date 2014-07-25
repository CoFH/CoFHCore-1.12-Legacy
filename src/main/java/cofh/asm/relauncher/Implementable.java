package cofh.asm.relauncher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Implementable {

	public String[] value();

	/**
	 * The *only* side on which these interfaces will be implemented if present
	 * (NONE == BOTH)
	 */
	public CoFHSide side() default CoFHSide.NONE;
}
