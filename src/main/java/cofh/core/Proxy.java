package cofh.core;

import cofh.core.chat.PacketIndexedChat;
import cofh.core.key.CoFHKeyHandler;
import cofh.core.key.KeyPacket;
import cofh.core.network.PacketSocial;
import cofh.core.network.PacketTile;
import cofh.core.network.PacketTileInfo;
import cofh.core.util.KeyBindingEmpower;
import cofh.core.util.KeyBindingMultiMode;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class Proxy {

	public void preInit() {

	}

	public void registerKeyBinds() {

		CoFHKeyHandler.addServerKeyBind(KeyBindingEmpower.instance);
		CoFHKeyHandler.addServerKeyBind(KeyBindingMultiMode.instance);
	}

	public void registerPacketInformation() {

		PacketIndexedChat.initialize();
		PacketSocial.initialize();
		KeyPacket.initialize();
		PacketTileInfo.initialize();
		PacketTile.initialize();
	}

	public void registerRenderInformation() {

	}

	public void registerTickHandlers() {

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.TERRAIN_GEN_BUS.register(this);
	}

	public int getKeyBind(String key) {

		return 0;
	}

	public void addIndexedChatMessage(IChatComponent chat, int index) {

	}

	/* EVENT HANDLERS */
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {

		if (!CoFHProps.enableLivingEntityDeathMessages || event.entity.worldObj.isRemote || !(event.entity instanceof EntityLiving)
				|| !((EntityLiving) event.entityLiving).hasCustomNameTag()) {
			return;
		}
		((WorldServer) event.entity.worldObj).func_73046_m().getConfigurationManager().sendChatMsg(event.entityLiving.func_110142_aN().func_151521_b());
	}

	@SubscribeEvent
	public void onOreRegisterEvent(OreRegisterEvent event) {

		OreDictionaryArbiter.registerOreDictionaryEntry(event.Ore, event.Name);
	}

	@SubscribeEvent
	public void onSaplingGrowTreeEvent(SaplingGrowTreeEvent event) {

		if (CoFHProps.treeGrowthChance > 1 && event.world.rand.nextInt(CoFHProps.treeGrowthChance) != 0) {
			event.setResult(Result.DENY);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	/* SERVER UTILS */
	public boolean isOp(String playerName) {

		MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		playerName = playerName.trim();
		for (String a : theServer.getConfigurationManager().func_152606_n()) {
			if (playerName.equalsIgnoreCase(a)) {
				return true; // TODO: this is completely horrible. needs improvement. will probably still be horrible.
			}
		}
		return false;
	}

	public boolean isClient() {

		return false;
	}

	public boolean isServer() {

		return true;
	}

	public World getClientWorld() {

		return null;
	}

	/* PLAYER UTILS */
	public EntityPlayer findPlayer(String player) {

		return null;
	}

	public EntityPlayer getClientPlayer() {

		return null;
	}

	public List<EntityPlayer> getPlayerList() {

		List<EntityPlayer> result = new LinkedList<EntityPlayer>();
		for (int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().worldServers.length; i++) {
			if (FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[i] != null) {
				result.addAll(FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[i].playerEntities);
			}
		}
		return result;
	}

	public void updateFriendListGui() {

	}

	/* SOUND UTILS */
	public float getSoundVolume(int category) {

		return 0;
	}

}
