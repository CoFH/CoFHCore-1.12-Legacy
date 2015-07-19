package cofh.asm;

import static org.objectweb.asm.Opcodes.*;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CoFHAccessTransformer implements IClassTransformer {

	private static List<String> mapFileList = new LinkedList<String>();

	public CoFHAccessTransformer() throws IOException {

		super();

		// file names are case sensitive. do not alter.
		mapFileList.add("CoFH_at.cfg");
		// CoFH_at.cfg must also contain all entries from cofhlib_at.cfg

		for (String file : mapFileList) {
			readMapFile(file);
		}
	}

	void readMapFile(String rulesFile) throws IOException {

		File file = new File(rulesFile);
		URL rulesResource;
		if (file.exists()) {
			rulesResource = file.toURI().toURL();
		} else {
			rulesResource = Resources.getResource(rulesFile);
		}
		processATFile(Resources.asCharSource(rulesResource, Charsets.UTF_8).openStream());
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {

		if (bytes == null) {
			return null;
		}

		ClassReader classReader = new ClassReader(bytes);
		l: {
			String owner = classReader.getClassName(), zuper = classReader.getSuperName();
			superClasses.put(owner, zuper);
			if (!superClasses.containsKey(zuper)) {
				// ensure super gets loaded and passed through us, already loaded classes (mostly native)
				// will just return and we'll have a null, which is fine
				superClasses.put(zuper, null);
				try {
					Class.forName(zuper.replace('/', '.'), false, ASMCore.class.getClassLoader());
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}

			for (; owner != null; owner = superClasses.get(owner)) {
				if (modifiers.containsKey(owner)) {
					break l;
				}
			}
			return bytes;
		}

		ClassNode cn = new ClassNode();
		classReader.accept(cn, 0);

		Modifier m = classAccess.get(cn.name);
		if (m != null) {
			cn.access = m.getFixedAccess(cn.access);
		}

		if (cn.innerClasses != null) {
			for (InnerClassNode in : cn.innerClasses) {
				m = classAccess.get(in.name);
				if (m != null) {
					in.access = m.getFixedAccess(in.access);
				}
			}
		}

		for (FieldNode fn : cn.fields) {
			m = fieldAccess.get(cn.name + '/' + fn.name);
			if (m != null) {
				fn.access = m.getFixedAccess(fn.access);
			}
			m = fieldAccess.get(cn.name + "/*");
			if (m != null) {
				fn.access = m.getFixedAccess(fn.access);
			}
		}

		List<MethodNode> nowOverridable = Lists.newArrayList();
		for (MethodNode mn : cn.methods) {

			int access = mn.access;
			for (String owner = cn.name; owner != null; owner = superClasses.get(owner)) {
				m = methodAccess.get(owner + '/' + mn.name + mn.desc);
				if (m != null) {
					access = m.getFixedAccess(access);
				}
				m = methodAccess.get(owner + "/*()V");
				if (m != null) {
					access = m.getFixedAccess(access);
				}
			}
			if ((mn.access & ACC_PRIVATE) != 0 && (access & ACC_PRIVATE) == 0) {
				nowOverridable.add(mn);
			}
			mn.access = access;
		}

		replaceInvokeSpecial(cn, nowOverridable);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(writer);
		return writer.toByteArray();
	}

	private void replaceInvokeSpecial(ClassNode clazz, List<MethodNode> toReplace) {

		for (MethodNode method : clazz.methods) {
			for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
				AbstractInsnNode insn = it.next();
				if (insn.getOpcode() == INVOKESPECIAL) {
					MethodInsnNode mInsn = (MethodInsnNode) insn;
					if ("<init>".equals(mInsn.name)) {
						continue;
					}
					for (MethodNode n : toReplace) {
						if (n.name.equals(mInsn.name) && n.desc.equals(mInsn.desc)) {
							mInsn.setOpcode(INVOKEVIRTUAL);
							break;
						}
					}
				}
			}
		}
	}

	private static HashMap<String, String> modifiers = new HashMap<String, String>();
	private static HashMap<String, String> superClasses = new HashMap<String, String>();
	private static HashMap<String, Modifier> classAccess = new HashMap<String, Modifier>();
	private static HashMap<String, Modifier> fieldAccess = new HashMap<String, Modifier>();
	private static HashMap<String, Modifier> methodAccess = new HashMap<String, Modifier>();

	protected static void processATFile(Reader rules) throws IOException {

		for (LineReader reader = new LineReader(rules);;) {

			String input = reader.readLine(), line;
			if (input == null) {
				return;
			} else if (input.length() == 0) {
				continue;
			}
			{
				int i = input.indexOf('#');
				line = i < 0 ? input : input.substring(0, i);
			}
			String[] parts = line.split(" ");
			if (parts.length > 3 || parts.length < 2) {
				throw new RuntimeException("Invalid config file line " + input);
			}

			String desc = "";
			String lookupName = parts[1].replace('.', '/');
			modifiers.put(lookupName, null);

			HashMap<String, Modifier> map;

			if (parts.length == 2) {
				map = classAccess;
			} else {
				String nameReference = parts[2];
				lookupName += '/' + nameReference;

				int parenIdx = nameReference.indexOf('(');
				if (parenIdx > 0) {
					map = methodAccess;
					desc = nameReference.substring(parenIdx);
					nameReference = nameReference.substring(0, parenIdx);
				} else {
					map = fieldAccess;
				}
				parts[1] = nameReference;
			}
			Modifier mod = map.get(lookupName);

			if (mod == null) {
				map.put(lookupName, new Modifier(parts[0], parts[1], desc));
			} else {
				mod.setTargetAccess(parts[0]);
			}
		}
	}

	static class Modifier {

		public String name;
		public String desc;
		public int targetAccess;
		public boolean changeFinal;
		public boolean markFinal;

		public Modifier(String access, String name, String desc) {

			targetAccess = ACC_PRIVATE;
			setTargetAccess(access);
			this.name = name;
			this.desc = desc;
		}

		public void setTargetAccess(String name) {

			switch (targetAccess) {
			// there is no need to check to see if access mode is private, we can never lower visibility
			case ACC_PRIVATE:
				if (name.startsWith("default"))
					targetAccess = 0;
				// continue
			case 0:
				if (name.startsWith("protected"))
					targetAccess = ACC_PROTECTED;
				// continue
			case ACC_PROTECTED:
				if (name.startsWith("public"))
					targetAccess = ACC_PUBLIC;
				// continue
			case ACC_PUBLIC:
				break;
			}

			if (name.endsWith("-f")) {
				changeFinal = true;
				markFinal = false;
			} else if (!changeFinal && name.endsWith("+f")) {
				changeFinal = true;
				markFinal = true;
			}
		}

		public int getFixedAccess(int access) {

			int t = targetAccess & 7;
			int ret = (access & ~7);

			switch (access & 7) {
			case ACC_PRIVATE:
				ret |= t;
				break;
			case 0: // default
				ret |= (t != ACC_PRIVATE ? t : 0 /* default */);
				break;
			case ACC_PROTECTED:
				ret |= (t != ACC_PRIVATE && t != 0 /* default */? t : ACC_PROTECTED);
				break;
			case ACC_PUBLIC:
				ret |= ACC_PUBLIC;
				break;
			default:
				throw new RuntimeException("The fuck?");
			}

			if (changeFinal) {
				if (markFinal) {
					ret |= ACC_FINAL;
				} else {
					ret &= ~ACC_FINAL;
				}
			}
			return ret;
		}
	}

	static class LineReader { // from Properties#LineReader

		public LineReader(InputStream inStream) {

			this.inStream = inStream;
			inByteBuf = new byte[8192];
		}

		public LineReader(Reader reader) {

			this.reader = reader;
			inCharBuf = new char[8192];
		}

		byte[] inByteBuf;
		char[] inCharBuf;
		char[] lineBuf = new char[1024];
		int inLimit = 0;
		int inOff = 0;
		InputStream inStream;
		Reader reader;

		String readLine() throws IOException {

			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isCommentLine = false;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = (inStream == null) ? reader.read(inCharBuf)
							: inStream.read(inByteBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0 || isCommentLine) {
							return null;
						}
						return new String(lineBuf, 0, len);
					}
				}
				if (inStream != null) {
					//The line below is equivalent to calling a
					//ISO8859-1 decoder.
					c = (char) (0xff & inByteBuf[inOff++]);
				} else {
					c = inCharBuf[inOff++];
				}
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						isCommentLine = true;
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					//flip the preceding backslash flag
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				}
				else {
					// reached EOL
					if (isCommentLine || len == 0) {
						isCommentLine = false;
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = (inStream == null)
								? reader.read(inCharBuf)
								: inStream.read(inByteBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return new String(lineBuf, 0, len);
						}
					}
					if (precedingBackslash) {
						len -= 1;
						//skip the leading whitespace characters in following line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return new String(lineBuf, 0, len);
					}
				}
			}
		}
	}

}
