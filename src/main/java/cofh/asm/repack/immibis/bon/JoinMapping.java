package cofh.asm.repack.immibis.bon;

import java.util.ArrayList;
import java.util.List;

public class JoinMapping extends Mapping {

	private final Mapping a, b;

	public JoinMapping(Mapping a, Mapping b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public String getClass(String in) {
		return b.getClass(a.getClass(in));
	}

	@Override
	public String getField(String clazz, String name, String desc) {
		return b.getField(a.getClass(clazz), a.getField(clazz, name, desc), a.mapTypeDescriptor(desc));
	}

	@Override
	public String getMethod(String clazz, String name, String desc) {
		return b.getMethod(a.getClass(clazz), a.getMethod(clazz, name, desc), a.mapMethodDescriptor(desc));
	}

	@Override
	public List<String> getExceptions(String clazz, String method, String desc) {
		List<String> rv = new ArrayList<String>();
		rv.addAll(a.getExceptions(clazz, method, desc));
		rv.addAll(b.getExceptions(a.getClass(clazz), a.getMethod(clazz, method, desc), a.mapMethodDescriptor(desc)));
		return rv;
	}

	@Override
	public String mapMethodDescriptor(String desc) {
		return b.mapMethodDescriptor(a.mapMethodDescriptor(desc));
	}

	@Override
	public String mapTypeDescriptor(String in) {
		return b.mapTypeDescriptor(a.mapTypeDescriptor(in));
	}

	@Override
	public void addPrefix(String old, String new_) {
		throw new UnsupportedOperationException("Mapping is unmodifiable");
	}

	@Override
	public void setClass(String in, String out) {
		throw new UnsupportedOperationException("Mapping is unmodifiable");
	}

	@Override
	public void setDefaultPackage(String p) {
		throw new UnsupportedOperationException("Mapping is unmodifiable");
	}

	@Override
	public void setExceptions(String clazz, String method, String desc, List<String> exc) {
		throw new UnsupportedOperationException("Mapping is unmodifiable");
	}

	@Override
	public void setField(String clazz, String name, String out) {
		throw new UnsupportedOperationException("Mapping is unmodifiable");
	}

	@Override
	public void setMethod(String clazz, String name, String desc, String out) {
		throw new UnsupportedOperationException("Mapping is unmodifiable");
	}

}
