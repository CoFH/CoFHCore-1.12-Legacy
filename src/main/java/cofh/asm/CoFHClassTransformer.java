package cofh.asm;

import cofh.asm.repack.codechicken.lib.asm.ASMBlock;
import cofh.asm.repack.codechicken.lib.asm.ASMReader;
import cofh.asm.repack.codechicken.lib.asm.ModularASMTransformer;
import cofh.asm.repack.codechicken.lib.asm.ModularASMTransformer.MethodInjector;
import cofh.asm.repack.codechicken.lib.asm.ObfMapping;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.util.Map;

import static cofh.asm.ASMCore.parsables;
import static cofh.asm.ASMCore.parse;

public class CoFHClassTransformer implements IClassTransformer {

	private static boolean scrappedData = false;

	private ModularASMTransformer transformer = new ModularASMTransformer();

	public CoFHClassTransformer() {

		ASMCore.init();
		loadTransformer();
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

		bytes = transformer.transform(name, bytes);

		return bytes;
	}

	private void loadTransformer() {

		Map<String, ASMBlock> blocks = ASMReader.loadResource("/assets/cofh/asm/hooks.asm");
		ObfMapping mapping;
		String desc = "(IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkGenerator;Lnet/minecraft/world/chunk/IChunkProvider;)V";
		mapping = new ObfMapping("net/minecraftforge/fml/common/registry/GameRegistry", "generateWorld", desc);
		transformer.add(new MethodInjector(mapping, blocks.get("i_preGenWorld"), true));
		transformer.add(new MethodInjector(mapping, blocks.get("i_postGenWorld"), false));

		mapping = new ObfMapping("net/minecraft/client/renderer/EntityRenderer", "func_78466_h", "(F)V");
		transformer.add(new MethodInjector(mapping, blocks.get("n_fogColor"), blocks.get("i_fogColor"), true));

	}
}
