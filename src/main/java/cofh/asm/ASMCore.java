package cofh.asm;

import static org.objectweb.asm.Opcodes.*;

import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Strippable;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
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

	static TObjectByteHashMap<String> hashes = new TObjectByteHashMap<String>(20, 1, (byte) 0);
	static THashSet<String> parsables, implementables, strippables;
	static final String implementableDesc, strippableDesc;
	static String side;

	static void init() {

	}

	static {

		implementableDesc = Type.getDescriptor(Implementable.class);
		strippableDesc = Type.getDescriptor(Strippable.class);

		parsables = new THashSet<String>(10);
		implementables = new THashSet<String>(10);
		strippables = new THashSet<String>(10);

		hashes.put("net.minecraft.world.WorldServer", (byte) 1);
		hashes.put("net.minecraft.world.World", (byte) 2);
		hashes.put("skyboy.core.world.WorldProxy", (byte) 3);
		hashes.put("skyboy.core.world.WorldServerProxy", (byte) 4);
		hashes.put("net.minecraft.block.BlockPane", (byte) 5);
		hashes.put("net.minecraft.inventory.Container", (byte) 6);
		hashes.put("net.minecraft.client.multiplayer.PlayerControllerMP", (byte) 7);
		hashes.put("net.minecraft.util.LongHashMap", (byte) 8);
		if (Boolean.parseBoolean(System.getProperty("cofh.lightedit", "true"))) {
			hashes.put("net.minecraft.world.chunk.Chunk", (byte) 9);
		}
		hashes.put("net.minecraft.client.Minecraft", (byte) 10);
		hashes.put("net.minecraft.client.renderer.RenderBlocks", (byte) 11);
		hashes.put("net.minecraft.tileentity.TileEntity", (byte) 12);
	}

	static final ArrayList<String> workingPath = new ArrayList<String>();
	private static final String[] emptyList = {};

	static class AnnotationInfo {

		public String side;
		public String[] values = emptyList;
	}

	static byte[] parse(String name, String transformedName, byte[] bytes) {

		workingPath.add(transformedName);

		if (implementables.contains(name)) {
			log.info("Adding runtime interfaces to " + transformedName);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (implement(cn)) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cn.accept(cw);
				bytes = cw.toByteArray();
			} else {
				log.debug("Nothing implemented on " + transformedName);
			}
		}

		if (strippables.contains(name)) {
			log.info("Stripping methods and fields from " + transformedName);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (strip(cn)) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				cn.accept(cw);
				bytes = cw.toByteArray();
			} else {
				log.debug("Nothing stripped from " + transformedName);
			}
		}

		workingPath.remove(workingPath.size() - 1);
		return bytes;
	}

	static synchronized void HACK(String name, byte[] bytes) {

		synchronized (workingPath) {
			workingPath.add(name);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (cn.innerClasses != null) {
				for (InnerClassNode node : cn.innerClasses) {
					log.debug("\tInner class: " + node.name);
					if (!workingPath.contains(node.name)) {
						try {
							Class.forName(node.name, false, ASMCore.class.getClassLoader());
						} catch (Throwable _) {
						}
					}
				}
			}
			workingPath.remove(workingPath.size() - 1);
		}
	}

	static byte[] transform(int index, String name, String transformedName, byte[] bytes) {

		ClassReader cr = new ClassReader(bytes);

		switch (index) {
		case 1:
			return writeWorldServer(name, transformedName, bytes, cr);
		case 2:
			return writeWorld(name, transformedName, bytes, cr);
		case 3:
			return writeWorldProxy(name, bytes, cr);
		case 4:
			return writeWorldServerProxy(name, bytes, cr);
		case 5:
			return alterBlockPane(name, transformedName, bytes, cr);
		case 6:
			return alterContainer(name, transformedName, bytes, cr);
		case 7:
			return alterController(name, transformedName, bytes, cr);
		case 8:
			return alterLongHashMap(name, transformedName, bytes, cr);
		case 9:
			return alterChunk(name, transformedName, bytes, cr);
		case 10:
			return alterMinecraft(name, transformedName, bytes, cr);
		case 11:
			return alterRenderBlocks(name, transformedName, bytes, cr);
		case 12:
			return alterTileEntity(name, transformedName, bytes, cr);

		default:
			return bytes;
		}
	}

	// { Improve Vanilla
	private static byte[] alterRenderBlocks(String name, String transformedName, byte[] bytes, ClassReader cr) {
		//.renderBlockStainedGlassPane(Block, int, int, int)
		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_147733_k", "func_150098_a", "func_147439_a" };
		} else {
			names = new String[] { "renderBlockStainedGlassPane", "canPaneConnectToBlock", "getBlock" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/block/Block;III)Z";
		final String Rsig = "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z";
		final String Ssig = "(Lnet/minecraft/block/Block;)Z";
		final String Csig = "(III)Lnet/minecraft/block/Block;";
		final String cc = "net/minecraft/block/BlockPane";
		final String fd = "net/minecraftforge/common/util/ForgeDirection";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(remapper.mapMethodName(name, n.name, n.desc)) && sig.equals(remapper.mapMethodDesc(n.desc))) {
					m = n;
					break;
				}
			}

			if (m == null)
				break l;

			m.localVariables = null;

			final String[] dirs = {"NORTH", "NORTH", "SOUTH", "SOUTH", "WEST", "WEST", "EAST", "EAST"};
			int di = 0;

			for (int i = 0, e = m.instructions.size(); i < e; ++i) {
				AbstractInsnNode n = m.instructions.get(i);
				if (n.getType() == AbstractInsnNode.METHOD_INSN) {
					MethodInsnNode mn = (MethodInsnNode)n;
					if (n.getOpcode() == INVOKEINTERFACE && n.getNext().getOpcode() == INVOKEVIRTUAL) {
						if (names[2].equals(remapper.mapMethodName(mn.owner, mn.name, mn.desc))) {
							if (Csig.equals(remapper.mapMethodDesc(mn.desc)) &&
									Ssig.equals(remapper.mapMethodDesc(((MethodInsnNode)mn.getNext()).desc))) {
								m.instructions.insertBefore(n, new FieldInsnNode(GETSTATIC, fd, dirs[di++], 'L'+fd+';'));
								m.instructions.insertBefore(n, new MethodInsnNode(INVOKEVIRTUAL, cc, "canPaneConnectTo", Rsig, false));
								m.instructions.remove(n.getNext());
								m.instructions.remove(n);
							}
						}
					}
				}
			}

			if (di == 0)
				 break l;

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private static byte[] alterBlockPane(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String names = "canPaneConnectTo"; // forge added

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names.equals(remapper.mapMethodName(name, n.name, n.desc)) && sig.equals(remapper.mapMethodDesc(n.desc))) {
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
			m.instructions.add(new MethodInsnNode(INVOKESTATIC, "cofh/asm/HooksCore", "paneConnectsTo", sig, false));
			m.instructions.add(new InsnNode(IRETURN));

			m.localVariables = null;

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterMinecraft(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_71407_l", "func_110550_d" };
		} else {
			names = new String[] { "runTick", "tick" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		String mOwner = remapper.unmap("net/minecraft/client/renderer/texture/TextureManager");

		l: {
			boolean updated = false;
			mc: for (MethodNode m : cn.methods) {
				String mName = remapper.mapMethodName(name, m.name, m.desc);
				if (names[0].equals(mName) && "()V".equals(m.desc)) {
					updated = true;
					for (int i = 0, e = m.instructions.size(); i < e; ++i) {
						AbstractInsnNode n = m.instructions.get(i);
						if (n.getOpcode() == INVOKEVIRTUAL) {
							MethodInsnNode mn = (MethodInsnNode) n;
							if (mOwner.equals(mn.owner) && names[1].equals(remapper.mapMethodName(mn.owner, mn.name, mn.desc)) && "()V".equals(mn.desc)) {
								m.instructions.set(mn, new MethodInsnNode(INVOKESTATIC, "cofh/asm/HooksCore", "tickTextures",
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

	private static byte[] alterChunk(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_150803_c", "field_76650_s" };
		} else {
			names = new String[] { "recheckGaps", "isGapLightingUpdated" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		l: {
			boolean updated = false;
			for (MethodNode m : cn.methods) {
				String mName = remapper.mapMethodName(name, m.name, m.desc);
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

	private static byte[] alterLongHashMap(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_76155_g", "func_76160_c", "func_76161_b" };
		} else {
			names = new String[] { "getHashedKey", "getEntry", "containsItem" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		l: {
			boolean updated = false;
			MethodNode getEntry = null, containsItem = null;
			for (MethodNode m : cn.methods) {
				String mName = remapper.mapMethodName(name, m.name, m.desc);
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
				ClassNode clone = new ClassNode(ASM4);
				cr.accept(clone, ClassReader.EXPAND_FRAMES);
				String sig = "(J)Lnet/minecraft/util/LongHashMap$Entry;";
				for (MethodNode m : clone.methods) {
					String mName = remapper.mapMethodName(name, m.name, m.desc);
					if (names[1].equals(mName) && sig.equals(remapper.mapMethodDesc(m.desc))) {
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

	private static byte[] alterController(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "func_85182_a", "field_85183_f" };
		} else {
			names = new String[] { "sameToolAndBlock", "currentItemHittingBlock" };
		}

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(III)Z";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		final String itemstack = remapper.unmap("net/minecraft/item/ItemStack");

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(remapper.mapMethodName(name, n.name, n.desc)) && sig.equals(n.desc)) {
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
						final String clazz = "cofh/asm/HooksCore";
						final String method = "areItemsEqualHook";
						final String sign = "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z";
						m.instructions.insertBefore(mn, new MethodInsnNode(INVOKESTATIC, clazz, method, sign, false));
						m.instructions.insertBefore(mn, new JumpInsnNode(IFEQ, jmp2));
						m.instructions.insertBefore(mn, new JumpInsnNode(GOTO, jmp));
						break;
					}
				}
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
		}
		return bytes;
	}

	private static byte[] alterContainer(String name, String transformedName, byte[] bytes, ClassReader cr) {

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
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		l: {
			MethodNode m = null;
			for (MethodNode n : cn.methods) {
				if (names[0].equals(remapper.mapMethodName(name, n.name, n.desc)) && sig.equals(remapper.mapMethodDesc(n.desc))) {
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

	private static byte[] alterTileEntity(String name, String transformedName, byte[] bytes, ClassReader cr) {

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, 0);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		cw.newMethod(name, "cofh_validate", "()V", true);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "cofh_validate", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 1);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	private static byte[] writeWorld(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "field_73019_z", "field_72986_A", "field_73011_w", "field_72984_F", "func_147448_a", "func_147455_a", "func_72939_s", "func_145830_o" };
		} else {
			names = new String[] { "saveHandler", "worldInfo", "provider", "theProfiler", "func_147448_a", "setTileEntity", "updateEntities", "hasWorldObj" };
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		final String sig = "(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		MethodNode addTileEntity = null, addTileEntities = null, setTileEntity = null, updateEntities = null;
		boolean found = false;
		for (MethodNode m : cn.methods) {
			if ("<init>".equals(m.name)) {
				if (sig.equals(remapper.mapMethodDesc(m.desc)))
						found = true;
				LabelNode a = new LabelNode(new Label());
				AbstractInsnNode n = m.instructions.getFirst();
				while (n.getOpcode() != INVOKESPECIAL ||
						!((MethodInsnNode)n).name.equals("<init>")) n = n.getNext();
				m.instructions.insert(n, n = a);
				m.instructions.insert(n, n = new LineNumberNode(-15000, a));
				m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new TypeInsnNode(NEW, "cofh/lib/util/LinkedHashList"));
				m.instructions.insert(n, n = new InsnNode(DUP));
				m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "cofh/lib/util/LinkedHashList", "<init>", "()V", false));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
			} else if ("addTileEntity".equals(m.name) && "(Lnet/minecraft/tileentity/TileEntity;)V".equals(remapper.mapMethodDesc(m.desc))) {
				addTileEntity = m;
			} else if (names[4].equals(remapper.mapMethodName(name, m.name, m.desc)) && "(Ljava/util/Collection;)V".equals(m.desc)) {
				addTileEntities = m;
			} else if (names[5].equals(remapper.mapMethodName(name, m.name, m.desc)) && "(IIILnet/minecraft/tileentity/TileEntity;)V".equals(remapper.mapMethodDesc(m.desc))) {
				setTileEntity = m;
			} else if (names[6].equals(remapper.mapMethodName(name, m.name, m.desc)) && "()V".equals(remapper.mapMethodDesc(m.desc))) {
				updateEntities = m;
			}
		}
		cn.fields.add(new FieldNode(ACC_PRIVATE, "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;", null, null));
		if (addTileEntity != null) {

			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n;
			addTileEntity.instructions.insert(n = a);
			addTileEntity.instructions.insert(n, n = new LineNumberNode(-15001, a));
			addTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			addTileEntity.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
			addTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 1));
			addTileEntity.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "push", "(Ljava/lang/Object;)Z", false));
			addTileEntity.instructions.insert(n, n = new InsnNode(POP));
		}
		if (setTileEntity != null) {

			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n = setTileEntity.instructions.getLast();
			while (n.getOpcode() != RETURN) n = n.getPrevious();
			n = n.getPrevious();
			setTileEntity.instructions.insert(n = a);
			setTileEntity.instructions.insert(n, n = new LineNumberNode(-15002, a));
			setTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			setTileEntity.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
			setTileEntity.instructions.insert(n, n = new VarInsnNode(ALOAD, 4));
			setTileEntity.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "push", "(Ljava/lang/Object;)Z", false));
			setTileEntity.instructions.insert(n, n = new InsnNode(POP));
		}
		if (addTileEntities != null) {
			LabelNode a = new LabelNode(new Label());
			AbstractInsnNode n = addTileEntities.instructions.getFirst();
			for (;;) {
				while (n.getOpcode() != CHECKCAST) n = n.getNext();
				if (remapper.mapType(((TypeInsnNode)n).desc).equals("net/minecraft/tileentity/TileEntity"))
					break;
			}
			addTileEntities.instructions.insert(n, n = a);
			addTileEntities.instructions.insert(n, n = new LineNumberNode(-15003, a));
			addTileEntities.instructions.insert(n, n = new InsnNode(DUP));
			addTileEntities.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			addTileEntities.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
			addTileEntities.instructions.insert(n, n = new InsnNode(SWAP));
			addTileEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "push", "(Ljava/lang/Object;)Z", false));
			addTileEntities.instructions.insert(n, n = new InsnNode(POP));
		}
		if (updateEntities != null) {
			AbstractInsnNode n = updateEntities.instructions.getFirst();
			while (n.getOpcode() != INVOKEVIRTUAL ||
					!"onChunkUnload".equals(((MethodInsnNode)n).name) ||
					!"()V".equals(((MethodInsnNode)n).desc)) n = n.getNext();
			while (n.getOpcode() != PUTFIELD) n = n.getNext();
			n = n.getPrevious().getPrevious();
			LabelNode lStart = new LabelNode(new Label());
			LabelNode lCond = new LabelNode(new Label());
			LabelNode lGuard = new LabelNode(new Label());
			LabelNode a = new LabelNode(new Label());
			updateEntities.instructions.insert(n, n = a);
			updateEntities.instructions.insert(n, n = new LineNumberNode(-15004, a));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(GOTO, lCond));
			updateEntities.instructions.insert(n, n = lStart);
			updateEntities.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
			updateEntities.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			updateEntities.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
			updateEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "cofh/lib/util/LinkedHashList", "shift", "()Ljava/lang/Object;", false));
			updateEntities.instructions.insert(n, n = new TypeInsnNode(CHECKCAST, "net/minecraft/tileentity/TileEntity"));
			updateEntities.instructions.insert(n, n = new InsnNode(DUP));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(IFNULL, lGuard));
			updateEntities.instructions.insert(n, n = new InsnNode(DUP));
			updateEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntity", names[7], "()Z", false));
			updateEntities.instructions.insert(n, n = new JumpInsnNode(IFNE, lGuard));
			updateEntities.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntity", "cofh_validate", "()V", false));
			updateEntities.instructions.insert(n, n = new InsnNode(ACONST_NULL));
			updateEntities.instructions.insert(n, n = lGuard);
			updateEntities.instructions.insert(n, n = new InsnNode(POP));
			updateEntities.instructions.insert(n, n = lCond);
			updateEntities.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
			updateEntities.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
			updateEntities.instructions.insert(n, n = new FieldInsnNode(GETFIELD, "net/minecraft/world/World", "cofh_recentTiles", "Lcofh/lib/util/LinkedHashList;"));
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
			mv.visitMethodInsn(INVOKESPECIAL, "net/minecraft/world/storage/WorldInfo", "<init>", "(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", false);
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

	private static byte[] writeWorldServer(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "field_73061_a", "field_73062_L", "field_73063_M", "field_85177_Q" };
		} else {
			names = new String[] { "mcServer", "theEntityTracker", "thePlayerManager", "worldTeleporter" };
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		final String sig = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

		l: {
			for (MethodNode m : cn.methods) {
				if ("<init>".equals(m.name) && sig.equals(remapper.mapMethodDesc(m.desc))) {
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
					"(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V", false);
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

		ClassNode world = new ClassNode(ASM4);
		{
			FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
			try {
				ClassReader reader = new ClassReader(LoadingPlugin.loader.getClassBytes(
						remapper.unmap("net/minecraft/world/World").replace('/', '.')));
				reader.accept(new FMLRemappingAdapter(world), ClassReader.SKIP_CODE);
			} catch (Throwable e) {
				Throwables.propagate(e);
			}
		}

		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.SKIP_FRAMES);

		for (MethodNode m : world.methods) {
			if (m.name.indexOf('<') != 0 && (m.access & ACC_STATIC) == 0) {
				{
					Iterator<MethodNode> i = cn.methods.iterator();
					while (i.hasNext()) {
						MethodNode m2 = i.next();
						if (m2.name.equals(m.name) && m2.desc.equals(m.desc)) {
							i.remove();
						}
					}
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.name, m.desc, m.signature, m.exceptions.toArray(new String[0]));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "skyboy/core/world/WorldProxy", "proxiedWorld", "Lnet/minecraft/world/World;");
				Type[] types = Type.getArgumentTypes(m.desc);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", m.name, m.desc, false);
				mv.visitInsn(Type.getReturnType(m.desc).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private static byte[] writeWorldServerProxy(String name, byte[] bytes, ClassReader cr) {

		ClassNode worldServer = new ClassNode(ASM4);
		{
			FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
			try {
				ClassReader reader = new ClassReader(LoadingPlugin.loader.getClassBytes(
						remapper.unmap("net/minecraft/world/WorldServer").replace('/', '.')));
				reader.accept(new FMLRemappingAdapter(worldServer), ClassReader.SKIP_CODE);
			} catch (Throwable e) {
				Throwables.propagate(e);
			}
		}
		ClassNode world = new ClassNode(ASM4);
		{
			FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
			try {
				ClassReader reader = new ClassReader(LoadingPlugin.loader.getClassBytes(
						remapper.unmap("net/minecraft/world/World").replace('/', '.')));
				reader.accept(new FMLRemappingAdapter(world), ClassReader.SKIP_CODE);
			} catch (Throwable e) {
				Throwables.propagate(e);
			}
		}

		ClassNode cn = new ClassNode(ASM4);
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

		for (MethodNode m : world.methods) {
			if (m.name.indexOf('<') != 0 && (m.access & ACC_STATIC) == 0) {
				{
					Iterator<MethodNode> i = cn.methods.iterator();
					while (i.hasNext()) {
						MethodNode m2 = i.next();
						if (m2.name.equals(m.name) && m2.desc.equals(m.desc)) {
							i.remove();
						}
					}
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.name, m.desc, m.signature, m.exceptions.toArray(new String[0]));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "skyboy/core/world/WorldServerProxy", "proxiedWorld", "Lnet/minecraft/world/WorldServer;");
				Type[] types = Type.getArgumentTypes(m.desc);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", m.name, m.desc, false);
				mv.visitInsn(Type.getReturnType(m.desc).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		for (MethodNode m : worldServer.methods) {
			if (m.name.indexOf('<') != 0 && (m.access & ACC_STATIC) == 0) {
				{
					Iterator<MethodNode> i = cn.methods.iterator();
					while (i.hasNext()) {
						MethodNode m2 = i.next();
						if (m2.name.equals(m.name) && m2.desc.equals(m.desc)) {
							i.remove();
						}
					}
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.name, m.desc, m.signature, m.exceptions.toArray(new String[0]));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "skyboy/core/world/WorldServerProxy", "proxiedWorld", "Lnet/minecraft/world/WorldServer;");
				Type[] types = Type.getArgumentTypes(m.desc);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/WorldServer", m.name, m.desc, false);
				mv.visitInsn(Type.getReturnType(m.desc).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();
		return bytes;
	}

	private static int getAccess(MethodNode m) {

		int r = m.access;
		r &= ~(ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED | ACC_FINAL | ACC_BRIDGE | ACC_ABSTRACT);
		r |= ACC_PUBLIC | ACC_SYNTHETIC;
		return r;
	}

	// }

	// { Implement & Strip
	static boolean implement(ClassNode cn) {

		if (cn.visibleAnnotations == null) {
			return false;
		}
		boolean interfaces = false;
		for (AnnotationNode n : cn.visibleAnnotations) {
			AnnotationInfo node = parseAnnotation(n, implementableDesc);
			if (node != null && side == node.side) {
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
						} catch (Throwable _) {
						}
					}
				}
			}
		}
		return interfaces;
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
							} catch (Throwable _) {
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
					String clazz = value[j];
					if (clazz.startsWith("mod:")) {
						needsRemoved = !Loader.isModLoaded(clazz.substring(4));
					} else if (clazz.startsWith("api:")) {
						needsRemoved = !ModAPIManager.INSTANCE.hasAPI(clazz.substring(4));
					} else {
						try {
							if (!workingPath.contains(clazz)) {
								Class.forName(clazz, false, ASMCore.class.getClassLoader());
							}
						} catch (Throwable _) {
							needsRemoved = true;
						}
					}
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

	// }

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
					} else if ("side".equals(k) && v instanceof String[]) {
						String t = ((String[]) v)[1];
						if (t != null) {
							info.side = t.toUpperCase().intern();
						}
					}
				}
			}
		}
		return info;
	}

	static void scrapeData(ASMDataTable table) {

		log.debug("Scraping data");

		side = FMLCommonHandler.instance().getSide().toString().toUpperCase().intern();

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

		log.debug("Found " + implementables.size() + " @Implementable and " + strippables.size() + " @Strippable");
	}
}
