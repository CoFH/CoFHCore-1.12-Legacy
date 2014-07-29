package cofh.asm;

import static org.objectweb.asm.Opcodes.*;

import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Strippable;
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

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PCCASMTransformer implements IClassTransformer {

	private static Logger log = LogManager.getLogger("CoFH ASM");
	private static boolean scrappedData = false;
	private static THashSet<String> parsables, implementables, strippables;
	private static final String implementableDesc, strippableDesc;
	private static String side;
	static {

		implementableDesc = Type.getDescriptor(Implementable.class);
		strippableDesc = Type.getDescriptor(Strippable.class);

		parsables = new THashSet<String>(10);
		implementables = new THashSet<String>(10);
		strippables = new THashSet<String>(10);
	}
	private static final String[] emptyList = {};
	private static final boolean ENABLE_HACK = Boolean.valueOf(System.getProperty("cofh.classloadinghack", "true"));

	private final ArrayList<String> workingPath = new ArrayList<String>();
	private ClassNode world = null, worldServer = null;

	private TObjectByteHashMap<String> hashes = new TObjectByteHashMap<String>(4, 2, (byte) 0);

	public PCCASMTransformer() {

		hashes.put("net.minecraft.world.WorldServer", (byte) 1);
		hashes.put("net.minecraft.world.World", (byte) 2);
		hashes.put("skyboy.core.world.WorldProxy", (byte) 3);
		hashes.put("skyboy.core.world.WorldServerProxy", (byte) 4);
	}

	public static void scrapeData(ASMDataTable table) {

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

		scrappedData = true;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {

		if (bytes == null) {
			return null;
		}

		l: if (scrappedData) {
			if (!parsables.contains(name)) {
				break l;
			}

			workingPath.add(transformedName);

			if (implementables.contains(name)) {
				log.info("Adding runtime interfaces to " + transformedName);
				ClassReader cr = new ClassReader(bytes);
				ClassNode cn = new ClassNode();
				cr.accept(cn, 0);
				if (this.implement(cn)) {
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
				if (this.strip(cn)) {
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
					cn.accept(cw);
					bytes = cw.toByteArray();
				} else {
					log.debug("Nothing stripped from " + transformedName);
				}
			}

			workingPath.remove(workingPath.size() - 1);
		}

		switch (hashes.get(transformedName)) {
		case 1:
			bytes = writeWorldServer(name, transformedName, bytes, new ClassReader(bytes));
			break;
		case 2:
			bytes = writeWorld(name, transformedName, bytes, new ClassReader(bytes));
			break;
		case 3:
			bytes = writeWorldProxy(name, bytes, new ClassReader(bytes));
			break;
		case 4:
			bytes = writeWorldServerProxy(name, bytes, new ClassReader(bytes));
			break;
		default:
			break;
		}

		if (ENABLE_HACK) {
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
								Class.forName(node.name, false, this.getClass().getClassLoader());
							} catch (Throwable _) {
							}
						}
					}
				}
				workingPath.remove(workingPath.size() - 1);
			}
		}

		return bytes;
	}

	private byte[] writeWorld(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names = null;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "field_73019_z", "field_72986_A", "field_73011_w", "field_72984_F" };
		} else {
			names = new String[] { "saveHandler", "worldInfo", "provider", "theProfiler" };
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		String sig = "(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		String sigObf = "(" + "L" + remapper.unmap("net/minecraft/world/storage/ISaveHandler") + ";" + "Ljava/lang/String;" + "L"
				+ remapper.unmap("net/minecraft/world/WorldProvider") + ";" + "L" + remapper.unmap("net/minecraft/world/WorldSettings") + ";" + "L"
				+ remapper.unmap("net/minecraft/profiler/Profiler") + ";" + ")V";

		l: {
			for (MethodNode m : cn.methods) {
				if ("<init>".equals(m.name) && (sig.equals(m.desc) || sigObf.equals(m.desc))) {
					break l;
				}
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			cn.accept(cw);
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
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, name, names[0], "Lnet/minecraft/world/storage/ISaveHandler;");
			mv.visitTypeInsn(NEW, "net/minecraft/world/storage/WorldInfo");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESPECIAL, "net/minecraft/world/storage/WorldInfo", "<init>", "(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V");
			mv.visitFieldInsn(PUTFIELD, name, names[1], "Lnet/minecraft/world/storage/WorldInfo;");
			mv.visitVarInsn(ALOAD, 3);
			mv.visitFieldInsn(PUTFIELD, name, names[2], "Lnet/minecraft/world/WorldProvider;");
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(PUTFIELD, name, names[3], "Lnet/minecraft/profiler/Profiler;");
			mv.visitInsn(RETURN);
			mv.visitMaxs(11, 10);
			mv.visitEnd();
			cw.visitEnd();
			bytes = cw.toByteArray();
		}
		{
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
			cr = new ClassReader(bytes);
			cr.accept(remapAdapter, ClassReader.EXPAND_FRAMES);
			cn = new ClassNode(ASM4);
			cr = new ClassReader(classWriter.toByteArray());
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
			world = cn;
		}
		return bytes;
	}

	private byte[] writeWorldServer(String name, String transformedName, byte[] bytes, ClassReader cr) {

		String[] names = null;
		if (LoadingPlugin.runtimeDeobfEnabled) {
			names = new String[] { "field_73061_a", "field_73062_L", "field_73063_M", "field_85177_Q" };
		} else {
			names = new String[] { "mcServer", "theEntityTracker", "thePlayerManager", "worldTeleporter" };
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		String sig = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V";
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		String sigObf = "(" + "L" + remapper.unmap("net/minecraft/server/MinecraftServer") + ";" + "L"
				+ remapper.unmap("net/minecraft/world/storage/ISaveHandler") + ";" + "Ljava/lang/String;" + "L"
				+ remapper.unmap("net/minecraft/world/WorldProvider") + ";" + "L" + remapper.unmap("net/minecraft/world/WorldSettings") + ";" + "L"
				+ remapper.unmap("net/minecraft/profiler/Profiler") + ";" + ")V";

		l: {
			for (MethodNode m : cn.methods) {
				if ("<init>".equals(m.name) && (sig.equals(m.desc) || sigObf.equals(m.desc))) {
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
					"(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V");
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
		{
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
			cr = new ClassReader(bytes);
			cr.accept(remapAdapter, ClassReader.EXPAND_FRAMES);
			cn = new ClassNode(ASM4);
			cr = new ClassReader(classWriter.toByteArray());
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
			worldServer = cn;
		}
		return bytes;
	}

	private byte[] writeWorldProxy(String name, byte[] bytes, ClassReader cr) {

		if (world == null) {
			try {
				Class.forName("net.minecraft.world.World");
			} catch (Throwable _) { /* Won't happen */
			}
		}

		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

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
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", m.name, m.desc);
				mv.visitInsn(Type.getReturnType(m.desc).getOpcode(IRETURN));
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private byte[] writeWorldServerProxy(String name, byte[] bytes, ClassReader cr) {

		if (worldServer == null) {
			try {
				Class.forName("net.minecraft.world.WorldServer");
			} catch (Throwable _) { /* Won't happen */
			}
		}
		if (world == null) {
			try {
				Class.forName("net.minecraft.world.World");
			} catch (Throwable _) { /* Won't happen */
			}
		}

		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

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
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", m.name, m.desc);
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
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/WorldServer", m.name, m.desc);
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

	private boolean implement(ClassNode cn) {

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
								Class.forName(clazz, false, this.getClass().getClassLoader());
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

	private boolean strip(ClassNode cn) {

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
									Class.forName(clazz, false, this.getClass().getClassLoader());
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

	private boolean checkRemove(AnnotationInfo node, Iterator<? extends Object> iter) {

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
								Class.forName(clazz, false, this.getClass().getClassLoader());
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

	private static AnnotationInfo parseAnnotation(AnnotationNode node, String desc) {

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

	private static class AnnotationInfo {

		public String side;
		public String[] values = emptyList;
	}
}
