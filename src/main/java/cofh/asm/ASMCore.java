package cofh.asm;

import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Strippable;
import cofh.asm.relauncher.Substitutable;
import gnu.trove.set.hash.THashSet;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class ASMCore {

	static Logger log = LogManager.getLogger("CoFH ASM");

	static THashSet<String> parsables, implementables, strippables, substitutables;
	static final String implementableDesc, strippableDesc, substitutableDesc;
	static String side;

	static void init() {

	}

	static {

		implementableDesc = Type.getDescriptor(Implementable.class);
		strippableDesc = Type.getDescriptor(Strippable.class);
		substitutableDesc = Type.getDescriptor(Substitutable.class);

		parsables = new THashSet<>(30);
		implementables = new THashSet<>(10);
		strippables = new THashSet<>(10);
		substitutables = new THashSet<>(10);
	}

	static final ArrayList<String> workingPath = new ArrayList<>();
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
							l:
							{
								for (MethodNode m : cn.methods) {
									if (info.method.equals(m.name) && mn.desc.equals(m.desc)) {
										mn.instructions.add(cloneList(m.instructions));
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
						//ret = !ModRange.createFromVersionSpec(mod, clazz).containsVersion(new ModVersion(mod, modc.getVersion()));
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

	private static InsnList cloneList(InsnList list) {

		ListIterator<AbstractInsnNode> iter = list.iterator();
		IdentityHashMap<LabelNode, LabelNode> some_form_of_bullshit_map = new IdentityHashMap<>();
		{
			IdentityHashMap<Label, Label> backing_bullshit = new IdentityHashMap<>();
			while (iter.hasNext()) {
				AbstractInsnNode n = iter.next();
				if (n instanceof LabelNode) {
					LabelNode node = (LabelNode) n;
					Label label = backing_bullshit.get(node.getLabel());
					if (label == null) {
						backing_bullshit.put(node.getLabel(), label = new Label());
					}
					some_form_of_bullshit_map.put(node, new LabelNode(label));
					/*
					  Look. I don't know. InsnNode.clone requires this map. InsnList and Method
					  do not provide clone, so I need to build this map to do this for no reason
					  I can think of. This is all bullshit, but apparently required bullshit.
					  Basic features? Pfft. Who would ever want to use those?
					 */
				}
			}
		}

		InsnList ret = new InsnList();
		iter = list.iterator();
		while (iter.hasNext()) {
			ret.add(iter.next().clone(some_form_of_bullshit_map));
		}
		return ret;
	}

	private static Map<String, ModContainer> mods;

	static Map<String, ModContainer> getLoadedMods() {

		if (mods == null) {
			mods = new HashMap<>();
			for (ModContainer m : Loader.instance().getModList()) {
				mods.put(m.getModId(), m);
			}
		}
		return mods;
	}

	private static Map<String, ModContainer> apis;

	public static Map<String, ModContainer> getLoadedAPIs() {

		if (apis == null) {
			apis = new HashMap<>();
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
				for (int i = 0, e = values.size(); i < e; ) {
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
		log.debug("Found " + implementables.size() / 2 + " @Implementable; " + strippables.size() / 2 + " @Strippable; " + substitutables.size() / 2 + " @Substitutable");
	}

}
