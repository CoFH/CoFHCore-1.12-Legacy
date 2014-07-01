package cofh.gui.client;

import cofh.gui.GuiBaseAdv;
import cofh.gui.GuiProps;
import cofh.gui.GuiTextList;
import cofh.gui.container.ContainerFriendsList;
import cofh.gui.element.ElementButton;
import cofh.gui.element.TabInfo;
import cofh.network.PacketHandler;
import cofh.social.RegistryFriends;
import cofh.social.SocialPacket;
import cofh.social.SocialPacket.Type;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiFriendsList extends GuiBaseAdv {

	static final String TEXTURE_PATH = GuiProps.PATH_GUI + "FriendsList.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
	static final String INFO = "Add/Remove Friends!\n\nFriends not provided.\n\nMom/Dad does not count as a friend.";

	static final int TB_HEIGHT = 12;

	GuiTextField tbName;
	GuiTextList taOnlineList;
	public GuiTextList taFriendsList;

	ElementButton flUp;
	ElementButton flDown;
	ElementButton olUp;
	ElementButton olDown;
	ElementButton addName;
	ElementButton removeName;

	String tempName = "";

	int tbNameX = 0;
	int tbNameY = 0;

	int taX = 0;
	int taY = 0;

	int taflX = 0;
	int taflY = 0;

	public GuiFriendsList(InventoryPlayer inventory) {

		super(new ContainerFriendsList(inventory), TEXTURE);
		name = "info.cofh.friendsList";
		drawInventory = false;
		ySize = 188;
	}

	@Override
	public void initGui() {

		super.initGui();
		addTab(new TabInfo(this, INFO));

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
		tbName = new GuiTextField(this.fontRendererObj, tbNameX, tbNameY, 128, TB_HEIGHT);
		tbName.setMaxStringLength(20);
		tbName.setText(temp);
		tbName.setEnableBackgroundDrawing(false);

		// Setup Text Area

		taFriendsList = new GuiTextList(this.fontRendererObj, taflX, taflY, 128, 10);
		taFriendsList.textLines = RegistryFriends.clientPlayerFriends;
		taFriendsList.drawBackground = false;
		taFriendsList.drawBorder = false;

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

		if (buttonName.equals("SetName")) {
			PacketHandler.sendToServer(new SocialPacket().addByte(Type.ADD_FRIEND.ordinal()).addString(tbName.getText()));
		} else if (buttonName.equals("RemoveName")) {
			PacketHandler.sendToServer(new SocialPacket().addByte(Type.REMOVE_FRIEND.ordinal()).addString(tbName.getText()));
		} else if (buttonName.equals("OnlineUp")) {
			taOnlineList.scrollDown();
		} else if (buttonName.equals("OnlineDown")) {
			taOnlineList.scrollUp();
		} else if (buttonName.equals("FriendsUp")) {
			taFriendsList.scrollDown();
		} else if (buttonName.equals("FriendsDown")) {
			taFriendsList.scrollUp();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		super.drawGuiContainerBackgroundLayer(f, x, y);
		mc.renderEngine.bindTexture(TEXTURE);

		tbName.drawTextBox();
		taOnlineList.drawText();
		taFriendsList.drawText();
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
			this.mc.thePlayer.closeScreen();
			return;
		}
		if (this.tbName.isFocused()) {

			if (j == 28) { // enter
				playSound("random.click", 1.0F, 0.7F);
			}
		}
		updateButtons();
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mButton) {

		int textAreaX = taFriendsList.xPos - guiLeft;
		int textAreaY = taFriendsList.yPos - guiTop;
		int onlineX = taOnlineList.xPos - guiLeft;
		int onlineY = taOnlineList.yPos - guiTop;

		if (onlineX <= mouseX && mouseX < onlineX + taOnlineList.width && mouseY >= onlineY && mouseY < onlineY + taOnlineList.height) {
			if (!taOnlineList.mouseClicked(mouseX, mouseY, mButton, onlineY).equalsIgnoreCase(tbName.getText())) {
				tbName.setText(taOnlineList.mouseClicked(mouseX, mouseY, mButton, onlineY));
			}
		} else if (textAreaX <= mouseX && mouseX < textAreaX + taFriendsList.width && mouseY >= textAreaY && mouseY < textAreaY + taFriendsList.height) {
			if (!taFriendsList.mouseClicked(mouseX, mouseY, mButton, textAreaY).equalsIgnoreCase(tbName.getText())) {
				tbName.setText(taFriendsList.mouseClicked(mouseX, mouseY, mButton, textAreaY));
			}
		} else if (tbNameX - guiLeft <= mouseX && mouseX < tbNameX - guiLeft + tbName.getWidth() && mouseY >= tbNameY - guiTop
				&& mouseY < tbNameY - guiTop + 12) {
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

		if (RegistryFriends.clientPlayerFriends.contains(tbName.getText().toLowerCase())) {
			addName.setDisabled();
			removeName.setActive();
		} else {
			addName.setActive();
			removeName.setDisabled();
		}

	}

	@Override
	public void handleMouseInput() {

		super.handleMouseInput();

		int textAreaX = taFriendsList.xPos - guiLeft;
		int textAreaY = taFriendsList.yPos - guiTop;
		int onlineX = taOnlineList.xPos - guiLeft;
		int onlineY = taOnlineList.yPos - guiTop;

		if (textAreaX <= mouseX && mouseX < textAreaX + taFriendsList.width && mouseY >= textAreaY && mouseY < textAreaY + taFriendsList.height) {
			int wheelDir = Mouse.getEventDWheel();

			if (wheelDir < 0) {
				taFriendsList.scrollUp();
			}

			if (wheelDir > 0) {
				taFriendsList.scrollDown();
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

		return taFriendsList.startLine != 0;
	}

	private boolean canScrollDownFriend() {

		return taFriendsList.textLines.size() > taFriendsList.displayLines
				&& taFriendsList.startLine < taFriendsList.textLines.size() - taFriendsList.displayLines;
	}

	public List<String> getOnlineNames() {

		List<String> online = new LinkedList<String>();
		for (Object curObj : Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoList) {
			online.add(((GuiPlayerInfo) curObj).name);
		}
		return online;
	}

}
