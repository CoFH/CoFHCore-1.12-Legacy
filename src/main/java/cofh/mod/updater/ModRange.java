package cofh.mod.updater;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.Restriction;
import net.minecraftforge.fml.common.versioning.VersionRange;

import java.util.ArrayList;
import java.util.List;

public class ModRange {

    public static VersionRange createFromVersionSpec(String label, String spec) throws InvalidVersionSpecificationException {

        if (spec == null) {
            return null;
        }

        List<Restriction> restrictions = new ArrayList<Restriction>();
        String process = spec;
        ArtifactVersion version = null;
        ArtifactVersion upperBound = null;
        ArtifactVersion lowerBound = null;

        while (process.startsWith("[") || process.startsWith("(")) {

            int index1 = process.indexOf(')');
            int index2 = process.indexOf(']');

            int index = index2;
            if (((index2 < 0) | index1 < index2) & index1 >= 0) {
                index = index1;
            }

            if (index < 0) {
                throw new InvalidVersionSpecificationException("Unbounded range: " + spec);
            }

            Restriction restriction = parseRestriction(label, process.substring(0, index + 1));
            if (lowerBound == null) {
                lowerBound = restriction.getLowerBound();
            }
            if (upperBound != null) {
                if (restriction.getLowerBound() == null || restriction.getLowerBound().compareTo(upperBound) < 0) {
                    throw new InvalidVersionSpecificationException("Ranges overlap: " + spec);
                }
            }
            restrictions.add(restriction);
            upperBound = restriction.getUpperBound();

            process = process.substring(index + 1).trim();

            if (process.length() > 0 && process.startsWith(",")) {
                process = process.substring(1).trim();
            }
        }

        if (process.length() > 0) {
            if (restrictions.size() > 0) {
                throw new InvalidVersionSpecificationException("Only fully-qualified sets allowed in multiple set scenario: " + spec);
            } else {
                version = getArtifactVersion(label, process);
                restrictions.add(Restriction.EVERYTHING);
            }
        }

        try {
            return VersionRange.newRange(version, restrictions);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Restriction parseRestriction(String label, String spec) throws InvalidVersionSpecificationException {

        boolean lowerBoundInclusive = spec.startsWith("[");
        boolean upperBoundInclusive = spec.endsWith("]");

        String process = spec.substring(1, spec.length() - 1).trim();

        Restriction restriction;

        int index = process.indexOf(',');

        if (index < 0) {
            if (!lowerBoundInclusive | !upperBoundInclusive) {
                throw new InvalidVersionSpecificationException("Single version must be surrounded by []: " + spec);
            }

            ArtifactVersion version = getArtifactVersion(label, process);

            restriction = new Restriction(version, lowerBoundInclusive, version, upperBoundInclusive);
        } else {
            String lowerBound = process.substring(0, index).trim();
            String upperBound = process.substring(index + 1).trim();
            if (lowerBound.equals(upperBound)) {
                throw new InvalidVersionSpecificationException("Range cannot have identical boundaries: " + spec);
            }

            ArtifactVersion lowerVersion = null;
            if (lowerBound.length() > 0) {
                lowerVersion = getArtifactVersion(label, lowerBound);
            }
            ArtifactVersion upperVersion = null;
            if (upperBound.length() > 0) {
                upperVersion = getArtifactVersion(label, upperBound);
            }

            if (upperVersion != null & lowerVersion != null && upperVersion.compareTo(lowerVersion) < 0) {
                throw new InvalidVersionSpecificationException("Range defies version ordering: " + spec);
            }

            restriction = new Restriction(lowerVersion, lowerBoundInclusive, upperVersion, upperBoundInclusive);
        }

        return restriction;
    }

    private static ArtifactVersion getArtifactVersion(String label, String process) {

        int index = process.indexOf('R');
        if (index > 0 && process.indexOf('C') != index + 1) {
            return new ModVersion(label, process);
        } else {
            return new ReleaseVersion(label, process);
        }
    }

}
