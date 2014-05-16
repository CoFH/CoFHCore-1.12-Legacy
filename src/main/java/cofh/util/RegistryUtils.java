package cofh.util;

import com.google.common.collect.BiMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;

public class RegistryUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void overwriteEntry(RegistryNamespaced registry, String name, Object object) {
		
		Object oldThing = registry.getObject(name);
		int id = registry.getIDForObject(oldThing);
		BiMap map = ((BiMap)registry.registryObjects);
		registry.underlyingIntegerMap.func_148746_a(object, id);
		map.remove(name);
		map.forcePut(name, object);
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean textureExists(String texture)
	{
		
		try {
			
			Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(texture));
			return true;
		} catch (Throwable _) {
			
			return false;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean itemTextureExists(String texture)
	{
		
		int i = texture.indexOf(':'); 
		if (i > 0)
			texture = texture.substring(0, i) + ":textures/items/" + texture.substring(i + 1, texture.length());
		else
			texture = "textures/items/" + texture;
		return textureExists(texture + ".png");
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean blockTextureExists(String texture)
	{
		
		int i = texture.indexOf(':'); 
		if (i > 0)
			texture = texture.substring(0, i) + ":textures/blocks/" + texture.substring(i + 1, texture.length());
		else
			texture = "textures/items/" + texture;
		return textureExists(texture + ".png");
	}
}
