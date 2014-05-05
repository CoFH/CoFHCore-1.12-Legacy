package cofh.asm;

import cofh.masquerade.RegistryCapes;
import cofh.masquerade.RegistrySkins;

public class HooksMasquerade {

	public static String getCapeUrl(String username) {

		return RegistryCapes.getPlayerCape(username);
	}

	public static String getSkinUrl(String username) {

		return RegistrySkins.getPlayerSkin(username);
	}

}
