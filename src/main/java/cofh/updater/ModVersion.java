package cofh.updater;

import cpw.mods.fml.common.versioning.ArtifactVersion;

public class ModVersion implements ArtifactVersion {

	private final String _label;
	private final ReleaseVersion _mcVer;
	private final ReleaseVersion _modVer;
	private final String _desc;

	public ReleaseVersion minecraftVersion() {

		return _mcVer;
	}

	public ReleaseVersion modVersion() {

		return _modVer;
	}

	public String description() {

		return _desc;
	}

	public ModVersion(String label, ReleaseVersion minecraftVersion, ReleaseVersion modVersion, String description) {

		_label = label;
		_mcVer = minecraftVersion;
		_modVer = modVersion;
		_desc = description;
	}

	public ModVersion(String label, String s) {

		String[] parts = s.split(" ", 2);
		String desc = "";

		if (parts.length > 1) {
			desc = parts[1].trim();
		}
		parts = parts[0].split("R", 2);

		_label = label;
		_mcVer = new ReleaseVersion("Minecraft", parts[0]);
		_modVer = new ReleaseVersion(label, parts[1]);
		_desc = desc;
	}

	public static ModVersion parse(String label, String s) {

		if (s == null || s.length() == 0) {
			return null;
		}
		return new ModVersion(label, s);
	}

	@Override
	public int compareTo(ArtifactVersion o) {

		if (o instanceof ModVersion) {
			return compareTo((ModVersion) o);
		}
		if (o instanceof ReleaseVersion) {
			ReleaseVersion r = (ReleaseVersion) o;
			if (_label.equals(r.getLabel())) {
				return _modVer.compareTo(r);
			} else if ("Minecraft".equals(r.getLabel())) {
				return _mcVer.compareTo(r);
			}
		}
		return 0;
	}

	public int compareTo(ModVersion o) {

		if (_mcVer.compareTo(o.minecraftVersion()) != 0) {
			return _mcVer.compareTo(o.minecraftVersion());
		}
		return _modVer.compareTo(o.modVersion());
	}

	@Override
	public String toString() {

		return _modVer.toString() + " for " + _mcVer.toString();
	}

	@Override
	public String getVersionString() {

		return _mcVer.getVersionString() + "R" + _modVer.getVersionString();
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
