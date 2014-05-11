package cofh.util;

import com.google.common.collect.BiMap;

import net.minecraft.util.RegistryNamespaced;

public class RegistryUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void overwriteEntry(RegistryNamespaced registry, String name, Object object) {
		
		((BiMap)registry.registryObjects).forcePut(name, object);
	}
}
