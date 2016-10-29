package cofh.core.gui.element;

import cofh.CoFHCore;
import cofh.api.tileentity.ISecurable;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.UUID;

public class TabSecurity extends TabBase {

    public static boolean enable;
    public static int defaultSide = 1;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x000000;
    public static int defaultBackgroundColor = 0x888888;

    // public static int defaultBackgroundColor = 0xe66a10;

    public static void initialize() {

        String category = "Tab.Security";
        // enable = CoFHCore.configClient.get(category, "Enable", true);
        defaultSide = MathHelper.clamp(CoFHCore.configClient.get(category, "Side", defaultSide), 0, 1);
        defaultHeaderColor = MathHelper.clamp(CoFHCore.configClient.get(category, "ColorHeader", defaultHeaderColor), 0, 0xffffff);
        defaultSubHeaderColor = MathHelper.clamp(CoFHCore.configClient.get(category, "ColorSubHeader", defaultSubHeaderColor), 0, 0xffffff);
        defaultTextColor = MathHelper.clamp(CoFHCore.configClient.get(category, "ColorText", defaultTextColor), 0, 0xffffff);
        defaultBackgroundColor = MathHelper.clamp(CoFHCore.configClient.get(category, "ColorBackground", defaultBackgroundColor), 0, 0xffffff);
        CoFHCore.configClient.save();
    }

    ISecurable myContainer;
    UUID myPlayer;

    public TabSecurity(GuiBase gui, ISecurable container, UUID playerName) {

        this(gui, defaultSide, container, playerName);
    }

    public TabSecurity(GuiBase gui, int side, ISecurable container, UUID playerName) {

        super(gui, side);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 112;
        myContainer = container;
        myPlayer = playerName;
    }

    @Override
    public void addTooltip(List<String> list) {

        if (!isFullyOpened()) {
            list.add(StringHelper.localize("info.cofh.owner") + ": " + myContainer.getOwnerName());
            return;
        }
        int x = gui.getMouseX() - currentShiftX;
        int y = gui.getMouseY() - currentShiftY;
        if (28 <= x && x < 44 && 20 <= y && y < 36) {
            list.add(StringHelper.localize("info.cofh.accessPublic"));
        } else if (48 <= x && x < 64 && 20 <= y && y < 36) {
            list.add(StringHelper.localize("info.cofh.accessRestricted"));
        } else if (68 <= x && x < 84 && 20 <= y && y < 36) {
            list.add(StringHelper.localize("info.cofh.accessPrivate"));
        }
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

        if (!myPlayer.equals(myContainer.getOwner().getId())) {
            return true;
        }
        if (!isFullyOpened()) {
            return false;
        }
        if (side == LEFT) {
            mouseX += currentWidth;
        }
        mouseX -= currentShiftX;
        mouseY -= currentShiftY;

        if (mouseX < 24 || mouseX >= 88 || mouseY < 16 || mouseY >= 40) {
            return false;
        }
        if (28 <= mouseX && mouseX < 44 && 20 <= mouseY && mouseY < 36) {
            if (!myContainer.getAccess().isPublic()) {
                myContainer.setAccess(ISecurable.AccessMode.PUBLIC);
                GuiBase.playClickSound(1.0F, 0.4F);
            }
        } else if (48 <= mouseX && mouseX < 64 && 20 <= mouseY && mouseY < 36) {
            if (!myContainer.getAccess().isRestricted()) {
                myContainer.setAccess(ISecurable.AccessMode.RESTRICTED);
                GuiBase.playClickSound(1.0F, 0.6F);
            }
        } else if (68 <= mouseX && mouseX < 84 && 20 <= mouseY && mouseY < 36) {
            if (!myContainer.getAccess().isPrivate()) {
                myContainer.setAccess(ISecurable.AccessMode.PRIVATE);
                GuiBase.playClickSound(1.0F, 0.8F);
            }
        }
        return true;
    }

    @Override
    protected void drawBackground() {

        super.drawBackground();

        if (!isFullyOpened()) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        GlStateManager.color(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(posX() + 24, posY + 16, 16, 20, 64, 24);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void drawForeground() {

        if (myContainer.getAccess().isPublic()) {
            drawTabIcon("IconAccessPublic");
        } else if (myContainer.getAccess().isRestricted()) {
            drawTabIcon("IconAccessFriends");
        } else if (myContainer.getAccess().isPrivate()) {
            drawTabIcon("IconAccessPrivate");
        }
        if (!isFullyOpened()) {
            return;
        }
        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.security"), posXOffset() + 18, posY + 6, headerColor);
        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.accessMode") + ":", posXOffset() + 6, posY + 42, subheaderColor);

        if (myContainer.getAccess().isPublic()) {
            gui.drawButton("IconAccessPublic", posX() + 28, posY + 20, 1);
            gui.drawButton("IconAccessFriends", posX() + 48, posY + 20, 1);
            gui.drawButton("IconAccessPrivate", posX() + 68, posY + 20, 1);
            getFontRenderer().drawString(StringHelper.localize("info.cofh.accessPublic"), posXOffset() + 14, posY + 54, textColor);
        } else if (myContainer.getAccess().isRestricted()) {
            gui.drawButton("IconAccessPublic", posX() + 28, posY + 20, 1);
            gui.drawButton("IconAccessFriends", posX() + 48, posY + 20, 1);
            gui.drawButton("IconAccessPrivate", posX() + 68, posY + 20, 1);
            getFontRenderer().drawString(StringHelper.localize("info.cofh.accessRestricted"), posXOffset() + 14, posY + 54, textColor);
        } else if (myContainer.getAccess().isPrivate()) {
            gui.drawButton("IconAccessPublic", posX() + 28, posY + 20, 1);
            gui.drawButton("IconAccessFriends", posX() + 48, posY + 20, 1);
            gui.drawButton("IconAccessPrivate", posX() + 68, posY + 20, 1);
            getFontRenderer().drawString(StringHelper.localize("info.cofh.accessPrivate"), posXOffset() + 14, posY + 54, textColor);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setFullyOpen() {

        if (!myPlayer.equals(myContainer.getOwner().getId())) {
            return;
        }
        super.setFullyOpen();
    }

}
