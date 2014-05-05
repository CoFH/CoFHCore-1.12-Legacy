package cofh.hud;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public interface IStringRender {

	int getStringColor();

	public byte getTicks();

	public void setTicks(byte toSet);

	void setY(int y);

	int getY();

	String getString();

	boolean renderStack();

	ItemStack getStackToRender();

	boolean renderIcon();

	IIcon getIconToRender();

	int getModuleID();

}
