package cofh.core.particles;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

public class ParticleRenderer {
	private static ParticleRenderer instance = new ParticleRenderer();

	private ParticleRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	private WeakReference<World> worldRef = new WeakReference<World>(null);

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderParticles(RenderWorldLastEvent event) {
		if (worldRef.get() == null) return;
		if (particles.isEmpty()) return;
		Minecraft mc = Minecraft.getMinecraft();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_FOG);
		float p_78471_1_ = event.partialTicks;

		mc.entityRenderer.enableLightmap(p_78471_1_);

		EntityLivingBase p_78874_1_ = mc.renderViewEntity;

		ParticleBase.interpPosX = p_78874_1_.lastTickPosX + (p_78874_1_.posX - p_78874_1_.lastTickPosX) * (double) event.partialTicks;
		ParticleBase.interpPosY = p_78874_1_.lastTickPosY + (p_78874_1_.posY - p_78874_1_.lastTickPosY) * (double) event.partialTicks;
		ParticleBase.interpPosZ = p_78874_1_.lastTickPosZ + (p_78874_1_.posZ - p_78874_1_.lastTickPosZ) * (double) event.partialTicks;
		ParticleBase.rX = ActiveRenderInfo.rotationX;
		ParticleBase.rZ = ActiveRenderInfo.rotationZ;
		ParticleBase.rYZ = ActiveRenderInfo.rotationYZ;
		ParticleBase.rXY = ActiveRenderInfo.rotationXY;
		ParticleBase.rXZ = ActiveRenderInfo.rotationXZ;

		synchronized (particles) {
			for (Map.Entry<ResourceLocation, LinkedList<ParticleBase>> entry : particles.entrySet()) {
				mc.getTextureManager().bindTexture(entry.getKey());
				LinkedList<ParticleBase> list = entry.getValue();
				if (list.isEmpty()) continue; //should not happen but hey...

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDepthMask(false);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();

				for (final ParticleBase particleBase : list) {
					tessellator.setBrightness(particleBase.brightness(event.partialTicks));
					try {
						particleBase.render(tessellator, event.partialTicks);
					} catch (Throwable throwable) {
						CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
						CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
						crashreportcategory.addCrashSectionCallable("Particle", new Callable() {
							public String call() {
								return particleBase.toString();
							}
						});
						crashreportcategory.addCrashSectionCallable("Particle Type", new Callable() {
							public String call() {
								return particleBase.location.toString();
							}
						});
						throw new ReportedException(crashreport);
					}
				}

				tessellator.draw();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}


		mc.entityRenderer.disableLightmap(p_78471_1_);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_FOG);
	}

	private static final LinkedList<ParticleBase> particles_toAdd = new LinkedList<ParticleBase>();
	private final LinkedHashMap<ResourceLocation, LinkedList<ParticleBase>> particles = new LinkedHashMap<ResourceLocation, LinkedList<ParticleBase>>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void updateParticles(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END || (particles.isEmpty() && particles_toAdd.isEmpty()))
			return;

		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.isGamePaused()) return;

		WorldClient mcWorld = Minecraft.getMinecraft().theWorld;
		if (mcWorld == null) return;

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
					if (!particleBase.advance())
						particleListIterator.remove();
				} catch (Exception err) {
					throw Throwables.propagate(err);
				}
			}

			if (particleBases.isEmpty())
				resourceSectionIterator.remove();
		}

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void unloadParticles(WorldEvent.Unload event) {
		if (!event.world.isRemote) return;

		if (event.world != Minecraft.getMinecraft().theWorld) {
			World world = worldRef.get();
			if (world != null && world != event.world) return;
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
