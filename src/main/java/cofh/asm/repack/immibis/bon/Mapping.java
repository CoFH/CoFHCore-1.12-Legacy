package cofh.asm.repack.immibis.bon;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping {
	private Map<String, String> classes = new HashMap<String, String>();
	private Map<String, String> methods = new HashMap<String, String>();
	private Map<String, String> fields = new HashMap<String, String>();
	private Map<String, List<String>> exceptions = new HashMap<String, List<String>>();
	private Map<String, String> classPrefixes = new HashMap<String, String>();
	private String defaultPackage = "";

	public void setClass(String in, String out) {
		classes.put(in, out);
	}

	public void setMethod(String clazz, String name, String desc, String out) {
		methods.put(clazz + "/" + name + desc, out);
	}

	public void setField(String clazz, String name, String out) {
		fields.put(clazz + "/" + name, out);
	}

	public void setExceptions(String clazz, String method, String desc, List<String> exc) {
		exceptions.put(clazz + "/" + method + desc, exc);
	}

	public String getClass(String in) {
		if(in == null)
			return null;
		if(in.startsWith("["))
			return "[" + getClass(in.substring(1));
		if(in.startsWith("L") && in.endsWith(";"))
			return "L" + getClass(in.substring(1, in.length() - 1)) + ";";

		if (in.length() == 1)
			switch (in.charAt(0))
			{
			case 'D': case 'Z': case 'B': case 'C':
			case 'S': case 'I': case 'J': case 'F':
				return in;
			default:
				break;
			}

		String ret = classes.get(in);
		if(ret != null)
			return ret;
		for(Map.Entry<String, String> e : classPrefixes.entrySet())
			if(in.startsWith(e.getKey()))
				return e.getValue() + in.substring(e.getKey().length());
		if(!in.contains("/"))
			return defaultPackage + in;
		return in;
	}

	public String getMethod(String clazz, String name, String desc) {
		String ret = methods.get(clazz + "/" + name + desc);
		return ret == null ? name : ret;
	}

	public String getField(String clazz, String name, String desc) {
		String ret = fields.get(clazz + "/" + name);
		return ret == null ? name : ret;
	}

	public List<String> getExceptions(String clazz, String method, String desc) {
		List<String> ret = exceptions.get(clazz + "/" + method + desc);
		return ret == null ? Collections.<String>emptyList() : ret;
	}

	public void addPrefix(String old, String new_) {
		classPrefixes.put(old, new_);
	}

	// p must include trailing slash
	public void setDefaultPackage(String p) {
		defaultPackage = p;
	}

	public String parseTypes(String type, boolean generic, boolean method) {
		if (type == null) return null;
		int pos = 0, len = type.length(), l = type.indexOf('<');
		char c;
		StringBuilder out = new StringBuilder(len);
		do {
			switch((c = type.charAt(pos)))
			{
			case '(': case ')': if (!method) break;
			case 'V': case 'Z': case 'B': case 'C':
			case 'S': case 'I': case 'J': case 'F':
			case 'D': case '[': case '<': case '>':
				out.append(c);
				pos++;
				continue;
			case 'L':
				{
					out.append('L');
					char o = ';';
					int end = type.indexOf(';', pos);
					if ((l > 0) & end > l) {
						end = l;
						o = '<';
						l = type.indexOf('<', l + 1);
					}
					final String obf = type.substring(pos + 1, end);
					out.append(getClass(obf)).append(o);
					pos = end + 1;
				}
				continue;
			default:
				if (!generic) break;
				out.append(c);
				pos++;
				continue;
			}
			throw new RuntimeException("Unknown character in descriptor: " + type.charAt(pos) + " (in " + type + ")");
		} while (pos < len);
		return out.toString();
	}

	public String mapMethodDescriptor(String desc) {
		// some basic sanity checks, doesn't ensure it's completely valid though
		if(desc.length() == 0 || desc.charAt(0) != '(' || desc.indexOf(")") < 1)
			throw new IllegalArgumentException("Not a valid method descriptor: " + desc);

		return parseTypes(desc, false, true);
	}

	public String mapTypeDescriptor(String in) {
		if (in.length() == 0)
			throw new IllegalArgumentException("Not a valid type descriptor: " + in);
		return parseTypes(in, false, false);
	}
}
