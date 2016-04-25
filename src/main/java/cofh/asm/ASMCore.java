package cofh.asm;

import static org.objectweb.asm.Opcodes.*;

import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Strippable;
import cofh.asm.relauncher.Substitutable;
import cofh.mod.updater.ModRange;
import cofh.mod.updater.ModVersion;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;

import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

class ASMCore {

	static Logger log = LogManager.getLogger("CoFH ASM");

	static TObjectByteHashMap<String> hashes = new TObjectByteHashMap<String>(30, 1, (byte) 0);
	static THashSet<String> parsables, implementables, strippables, substitutables;
	static final String implementableDesc, strippableDesc, substitutableDesc;
	static String side;

	static void init() {

	}

	static {

		implementableDesc = Type.getDescriptor(Implementable.class);
		strippableDesc = Type.getDescriptor(Strippable.class);
		substitutableDesc = Type.getDescriptor(Substitutable.class);

		parsables = new THashSet<String>(30);
		implementables = new THashSet<String>(10);
		strippables = new THashSet<String>(10);
		substitutables = new THashSet<String>(10);

		hashes.put("net.minecraft.world.WorldServer", (byte) 1);
		hashes.put("net.minecraft.world.World", (byte) 2);
		hashes.put("skyboy.core.world.WorldProxy", (byte) 3);
		hashes.put("skyboy.core.world.WorldServerProxy", (byte) 4);
		hashes.put("net.minecraft.block.BlockPane", (byte) 5);
		hashes.put("net.minecraft.block.Block", (byte) 6);
		hashes.put("net.minecraft.client.multiplayer.PlayerControllerMP", (byte) 7);
		hashes.put("net.minecraft.util.LongHashMap", (byte) 8);
		if (Boolean.parseBoolean(System.getProperty("cofh.lightedit", "true"))) {
			hashes.put("net.minecraft.world.chunk.Chunk", (byte) 9);
		}
		hashes.put("net.minecraft.client.Minecraft", (byte) 10);
		hashes.put("net.minecraft.client.renderer.RenderBlocks", (byte) 11);
		hashes.put("net.minecraft.tileentity.TileEntity", (byte) 12);
		hashes.put("net.minecraft.inventory.Container", (byte) 13);
		hashes.put("net.minecraft.entity.Entity", (byte) 14);
		hashes.put("net.minecraft.entity.item.EntityItem", (byte) 15);
		hashes.put("cofh.asmhooks.HooksCore", (byte) 16);
		hashes.put("net.minecraft.enchantment.Enchantment", (byte) 17);
		hashes.put("net.minecraft.item.Item", (byte) 18);
		hashes.put("net.minecraft.client.gui.GuiKeyBindingList$KeyEntry", (byte) 19);
		hashes.put("net.minecraft.client.settings.KeyBinding", (byte) 20);
		hashes.put("cpw.mods.fml.common.registry.GameRegistry", (byte) 21);
		if (Boolean.parseBoolean(System.getProperty("cofh.profiler.debug", "false"))) {
			hashes.put("net.minecraft.profiler.Profiler", (byte) 22);
		}
	}

	static final ArrayList<String> workingPath = new ArrayList<String>();
	private static final String[] emptyList = {};

	static class AnnotationInfo {

		public String side = "NONE";
		public String[] values = emptyList;
		public String method = "!unmatchable!";
	}

	static byte[] parse(String name, String transformedName, byte[] bytes) {

		workingPath.add(transformedName);

		if (implementables.contains(name)) {
			log.debug("Adding runtime interfaces to " + transformedName);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (implement(cn)) {
				ClassWriter cw = new ClassWriter(0);
				cn.accept(cw);
				bytes = cw.toByteArray();
			} else {
				log.debug("Nothing implemented on " + transformedName);
			}
		}

		if (substitutables.contains(name)) {
			log.debug("Substituting methods from " + transformedName);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (substitute(cn)) {
				ClassWriter cw = new ClassWriter(0);
				cn.accept(cw);
				bytes = cw.toByteArray();
			} else {
				log.debug("Nothing substituted from " + transformedName);
			}
		}

		if (strippables.contains(name)) {
			log.debug("Stripping methods and fields from " + transformedName);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (strip(cn)) {
				ClassWriter cw = new ClassWriter(0);
				cn.accept(cw);
				bytes = cw.toByteArray();
			} else {
				log.debug("Nothing stripped from " + transformedName);
			}
		}

		workingPath.remove(workingPath.size() - 1);
		return bytes;
	}

	static byte[] transform(int index, String name, String transformedName, byte[] bytes) {

		ClassReader cr = new ClassReader(bytes);

		switch (index) {
		case 1:
			return writeWorldServer(transformedName, bytes, cr);
		case 2:
			return writeWorld(transformedName, bytes, cr);
		case 3:
			return writeWorldProxy(name, bytes, cr);
		case 4:
			return writeWorldServerProxy(name, bytes, cr);
		case 5:
			return alterBlockPane(transformedName, bytes, cr);
		case 6:
			return alterBlock(transformedName, bytes, cr);
		case 7:
			return alterController(transformedName, bytes, cr);
		case 8:
			return alterLongHashMap(transformedName, bytes, cr);
		case 9:
			return alterChunk(transformedName, bytes, cr);
		case 10:
			return alterMinecraft(transformedName, bytes, cr);
		case 11:
			return alterRenderBlocks(transformedName, bytes, cr);
		case 12:
			return alterTileEntity(transformedName, bytes, cr);
		case 13:
			return alterContainer(transformedName, bytes, cr);
		case 14:
			return alterEntity(transformedName, bytes, cr);
		case 15:
			return alterEntityItem(transformedName, bytes, cr);
		case 16:
			return alterHooksCore(name, bytes, cr);
		case 17:
			return alterEnchantment(transformedName, bytes, cr);
		case 18:
			return alterItem(transformedName, bytes, cr);
		case 19:
			return alterKeyEntry(transformedName, bytes, cr);
		case 20:
			return alterKeyBinding(transformedName, bytes, cr);
		case 21:
			return alterGameRegistry(name, bytes, cr);
		case 22:
			return alterProfiler(transformedName, bytes, cr);

		default:
			return bytes;
		}
	}

	// { Fix Forge

