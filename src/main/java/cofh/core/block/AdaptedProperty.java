package cofh.core.block;

import net.minecraft.block.properties.IProperty;
import net.minecraftforge.common.property.Properties;

/**
 * A wrapper around PropertyAdapter that exposes the original property.
 *
 * @author amadornes
 */
public class AdaptedProperty<V extends Comparable<V>> extends Properties.PropertyAdapter<V> {

	private final IProperty<V> parent;

	public AdaptedProperty(IProperty<V> parent) {

		super(parent);
		this.parent = parent;
	}

	public IProperty<V> getActualProperty() {

		return parent;
	}

}
