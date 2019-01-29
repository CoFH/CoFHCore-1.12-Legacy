package cofh.core.inventory;

import gnu.trove.set.hash.THashSet;

import java.util.Set;

public class OreValidator {

	public Set<String> orePrefix = new THashSet<>();
	public Set<String> oreExact = new THashSet<>();
	public Set<String> oreBlacklist = new THashSet<>();

	public boolean validate(String oreName) {

		if (oreExact.contains(oreName)) {
			return true;
		}
		if (oreBlacklist.contains(oreName)) {
			return false;
		}
		for (String prefix : orePrefix) {
			if (oreName.startsWith(prefix) && oreName.length() > prefix.length() && Character.isUpperCase(oreName.codePointAt(prefix.length()))) {
				return true;
			}
		}
		return false;
	}

	public boolean addPrefix(String oreName) {

		return orePrefix.add(oreName);
	}

	public boolean addExact(String oreName) {

		return oreExact.add(oreName);
	}

	public boolean addBlacklist(String oreName) {

		return oreBlacklist.add(oreName);
	}

}