	private static byte[] alterGameRegistry(String name, byte[] bytes, ClassReader cr) {

		// implements https://github.com/MinecraftForge/MinecraftForge/issues/2034
		String names = "generateWorld";

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names.equals(n.name)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			final String sig = "(Lnet/minecraft/world/World;II)V";

			AbstractInsnNode n = m.instructions.getFirst();
			m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 2));
			m.instructions.insert(n, n = new VarInsnNode(ILOAD, 0));
			m.instructions.insert(n, n = new VarInsnNode(ILOAD, 1));
			m.instructions.insert(n, new MethodInsnNode(INVOKESTATIC, "cofh/asmhooks/HooksCore", "preGenerateWorld", sig, false));

			for (n = m.instructions.getFirst(); n != null; n = n.getNext()) {
				if (n.getOpcode() == RETURN) {
					m.instructions.insertBefore(n, new VarInsnNode(ALOAD, 2));
					m.instructions.insertBefore(n, new VarInsnNode(ILOAD, 0));
					m.instructions.insertBefore(n, new VarInsnNode(ILOAD, 1));
					m.instructions.insertBefore(n, new MethodInsnNode(INVOKESTATIC, "cofh/asmhooks/HooksCore", "postGenerateWorld", sig, false));
				}
			}

			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	// }



	private static byte[] alterProfiler(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_76319_b", "func_76320_a", "func_76318_c", "func_76317_a" };
		} else {
			names = new String[] { "endSection", "startSection", "endStartSection", "clearProfiling" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		cn.fields.add(new FieldNode(ACC_PRIVATE | ACC_SYNTHETIC, "cofh_stack", "Ljava/util/Deque;", null, null));
		cn.fields.add(new FieldNode(ACC_PRIVATE | ACC_SYNTHETIC, "cofh_endStart", "Z", null, Boolean.FALSE));
		for (MethodNode m : cn.methods) {
			if ("<init>".equals(m.name)) {
				LabelNode a = new LabelNode(new Label());
				AbstractInsnNode n;
				for (n = m.instructions.getFirst(); n != null; n = n.getNext()) {
					if (n.getOpcode() == INVOKESPECIAL) {
						break;
					}
				}
				m.instructions.insert(n, n = a);
				m.instructions.insert(n, n = new LineNumberNode(-15000, a));
				m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/util/LinkedList"));
				m.instructions.insert(n, n = new InsnNode(DUP));
				m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/util/LinkedList", "<init>", "()V", false));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
			} else if (names[0].equals(m.name)) {
				int c = 0;
				for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
					if (n.getOpcode() == ALOAD && ++c > 1) {
						LabelNode lCond = new LabelNode(new Label());
						LabelNode lGuard = new LabelNode(new Label());
						m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_endStart", "Z"));
						m.instructions.insert(n, n = new JumpInsnNode(IFNE, lGuard));
						m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEINTERFACE, "java/util/Deque", "pop", "()Ljava/lang/Object;", true));
						m.instructions.insert(n, n = new TypeInsnNode(CHECKCAST, "java/lang/Throwable"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false));
						m.instructions.insert(n, n = new InsnNode(ARRAYLENGTH));
						m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/lang/Throwable"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false));
						m.instructions.insert(n, n = new InsnNode(ARRAYLENGTH));
						m.instructions.insert(n, n = new JumpInsnNode(IF_ICMPLE, lCond));
						m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/lang/Error"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new LdcInsnNode("Detected bad stack depth call to endSection"));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/lang/Error", "<init>", "(Ljava/lang/String;)V", false));
						m.instructions.insert(n, n = new InsnNode(SWAP));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "initCause", "(Ljava/lang/Throwable;)Ljava/lang/Throwable;", false));
						m.instructions.insert(n, n = new InsnNode(ATHROW));
						m.instructions.insert(n, n = new FrameNode(F_SAME1, 0, null, 0, new Object[] { "java/lang/Throwable" }));
						m.instructions.insert(n, n = lCond);
						m.instructions.insert(n, n = new InsnNode(POP));
						m.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
						m.instructions.insert(n, n = lGuard);
						break;
					}
				}
			} else if (names[1].equals(m.name)) {
				int c = 0;
				for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
					if (n.getOpcode() == ALOAD && ++c > 1) {
						LabelNode lGuard = new LabelNode(new Label());
						m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_endStart", "Z"));
						m.instructions.insert(n, n = new JumpInsnNode(IFNE, lGuard));
						m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
						m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/lang/Error"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new LdcInsnNode("Failed to call endSection after calling startSection"));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/lang/Error", "<init>", "(Ljava/lang/String;)V", false));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEINTERFACE, "java/util/Deque", "push", "(Ljava/lang/Object;)V", true));
						m.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
						m.instructions.insert(n, n = lGuard);
						break;
					}
				}
			} else if (names[2].equals(m.name)) {
				AbstractInsnNode n;
				m.instructions.insertBefore(m.instructions.getFirst(), n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new InsnNode(ICONST_1));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, name, "cofh_endStart", "Z"));
				m.instructions.insertBefore(m.instructions.getLast(), n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new InsnNode(ICONST_0));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, name, "cofh_endStart", "Z"));
			} else if (names[3].equals(m.name)) {
				AbstractInsnNode n;
				m.instructions.insertBefore(m.instructions.getFirst(), n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
				m.instructions.insert(n, n = new MethodInsnNode(INVOKEINTERFACE, "java/util/Deque", "clear", "()V", true));
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();
		//saveTransformedClass(bytes, name);

		return bytes;
	}

    private static void saveTransformedClass(final byte[] data, final String transformedName) {

        final File outFile = new File(new File("../decompClasses"), transformedName.replace('.', File.separatorChar) + ".class");
        final File outDir = outFile.getParentFile();

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (outFile.exists()) {
            outFile.delete();
        }

        try {
            final FileOutputStream output = new FileOutputStream(outFile);
            output.write(data);
            output.close();
        } catch (IOException ex) {
        }
    }

	// { Improve Vanilla
	private static byte[] alterContainer(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_75135_a", "field_75151_b" };
		} else {
			names = new String[] { "mergeItemStack", "inventorySlots" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/item/ItemStack;IIZ)Z";

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name) && sig.equals(n.desc)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			m.instructions.clear();
			m.instructions.add(new VarInsnNode(ALOAD, 0));
			m.instructions.add(new FieldInsnNode(GETFIELD, name, names[1], "Ljava/util/List;"));
			m.instructions.add(new VarInsnNode(ALOAD, 1));
			m.instructions.add(new VarInsnNode(ILOAD, 2));
			m.instructions.add(new VarInsnNode(ILOAD, 3));
			m.instructions.add(new VarInsnNode(ILOAD, 4));
			m.instructions.add(new InsnNode(ICONST_0));
			m.instructions.add(new MethodInsnNode(INVOKESTATIC, "cofh/lib/util/helpers/InventoryHelper", "mergeItemStack",
					"(Ljava/util/List;Lnet/minecraft/item/ItemStack;IIZZ)Z", false));
			m.instructions.add(new InsnNode(IRETURN));

			// this fixes a crash in dev and with cauldron
			m.localVariables = null;

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterEnchantment(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_92089_a", "func_77973_b", "" };
		} else {
			names = new String[] { "canApply", "getItem", "" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			AbstractInsnNode n = m.instructions.getFirst();
			LabelNode end = new LabelNode(), out = new LabelNode();
			m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 1));
			m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", names[1], "()Lnet/minecraft/item/Item;", false));
			m.instructions.insert(n, n = new VarInsnNode(ALOAD, 1));
			m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/Item", "cofh_canEnchantApply",
					"(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/Enchantment;)I", false));
			m.instructions.insert(n, n = new InsnNode(DUP));
			m.instructions.insert(n, n = new JumpInsnNode(IFLT, end));
			m.instructions.insert(n, n = new JumpInsnNode(IFEQ, out));
			m.instructions.insert(n, n = new InsnNode(ICONST_1));
			m.instructions.insert(n, n = new InsnNode(IRETURN));
			m.instructions.insert(n, n = out);
			m.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
			m.instructions.insert(n, n = new InsnNode(ICONST_0));
			m.instructions.insert(n, n = new InsnNode(IRETURN));
			m.instructions.insert(n, n = end);
			m.instructions.insert(n, n = new FrameNode(F_SAME1, 0, null, 0, new Object[] { INTEGER }));
			m.instructions.insert(n, n = new InsnNode(POP));

			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterItem(String name, byte[] bytes, ClassReader cr) {

		name = name.replace('.', '/');
		ClassWriter cw = new ClassWriter(0);
		cr.accept(cw, 0);
		String sig = "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/Enchantment;)I";
		cw.newMethod(name, "cofh_canEnchantApply", sig, true);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "cofh_canEnchantApply", sig, null, null);
		mv.visitCode();
		mv.visitInsn(ICONST_M1);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		cw.visitEnd();
		bytes = cw.toByteArray();
		return bytes;
	}

	private static byte[] alterBlock(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_149671_p", "", "" };
		} else {
			names = new String[] { "registerBlocks", "t", "" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
				if (n.getOpcode() == NEW) {
					AbstractInsnNode p = n.getPrevious().getPrevious();
					if (p.getOpcode() != BIPUSH) {
						continue;
					}
					TypeInsnNode node = ((TypeInsnNode) n);
					switch (((IntInsnNode) p).operand) {
					case 8: // flowing water
						node.desc = "cofh/asmhooks/block/BlockTickingWater";
						break;
					case 9: // still water
						node.desc = "cofh/asmhooks/block/BlockWater";
						break;
					default:
						node = null;
					}
					if (node != null) {
						((MethodInsnNode) n.getNext().getNext().getNext()).owner = node.desc;
					}
				}
			}

			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterEntityItem(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_85054_d", "", "" };
		} else {
			names = new String[] { "searchForOtherItemsNearby", "", "" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			m.localVariables = null;

			m.instructions.clear();
			m.instructions.add(new VarInsnNode(ALOAD, 0));
			m.instructions.add(new MethodInsnNode(INVOKESTATIC, "cofh/asmhooks/HooksCore", "stackItems", "(Lnet/minecraft/entity/item/EntityItem;)V", false));
			m.instructions.add(new InsnNode(RETURN));

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private static byte[] alterEntity(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_70091_d", "func_72945_a", "func_70104_M" };
		} else {
			names = new String[] { "moveEntity", "getCollidingBoundingBoxes", "canBePushed" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		String mOwner = "net/minecraft/world/World";

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			for (int i = 0, e = m.instructions.size(); i < e; ++i) {
				AbstractInsnNode n = m.instructions.get(i);
				if (n.getOpcode() == INVOKEVIRTUAL) {
					MethodInsnNode mn = (MethodInsnNode) n;
					if (mOwner.equals(mn.owner) && names[1].equals(mn.name)) {
						mn.setOpcode(INVOKESTATIC);
						mn.owner = "cofh/asmhooks/HooksCore";
						mn.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;";
						mn.name = "getEntityCollisionBoxes";
					}
				}
			}

			/*
			 *
			 * mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity", "boundingBox", "Lnet/minecraft/util/AxisAlignedBB;");
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/AxisAlignedBB", "copy", "()Lnet/minecraft/util/AxisAlignedBB;"); mv.visitVarInsn(ASTORE,
			 * 19); Label l27 = new Label(); mv.visitLabel(l27); mv.visitLineNumber(617, l27); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD,
			 * "net/minecraft/entity/Entity", "onGround", "Z"); Label l28 = new Label(); mv.visitJumpInsn(IFEQ, l28); mv.visitVarInsn(ALOAD, 0);
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/Entity", "isSneaking", "()Z"); mv.visitJumpInsn(IFEQ, l28); mv.visitVarInsn(ALOAD, 0);
			 * mv.visitTypeInsn(INSTANCEOF, "net/minecraft/entity/player/EntityPlayer"); mv.visitJumpInsn(IFEQ, l28); mv.visitInsn(ICONST_1); Label l29 = new
			 * Label(); mv.visitJumpInsn(GOTO, l29); mv.visitLabel(l28); mv.visitFrame(Opcodes.F_FULL, 11, new Object[] {"net/minecraft/entity/Entity",
			 * Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE,
			 * "net/minecraft/util/AxisAlignedBB"}, 0, new Object[] {}); mv.visitInsn(ICONST_0); mv.visitLabel(l29); mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
			 * new Object[] {Opcodes.INTEGER}); mv.visitVarInsn(ISTORE, 20); Label l30 = new Label(); mv.visitLabel(l30); mv.visitLineNumber(619, l30);
			 * mv.visitVarInsn(ILOAD, 20); Label l31 = new Label(); mv.visitJumpInsn(IFEQ, l31); Label l32 = new Label(); mv.visitLabel(l32);
			 * mv.visitLineNumber(623, l32); mv.visitLdcInsn(new Double("0.05")); mv.visitVarInsn(DSTORE, 21); Label l33 = new Label(); mv.visitLabel(l33);
			 * Label l34 = new Label(); mv.visitJumpInsn(GOTO, l34); Label l35 = new Label(); mv.visitLabel(l35); mv.visitLineNumber(625, l35);
			 * mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.DOUBLE}, 0, null); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DLOAD,
			 * 21); mv.visitInsn(DCMPG); Label l36 = new Label(); mv.visitJumpInsn(IFGE, l36); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DLOAD, 21);
			 * mv.visitInsn(DNEG); mv.visitInsn(DCMPL); mv.visitJumpInsn(IFLT, l36); Label l37 = new Label(); mv.visitLabel(l37); mv.visitLineNumber(627, l37);
			 * mv.visitInsn(DCONST_0); mv.visitVarInsn(DSTORE, 1); Label l38 = new Label(); mv.visitLabel(l38); mv.visitLineNumber(628, l38); Label l39 = new
			 * Label(); mv.visitJumpInsn(GOTO, l39); mv.visitLabel(l36); mv.visitLineNumber(629, l36); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			 * mv.visitVarInsn(DLOAD, 1); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); Label l40 = new Label(); mv.visitJumpInsn(IFLE, l40); Label l41 = new
			 * Label(); mv.visitLabel(l41); mv.visitLineNumber(631, l41); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DSUB);
			 * mv.visitVarInsn(DSTORE, 1); Label l42 = new Label(); mv.visitLabel(l42); mv.visitLineNumber(632, l42); mv.visitJumpInsn(GOTO, l39);
			 * mv.visitLabel(l40); mv.visitLineNumber(635, l40); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1);
			 * mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DADD); mv.visitVarInsn(DSTORE, 1); mv.visitLabel(l39); mv.visitLineNumber(623, l39);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DSTORE, 13); mv.visitLabel(l34);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); Label l43 = new Label();
			 * mv.visitJumpInsn(IFEQ, l43); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity", "worldObj",
			 * "Lnet/minecraft/world/World;"); mv.visitVarInsn(ALOAD, 0); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity",
			 * "boundingBox", "Lnet/minecraft/util/AxisAlignedBB;"); mv.visitVarInsn(DLOAD, 1); mv.visitLdcInsn(new Double("-0.9375")); mv.visitInsn(DCONST_0);
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/AxisAlignedBB", "getOffsetBoundingBox", "(DDD)Lnet/minecraft/util/AxisAlignedBB;");
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getCollidingBoundingBoxes",
			 * "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"); mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List",
			 * "isEmpty", "()Z"); mv.visitJumpInsn(IFNE, l35); Label l44 = new Label(); mv.visitLabel(l44); mv.visitLineNumber(639, l44);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitJumpInsn(GOTO, l43); Label l45 = new Label(); mv.visitLabel(l45);
			 * mv.visitLineNumber(641, l45); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DLOAD, 21);
			 * mv.visitInsn(DCMPG); Label l46 = new Label(); mv.visitJumpInsn(IFGE, l46); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DLOAD, 21);
			 * mv.visitInsn(DNEG); mv.visitInsn(DCMPL); mv.visitJumpInsn(IFLT, l46); Label l47 = new Label(); mv.visitLabel(l47); mv.visitLineNumber(643, l47);
			 * mv.visitInsn(DCONST_0); mv.visitVarInsn(DSTORE, 5); Label l48 = new Label(); mv.visitLabel(l48); mv.visitLineNumber(644, l48); Label l49 = new
			 * Label(); mv.visitJumpInsn(GOTO, l49); mv.visitLabel(l46); mv.visitLineNumber(645, l46); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			 * mv.visitVarInsn(DLOAD, 5); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); Label l50 = new Label(); mv.visitJumpInsn(IFLE, l50); Label l51 = new
			 * Label(); mv.visitLabel(l51); mv.visitLineNumber(647, l51); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DSUB);
			 * mv.visitVarInsn(DSTORE, 5); Label l52 = new Label(); mv.visitLabel(l52); mv.visitLineNumber(648, l52); mv.visitJumpInsn(GOTO, l49);
			 * mv.visitLabel(l50); mv.visitLineNumber(651, l50); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 5);
			 * mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DADD); mv.visitVarInsn(DSTORE, 5); mv.visitLabel(l49); mv.visitLineNumber(639, l49);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DSTORE, 17); mv.visitLabel(l43);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 5); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); Label l53 = new Label();
			 * mv.visitJumpInsn(IFEQ, l53); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity", "worldObj",
			 * "Lnet/minecraft/world/World;"); mv.visitVarInsn(ALOAD, 0); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity",
			 * "boundingBox", "Lnet/minecraft/util/AxisAlignedBB;"); mv.visitInsn(DCONST_0); mv.visitLdcInsn(new Double("-0.9375")); mv.visitVarInsn(DLOAD, 5);
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/AxisAlignedBB", "getOffsetBoundingBox", "(DDD)Lnet/minecraft/util/AxisAlignedBB;");
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getCollidingBoundingBoxes",
			 * "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"); mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List",
			 * "isEmpty", "()Z"); mv.visitJumpInsn(IFNE, l45); Label l54 = new Label(); mv.visitLabel(l54); mv.visitLineNumber(655, l54);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitJumpInsn(GOTO, l53); Label l55 = new Label(); mv.visitLabel(l55);
			 * mv.visitLineNumber(657, l55); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DLOAD, 21);
			 * mv.visitInsn(DCMPG); Label l56 = new Label(); mv.visitJumpInsn(IFGE, l56); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DLOAD, 21);
			 * mv.visitInsn(DNEG); mv.visitInsn(DCMPL); mv.visitJumpInsn(IFLT, l56); Label l57 = new Label(); mv.visitLabel(l57); mv.visitLineNumber(659, l57);
			 * mv.visitInsn(DCONST_0); mv.visitVarInsn(DSTORE, 1); Label l58 = new Label(); mv.visitLabel(l58); mv.visitLineNumber(660, l58); Label l59 = new
			 * Label(); mv.visitJumpInsn(GOTO, l59); mv.visitLabel(l56); mv.visitLineNumber(661, l56); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			 * mv.visitVarInsn(DLOAD, 1); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); Label l60 = new Label(); mv.visitJumpInsn(IFLE, l60); Label l61 = new
			 * Label(); mv.visitLabel(l61); mv.visitLineNumber(663, l61); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DSUB);
			 * mv.visitVarInsn(DSTORE, 1); Label l62 = new Label(); mv.visitLabel(l62); mv.visitLineNumber(664, l62); mv.visitJumpInsn(GOTO, l59);
			 * mv.visitLabel(l60); mv.visitLineNumber(667, l60); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1);
			 * mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DADD); mv.visitVarInsn(DSTORE, 1); mv.visitLabel(l59); mv.visitLineNumber(670, l59);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DCMPG); Label l63 = new
			 * Label(); mv.visitJumpInsn(IFGE, l63); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DNEG); mv.visitInsn(DCMPL);
			 * mv.visitJumpInsn(IFLT, l63); Label l64 = new Label(); mv.visitLabel(l64); mv.visitLineNumber(672, l64); mv.visitInsn(DCONST_0);
			 * mv.visitVarInsn(DSTORE, 5); Label l65 = new Label(); mv.visitLabel(l65); mv.visitLineNumber(673, l65); Label l66 = new Label();
			 * mv.visitJumpInsn(GOTO, l66); mv.visitLabel(l63); mv.visitLineNumber(674, l63); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			 * mv.visitVarInsn(DLOAD, 5); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); Label l67 = new Label(); mv.visitJumpInsn(IFLE, l67); Label l68 = new
			 * Label(); mv.visitLabel(l68); mv.visitLineNumber(676, l68); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DSUB);
			 * mv.visitVarInsn(DSTORE, 5); Label l69 = new Label(); mv.visitLabel(l69); mv.visitLineNumber(677, l69); mv.visitJumpInsn(GOTO, l66);
			 * mv.visitLabel(l67); mv.visitLineNumber(680, l67); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 5);
			 * mv.visitVarInsn(DLOAD, 21); mv.visitInsn(DADD); mv.visitVarInsn(DSTORE, 5); mv.visitLabel(l66); mv.visitLineNumber(683, l66);
			 * mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1); mv.visitVarInsn(DSTORE, 13); Label l70 = new Label();
			 * mv.visitLabel(l70); mv.visitLineNumber(684, l70); mv.visitVarInsn(DLOAD, 5); mv.visitVarInsn(DSTORE, 17); mv.visitLabel(l53);
			 * mv.visitLineNumber(655, l53); mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null); mv.visitVarInsn(DLOAD, 1); mv.visitInsn(DCONST_0);
			 * mv.visitInsn(DCMPL); mv.visitJumpInsn(IFEQ, l31); mv.visitVarInsn(DLOAD, 5); mv.visitInsn(DCONST_0); mv.visitInsn(DCMPL); mv.visitJumpInsn(IFEQ,
			 * l31); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity", "worldObj", "Lnet/minecraft/world/World;");
			 * mv.visitVarInsn(ALOAD, 0); mv.visitVarInsn(ALOAD, 0); mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/Entity", "boundingBox",
			 * "Lnet/minecraft/util/AxisAlignedBB;"); mv.visitVarInsn(DLOAD, 1); mv.visitLdcInsn(new Double("-0.9375")); mv.visitVarInsn(DLOAD, 5);
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/AxisAlignedBB", "getOffsetBoundingBox", "(DDD)Lnet/minecraft/util/AxisAlignedBB;");
			 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getCollidingBoundingBoxes",
			 * "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"); mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List",
			 * "isEmpty", "()Z"); mv.visitJumpInsn(IFNE, l55); mv.visitLabel(l31); mv.visitLineNumber(688, l31); mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
			 * mv.visitVarInsn(ALOAD, 0);
			 */

			m = new MethodNode(ACC_PUBLIC, "cofh_collideCheck", "()Z", null, null);
			cn.methods.add(m);
			m.instructions.insert(new InsnNode(IRETURN));
			m.instructions.insert(new MethodInsnNode(INVOKEVIRTUAL, name, names[2], "()Z", false));
			m.instructions.insert(new VarInsnNode(ALOAD, 0));

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private static byte[] alterHooksCore(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_70104_M" };
		} else {
			names = new String[] { "canBePushed" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		MethodNode m = null;
		for (MethodNode n : cn.methods) {
			if ("getEntityCollisionBoxes".equals(n.name)) {
				m = n;
				break;
			}
		}

		for (int i = 0, e = m.instructions.size(); i < e; ++i) {
			AbstractInsnNode n = m.instructions.get(i);
			if (n.getOpcode() == INVOKEVIRTUAL) {
				MethodInsnNode mn = (MethodInsnNode) n;
				if (names[0].equals(mn.name)) {
					mn.name = "cofh_collideCheck";
				}
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		bytes = cw.toByteArray();

		return bytes;
	}

	private static byte[] alterKeyEntry(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_148279_a", "func_151463_i" };
		} else {
			names = new String[] { "drawEntry", "getKeyCode" };
		}

		name = name.replace('.', '/');

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		MethodNode m = null;
		for (MethodNode n : cn.methods) {
			if (names[0].equals(n.name)) {
				m = n;
				break;
			}
		}

		if (m != null) {
			for (int i = 0, e = m.instructions.size(); i < e; ++i) {
				AbstractInsnNode n = m.instructions.get(i);
				if (n.getOpcode() == INVOKEVIRTUAL && n.getNext().getOpcode() == IFEQ) {
					MethodInsnNode mn = (MethodInsnNode) n;
					if (names[1].equals(mn.name)) {
						mn.name = "cofh_conflictCode";
					}
				}
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private static byte[] alterKeyBinding(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_148279_a", "func_151463_i" };
		} else {
			names = new String[] { "drawEntry", "getKeyCode" };
		}

		name = name.replace('.', '/');
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cr.accept(cw, 0);
		cw.newMethod(name, "cofh_conflictCode", "()I", true);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "cofh_conflictCode", "()I", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/client/settings/KeyBinding", names[1], "()I", false);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
		cw.visitEnd();
		bytes = cw.toByteArray();

		return bytes;
	}

	private static byte[] alterRenderBlocks(String name, byte[] bytes, ClassReader cr) {

		// .renderBlockStainedGlassPane(Block, int, int, int)
		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_147733_k", "func_150098_a", "func_147439_a" };
		} else {
			names = new String[] { "renderBlockStainedGlassPane", "canPaneConnectToBlock", "getBlock" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/block/Block;III)Z";
		final String Rsig = "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z";
		final String Ssig = "(Lnet/minecraft/block/Block;)Z";
		final String Csig = "(III)Lnet/minecraft/block/Block;";
		final String cc = "net/minecraft/block/BlockPane";
		final String fd = "net/minecraftforge/common/util/ForgeDirection";

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name) && sig.equals(n.desc)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			m.localVariables = null;

			final String[] dirs = { "NORTH", "NORTH", "SOUTH", "SOUTH", "WEST", "WEST", "EAST", "EAST" };
			int di = 0;

			for (int i = 0, e = m.instructions.size(); i < e; ++i) {
				AbstractInsnNode n = m.instructions.get(i);
				if (n.getType() == AbstractInsnNode.METHOD_INSN) {
					MethodInsnNode mn = (MethodInsnNode) n;
					if (n.getOpcode() == INVOKEINTERFACE && n.getNext().getOpcode() == INVOKEVIRTUAL) {
						if (names[2].equals(mn.name)) {
							if (Csig.equals(mn.desc) && Ssig.equals(((MethodInsnNode) mn.getNext()).desc)) {
								m.instructions.insertBefore(n, new FieldInsnNode(GETSTATIC, fd, dirs[di++], 'L' + fd + ';'));
								m.instructions.insertBefore(n, new MethodInsnNode(INVOKEVIRTUAL, cc, "canPaneConnectTo", Rsig, false));
								m.instructions.remove(n.getNext());
								m.instructions.remove(n);
							}
						}
					}
				}
			}

			if (di == 0) {
				break l;
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private static byte[] alterBlockPane(String name, byte[] bytes, ClassReader cr) {

		String names = "canPaneConnectTo"; // forge added

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z";

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names.equals(n.name) && sig.equals(n.desc)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			m.instructions.clear();
			m.instructions.add(new VarInsnNode(ALOAD, 1));
			m.instructions.add(new VarInsnNode(ILOAD, 2));
			m.instructions.add(new VarInsnNode(ILOAD, 3));
			m.instructions.add(new VarInsnNode(ILOAD, 4));
			m.instructions.add(new VarInsnNode(ALOAD, 5));
			m.instructions.add(new MethodInsnNode(INVOKESTATIC, "cofh/asmhooks/HooksCore", "paneConnectsTo", sig, false));
			m.instructions.add(new InsnNode(IRETURN));

			m.localVariables = null;

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterMinecraft(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_71407_l", "func_110550_d" };
		} else {
			names = new String[] { "runTick", "tick" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		String mOwner = "net/minecraft/client/renderer/texture/TextureManager";

		l: {
			boolean updated = false;
			mc: for (MethodNode m : cn.methods) {
				String mName = m.name;
				if (names[0].equals(mName) && "()V".equals(m.desc)) {
					updated = true;
					for (int i = 0, e = m.instructions.size(); i < e; ++i) {
						AbstractInsnNode n = m.instructions.get(i);
						if (n.getOpcode() == INVOKEVIRTUAL) {
							MethodInsnNode mn = (MethodInsnNode) n;
							if (mOwner.equals(mn.owner) && names[1].equals(mn.name) && "()V".equals(mn.desc)) {
								m.instructions.set(mn, new MethodInsnNode(INVOKESTATIC, "cofh/asmhooks/HooksCore", "tickTextures",
										"(Lnet/minecraft/client/renderer/texture/ITickable;)V", false));
								break mc;
							}
						}
					}
				}
			}

			if (!updated) {
				break l;
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterChunk(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_150803_c", "field_76650_s" };
		} else {
			names = new String[] { "recheckGaps", "isGapLightingUpdated" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		l: {
			boolean updated = false;
			for (MethodNode m : cn.methods) {
				String mName = m.name;
				if (names[0].equals(mName) && "(Z)V".equals(m.desc)) {
					updated = true;
					for (int i = 0, e = m.instructions.size(); i < e; ++i) {
						AbstractInsnNode n = m.instructions.get(i);
						if (n.getOpcode() == RETURN) {
							m.instructions.insertBefore(n, new VarInsnNode(ALOAD, 0));
							m.instructions.insertBefore(n, new InsnNode(ICONST_0));
							m.instructions.insertBefore(n, new FieldInsnNode(PUTFIELD, name, names[1], "Z"));
							break;
						}
					}
				}
			}

			if (!updated) {
				break l;
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterLongHashMap(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_76155_g", "func_76160_c", "func_76161_b" };
		} else {
			names = new String[] { "getHashedKey", "getEntry", "containsItem" };
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		l: {
			boolean updated = false;
			MethodNode getEntry = null, containsItem = null;
			for (MethodNode m : cn.methods) {
				String mName = m.name;
				if (names[0].equals(mName) && "(J)I".equals(m.desc)) {
					updated = true;
					for (int i = 0, e = m.instructions.size(); i < e; ++i) {
						AbstractInsnNode n = m.instructions.get(i);
						if (n.getOpcode() == LXOR) {
							m.instructions.insertBefore(n, new LdcInsnNode(new Long(13L)));
							m.instructions.insertBefore(n, new InsnNode(LMUL));
							break;
						}
					}
					if (containsItem != null) {
						break;
					}
				} else if (names[2].equals(mName) && "(J)Z".equals(m.desc)) {
					containsItem = m;
					if (updated) {
						break;
					}
				}
			}

			mc: if (containsItem != null) {
				// { cloning methods to get a different set of instructions to avoid erasing getEntry
				ClassNode clone = new ClassNode(ASM5);
				cr.accept(clone, ClassReader.EXPAND_FRAMES);
				String sig = "(J)Lnet/minecraft/util/LongHashMap$Entry;";
				for (MethodNode m : clone.methods) {
					String mName = m.name;
					if (names[1].equals(mName) && sig.equals(m.desc)) {
						getEntry = m;
						break;
					}
				}
				// }
				if (getEntry == null) {
					break mc;
				}
				updated = true;
				containsItem.instructions.clear();
				containsItem.instructions.add(getEntry.instructions);
				/**
				 * this looks counter intuitive (replacing getEntry != null check with the full method) but due to how the JVM handles inlining, this needs to
				 * be done manually
				 */
				for (AbstractInsnNode n = containsItem.instructions.get(0); n != null; n = n.getNext()) {
					if (n.getOpcode() == ARETURN) {
						AbstractInsnNode n2 = n.getPrevious();
						if (n2.getOpcode() == ACONST_NULL) {
							containsItem.instructions.set(n2, new InsnNode(ICONST_0));
						} else {
							containsItem.instructions.set(n2, new InsnNode(ICONST_1));
						}
						containsItem.instructions.set(n, n = new InsnNode(IRETURN));
					}
				}
			}

			if (!updated) {
				break l;
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterController(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_85182_a", "field_85183_f" };
		} else {
			names = new String[] { "sameToolAndBlock", "currentItemHittingBlock" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		final String sig = "(III)Z";
		final String itemstack = "net/minecraft/item/ItemStack";

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(n.name) && sig.equals(n.desc)) {
					m = n;
					break;
				}
			}

			if (m == null) {
				break l;
			}

			for (int i = 0, e = m.instructions.size(); i < e; i++) {
				AbstractInsnNode n = m.instructions.get(i);
				if (n.getOpcode() == INVOKEVIRTUAL) {
					MethodInsnNode mn = (MethodInsnNode) n;
					if (itemstack.equals(mn.owner)) {
						LabelNode jmp = null, jmp2 = null;
						s: for (int j = i; j < e; ++j) {
							n = m.instructions.get(j);
							if (n.getOpcode() == ICONST_1) {
								for (int k = j; k > i; --k) {
									n = m.instructions.get(k);
									if (n.getType() == AbstractInsnNode.LABEL) {
										jmp = (LabelNode) n;
										break;
									}
								}
								for (int k = j; k < e; ++k) {
									n = m.instructions.get(k);
									if (n.getType() == AbstractInsnNode.LABEL) {
										jmp2 = (LabelNode) n;
										break s;
									}
								}
							}
						}
						if (jmp == null || jmp2 == null) {
							break l;
						}

						// presently on stack: player.getHeldItem()
						m.instructions.insertBefore(mn, new VarInsnNode(ALOAD, 0));
						m.instructions.insertBefore(mn, new FieldInsnNode(GETFIELD, name, names[1], 'L' + itemstack + ';'));
						final String clazz = "cofh/asmhooks/HooksCore";
						final String method = "areItemsEqualHook";
						final String sign = "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z";
						m.instructions.insertBefore(mn, new MethodInsnNode(INVOKESTATIC, clazz, method, sign, false));
						m.instructions.insertBefore(mn, new JumpInsnNode(IFEQ, jmp2));
						m.instructions.insertBefore(mn, new JumpInsnNode(GOTO, jmp));
						break;
					}
				}
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterTileEntity(String name, byte[] bytes, ClassReader cr) {

		name = name.replace('.', '/');
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cr.accept(cw, 0);
		cw.newMethod(name, "cofh_validate", "()V", true);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "cofh_validate", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 1);
		mv.visitEnd();
		cw.visitEnd();

		cw.newMethod(name, "cofh_invalidate", "()V", true);
		mv = cw.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "cofh_invalidate", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 1);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	private static byte[] writeWorld(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "field_73019_z", "field_72986_A", "field_73011_w", "field_72984_F", "func_147448_a", "func_147455_a", "func_72939_s",
					"func_145830_o", "field_147481_N", "func_147457_a" };
		} else {
			names = new String[] { "saveHandler", "worldInfo", "provider", "theProfiler", "func_147448_a", "setTileEntity", "updateEntities", "hasWorldObj",
					"field_147481_N", "func_147457_a" };
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V";

		MethodNode addTileEntity = null, addTileEntities = null, setTileEntity = null, updateEntities = null, unloadTile = null;
		boolean found = false;
		for (MethodNode m : cn.methods) {
			if ("<init>".equals(m.name)) {
				if (sig.equals(m.desc)) {
					found = true;
				}
				LabelNode a = new LabelNode(new Label());
				AbstractInsnNode n = m.instructions.getFirst();
				while (n.getOpcode() != INVOKESPECIAL || !((MethodInsnNode) n).name.equals("<init>")) {
					n = n.getNext();
				}
				m.instructions.insert(n, n = a);
				m.instructions.insert(n, n = new LineNumberNode(-15000, a));
				m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new TypeInsnNode(NEW, "cofh/lib/util/IdentityLinkedHashList"));
				m.instructions.insert(n, n = new InsnNode(DUP));
				m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "cofh/lib/util/IdentityLinkedHashList", "<init>", "()V", false));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
			} else if ("addTileEntity".equals(m.name) && "(Lnet/minecraft/tileentity/TileEntity;)V".equals(m.desc)) {
				addTileEntity = m;
			} else if (names[4].equals(m.name) && "(Ljava/util/Collection;)V".equals(m.desc)) {
				addTileEntities = m;
			} else if (names[5].equals(m.name) && "(IIILnet/minecraft/tileentity/TileEntity;)V".equals(m.desc)) {
				setTileEntity = m;
			} else if (names[6].equals(m.name) && "()V".equals(m.desc)) {
				updateEntities = m;
			} else if (names[9].equals(m.name) && "(Lnet/minecraft/tileentity/TileEntity;)V".equals(m.desc)) {
				unloadTile = m;
			}
		}

		cn.fields.add(new FieldNode(ACC_PRIVATE | ACC_SYNTHETIC, "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;", null, null));

		if (unloadTile != null) {

			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n;
			unloadTile.instructions.insert(n = a);
			unloadTile.instructions.insert(n, n = new LineNumberNode(-15005, a));
			unloadTile.instructions.insert(n, n = new VarInsnNode(ALOAD, 1));
			unloadTile.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntity", "cofh_invalidate", "()V", false));
		}

		if (addTileEntity != null) {

			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n;
			addTileEntity.instructions.insert(n = a);
			addTileEntity.instructions.insert(n, n = new LineNumberNode(-15001, a));
			addTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			addTileEntity.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles",
					"Lcofh/lib/util/LinkedHashList;"));
			addTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 1));
			addTileEntity.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "push", "(Ljava/lang/Object;)Z", false));
			addTileEntity.instructions.insert(n, n = new InsnNode(POP));
		}

		if (setTileEntity != null) {

			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n = setTileEntity.instructions.getLast();
			while (n.getOpcode() != RETURN) {
				n = n.getPrevious();
			}
			n = n.getPrevious();
			setTileEntity.instructions.insert(n = a);
			setTileEntity.instructions.insert(n, n = new LineNumberNode(-15002, a));
			setTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			setTileEntity.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles",
					"Lcofh/lib/util/LinkedHashList;"));
			setTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 4));
			setTileEntity.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "push", "(Ljava/lang/Object;)Z", false));
			setTileEntity.instructions.insert(n, n = new InsnNode(POP));
		}

		if (addTileEntities != null) {
			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n = addTileEntities.instructions.getFirst();
			for (;;) {
				while (n.getOpcode() != CHECKCAST) {
					n = n.getNext();
				}
				if ((((TypeInsnNode) n).desc).equals("net/minecraft/tileentity/TileEntity")) {
					break;
				}
			}
			addTileEntities.instructions.insert(n, n = a);
			addTileEntities.instructions.insert(n, n = new LineNumberNode(-15003, a));
			addTileEntities.instructions.insert(n, n = new InsnNode(DUP));
			addTileEntities.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			addTileEntities.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles",
					"Lcofh/lib/util/LinkedHashList;"));
			addTileEntities.instructions.insert(n, n = new InsnNode(SWAP));
			addTileEntities.instructions.insert(n,
					n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "push", "(Ljava/lang/Object;)Z", false));
			addTileEntities.instructions.insert(n, n = new InsnNode(POP));
		}

		if (updateEntities != null) {
			AbstractInsnNode n = updateEntities.instructions.getFirst();
			while (n.getOpcode() != INVOKEVIRTUAL || !"onChunkUnload".equals(((MethodInsnNode) n).name) || !"()V".equals(((MethodInsnNode) n).desc)) {
				n = n.getNext();
			}
			while (n.getOpcode() != PUTFIELD || !names[8].equals(((FieldInsnNode) n).name)) {
				n = n.getPrevious();
			}
			n = n.getNext();
			LabelNode lStart = new LabelNode(new Label());
			LabelNode lCond = new LabelNode(new Label());
			LabelNode lGuard = new LabelNode(new Label());
			LabelNode a = new LabelNode(new Label());
			updateEntities.instructions.insertBefore(n, n = a);
			updateEntities.instructions.insert(n, n = new LineNumberNode(-15004, a));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(GOTO, lCond));
			updateEntities.instructions.insert(n, n = lStart);
			updateEntities.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));

			updateEntities.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			updateEntities.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles",
					"Lcofh/lib/util/LinkedHashList;"));
			updateEntities.instructions
					.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "shift", "()Ljava/lang/Object;", false));
			updateEntities.instructions.insert(n, n = new TypeInsnNode(CHECKCAST, "net/minecraft/tileentity/TileEntity"));
			updateEntities.instructions.insert(n, n = new InsnNode(DUP));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(IFNULL, lGuard));
			updateEntities.instructions.insert(n, n = new InsnNode(DUP));
			updateEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntity", names[7], "()Z", false));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(IFEQ, lGuard));
			updateEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntity", "cofh_validate", "()V", false));
			updateEntities.instructions.insert(n, n = new InsnNode(ACONST_NULL));
			updateEntities.instructions.insert(n, n = lGuard);
			updateEntities.instructions.insert(n, n = new FrameNode(F_SAME1, 0, null, 0, new Object[] { "Lnet/minecraft/tileentity/TileEntity;" }));
			updateEntities.instructions.insert(n, n = new InsnNode(POP));
			updateEntities.instructions.insert(n, n = lCond);
			updateEntities.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
			updateEntities.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			updateEntities.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles",
					"Lcofh/lib/util/LinkedHashList;"));
			updateEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "size", "()I", false));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(IFNE, lStart));
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		if (!found) {
			/*
			 * new World constructor World(ISaveHandler saveHandler, String worldName, WorldProvider provider, WorldSettings worldSettings, Profiler
			 * theProfiler)
			 */
			cw.newMethod(name, "<init>", sig, true);
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", sig, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, name, names[0], "Lnet/minecraft/world/storage/ISaveHandler;");
			mv.visitTypeInsn(NEW, "net/minecraft/world/storage/WorldInfo");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESPECIAL, "net/minecraft/world/storage/WorldInfo", "<init>", "(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V",
					false);
			mv.visitFieldInsn(PUTFIELD, name, names[1], "Lnet/minecraft/world/storage/WorldInfo;");
			mv.visitVarInsn(ALOAD, 3);
			mv.visitFieldInsn(PUTFIELD, name, names[2], "Lnet/minecraft/world/WorldProvider;");
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(PUTFIELD, name, names[3], "Lnet/minecraft/profiler/Profiler;");
			mv.visitInsn(RETURN);
			mv.visitMaxs(11, 10);
			mv.visitEnd();
			cw.visitEnd();
		}
		bytes = cw.toByteArray();
		return bytes;
	}

	private static byte[] writeWorldServer(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "field_73061_a", "field_73062_L", "field_73063_M", "field_85177_Q" };
		} else {
			names = new String[] { "mcServer", "theEntityTracker", "thePlayerManager", "worldTeleporter" };
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		final String sig = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V";

		l: {
			for (MethodNode m : cn.methods) {
				if ("<init>".equals(m.name) && sig.equals(m.desc)) {
					break l;
				}
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			cn.accept(cw);
			/*
			 * new WorldServer constructor WorldServer(MinecraftServer minecraftServer, ISaveHandler saveHandler, String worldName, WorldProvider provider,
			 * WorldSettings worldSettings, Profiler theProfiler)
			 */
			cw.newMethod(name, "<init>", sig, true);
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", sig, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitVarInsn(ALOAD, 6);
			// [World] super(saveHandler, worldName, provider, worldSettings, theProfiler);
			mv.visitMethodInsn(
					INVOKESPECIAL,
					"net/minecraft/world/World",
					"<init>",
					"(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V",
					false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, name, names[0], "Lnet/minecraft/server/MinecraftServer;");
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTFIELD, name, names[1], "Lnet/minecraft/entity/EntityTracker;");
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTFIELD, name, names[2], "Lnet/minecraft/server/management/PlayerManager;");
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTFIELD, name, names[3], "Lnet/minecraft/world/Teleporter;");
			mv.visitInsn(RETURN);
			mv.visitMaxs(11, 10);
			mv.visitEnd();
			cw.visitEnd();
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] writeWorldProxy(String name, byte[] bytes, ClassReader cr) {

		Method[] world = null;
		try {
			world = net.minecraft.world.World.class.getDeclaredMethods();
		} catch (Throwable e) {
			Throwables.propagate(e);
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.SKIP_FRAMES);

		for (Method m : world) {
			if (!Modifier.isStatic(m.getModifiers())) {
				String desc = Type.getMethodDescriptor(m);
				{
					Iterator<MethodNode> i = cn.methods.iterator();
					while (i.hasNext()) {
						MethodNode m2 = i.next();
						if (m2.name.equals(m.getName()) && m2.desc.equals(desc)) {
							i.remove();
						}
					}
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.getName(), desc, null, getExceptions(m));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "skyboy/core/world/WorldProxy", "proxiedWorld", "Lnet/minecraft/world/World;");
				Type[] types = Type.getArgumentTypes(m);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", m.getName(), desc, false);
				mv.visitInsn(Type.getReturnType(m).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private static byte[] writeWorldServerProxy(String name, byte[] bytes, ClassReader cr) {

		Method[] worldServer = null;
		try {
			worldServer = net.minecraft.world.WorldServer.class.getDeclaredMethods();
		} catch (Throwable e) {
			Throwables.propagate(e);
		}
		Method[] world = null;
		try {
			world = net.minecraft.world.World.class.getDeclaredMethods();
		} catch (Throwable e) {
			Throwables.propagate(e);
		}

		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, ClassReader.SKIP_FRAMES);

		cn.superName = "net/minecraft/world/WorldServer";
		for (MethodNode m : cn.methods) {
			if ("<init>".equals(m.name)) {
				InsnList l = m.instructions;
				for (int i = 0, e = l.size(); i < e; i++) {
					AbstractInsnNode n = l.get(i);
					if (n instanceof MethodInsnNode) {
						MethodInsnNode mn = (MethodInsnNode) n;
						if (mn.getOpcode() == INVOKESPECIAL) {
							mn.owner = cn.superName;
							break;
						}
					}
				}
			}
		}

		for (Method m : world) {
			if (!Modifier.isStatic(m.getModifiers())) {
				String desc = Type.getMethodDescriptor(m);
				{
					Iterator<MethodNode> i = cn.methods.iterator();
					while (i.hasNext()) {
						MethodNode m2 = i.next();
						if (m2.name.equals(m.getName()) && m2.desc.equals(desc)) {
							i.remove();
						}
					}
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.getName(), desc, null, getExceptions(m));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "skyboy/core/world/WorldServerProxy", "proxiedWorld", "Lnet/minecraft/world/WorldServer;");
				Type[] types = Type.getArgumentTypes(m);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", m.getName(), desc, false);
				mv.visitInsn(Type.getReturnType(m).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		for (Method m : worldServer) {
			if (!Modifier.isStatic(m.getModifiers())) {
				String desc = Type.getMethodDescriptor(m);
				{
					Iterator<MethodNode> i = cn.methods.iterator();
					while (i.hasNext()) {
						MethodNode m2 = i.next();
						if (m2.name.equals(m.getName()) && m2.desc.equals(desc)) {
							i.remove();
						}
					}
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.getName(), desc, null, getExceptions(m));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "skyboy/core/world/WorldServerProxy", "proxiedWorld", "Lnet/minecraft/world/WorldServer;");
				Type[] types = Type.getArgumentTypes(m);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/WorldServer", m.getName(), desc, false);
				mv.visitInsn(Type.getReturnType(m).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();
		return bytes;
	}

	// }

	private static int getAccess(Method m) {

		int r = m.getModifiers();
		r &= ~(ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED | ACC_FINAL | ACC_BRIDGE | ACC_ABSTRACT);
		r |= ACC_PUBLIC | ACC_SYNTHETIC;
		return r;
	}

	private static String[] getExceptions(Method m) {

		Class<?>[] d = m.getExceptionTypes();
		if (d == null) {
			return null;
		}
		String[] r = new String[d.length];
		for (int i = 0; i < d.length; ++i) {
			r[i] = Type.getInternalName(d[i]);
		}
		return r;
	}

	// { Annotation parsing
	static boolean implement(ClassNode cn) {

		if (cn.visibleAnnotations == null) {
			return false;
		}
		boolean interfaces = false;
		for (AnnotationNode n : cn.visibleAnnotations) {
			AnnotationInfo node = parseAnnotation(n, implementableDesc);
			if (node != null && (node.side == "NONE" || side == node.side)) {
				String[] value = node.values;
				for (int j = 0, l = value.length; j < l; ++j) {
					String clazz = value[j].trim();
					String cz = clazz.replace('.', '/');
					if (!cn.interfaces.contains(cz)) {
						try {
							if (!workingPath.contains(clazz)) {
								Class.forName(clazz, false, ASMCore.class.getClassLoader());
							}
							cn.interfaces.add(cz);
							interfaces = true;
						} catch (Throwable $) {
						}
					}
				}
			}
		}
		return interfaces;
	}

	static boolean substitute(ClassNode cn) {

		boolean altered = false;
		if (cn.methods != null) {
			Iterator<MethodNode> iter = cn.methods.iterator();
			while (iter.hasNext()) {
				MethodNode mn = iter.next();
				if (mn.visibleAnnotations != null) {
					for (AnnotationNode node : mn.visibleAnnotations) {
						AnnotationInfo info = parseAnnotation(node, substitutableDesc);
						if (checkSub(info, mn)) {
							altered = true;

							mn.instructions.clear();
							mn.localVariables = null;
							l: {
								for (MethodNode m : cn.methods) {
									if (info.method.equals(m.name) && mn.desc.equals(m.desc)) {
										mn.instructions.add(m.instructions);
										break l;
									}
								}
								Type rType = Type.getReturnType(mn.desc);
								switch (rType.getSort()) {
								case Type.METHOD:
								case Type.ARRAY:
								case Type.OBJECT:
									mn.instructions.add(new InsnNode(ACONST_NULL));
									break;
								case Type.FLOAT:
									mn.instructions.add(new InsnNode(FCONST_0));
									break;
								case Type.DOUBLE:
									mn.instructions.add(new InsnNode(DCONST_0));
									break;
								case Type.LONG:
									mn.instructions.add(new InsnNode(LCONST_0));
									break;
								default:
									mn.instructions.add(new InsnNode(ICONST_0));
									switch (rType.getSort()) {
									case Type.SHORT:
										mn.instructions.add(new InsnNode(I2S));
										break;
									case Type.CHAR:
										mn.instructions.add(new InsnNode(I2C));
										break;
									case Type.BYTE:
										mn.instructions.add(new InsnNode(I2B));
										break;
									}
									break;
								case Type.VOID:
									break;
								}
								mn.instructions.add(new InsnNode(rType.getOpcode(IRETURN)));
							}
						}
					}
				}
			}
		}
		return altered;
	}

	static boolean checkSub(AnnotationInfo node, MethodNode method) {

		if (node != null) {
			boolean needsReplaced = node.side == side;
			if (!needsReplaced) {
				String[] value = node.values;
				for (int j = 0, l = value.length; j < l; ++j) {
					needsReplaced = parseValue(value[j]);
					if (needsReplaced) {
						break;
					}
				}
			}
			if (needsReplaced) {
				if (node.method.equals(method.name)) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	static boolean strip(ClassNode cn) {

		boolean altered = false;
		if (cn.visibleAnnotations != null) {
			for (AnnotationNode n : cn.visibleAnnotations) {
				AnnotationInfo node = parseAnnotation(n, strippableDesc);
				if (node != null) {
					String[] value = node.values;
					boolean wrongSide = side == node.side;
					for (int j = 0, l = value.length; j < l; ++j) {
						String clazz = value[j];
						String cz = clazz.replace('.', '/');
						if (cn.interfaces.contains(cz)) {
							boolean remove = true;
							try {
								if (!wrongSide && !workingPath.contains(clazz)) {
									Class.forName(clazz, false, ASMCore.class.getClassLoader());
									remove = false;
								}
							} catch (Throwable $) {
							}
							if (remove) {
								cn.interfaces.remove(cz);
								altered = true;
							}
						}
					}
				}
			}
		}
		if (cn.methods != null) {
			Iterator<MethodNode> iter = cn.methods.iterator();
			while (iter.hasNext()) {
				MethodNode mn = iter.next();
				if (mn.visibleAnnotations != null) {
					for (AnnotationNode node : mn.visibleAnnotations) {
						if (checkRemove(parseAnnotation(node, strippableDesc), iter)) {
							altered = true;
							break;
						}
					}
				}
			}
		}
		if (cn.fields != null) {
			Iterator<FieldNode> iter = cn.fields.iterator();
			while (iter.hasNext()) {
				FieldNode fn = iter.next();
				if (fn.visibleAnnotations != null) {
					for (AnnotationNode node : fn.visibleAnnotations) {
						if (checkRemove(parseAnnotation(node, strippableDesc), iter)) {
							altered = true;
							break;
						}
					}
				}
			}
		}
		return altered;
	}

	static boolean checkRemove(AnnotationInfo node, Iterator<? extends Object> iter) {

		if (node != null) {
			boolean needsRemoved = node.side == side;
			if (!needsRemoved) {
				String[] value = node.values;
				for (int j = 0, l = value.length; j < l; ++j) {
					needsRemoved = parseValue(value[j]);
					if (needsRemoved) {
						break;
					}
				}
			}
			if (needsRemoved) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	static boolean parseValue(String clazz) {

		boolean ret = false;
		String mod = clazz.length() > 4 ? clazz.substring(4) : clazz;
		if (clazz.startsWith("mod:")) {
			int i = mod.indexOf('@');
			if (i > 0) {
				clazz = mod.substring(i + 1);
				mod = mod.substring(0, i);
			}
			ret = !Loader.isModLoaded(mod);
			if (!ret && i > 0) {
				ModContainer modc = getLoadedMods().get(mod);
				try {
					if (Boolean.parseBoolean(modc.getCustomModProperties().get("cofhversion"))) {
						ret = !ModRange.createFromVersionSpec(mod, clazz).containsVersion(new ModVersion(mod, modc.getVersion()));
					} else {
						ret = !VersionRange.createFromVersionSpec(clazz).containsVersion(modc.getProcessedVersion());
					}
				} catch (InvalidVersionSpecificationException e) {
					ret = true;
				}
			}
		} else if (clazz.startsWith("api:")) {
			int i = mod.indexOf('@');
			if (i > 0) {
				clazz = mod.substring(i + 1);
				mod = mod.substring(0, i);
			}
			ret = !ModAPIManager.INSTANCE.hasAPI(mod);
			if (!ret && i > 0) {
				ModContainer modc = getLoadedAPIs().get(mod);
				try {
					ret = !VersionRange.createFromVersionSpec(clazz).containsVersion(modc.getProcessedVersion());
				} catch (InvalidVersionSpecificationException e) {
					ret = true;
				}
			}
		} else {
			try {
				if (!workingPath.contains(clazz)) {
					Class.forName(clazz, false, ASMCore.class.getClassLoader());
				}
			} catch (Throwable $) {
				ret = true;
			}
		}
		return ret;
	}

	// }

	private static Map<String, ModContainer> mods;

	static Map<String, ModContainer> getLoadedMods() {

		if (mods == null) {
			mods = new HashMap<String, ModContainer>();
			for (ModContainer m : Loader.instance().getModList()) {
				mods.put(m.getModId(), m);
			}
		}
		return mods;
	}

	private static Map<String, ModContainer> apis;

	static Map<String, ModContainer> getLoadedAPIs() {

		if (apis == null) {
			apis = new HashMap<String, ModContainer>();
			for (ModContainer m : ModAPIManager.INSTANCE.getAPIList()) {
				apis.put(m.getModId(), m);
			}
		}
		return apis;
	}

	static AnnotationInfo parseAnnotation(AnnotationNode node, String desc) {

		AnnotationInfo info = null;
		if (node.desc.equals(desc)) {
			info = new AnnotationInfo();
			if (node.values != null) {
				List<Object> values = node.values;
				for (int i = 0, e = values.size(); i < e;) {
					Object k = values.get(i++);
					Object v = values.get(i++);
					if ("value".equals(k)) {
						if (!(v instanceof List && ((List<?>) v).size() > 0 && ((List<?>) v).get(0) instanceof String)) {
							continue;
						}
						info.values = ((List<?>) v).toArray(emptyList);
					} else if ("side".equals(k) && v instanceof String) {
						info.side = ((String) v).toUpperCase().intern();
					} else if ("method".equals(k) && v instanceof String) {
						info.method = (String) v;
					}
				}
			}
		}
		return info;
	}

	static void scrapeData(ASMDataTable table) {

		log.debug("Scraping data");

		side = FMLCommonHandler.instance().getSide().toString().toUpperCase(Locale.US).intern();

		for (ASMData data : table.getAll(Implementable.class.getName())) {
			String name = data.getClassName();
			parsables.add(name);
			parsables.add(name + "$class");
			implementables.add(name);
			implementables.add(name + "$class");
		}
		for (ASMData data : table.getAll(Strippable.class.getName())) {
			String name = data.getClassName();
			parsables.add(name);
			parsables.add(name + "$class");
			strippables.add(name);
			strippables.add(name + "$class");
		}
		for (ASMData data : table.getAll(Substitutable.class.getName())) {
			String name = data.getClassName();
			parsables.add(name);
			parsables.add(name + "$class");
			substitutables.add(name);
			substitutables.add(name + "$class");
		}
		log.debug("Found " + implementables.size()/2 + " @Implementable; " + strippables.size()/2 + " @Strippable; " + substitutables.size()/2 + " @Substitutable");
	}

}
