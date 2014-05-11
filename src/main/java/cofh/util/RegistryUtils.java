package cofh.util;

import com.google.common.collect.BiMap;

import net.minecraft.util.RegistryNamespaced;

public class RegistryUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void overwriteEntry(RegistryNamespaced registry, String name, Object object) {
		
		Object oldThing = registry.getObject(name);
		int id = registry.getIDForObject(oldThing);
		((BiMap)registry.registryObjects).forcePut(name, object);
		registry.underlyingIntegerMap.func_148746_a(object, id);
	}
}
