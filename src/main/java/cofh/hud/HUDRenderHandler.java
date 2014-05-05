package cofh.hud;

import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import org.apache.logging.log4j.spi.AbstractLogger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cofh.CoFHCore;
import cofh.core.ProxyClient;
import cofh.render.RenderHelper;
import cofh.render.RenderItemUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class HUDRenderHandler {

	public static final HUDRenderHandler instance = new HUDRenderHandler();

	public static LinkedList<IStringRender> stringsToRender = new LinkedList<IStringRender>();
	public static LinkedList<IStringRender> stringsToRenderToAdd = new LinkedList<IStringRender>();
	public static LinkedList<IStringRender> stringsToRenderToRemove = new LinkedList<IStringRender>();

	private static LinkedList<IStringRender> stringsRendering = new LinkedList<IStringRender>();

	int tickCounter = 0;

	@SubscribeEvent
	public void tickEnd(ClientTickEvent evt) {

		if (evt.phase != Phase.END) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		for (IHUDModule curModule : CoFHHUD.modules) {
			curModule.clientTick(mc);
		}
		tickCounter++;
		tickCounter %= 4;
		if (tickCounter == 0) {
			if (!stringsToRenderToAdd.isEmpty()) {
				synchronized (stringsToRenderToAdd) {
					stringsToRender.addAll(stringsToRenderToAdd);
					stringsToRenderToAdd.clear();
				}
			}
			if (!stringsToRender.isEmpty()) {
				if (stringsToRender.get(0).getTicks() < 10) {
					for (int a = 0; a < stringsToRender.size(); a++) {
						stringsToRender.get(a).setTicks((byte) (stringsToRender.get(a).getTicks() + 1));
					}
				} else {
					for (int i = 0; i < stringsToRender.size(); i++) {
						stringsToRender.get(i).setTicks((byte) (stringsToRender.get(i).getTicks() + 1));
						stringsToRender.get(i).setY(stringsToRender.get(i).getY() - 1);
					}
				}
			}
			if (!stringsToRenderToRemove.isEmpty()) {
				synchronized (stringsToRenderToRemove) {
					stringsToRender.removeAll(stringsToRenderToRemove);
				}
			}
		}
	}

	@SubscribeEvent
	public void tickEnd(RenderTickEvent evt) {

		try {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.ingameGUI != null && mc.currentScreen == null && mc.theWorld != null) {

				ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
				int scaledWidth = scaledRes.getScaledWidth();
				int scaledHeight = scaledRes.getScaledHeight();

				for (IHUDModule curModule : CoFHHUD.modules) {
					curModule.renderHUD(mc, scaledHeight, scaledWidth);
				}
				stringsRendering = (LinkedList<IStringRender>) stringsToRender.clone();
				if (!stringsRendering.isEmpty()) {
					int xPos = 0;
					int yPos = 0;
					IStringRender curString;
					float toOffset = stringsRendering.get(0).getTicks() < 10 ? 0 : (.25F * tickCounter);
					for (int i = 0; i < stringsRendering.size(); i++) {
						curString = stringsRendering.get(i);
						if (curString.getY() > scaledHeight / 2 - 5) {
							break;
						}
						RenderHelper.setDefaultFontTextureSheet();
						String toShow = curString.getString();

						GL11.glPushMatrix();
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glScalef(0.5F, 0.5F, 0.5F);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

						xPos = scaledWidth - (curString.renderIcon() || curString.renderStack() ? 17 : 3) - mc.fontRenderer.getStringWidth(toShow) / 2;
						yPos = (int) (scaledHeight - 10 - curString.getY() + 3 + toOffset);
						ProxyClient.fontRenderer.drawStringWithShadow(toShow, xPos * 2, yPos * 2, curString.getStringColor());

						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();

						if (curString.renderStack()) {
							GL11.glEnable(GL12.GL_RESCALE_NORMAL);

							net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

							xPos = scaledWidth - 15;
							yPos = (int) (scaledHeight - 10 - curString.getY() + toOffset);
							RenderItemUtils.renderItemStackAtScale(xPos * 1.33F, yPos * 1.33F, 1, curString.getStackToRender(), mc, 0.75F, false);

							net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
							GL11.glDisable(GL12.GL_RESCALE_NORMAL);
						}
					}
				}
			}
		} catch (Throwable t) {
			CoFHCore.log.error(AbstractLogger.CATCHING_MARKER, "An exception occured in CoFH HUD but was handled.", t);
		}
	}

	public int getNextY() {

		if (stringsToRender.isEmpty()) {
			return 2;
		}
		return stringsToRender.get(stringsToRender.size() - 1).getY() + 14;
	}

	public void resetString(int index, IStringRender replacement) {

		if (index > stringsToRender.size() - 1) {
			return;
		}
		int toRemove = 14;
		toRemove = stringsToRender.get(index).getY() < 2 ? stringsToRender.get(index).getY() + toRemove - 2 : toRemove;
		stringsToRenderToRemove.add(stringsToRender.get(index));

		for (int i = index; i < stringsToRender.size(); i++) {
			stringsToRender.get(i).setY(stringsToRender.get(i).getY() - toRemove);
		}
		replacement.setY(getNextY());
		stringsToRenderToAdd.add(replacement);
		replacement.setTicks((byte) 0);
	}

}
