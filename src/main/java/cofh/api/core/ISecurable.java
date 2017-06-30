package cofh.api.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Implement this interface on Objects which can have access restrictions.
 *
 * @author King Lemming
 */
public interface ISecurable {

	/**
	 * Enum for Access Modes - TeamOnly allows Team access, FriendsOnly is Friends Only, Private is Owner only.
	 *
	 * @author King Lemming
	 */
	enum AccessMode {
		PUBLIC, FRIENDS, TEAM, PRIVATE;

		public boolean isPublic() {

			return this == PUBLIC;
		}

		public boolean isPrivate() {

			return this == PRIVATE;
		}

		public boolean isTeamOnly() {

			return this == TEAM;
		}

		public boolean isFriendsOnly() {

			return this == FRIENDS;
		}

		public static AccessMode stepForward(AccessMode curAccess) {

			return curAccess == PUBLIC ? TEAM : curAccess == TEAM ? FRIENDS : curAccess == FRIENDS ? PRIVATE : PUBLIC;
		}

		public static AccessMode stepBackward(AccessMode curAccess) {

			return curAccess == PUBLIC ? PRIVATE : curAccess == PRIVATE ? FRIENDS : curAccess == FRIENDS ? TEAM : PUBLIC;
		}
	}

	boolean canPlayerAccess(EntityPlayer player);

	boolean setAccess(AccessMode access);

	boolean setOwnerName(String name);

	boolean setOwner(GameProfile name);

	AccessMode getAccess();

	String getOwnerName();

	GameProfile getOwner();

}
