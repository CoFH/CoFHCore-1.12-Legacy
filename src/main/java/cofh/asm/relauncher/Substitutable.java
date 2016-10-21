package cofh.asm.relauncher;

import java.lang.annotation.*;

/**
 * This annotation will replace the bytecode of the annotated method with that of the named method if the <tt>value</tt> condition is not met.
 * <p>
 * Ensure the named method has an identical signature (return type & parameters) to the annotated method. <br>
 * Ensure you do not switch on an enum inside the annotated method. JavaC implementation details means this will cause crashes.
 * <p>
 * Takes a class name as the value. e.g., "cofh.lib.network.ByteBufHelper"; requiring that class be available <br>
 * Can also substitute on modid using e.g., "mod:CoFHCore" as a value; requiring that mod be available <br>
 * Can also substitute on API using e.g., "api:CoFHAPI|energy" as a value; requiring that API be available <br>
 * Mod and API values can have a version range associated e.g., "mod:ThermalExpansion@[4.0.0, 4.1.0)"
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Substitutable {

    public String[] value();

    public String method();

    /**
     * The side from which this method will *always* be substituted.
     */
    public CoFHSide side() default CoFHSide.NONE;

}
