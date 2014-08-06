package cofh.asm;

import static cofh.asm.ASMCore.*;

import cpw.mods.fml.common.discovery.ASMDataTable;

import net.minecraft.launchwrapper.IClassTransformer;

public class PCCASMTransformer implements IClassTransformer {

	private static boolean scrappedData = false;
	private static final boolean ENABLE_HACK = Boolean.valueOf(System.getProperty("cofh.classloadinghack", "false"));
	
	public PCCASMTransformer() {

		ASMCore.init();
	}

	public static void scrapeData(ASMDataTable table) {

		ASMCore.scrapeData(table);
		scrappedData = true;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {

		if (bytes == null) {
			return null;
		}

		if (scrappedData && parsables.contains(name)) {
			bytes = parse(name, transformedName, bytes);
		}

		int index = hashes.get(transformedName);
		if (index != 0) {
			bytes = ASMCore.transform(index, name, transformedName, bytes);
		}

		if (ENABLE_HACK) {
			HACK(name, bytes);
		}

		return bytes;
	}
}
