package cofh.core.render.customcharrendering;

import cofh.core.ProxyClient;
import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.SecurityHelper;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class RenderPlayerFace implements ICustomCharRenderer {

	static GameProfile profile;

	// Cache the Texture data for the Game Profiles
	private static final HashMap<GameProfile, GameProfile> textureCache = new HashMap<GameProfile, GameProfile>();

	public final static char CHAR_FACE = '\u0378';

	public static char init(CoFHFontRenderer render) {

		RenderPlayerFace renderPlayerFace = new RenderPlayerFace();
		render.renderOverrides.put(CHAR_FACE, renderPlayerFace);
		return CHAR_FACE;
	}

	public static GameProfile loadTextures(GameProfile profile) {

		if (profile == null) {
			return null;
		}

		if (profile.isComplete()) {
			GameProfile newProfile = textureCache.get(profile);
			if (newProfile != null) {
				return newProfile;
			}

			if (!profile.getProperties().containsKey("textures")) {
				Property property = (Property) Iterables.getFirst(profile.getProperties().get("textures"), (Object) null);

				if (property == null) {
					profile = Minecraft.getMinecraft().getSessionService().fillProfileProperties(profile, true);
					textureCache.put(profile, profile);
				}
			}
		}
		return profile;
	}

	public static CoFHFontRenderer loadProfile(ItemStack item) {

		GameProfile profile = SecurityHelper.getOwner(item);
		if (profile == SecurityHelper.UNKNOWN_GAME_PROFILE) {
			profile = null;
		}

		return setProfile(profile);
	}

	private static CoFHFontRenderer setProfile(GameProfile gameProfile) {

		profile = gameProfile != null ? loadTextures(gameProfile) : null;
		return ProxyClient.fontRenderer;
	}

	@Override
	public float renderChar(char letter, boolean italicFlag, float x, float y, CoFHFontRenderer fontRenderer) {

		ResourceLocation resourcelocation = new ResourceLocation("textures/entity/steve.png");

		if (profile != null) {
			Minecraft minecraft = Minecraft.getMinecraft();
			Map map = minecraft.getSkinManager().loadSkinFromCache(profile);

			if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				resourcelocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture) map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
			}
		}

		fontRenderer.bindTexture(resourcelocation);

		GL11.glColor4f(1, 1, 1, 1);

		float italicOffset = italicFlag ? 1.0F : 0.0F;

		float innerFace = 0F;// 0.5F / 9F * 8F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(8F / 64F, 8F / 32F);
		GL11.glVertex3f(x + innerFace + italicOffset, y + innerFace, 0.0F);
		GL11.glTexCoord2f(8F / 64F, (8F + 8F) / 32F);
		GL11.glVertex3f(x + innerFace - italicOffset, y + 7.99F - innerFace, 0.0F);
		GL11.glTexCoord2f((8F + 8F) / 64F, 8F / 32F);
		GL11.glVertex3f(x + 7.99F - innerFace + italicOffset, y + innerFace, 0.0F);
		GL11.glTexCoord2f((8F + 8F) / 64F, (8F + 8F) / 32F);
		GL11.glVertex3f(x + 7.99F - innerFace - italicOffset, y + 7.99F - innerFace, 0.0F);
		GL11.glEnd();

		float hatZ = 0.01F;
		float outerFace = 0.5F / 9F * 8F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(40F / 64F, 8F / 32F);
		GL11.glVertex3f(x + italicOffset - outerFace, y - outerFace, hatZ);
		GL11.glTexCoord2f(40F / 64F, (8F + 8F) / 32F);
		GL11.glVertex3f(x - italicOffset - outerFace, y + 7.99F + outerFace, hatZ);
		GL11.glTexCoord2f((40F + 8F) / 64F, 8F / 32F);
		GL11.glVertex3f(x + 7.99F + italicOffset + outerFace, y - outerFace, hatZ);
		GL11.glTexCoord2f((40F + 8F) / 64F, (8F + 8F) / 32F);
		GL11.glVertex3f(x + 7.99F - italicOffset + outerFace, y + 7.99F + outerFace, hatZ);
		GL11.glEnd();

		fontRenderer.resetColor();

		return 8.02F;
	}

	@Override
	public int getCharWidth(char letter, CoFHFontRenderer fontRenderer) {

		return 8;
	}

}
