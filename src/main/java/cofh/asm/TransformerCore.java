package cofh.asm;

import cofh.CoFHCore;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class TransformerCore implements IClassTransformer {

	public static LaunchClassLoader cl = (LaunchClassLoader) CoFHCore.class.getClassLoader();

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {

		if (transformedName.startsWith("net.minecraft.entity.projectile.")) {
			// System.out.println("TransformedName: " + transformedName + " - UnTra: " + name);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(262144);
			cr.accept(cn, 0);

			for (FieldNode theNode : cn.fields) {
				theNode.access &= ~7; // low 3 bits are public/private/protected/default
				theNode.access |= Opcodes.ACC_PUBLIC;
			}

			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			return cw.toByteArray();

		}

		return bytes;
	}

}
