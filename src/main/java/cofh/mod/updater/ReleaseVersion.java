package cofh.mod.updater;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class ReleaseVersion implements ArtifactVersion {

	private final String _label;
	private final int _major;
	private final int _minor;
	private final int _patch;
	private final int _rc;
	private final int _beta;

	public ReleaseVersion(String label, int major, int minor, int patch) {

		this(label, major, minor, patch, 0, 0);
	}

	public ReleaseVersion(String label, int major, int minor, int patch, int rc, int beta) {

		_label = label;
		_major = major;
		_minor = minor;
		_patch = patch;
		_rc = rc;
		_beta = beta;
	}

	public ReleaseVersion(String label, String s) {

		int major = 0;
		int minor = 0;
		int patch = 0;
		int rc = 0;
		int beta = 0;
		String main = s;
		String[] parts;

		parts = main.split("RC");
		if (parts.length > 1) {
			rc = Integer.parseInt(parts[1]);
			main = parts[0];
		}
		parts = main.split("B");
		if (parts.length > 1) {
			beta = Integer.parseInt(parts[1]);
			main = parts[0];
		}
		parts = main.split("\\.");
		switch (parts.length) {

			default:
			case 3:
				patch = Integer.parseInt(parts[2]);
			case 2:
				minor = Integer.parseInt(parts[1]);
			case 1:
				major = Integer.parseInt(parts[0]);
			case 0:
				break;
		}

		_label = label;
		_major = major;
		_minor = minor;
		_patch = patch;
		_rc = rc;
		_beta = beta;
	}

	public static ReleaseVersion parse(String label, String s) {

		return new ReleaseVersion(label, s);
	}

	public int major() {

		return _major;
	}

	public int minor() {

		return _minor;
	}

	public int patch() {

		return _patch;
	}

	public int rc() {

		return _rc;
	}

	public int beta() {

		return _beta;
	}

	public boolean isStable() {

		return _rc == 0 & _beta == 0;
	}

	public boolean isRC() {

		return _rc > 0;
	}

	public boolean isBeta() {

		return _beta > 0;
	}

	@Override
	public int compareTo(ArtifactVersion o) {

		if (o instanceof ReleaseVersion) {
			return compareTo((ReleaseVersion) o);
		}
		if (o instanceof ModVersion) {
			ModVersion r = (ModVersion) o;
			if (_label.equals(r.getLabel())) {
				return compareTo(r.modVersion());
			} else if ("Minecraft".equals(_label)) {
				return compareTo(r.minecraftVersion());
			}
		}
		return 0;
	}

	public int compareTo(ReleaseVersion arg0) {

		if (this.major() != arg0.major()) {
			return this.major() < arg0.major() ? -1 : 1;
		}
		if (this.minor() != arg0.minor()) {
			return this.minor() < arg0.minor() ? -1 : 1;
		}
		if (this.patch() != arg0.patch()) {
			return this.patch() < arg0.patch() ? -1 : 1;
		}

		if (this.isStable() && !arg0.isStable()) {
			return 1;
		}
		if (this.isRC() && arg0.isBeta()) {
			return 1;
		}
		if (!this.isStable() && arg0.isStable()) {
			return -1;
		}
		if (this.isBeta() && arg0.isRC()) {
			return -1;
		}

		if (this.rc() != arg0.rc()) {
			return this.rc() < arg0.rc() ? -1 : 1;
		}
		if (this.beta() != arg0.beta()) {
			return this.beta() < arg0.beta() ? -1 : 1;
		}
		return 0;
	}

	@Override
	public String toString() {

		return _label + " " + getVersionString();
	}

	@Override
	public String getVersionString() {

		String v = _major + "." + _minor + "." + _patch;
		if (_rc != 0) {
			v += "RC" + _rc;
		}
		if (_beta != 0) {
			v += "B" + _beta;
		}
		return v;
	}

	@Override
	public String getLabel() {

		return _label;
	}

	@Override
	public boolean containsVersion(ArtifactVersion source) {

		return compareTo(source) == 0;
	}

	@Override
	public String getRangeString() {

		return null;
	}

}
