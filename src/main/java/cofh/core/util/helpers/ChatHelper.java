package cofh.core.util.helpers;

import cofh.CoFHCore;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketIndexedChat;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

public class ChatHelper {

	public static final int cofhTempChatIndexServer = -661464083; // Random integer
	public static final int cofhTempChatIndexClient = -1245781222; // Random integer
	public static final boolean indexChat;

	static {
		String category = "gui.chat";
		CoFHCore.CONFIG_CLIENT.getCategory(category).setComment("The options in this section change core Minecraft behavior and are not limited to CoFH mods.");

		String comment = "Set to false to disable outdated CoFH info chat messages being removed from chat";

		indexChat = CoFHCore.CONFIG_CLIENT.get(category, "RemoveOutdatedChat", true, comment);
	}

	public static ITextComponent getChatComponent(Object object) {

		if (object instanceof ITextComponent) {
			return (ITextComponent) object;
		} else if (object instanceof String) {
			return new TextComponentString((String) object);
		} else if (object instanceof ItemStack) {
			return ((ItemStack) object).getTextComponent();
		} else if (object instanceof StatBase) {
			return ((StatBase) object).getStatName();
		} else if (object instanceof Entity) {
			return ((Entity) object).getDisplayName();
		} else if (object instanceof ICommandSender) {
			return ((ICommandSender) object).getDisplayName();
		} else {
			return new TextComponentString(String.valueOf(object));
		}
	}

	public static ITextComponent formChatComponent(Object... chats) {

		ITextComponent chat = getChatComponent(chats[0]);

		for (int i = 1, chatsLength = chats.length; i < chatsLength; i++) {
			chat.appendSibling(getChatComponent(chats[i]));
		}

		return chat;
	}

	public static ITextComponent translate(String key) {

		return new TextComponentTranslation(key);
	}

	public static String toJSON(ITextComponent chatComponent) {

		return ITextComponent.Serializer.componentToJson(chatComponent);
	}

	public static ITextComponent fromJSON(String string) {

		return ITextComponent.Serializer.fromJsonLenient(string);
	}

	public static void sendIndexedChatMessageToPlayer(EntityPlayer player, ITextComponent message) {

		if (player.world == null || player instanceof FakePlayer) {
			return;
		}
		if (indexChat) {
			if (!player.world.isRemote) {
				PacketHandler.sendTo(new PacketIndexedChat(message, cofhTempChatIndexServer), player);
			} else {
				CoFHCore.proxy.addIndexedChatMessage(message, cofhTempChatIndexClient);
			}
		} else {

			player.sendMessage(message);
		}
	}

	public static void sendIndexedChatMessagesToPlayer(EntityPlayer player, List<ITextComponent> messages) {

		if (player.world == null || player instanceof FakePlayer) {
			return;
		}
		if (indexChat) {
			for (int i = 0; i < messages.size(); i++) {
				if (!player.world.isRemote) {
					PacketHandler.sendTo(new PacketIndexedChat(messages.get(i), cofhTempChatIndexServer + i), player);
				} else {
					CoFHCore.proxy.addIndexedChatMessage(messages.get(i), cofhTempChatIndexClient + i);
				}
			}
		} else {
			for (ITextComponent message : messages) {
				player.sendMessage(message);
			}
		}
	}

}
