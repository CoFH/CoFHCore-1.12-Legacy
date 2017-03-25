package cofh.core.render;

import cofh.core.item.IFOVUpdateItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class RenderEventHandler {

	public static final RenderEventHandler instance = new RenderEventHandler();

	@SubscribeEvent
	public void handleFOVUpdateEvent(FOVUpdateEvent event) {

		ItemStack stack = event.getEntity().getActiveItemStack();

		if (stack != null && stack.getItem() instanceof IFOVUpdateItem) {
			event.setNewfov(event.getFov() - ((IFOVUpdateItem) stack.getItem()).getFOVMod(stack, event.getEntity()));
		}
	}

}
