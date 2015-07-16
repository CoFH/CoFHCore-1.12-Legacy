package cofh.core.chat;

import cofh.CoFHCore;
import cofh.core.network.PacketHandler;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;

public class ChatHelper {

	public static final int cofhTempChatIndexServer = -661464083; // Random integer
	public static final int cofhTempChatIndexClient = -1245781222; // Random integer
	public static final boolean indexChat;

	static {
		String category = "gui.chat";
		CoFHCore.configClient.getCategory(category).setComment("The options in this section change core Minecraft behavior and are not limited to CoFH mods.");

		String comment = "Set to false to disable outdated CoFH info chat messages being removed from chat";

		indexChat = CoFHCore.configClient.get(category, "RemoveOutdatedChat", true, comment);
	}

	public static IChatComponent getChatComponent(Object object) {

		if (object instanceof IChatComponent) {
			return (IChatComponent) object;
		} else if (object instanceof String) {
			return new ChatComponentText((String) object);
		} else if (object instanceof ItemStack) {
			return ((ItemStack) object).func_151000_E();
		} else if (object instanceof StatBase) {
			return ((StatBase) object).func_150951_e();
		} else if (object instanceof Entity) {
			return ((Entity) object).func_145748_c_();
		} else if (object instanceof ICommandSender) {
			return ((ICommandSender) object).func_145748_c_();
		} else {
			return new ChatComponentText(String.valueOf(object));
		}
	}

	public static IChatComponent formChatComponent(Object... chats) {

		IChatComponent chat = getChatComponent(chats[0]);

		for (int i = 1, chatsLength = chats.length; i < chatsLength; i++) {
			chat.appendSibling(getChatComponent(chats[i]));
		}

		return chat;
	}

	public static IChatComponent translate(String key) {

		return new ChatComponentTranslation(key);
	}

	public static String toJSON(IChatComponent chatComponent) {

		return IChatComponent.Serializer.func_150696_a(chatComponent);
	}

	public static IChatComponent fromJSON(String string) {

		return IChatComponent.Serializer.func_150699_a(string);
	}

	public static void sendIndexedChatMessageToPlayer(EntityPlayer player, IChatComponent message) {

		if (player.worldObj == null || player instanceof FakePlayer) {
			return;
		}

		if (indexChat) {
			if (!player.worldObj.isRemote) {
				PacketHandler.sendTo(new PacketIndexedChat(message, cofhTempChatIndexServer), player);
			} else {
				CoFHCore.proxy.addIndexedChatMessage(message, cofhTempChatIndexClient);
			}
		} else {

			player.addChatComponentMessage(message);
		}
	}

	public static void sendIndexedChatMessagesToPlayer(EntityPlayer player, List<IChatComponent> messages) {

		if (player.worldObj == null || player instanceof FakePlayer) {
			return;
		}

		if (indexChat) {
			for (int i = 0; i < messages.size(); i++) {
				if (!player.worldObj.isRemote) {
					PacketHandler.sendTo(new PacketIndexedChat(messages.get(i), cofhTempChatIndexServer + i), player);
				} else {
					CoFHCore.proxy.addIndexedChatMessage(messages.get(i), cofhTempChatIndexClient + i);
				}
			}
		} else {
			for (int i = 0; i < messages.size(); i++) {
				player.addChatComponentMessage(messages.get(i));
			}
		}
	}

}
