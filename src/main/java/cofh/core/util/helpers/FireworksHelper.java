package cofh.core.util.helpers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains helper functions to assist with working with fireworks.
 *
 * @author Tonius
 */
public final class FireworksHelper {

	private FireworksHelper() {

	}

	/**
	 * Represents a single explosion that a firework rocket can contain.
	 *
	 * @author Tonius
	 */
	public static final class Explosion {

		/**
		 * The different shapes that an explosion can have.
		 *
		 * @author Tonius
		 */
		public enum Type {
			/**
			 * Small ball (default)
			 */
			BALL, /**
			 * Large ball (made with Fire Charge)
			 */
			LARGE_BALL, /**
			 * Star-shaped (made with Gold Nugget)
			 */
			STAR, /**
			 * Creeper face (made with any Head)
			 */
			CREEPER, /**
			 * Burst (made with Feather)
			 */
			BURST
		}

		/**
		 * Generates a randomized Explosion instance.
		 *
		 * @param primaryColors The amount of different primary colors that the Explosion will have.
		 * @param fadeColors    The amount of different fade colors that the Explosion will have.
		 * @return A random Explosion instance
		 */
		public static Explosion getRandom(int primaryColors, int fadeColors) {

			primaryColors = MathHelper.clamp(primaryColors, 1, Integer.MAX_VALUE);
			fadeColors = MathHelper.clamp(fadeColors, 1, Integer.MAX_VALUE);

			Explosion e = new Explosion();

			int v;
			switch (v = MathHelper.RANDOM.nextInt(4)) {
				case 2:
				case 0:
					e.setTwinkle(true);
					if (v == 0) {
						break;
					}
				case 1:
					e.setTrail(true);
			}

			e.setType(MathHelper.RANDOM.nextInt(5));

			for (int i = 0; i < primaryColors; i++) {
				Color color = new Color(Color.HSBtoRGB(MathHelper.RANDOM.nextFloat() * 360, MathHelper.RANDOM.nextFloat() * 0.15F + 0.8F, 0.85F));
				e.addPrimaryColor(color.getRed(), color.getGreen(), color.getBlue());
			}

			for (int i = 0; i < fadeColors; i++) {
				Color color = new Color(Color.HSBtoRGB(MathHelper.RANDOM.nextFloat() * 360, MathHelper.RANDOM.nextFloat() * 0.15F + 0.8F, 0.85F));
				e.addFadeColor(color.getRed(), color.getGreen(), color.getBlue());
			}

			return e;
		}

		private boolean twinkle = false;
		private boolean trail = false;
		private List<Integer> primaryColors = new ArrayList<>();
		private List<Integer> fadeColors = new ArrayList<>();
		private Type type = Type.BALL;

		/**
		 * Sets whether the explosion should have the 'twinkle' effect (made with Glowstone Dust).
		 *
		 * @param twinkle Whether to have the 'twinkle' effect.
		 * @return The current Explosion instance.
		 */
		public Explosion setTwinkle(boolean twinkle) {

			this.twinkle = twinkle;
			return this;
		}

		/**
		 * Sets whether the explosion should have the 'trail' effect (made with Diamond).
		 *
		 * @param trail Whether to have the 'trail' effect.
		 * @return The current Explosion instance.
		 */
		public Explosion setTrail(boolean trail) {

			this.trail = trail;
			return this;
		}

		/**
		 * Sets the explosion type using the {@link Type} enum.
		 *
		 * @param type The explosion type to set.
		 * @return The current Explosion instance.
		 */
		public Explosion setType(Type type) {

			this.type = type;
			return this;
		}

		/**
		 * Sets the explosion type using an integer.
		 *
		 * @param type The explosion type as an integer.
		 * @return The current Explosion instance.
		 */
		public Explosion setType(int type) {

			this.setType(Type.values()[MathHelper.clamp(type, 0, Type.values().length - 1)]);
			return this;
		}

		/**
		 * Adds a primary color to the explosion.
		 *
		 * @param red   The RGB red value of the color to add (0 - 255).
		 * @param green The RGB green value of the color to add (0 - 255).
		 * @param blue  The RGB blue value of the color to add (0 - 255).
		 * @return The current Explosion instance.
		 */
		public Explosion addPrimaryColor(int red, int green, int blue) {

			this.primaryColors.add((red << 16) + (green << 8) + blue);
			return this;
		}

