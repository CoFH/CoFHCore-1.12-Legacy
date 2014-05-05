package cofh.updater;

public class ReleaseVersion implements Comparable<ReleaseVersion> {

	private int _major;
	private int _minor;
	private int _patch;
	private int _rc;
	private int _beta;

	public ReleaseVersion(int major, int minor, int patch) {

		this(major, minor, patch, 0, 0);
	}

	public ReleaseVersion(int major, int minor, int patch, int rc, int beta) {

		_major = major;
		_minor = minor;
		_patch = patch;
		_rc = rc;
		_beta = beta;
	}

	public static ReleaseVersion parse(String s) {

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
		major = Integer.parseInt(parts[0]);
		minor = Integer.parseInt(parts[1]);
		patch = Integer.parseInt(parts[2]);

		return new ReleaseVersion(major, minor, patch, rc, beta);
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

		String v = _major + "." + _minor + "." + _patch;
		if (_rc != 0) {
			v += "RC" + _rc;
		}
		if (_beta != 0) {
			v += "B" + _beta;
		}
		return v;
	}
}
