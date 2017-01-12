package cofh.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import static cofh.asm.ASMCore.parsables;
import static cofh.asm.ASMCore.parse;

public class CoFHClassTransformer implements IClassTransformer {

	private static boolean scrappedData = false;

	public CoFHClassTransformer() {

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

		//int index = hashes.get(transformedName);
		//if (index != 0) {
		//    bytes = ASMCore.transform(index, name, transformedName, bytes);
		//}

		return bytes;
	}
}
