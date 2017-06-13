package cofh.core.gui.client;

import cofh.core.gui.GuiCore;
import cofh.core.gui.GuiTextList;
import cofh.core.gui.container.ContainerFriendsList;
import cofh.core.gui.element.TabInfo;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketSocial;
import cofh.core.network.PacketSocial.PacketTypes;
import cofh.core.util.RegistrySocial;
import cofh.lib.gui.GuiProps;
import cofh.lib.gui.element.ElementButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class GuiFriendList extends GuiCore {

	static final String TEXTURE_PATH = GuiProps.PATH_GUI + "friend_list.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

	static final int TB_HEIGHT = 12;

	GuiTextField tbName;
	GuiTextList taOnlineList;
	public GuiTextList taFriendList;

	ElementButton flUp;
	ElementButton flDown;
	ElementButton olUp;
	ElementButton olDown;
	ElementButton addName;
	ElementButton removeName;

	String tempName;

	int tbNameX = 0;
	int tbNameY = 0;

	int taX = 0;
	int taY = 0;

	int taflX = 0;
	int taflY = 0;

	public GuiFriendList(InventoryPlayer inventory) {

		super(new ContainerFriendsList(inventory), TEXTURE);
		name = "gui.cofh.friendList";
		drawInventory = false;
		ySize = 188;

		generateInfo("tab.cofh.friendList");
	}

	@Override
	public void initGui() {

		super.initGui();
		addTab(new TabInfo(this, myInfo));

		tbNameX = guiLeft + 8 + 4;
		tbNameY = guiTop + 15 + 2;

		taX = guiLeft + 8;
		taY = guiTop + 32;

		taflX = guiLeft + 8;
		taflY = guiTop + 80;

		// Setup Text Box
		String temp = "";
		if (tbName != null) { // Stops GUI resize deleting text.
			temp = tbName.getText();
		}
		tbName = new GuiTextField(0, this.fontRendererObj, tbNameX, tbNameY, 128, TB_HEIGHT);
		tbName.setMaxStringLength(20);
		tbName.setText(temp);
		tbName.setEnableBackgroundDrawing(false);

		// Setup Text Area

		taFriendList = new GuiTextList(this.fontRendererObj, taflX, taflY, 128, 10);
		taFriendList.textLines = RegistrySocial.clientPlayerFriends;
		taFriendList.drawBackground = false;
		taFriendList.drawBorder = false;

		taOnlineList = new GuiTextList(this.fontRendererObj, taX, taY, 128, 4);
		taOnlineList.textLines = getOnlineNames();
		taOnlineList.drawBackground = false;
		taOnlineList.drawBorder = false;

		addName = new ElementButton(this, 139, 13, "SetName", 208, 128, 208, 144, 208, 160, 16, 16, TEXTURE_PATH);
		removeName = new ElementButton(this, 155, 13, "RemoveName", 224, 128, 224, 144, 224, 160, 16, 16, TEXTURE_PATH);

		olUp = new ElementButton(this, 147, 33, "OnlineUp", 208, 64, 208, 80, 208, 96, 16, 16, TEXTURE_PATH);
		olDown = new ElementButton(this, 147, 58, "OnlineDown", 224, 64, 224, 80, 224, 96, 16, 16, TEXTURE_PATH);
		flUp = new ElementButton(this, 147, 87, "FriendsUp", 208, 64, 208, 80, 208, 96, 16, 16, TEXTURE_PATH);
		flDown = new ElementButton(this, 147, 159, "FriendsDown", 224, 64, 224, 80, 224, 96, 16, 16, TEXTURE_PATH);

		addElement(addName);
		addElement(removeName);
		addElement(olUp);
		addElement(olDown);
		addElement(flUp);
		addElement(flDown);

		updateButtons();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		switch (buttonName) {
			case "SetName":
				PacketHandler.sendToServer(new PacketSocial().addByte(PacketTypes.ADD_FRIEND.ordinal()).addString(tbName.getText()));
				break;
			case "RemoveName":
				PacketHandler.sendToServer(new PacketSocial().addByte(PacketTypes.REMOVE_FRIEND.ordinal()).addString(tbName.getText()));
				break;
			case "OnlineUp":
				taOnlineList.scrollDown();
				break;
			case "OnlineDown":
				taOnlineList.scrollUp();
				break;
			case "FriendsUp":
				taFriendList.scrollDown();
				break;
			case "FriendsDown":
				taFriendList.scrollUp();
				break;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		super.drawGuiContainerBackgroundLayer(f, x, y);
		mc.renderEngine.bindTexture(TEXTURE);

		tbName.drawTextBox();
		taOnlineList.drawText();
		taFriendList.drawText();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	public void onGuiClosed() {

		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	public void updateScreen() {

		tbName.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char i, int j) {

		this.tbName.textboxKeyTyped(i, j);
		if (j == 1) { // esc
			this.mc.player.closeScreen();
			return;
		}
		if (this.tbName.isFocused()) {

			if (j == 28) { // enter
				playClickSound(1.0F, 0.7F);
			}
		}
		updateButtons();
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mButton) throws IOException {

		int textAreaX = taFriendList.xPos - guiLeft;
		int textAreaY = taFriendList.yPos - guiTop;
		int onlineX = taOnlineList.xPos - guiLeft;
		int onlineY = taOnlineList.yPos - guiTop;

		if (onlineX <= mouseX && mouseX < onlineX + taOnlineList.width && mouseY >= onlineY && mouseY < onlineY + taOnlineList.height) {
			if (!taOnlineList.mouseClicked(mouseX, mouseY, mButton, onlineY).equalsIgnoreCase(tbName.getText())) {
				tbName.setText(taOnlineList.mouseClicked(mouseX, mouseY, mButton, onlineY));
			}
		} else if (textAreaX <= mouseX && mouseX < textAreaX + taFriendList.width && mouseY >= textAreaY && mouseY < textAreaY + taFriendList.height) {
			if (!taFriendList.mouseClicked(mouseX, mouseY, mButton, textAreaY).equalsIgnoreCase(tbName.getText())) {
				tbName.setText(taFriendList.mouseClicked(mouseX, mouseY, mButton, textAreaY));
			}
		} else if (tbNameX - guiLeft <= mouseX && mouseX < tbNameX - guiLeft + tbName.getWidth() && mouseY >= tbNameY - guiTop && mouseY < tbNameY - guiTop + 12) {
			tbName.setFocused(true);

		} else {
			super.mouseClicked(mX, mY, mButton);
		}
		updateButtons();
	}

	public void updateButtons() {

		if (canScrollUpFriend()) {
			flUp.setActive();
		} else {
			flUp.setDisabled();
		}
		if (canScrollDownFriend()) {
			flDown.setActive();
		} else {
			flDown.setDisabled();
		}
		if (canScrollUpOnline()) {
			olUp.setActive();
		} else {
			olUp.setDisabled();
		}
		if (canScrollDownOnline()) {
			olDown.setActive();
		} else {
			olDown.setDisabled();
		}

		if (RegistrySocial.clientPlayerFriends.contains(tbName.getText().toLowerCase(Locale.US))) {
			addName.setDisabled();
			removeName.setActive();
		} else {
			addName.setActive();
			removeName.setDisabled();
		}

	}

	@Override
	public void handleMouseInput() throws IOException {

		super.handleMouseInput();

		int textAreaX = taFriendList.xPos - guiLeft;
		int textAreaY = taFriendList.yPos - guiTop;
		int onlineX = taOnlineList.xPos - guiLeft;
		int onlineY = taOnlineList.yPos - guiTop;

		if (textAreaX <= mouseX && mouseX < textAreaX + taFriendList.width && mouseY >= textAreaY && mouseY < textAreaY + taFriendList.height) {
			int wheelDir = Mouse.getEventDWheel();

			if (wheelDir < 0) {
				taFriendList.scrollUp();
			}

			if (wheelDir > 0) {
				taFriendList.scrollDown();
			}
		} else if (onlineX <= mouseX && mouseX < onlineX + taOnlineList.width && mouseY >= onlineY && mouseY < onlineY + taOnlineList.height) {
			int wheelDir = Mouse.getEventDWheel();

			if (wheelDir < 0) {
				taOnlineList.scrollUp();
			}

			if (wheelDir > 0) {
				taOnlineList.scrollDown();
			}
		}
		updateButtons();
	}

	private boolean canScrollUpOnline() {

		return taOnlineList.startLine != 0;
	}

	private boolean canScrollDownOnline() {

		return taOnlineList.textLines.size() > taOnlineList.displayLines && taOnlineList.startLine < taOnlineList.textLines.size() - taOnlineList.displayLines;
	}

	private boolean canScrollUpFriend() {

		return taFriendList.startLine != 0;
	}

	private boolean canScrollDownFriend() {

		return taFriendList.textLines.size() > taFriendList.displayLines && taFriendList.startLine < taFriendList.textLines.size() - taFriendList.displayLines;
	}

	public List<String> getOnlineNames() {

		List<String> online = new LinkedList<>();
		for (NetworkPlayerInfo playerInfo : Minecraft.getMinecraft().player.connection.getPlayerInfoMap()) {
			online.add(playerInfo.getGameProfile().getName());
		}
		return online;
	}

}
