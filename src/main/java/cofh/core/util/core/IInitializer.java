package cofh.core.util.core;

/**
 * Interface which can be put on just about anything to allow for modular registration.
 *
 * @author King Lemming
 */
public interface IInitializer {

	boolean preInit();

	boolean initialize();

}
