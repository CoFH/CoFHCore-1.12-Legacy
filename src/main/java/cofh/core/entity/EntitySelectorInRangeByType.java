package cofh.core.entity;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public class EntitySelectorInRangeByType implements Predicate<Entity> {

    private final double origX;
    private final double origY;
    private final double origZ;
    private final double distance;
    private final Class<?> types[];

    public EntitySelectorInRangeByType(Entity origin, double distance, Class<?>... types) {

        this(origin.posX, origin.posY, origin.posZ, distance, types);
    }

    public EntitySelectorInRangeByType(double originX, double originY, double originZ, double distance, Class<?>... types) {

        origX = originX;
        origY = originY;
        origZ = originZ;
        this.distance = distance;
        this.types = types;
    }

    @Override
    public boolean apply(@Nullable Entity entity) {
        // Out of range? Not applicable.
        if (entity == null) {
            return false;
        }
        if (entity.getDistanceSq(origX, origY, origZ) > distance * distance) {
            return false;
        }
        // No specific types to check for? Applicable.
        if (types == null) {
            return true;
        }
        // Check types. Applicable if found and assignable...
        for (Class<?> type : types) {
            if (type.isAssignableFrom(entity.getClass())) {
                return true;
            }
        }
        // ...otherwise, not.
        return false;
    }
}
