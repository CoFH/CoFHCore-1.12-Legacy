package cofh.asm.relauncher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE })
/**
 * When using this annotation on methods, ensure you do not switch on an enum inside that method.
 * JavaC implementation details means this will cause crashes.
 * <p>
 * Can also strip on modid using "mod:CoFHCore" as a value
 */
public @interface Strippable {

	public String[] value();
}
