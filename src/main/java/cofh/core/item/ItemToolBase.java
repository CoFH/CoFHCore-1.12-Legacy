package cofh.core.item;

import cofh.api.item.IMultiModeItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class ItemToolBase extends ItemCoFHBase implements IMultiModeItem {

	public ItemToolBase() {

		super();
		setMaxStackSize(1);
	}

	public ItemToolBase(String modName) {

		super(modName);
		setMaxStackSize(1);
	}

	//	@Override
	//	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
	//
	//		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
	//			tooltip.add(StringHelper.shiftForDetails());
	//		}
	//		if (!StringHelper.isShiftKeyDown()) {
	//			return;
	//		}
	//		addInformationDelegate(stack, player, tooltip, advanced);
	//
	//		if (getNumModes(stack) > 0) {
	//			tooltip.add(StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
	//					+ Keyboard.getKeyName(KeyBindingMultiMode.instance.getKey()) + " " + StringHelper.localize("info.cofh.modeChange") + StringHelper.END);
	//		}
	//	}

	@Override
	public boolean isFull3D() {

		return true;
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        return player.canPlayerEdit(pos.offset(facing), facing, stack) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

	/* IMultiModeItem */
	@Override
	public int getMode(ItemStack stack) {

		return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("Mode");
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("Mode", mode);
		return false;
	}

	@Override
	public boolean incrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public boolean decrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public int getNumModes(ItemStack stack) {

		return 0;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

	}

}
