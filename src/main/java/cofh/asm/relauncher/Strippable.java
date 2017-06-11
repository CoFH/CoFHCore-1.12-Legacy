package cofh.asm.relauncher;

import java.lang.annotation.*;

/**
 * This annotation will remove the annotated method, field, or class if the <tt>value</tt> condition is not met.
 * <p>
 * When used on a class, methods from referenced interfaces will not be removed <br>
 * When using this annotation on methods, ensure you do not switch on an enum inside that method. JavaC implementation details means this will cause crashes.
 * <p>
 * Takes a class name as the value. e.g., "cofh.lib.network.ByteBufHelper"; requiring that class be available <br>
 * Can also substitute on modid using e.g., "mod:CoFHCore" as a value; requiring that mod be available <br>
 * Can also substitute on API using e.g., "api:CoFHAPI|energy" as a value; requiring that API be available <br>
 * Mod and API values can have a version range associated e.g., "mod:ThermalExpansion@[4.0.0, 4.1.0)"
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target ({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE })
public @interface Strippable {

	String[] value();

	/**
	 * The side from which these interfaces will *always* be stripped.
	 */
	CoFHSide side() default CoFHSide.NONE;
}
