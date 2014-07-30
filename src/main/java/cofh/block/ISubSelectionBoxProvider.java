package cofh.block;

import cofh.repack.codechicken.lib.raytracer.IndexedCuboid6;

import java.util.List;

public interface ISubSelectionBoxProvider {

	public void addTraceableCuboids(List<IndexedCuboid6> cuboids);

}
