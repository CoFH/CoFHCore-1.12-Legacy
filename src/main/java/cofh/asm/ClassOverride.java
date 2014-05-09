package cofh.asm;

import codechicken.lib.asm.ObfMapping;
import cofh.CoFHCore;
import cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import java.io.DataInputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassOverride {

	private static DeobfuscationTransformer transformer = new DeobfuscationTransformer() {

		@Override
		public String remapClassName(String name) {

			return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
		}

		@Override
		public String unmapClassName(String name) {

			return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.', '/')).replace('/', '.');
		}
	};

	public static byte[] overrideBytes(String name, byte[] bytes, String className, File location) {

		if (!className.equals(name.replace(".", "/"))) {
			return bytes;
		}

		try {
			ZipFile zip = new ZipFile(location);
			ZipEntry entry = zip.getEntry(className + ".class");
			if (entry == null) {
				zip.close();

				if (ObfMapping.obfuscated) {
					CoFHCore.log.warn(name + " not found in " + location.getName());
				} else// try and reverse runtime deobf
				{
					String rev = FMLDeobfuscatingRemapper.INSTANCE.unmap(className);
					if (rev != null && !rev.equals(className)) {
						byte[] nbytes = overrideBytes(rev.replace('/', '.'), bytes, rev, location);
						return transformer.transform(rev, null, nbytes);
					}
				}
			} else {
				DataInputStream zin = new DataInputStream(zip.getInputStream(entry));
				bytes = new byte[(int) entry.getSize()];
				zin.readFully(bytes);
				zip.close();
				CoFHCore.log.info(name + " was overriden from " + location.getName());
			}
		} catch (Exception e) {
			throw new RuntimeException("Error overriding " + name + " from " + location.getName(), e);
		}
		return bytes;
	}
}
