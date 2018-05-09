package cofh.core.util.helpers;

import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BaublesHelper {

	@CapabilityInject (IBaublesItemHandler.class)
	public static Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

	public static Iterable<ItemStack> getBaubles(Entity entity) {

		if (CAPABILITY_BAUBLES == null) {
			return Collections.emptyList();
		}
		IBaublesItemHandler handler = entity.getCapability(CAPABILITY_BAUBLES, null);

		if (handler == null) {
			return Collections.emptyList();
		}
		return IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
	}

}
