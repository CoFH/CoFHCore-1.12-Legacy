package cofh.core.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class EntitySelectorInRangeByType implements IEntitySelector {

	private final double origX;
	private final double origY;
	private final double origZ;
	private final double distance;
	private final Class<? extends Entity> types[];

	public EntitySelectorInRangeByType(Entity origin, double distance, Class<? extends Entity>... types) {

		this(origin.posX, origin.posY, origin.posZ, distance, types);
	}

	public EntitySelectorInRangeByType(double originX, double originY, double originZ, double distance, Class<? extends Entity>... types) {

		origX = originX;
		origY = originY;
		origZ = originZ;
		this.distance = distance;
		this.types = types;
	}

	@Override
	public boolean isEntityApplicable(Entity entity) {

		// Out of range? Not applicable.
		if (entity.getDistanceSq(origX, origY, origZ) > distance * distance) {
			return false;
		}
		// No specific types to check for? Applicable.
		if (types == null) {
			return true;
		}
		// Check types. Applicable if found and assignable...
		for (Class<? extends Entity> type : types) {
			if (type.isAssignableFrom(entity.getClass())) {
				return true;
			}
		}
		// ...otherwise, not.
		return false;
	}

}
