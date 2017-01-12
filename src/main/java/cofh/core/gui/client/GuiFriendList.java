package cofh.core.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.container.ContainerFriendsList;
import cofh.core.gui.element.TabInfo;
import cofh.lib.gui.GuiProps;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementListBox;
import cofh.lib.gui.element.ElementSlider;
import cofh.lib.gui.element.ElementTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiFriendList extends GuiBaseAdv {

	static final String TEXTURE_PATH = GuiProps.PATH_GUI + "friends_list.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

	static final int TB_HEIGHT = 12;

	ElementListBox box_online;
	ElementListBox box_friends;

	ElementSlider slider_online;
	ElementSlider slider_friends;

	ElementTextField tf_name;

	ElementButton add;
	ElementButton remove;

	public GuiFriendList(InventoryPlayer inventory) {

		super(new ContainerFriendsList(inventory), TEXTURE);
		name = "info.cofh.friendsList";
		drawInventory = false;
		ySize = 188;

		generateInfo("tab.cofh.friend", 2);
	}

	@Override
	public void initGui() {

		super.initGui();

		Keyboard.enableRepeatEvents(true);

		addTab(new TabInfo(this, myInfo));

	}

	@Override
	public void onGuiClosed() {

		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	protected void updateElementInformation() {

	}

}
