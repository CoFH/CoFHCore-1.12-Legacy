package cofh.asm;

import java.util.Iterator;
import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cofh.CoFHCore;

public class TransformerMasquerade implements IClassTransformer {

	// Capes
	public static String abstractClientPlayer = "net.minecraft.client.entity.AbstractClientPlayer";
	public static String getCapeUrl = LoadingPlugin.runtimeDeobfEnabled ? "func_110308_e" : "getCapeUrl";
	public static String getSkinUrl = LoadingPlugin.runtimeDeobfEnabled ? "func_110300_d" : "getSkinUrl";

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {

		if (abstractClientPlayer.equals(transformedName)) {
			CoFHCore.log.info("AbstractClientPlayer Detected...");

			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(262144);
			cr.accept(cn, 0);
			Iterator<MethodNode> methodIterator = cn.methods.iterator();
			while (methodIterator.hasNext()) {

				MethodNode mn = methodIterator.next();
				if (mn.name.equals(getCapeUrl)) {
					System.out.println("getCapeUrl Transforming...");
					transformGetCapeUrl(mn);
				} else if (mn.name.equals(getSkinUrl)) {
					System.out.println("getSkinUrl Transforming...");
					transformGetSkinUrl(mn);
				}

			}
			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			return cw.toByteArray();
		}
		return bytes;
	}

	public void transformGetCapeUrl(MethodNode method) {

		ListIterator<AbstractInsnNode> iter = method.instructions.iterator();

		LocalVariableNode Username = getLocalVariableIndex(method, "", 0);
		while (iter.hasNext()) {
			AbstractInsnNode node = iter.next();

			if (node.getOpcode() == 176) {
				method.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, Username.index));
				method.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "cofh/asm/HooksMasquerade", "getCapeUrl", "(" + Username.desc
						+ ")Ljava/lang/String;"));
			}
		}
	}

	public void transformGetSkinUrl(MethodNode method) {

		ListIterator<AbstractInsnNode> iter = method.instructions.iterator();

		LocalVariableNode Username = getLocalVariableIndex(method, "", 0);
		while (iter.hasNext()) {
			AbstractInsnNode node = iter.next();

			if (node.getOpcode() == 176) {
				method.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, Username.index));
				method.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "cofh/asm/HooksMasquerade", "getSkinUrl", "(" + Username.desc
						+ ")Ljava/lang/String;"));
			}
		}
	}

	public LocalVariableNode getLocalVariableIndex(MethodNode mn, String OptifineName, int def) {

		for (Object a : mn.localVariables) {
			LocalVariableNode node = (LocalVariableNode) a;
			if (node.name.equals(OptifineName)) {
				return node;
			}
		}
		return mn.localVariables.get(def);
	}

}
