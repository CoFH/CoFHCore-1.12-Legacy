package cofh.core.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

/**
 * This class contains helper functions related to Damage types that CoFH mods add.
 *
 * @author King Lemming
 */
public class DamageHelper {

	private DamageHelper() {

	}

	/* DAMAGE SOURCES */
	public static final DamageSourcePyrotheum pyrotheum = new DamageSourcePyrotheum();
	public static final DamageSourceCryotheum cryotheum = new DamageSourceCryotheum();
	public static final DamageSourcePetrotheum petrotheum = new DamageSourcePetrotheum();
	public static final DamageSourceMana mana = new DamageSourceMana();
	public static final DamageSourceFlux flux = new DamageSourceFlux();

	/* DAMAGE SOURCE CLASSES */
	public static class DamageSourcePyrotheum extends DamageSource {

		protected DamageSourcePyrotheum() {

			super("pyrotheum");
			this.setDamageBypassesArmor();
			this.isFireDamage();
		}
	}

	public static class DamageSourceCryotheum extends DamageSource {

		protected DamageSourceCryotheum() {

			super("cryotheum");
			this.setDamageBypassesArmor();
		}
	}

	public static class DamageSourcePetrotheum extends DamageSource {

		protected DamageSourcePetrotheum() {

			super("petrotheum");
			this.setDamageBypassesArmor();
		}
	}

	public static class DamageSourceMana extends DamageSource {

		protected DamageSourceMana() {

			super("mana");
			this.setDamageBypassesArmor();
			this.isMagicDamage();
		}
	}

	public static class DamageSourceFlux extends DamageSource {

		protected DamageSourceFlux() {

			super("flux");
			this.setDamageBypassesArmor();
		}
	}

	/* ENTITY DAMAGE SOURCES */
	public static class EntityDamageSourceFlux extends EntityDamageSource {

		public EntityDamageSourceFlux(String type, Entity entity) {

			super(type, entity);
			this.setDamageBypassesArmor();
		}
	}

	/* HELPERS */
	public static DamageSource causePlayerFluxDamage(EntityPlayer entityPlayer) {

		return new EntityDamageSourceFlux("player", entityPlayer);
	}

}
