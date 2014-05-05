package cofh.hud.modules;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cofh.core.CoFHProps;
import cofh.hud.CoFHHUD;
import cofh.hud.HUDRenderHandler;
import cofh.hud.IHUDModule;
import cofh.hud.IStringRender;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

public class HUDModuleItemPickup implements IHUDModule {

	public static final HUDModuleItemPickup instance = new HUDModuleItemPickup();
	public static int moduleID;

	public static class ItemPickup implements IStringRender {

		public ItemStack theItem;
		int y;
		byte ticks = 0;
		static int myColor = new Color(255, 255, 255, 255).getRGB();
		protected String displayText = "";

		public ItemPickup(ItemStack theItem, int y) {

			this.theItem = theItem;
			addStackSize(0);
			this.y = y;
		}

		@Override
		public void setY(int y) {

			this.y = y;
			if (y < -14) {
				HUDRenderHandler.stringsToRenderToRemove.add(this);
			}
		}

		@Override
		public int getY() {

			return y;
		}

		@Override
		public String getString() {

			return displayText;

		}

		@Override
		public boolean renderStack() {

			return true;
		}

		@Override
		public ItemStack getStackToRender() {

			return theItem;
		}

		@Override
		public boolean renderIcon() {

			return false;
		}

		@Override
		public IIcon getIconToRender() {

			return null;
		}

		@Override
		public int getStringColor() {

			return myColor;
		}

		@Override
		public int getModuleID() {

			return moduleID;
		}

		public void addStackSize(int stackSize) {

			theItem.stackSize += stackSize;
			int maxStackSize = theItem.getMaxStackSize();

			if (!StringHelper.displayStackCount || theItem.stackSize < maxStackSize || maxStackSize == 1) {
				displayText = theItem.stackSize + "x" + theItem.getDisplayName();
			} else {
				if (theItem.stackSize % maxStackSize != 0) {
					displayText = maxStackSize + "x" + theItem.stackSize / maxStackSize + "+" + theItem.stackSize % maxStackSize + " "
							+ theItem.getDisplayName();
				} else {
					displayText = maxStackSize + "x" + theItem.stackSize / maxStackSize + " " + theItem.getDisplayName();
				}
			}
		}

		@Override
		public byte getTicks() {

			return ticks;
		}

		@Override
		public void setTicks(byte toSet) {

			ticks = toSet;
		}
	}

	public static void initialize() {

		if (CoFHProps.enableItemPickupModule) {
			MinecraftForge.EVENT_BUS.register(instance);
			moduleID = CoFHHUD.registerHUDModule(null);
		}
	}

	ItemPickup anItem;
	ItemStack anStack;
	int stackSize = 0;

	@Override
	public void renderHUD(Minecraft mc, int a, int b) {

	}

	@Override
	public void clientTick(Minecraft mc) {

	}

	@SubscribeEvent
	public void EventItemPickup(EntityItemPickupEvent theEvent) {

		theEvent.item.getEntityData().setInteger("ss", theEvent.item.getEntityItem().stackSize);
	}

	@Override
	public void setModuleID(int i) {

		moduleID = i;
	}

	@SubscribeEvent
	public void notifyPickup(ItemPickupEvent evt) {

		EntityItem item = evt.pickedUp;
		anStack = item.getEntityItem().copy();
		anStack.stackSize = item.getEntityData().getInteger("ss") - anStack.stackSize;
		if (anStack.stackSize > 0) {
			int index = 0;
			for (int i = 0; i < HUDRenderHandler.stringsToRender.size(); i++) {
				if (HUDRenderHandler.stringsToRender.get(i).getModuleID() == moduleID) {
					anItem = (ItemPickup) HUDRenderHandler.stringsToRender.get(i);
					if (ItemHelper.itemsEqualWithMetadata(anItem.theItem, anStack, true)) {
						anItem.addStackSize(anStack.stackSize);
						HUDRenderHandler.instance.resetString(index, HUDRenderHandler.stringsToRender.get(i));
						return;
					}
				}
				index++;
			}
			HUDRenderHandler.stringsToRenderToAdd.add(new ItemPickup(anStack, HUDRenderHandler.instance.getNextY()));
		}
	}

}
