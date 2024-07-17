package cofh.lib.util;

import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.RegistryDelegate;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class RegistryUtils {

	private RegistryUtils() {

	}

	private static class Repl {

		private static IdentityHashMap<RegistryNamespaced, Multimap<String, Object>> replacements;
		private static Class<RegistryDelegate<?>> DelegateClass;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static void overwrite_do(RegistryNamespaced registry, String name, Object object, Object oldThing) {

			int id = registry.getIDForObject(oldThing);
			BiMap map = ((BiMap) registry.registryObjects);
			registry.underlyingIntegerMap.func_148746_a(object, id);
			map.remove(name);
			map.forcePut(name, object);
		}

		private static void alterDelegateChain(RegistryNamespaced registry, String id, Object object) {

			Multimap<String, Object> map = replacements.get(registry);
			List<Object> c = (List<Object>) map.get(id);
			int i = 0, e = c.size() - 1;
			Object end = c.get(e);
			for (; i <= e; ++i) {
				Object t = c.get(i);
				Repl.alterDelegate(t, end);
			}
		}

		private static void alterDelegate(Object obj, Object repl) {

			if (obj instanceof Item) {
				RegistryDelegate<Item> delegate = ((Item) obj).delegate;
				ReflectionHelper.setPrivateValue(DelegateClass, delegate, repl, "referant");
				ReflectionHelper.setPrivateValue(DelegateClass, ((Item) repl).delegate, delegate.name(), "name");
			}
		}

		static {

			replacements = new IdentityHashMap<RegistryNamespaced, Multimap<String, Object>>(2);
			MinecraftForge.EVENT_BUS.register(new RegistryUtils());
			try {
				DelegateClass = (Class<RegistryDelegate<?>>) Class.forName("cpw.mods.fml.common.registry.RegistryDelegate$Delegate");
			} catch (Throwable e) {
				Throwables.propagate(e);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void _(WorldEvent.Load event) {

		if (Repl.replacements.size() < 1) {
			return;
		}
		for (Map.Entry<RegistryNamespaced, Multimap<String, Object>> entry : Repl.replacements.entrySet()) {
			RegistryNamespaced reg = entry.getKey();
			Multimap<String, Object> map = entry.getValue();
			Iterator<String> v = map.keySet().iterator();
			while (v.hasNext()) {
				String id = v.next();
				List<Object> c = (List<Object>) map.get(id);
				int i = 0, e = c.size() - 1;
				Object end = c.get(e);
				if (reg.getIDForObject(c.get(0)) != reg.getIDForObject(end)) {
					for (; i <= e; ++i) {
						Object t = c.get(i);
						Object oldThing = reg.getObject(id);
						Repl.overwrite_do(reg, id, t, oldThing);
						Repl.alterDelegate(oldThing, end);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void overwriteEntry(RegistryNamespaced registry, String name, Object object) {

		Object oldThing = registry.getObject(name);
		Repl.overwrite_do(registry, name, object, oldThing);
		Multimap<String, Object> reg = Repl.replacements.get(registry);
		if (reg == null) {
			Repl.replacements.put(registry, reg = ArrayListMultimap.create());
		}
		if (!reg.containsKey(name)) {
			reg.put(name, oldThing);
		}
		reg.put(name, object);
		Repl.alterDelegateChain(registry, name, object);
	}

	@SideOnly(Side.CLIENT)
	public static boolean textureExists(ResourceLocation texture) {

		try {
			Minecraft.getMinecraft().getResourceManager().getAllResources(texture);
			return true;
		} catch (Throwable t) { // pokemon!
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public static boolean textureExists(String texture) {

		return textureExists(new ResourceLocation(texture));
	}

	@SideOnly(Side.CLIENT)
	public static boolean blockTextureExists(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/blocks/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/blocks/" + texture;
		}
		return textureExists(texture + ".png");
	}

	@SideOnly(Side.CLIENT)
	public static boolean itemTextureExists(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/items/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/items/" + texture;
		}
		return textureExists(texture + ".png");
	}

	@SideOnly(Side.CLIENT)
	public static int getTextureColor(ResourceLocation texture) {

		try {
			BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());

			int[] a = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), a, 0, image.getWidth());

			int r = a[0];
			for (int i = a.length; i-- > 1;) {
				int t = a[i], v;
				v = (((r >> 24) & 255) + ((t >> 24) & 255)) / 2;
				r &= 0x00FFFFFF;
				r |= v << 24;
				v = (((r >> 16) & 255) + ((t >> 16) & 255)) / 2;
				r &= 0xFF00FFFF;
				r |= v << 16;
				v = (((r >> 8) & 255) + ((t >> 8) & 255)) / 2;
				r &= 0xFFFF00FF;
				r |= v << 8;
				v = (((r >> 0) & 255) + ((t >> 0) & 255)) / 2;
				r &= 0xFFFFFF00;
				r |= v << 0;
			}
			return r;
		} catch (Throwable t) { // pokemon!
			return 0xFFFFFF;
		}
	}

	@SideOnly(Side.CLIENT)
	public static int getTextureColor(String texture) {

		return getTextureColor(new ResourceLocation(texture));
	}

	@SideOnly(Side.CLIENT)
	public static int getBlockTextureColor(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/blocks/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/blocks/" + texture;
		}
		return getTextureColor(texture + ".png");
	}

	@SideOnly(Side.CLIENT)
	public static int getItemTextureColor(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/items/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/items/" + texture;
		}
		return getTextureColor(texture + ".png");
	}

}
