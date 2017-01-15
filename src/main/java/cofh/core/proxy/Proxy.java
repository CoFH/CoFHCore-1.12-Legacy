package cofh.core.proxy;

import cofh.core.CoFHProps;
import cofh.core.chat.PacketIndexedChat;
import cofh.core.key.CoFHKeyHandler;
import cofh.core.key.KeyPacket;
import cofh.core.network.PacketSocial;
import cofh.core.network.PacketTile;
import cofh.core.network.PacketTileInfo;
import cofh.core.sided.IFunctionSided;
import cofh.core.sided.IRunnableClient;
import cofh.core.sided.IRunnableServer;
import cofh.core.util.KeyBindingMultiMode;
import cofh.core.util.oredict.OreDictionaryArbiter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

import java.util.LinkedList;
import java.util.List;

public class Proxy {

	public void preInit() {

	}

	public void registerKeyBinds() {

		CoFHKeyHandler.addServerKeyBind(KeyBindingMultiMode.instance);
		// CoFHKeyHandler.addServerKeyBind(KeyBindingAugments.instance);
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

	@SubscribeEvent
	public void save(Save evt) {

		//        if (evt.getWorld().provider.getDimension() == 0) {
		//            RegistryEnderAttuned.save();
		//        }
	}

	public int getKeyBind(String key) {

		return 0;
	}

	public void addIndexedChatMessage(ITextComponent chat, int index) {

	}

	/* EVENT HANDLERS */
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {

		Entity entity = event.getEntity();
		if (!CoFHProps.enableLivingEntityDeathMessages || entity.worldObj.isRemote || !(entity instanceof EntityLiving) || !event.getEntityLiving().hasCustomName()) {
			return;
		}
		((WorldServer) entity.worldObj).getMinecraftServer().getPlayerList().sendChatMsg(event.getEntityLiving().getCombatTracker().getDeathMessage());
	}

	@SubscribeEvent
	public void onOreRegisterEvent(OreRegisterEvent event) {

		OreDictionaryArbiter.registerOreDictionaryEntry(event.getOre(), event.getName());
	}

	@SubscribeEvent
	public void onSaplingGrowTreeEvent(SaplingGrowTreeEvent event) {

		if (CoFHProps.treeGrowthChance > 1 && event.getWorld().rand.nextInt(CoFHProps.treeGrowthChance) != 0) {
			event.setResult(Result.DENY);
		}
	}

	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

	}

	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	/* SERVER UTILS */
	public boolean isOp(String playerName) {

		MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		playerName = playerName.trim();
		for (String a : theServer.getPlayerList().getOppedPlayerNames()) {
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

	public IThreadListener getThreadListener() {

		return FMLCommonHandler.instance().getMinecraftServerInstance();
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

	public void runServer(IRunnableServer runnable) {

		runnable.runServer();
	}

	public void runClient(IRunnableClient runnable) {

	}

	public <F, T> T apply(IFunctionSided<F, T> function, F input) {

		return function.applyServer(input);
	}
}
