package cofh.core.particles;

import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

public class ParticleRenderer {

	private static ParticleRenderer instance = new ParticleRenderer();

	private ParticleRenderer() {

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	private WeakReference<World> worldRef = new WeakReference<World>(null);

	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void renderParticles(RenderWorldLastEvent event) {

		if (worldRef.get() == null) {
			return;
		}
		if (particles.isEmpty()) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();

		GlStateManager.enableBlend();
		GlStateManager.enableFog();
		float p_78471_1_ = event.getPartialTicks();

		mc.entityRenderer.enableLightmap();

		Entity p_78874_1_ = mc.getRenderViewEntity();

		ParticleBase.interpPosX = p_78874_1_.lastTickPosX + (p_78874_1_.posX - p_78874_1_.lastTickPosX) * (double) event.getPartialTicks();
		ParticleBase.interpPosY = p_78874_1_.lastTickPosY + (p_78874_1_.posY - p_78874_1_.lastTickPosY) * (double) event.getPartialTicks();
		ParticleBase.interpPosZ = p_78874_1_.lastTickPosZ + (p_78874_1_.posZ - p_78874_1_.lastTickPosZ) * (double) event.getPartialTicks();
		ParticleBase.rX = ActiveRenderInfo.getRotationX();
		ParticleBase.rZ = ActiveRenderInfo.getRotationZ();
		ParticleBase.rYZ = ActiveRenderInfo.getRotationYZ();
		ParticleBase.rXY = ActiveRenderInfo.getRotationXY();
		ParticleBase.rXZ = ActiveRenderInfo.getRotationXZ();

		synchronized (particles) {
			for (Map.Entry<ResourceLocation, LinkedList<ParticleBase>> entry : particles.entrySet()) {
				mc.getTextureManager().bindTexture(entry.getKey());
				LinkedList<ParticleBase> list = entry.getValue();
				if (list.isEmpty()) {
					continue; //should not happen but hey...
				}

				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer buffer = tessellator.getBuffer();
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

				for (final ParticleBase particleBase : list) {
					//tessellator.setBrightness(particleBase.brightness(event.getPartialTicks()));
					try {
						particleBase.render(buffer, event.getPartialTicks());
					} catch (Throwable throwable) {
						CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
						CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
						crashreportcategory.addCrashSection("Particle", new Callable() {
							public String call() {

								return particleBase.toString();
							}
						});
						crashreportcategory.addCrashSection("Particle Type", new Callable() {
							public String call() {

								return particleBase.location.toString();
							}
						});
						throw new ReportedException(crashreport);
					}
				}

				tessellator.draw();
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}

		mc.entityRenderer.disableLightmap();

		GlStateManager.disableBlend();
		GlStateManager.disableFog();
	}

	private static final LinkedList<ParticleBase> particles_toAdd = new LinkedList<ParticleBase>();
	private final LinkedHashMap<ResourceLocation, LinkedList<ParticleBase>> particles = new LinkedHashMap<ResourceLocation, LinkedList<ParticleBase>>();

	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void updateParticles(TickEvent.ClientTickEvent event) {

		if (event.phase == TickEvent.Phase.END || (particles.isEmpty() && particles_toAdd.isEmpty())) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.isGamePaused()) {
			return;
		}

		WorldClient mcWorld = Minecraft.getMinecraft().theWorld;
		if (mcWorld == null) {
			return;
		}

		World world = worldRef.get();
		if (world == null || mcWorld != world) {
			clearParticles();
			worldRef = new WeakReference<World>(mcWorld);
		}

		ParticleBase newParticle;
		while ((newParticle = particles_toAdd.poll()) != null) {
			LinkedList<ParticleBase> list = particles.get(newParticle.location);
			if (list == null) {
				list = new LinkedList<ParticleBase>();
				particles.put(newParticle.location, list);
			}

			while (list.size() > 4000) {
				list.poll();
			}

			list.add(newParticle);
		}

		for (Iterator<Map.Entry<ResourceLocation, LinkedList<ParticleBase>>> resourceSectionIterator = particles.entrySet().iterator(); resourceSectionIterator.hasNext(); ) {
			LinkedList<ParticleBase> particleBases = resourceSectionIterator.next().getValue();

			for (Iterator<ParticleBase> particleListIterator = particleBases.iterator(); particleListIterator.hasNext(); ) {
				ParticleBase particleBase = particleListIterator.next();
				try {
					if (!particleBase.advance()) {
						particleListIterator.remove();
					}
				} catch (Exception err) {
					throw Throwables.propagate(err);
				}
			}

			if (particleBases.isEmpty()) {
				resourceSectionIterator.remove();
			}
		}

	}

	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void unloadParticles(WorldEvent.Unload event) {

		if (!event.getWorld().isRemote) {
			return;
		}

		if (event.getWorld() != Minecraft.getMinecraft().theWorld) {
			World world = worldRef.get();
			if (world != null && world != event.getWorld()) {
				return;
			}
		}

		clearParticles();
		worldRef = new WeakReference<World>(null);
	}

	public void clearParticles() {

		particles.clear();
		particles_toAdd.clear();
	}

	public static void spawnParticle(ParticleBase particleBase) {

		particles_toAdd.add(particleBase);
	}
}