		/**
		 * Adds a fade color to the explosion.
		 *
		 * @param red   The RGB red value of the color to add (0 - 255).
		 * @param green The RGB green value of the color to add (0 - 255).
		 * @param blue  The RGB blue value of the color to add (0 - 255).
		 * @return The current Explosion instance.
		 */
		public Explosion addFadeColor(int red, int green, int blue) {

			this.fadeColors.add((red << 16) + (green << 8) + blue);
			return this;
		}

		/**
		 * Converts the Explosion to an {@link NBTTagCompound} for use in creating fireworks {@link ItemStack}s.
		 *
		 * @return An NBTTagCompound representing the Explosion.
		 */
		public NBTTagCompound getTagCompound() {

			NBTTagCompound tag = new NBTTagCompound();

			tag.setBoolean("Flicker", this.twinkle);
			tag.setBoolean("Trail", this.trail);

			tag.setByte("Type", (byte) this.type.ordinal());

			int[] colorArray = new int[this.primaryColors.size()];
			for (int i = 0; i < this.primaryColors.size(); i++) {
				colorArray[i] = this.primaryColors.get(i);
			}
			tag.setIntArray("Colors", colorArray);

			colorArray = new int[this.fadeColors.size()];
			for (int i = 0; i < this.fadeColors.size(); i++) {
				colorArray[i] = this.fadeColors.get(i);
			}
			tag.setIntArray("FadeColors", colorArray);

			return tag;
		}

		/**
		 * Converts the Explosion to a Firework Star {@link ItemStack}.
		 *
		 * @return A Firework Star ItemStack representing the Explosion.
		 */
		public ItemStack getFireworkStarStack() {

			NBTTagCompound tags = new NBTTagCompound();
			NBTTagCompound explosionTag = this.getTagCompound();
			tags.setTag("Explosion", explosionTag);

			ItemStack stack = new ItemStack(Items.FIREWORK_CHARGE);
			stack.setTagCompound(tags);
			return stack;
		}

	}

	/**
	 * Creates a fireworks {@link ItemStack}, with the resulting fireworks having a certain flight duration and a variety of {@link Explosion}s.
	 *
	 * @param flightDuration The flight duration of the fireworks. Possible range is between 0 (inclusive) and 3 (inclusive).
	 * @param explosions     The explosions that will occur when the fireworks detonate.
	 * @return A fireworks ItemStack.
	 */
	public static ItemStack getFireworksStack(int flightDuration, Explosion... explosions) {

		NBTTagCompound tags = new NBTTagCompound();

		NBTTagCompound fireworksTag = new NBTTagCompound();
		NBTTagList explosionsList = new NBTTagList();
		if (explosions != null) {
			for (Explosion e : explosions) {
				if (e == null) {
					continue;
				}
				explosionsList.appendTag(e.getTagCompound());
			}
		}

		fireworksTag.setByte("Flight", (byte) MathHelper.clamp(flightDuration, 0, 3));
		fireworksTag.setTag("Explosions", explosionsList);
		tags.setTag("Fireworks", fireworksTag);

		ItemStack stack = new ItemStack(Items.FIREWORKS);
		stack.setTagCompound(tags);
		return stack;
	}

	/**
	 * Creates a randomized fireworks {@link ItemStack}.
	 *
	 * @param flightDuration The flight duration of the fireworks. Possible range is between 0 (inclusive) and 3 (inclusive).
	 * @param explosions     The amount of {@link Explosion}s that will occur when the fireworks detonate.
	 * @param primaryColors  The amount of different primary colors that each Explosion will have.
	 * @param fadeColors     The amount of different fade colors that each Explosion will have.
	 * @return A randomized fireworks ItemStack.
	 */
	public static ItemStack getRandomFireworks(int flightDuration, int explosions, int primaryColors, int fadeColors) {

		explosions = MathHelper.clamp(explosions, 0, Integer.MAX_VALUE);
		Explosion[] explosionsArray = new Explosion[explosions];
		for (int i = 0; i < explosions; i++) {
			explosionsArray[i] = Explosion.getRandom(primaryColors, fadeColors);
		}

		return getFireworksStack(flightDuration, explosionsArray);
	}

}
