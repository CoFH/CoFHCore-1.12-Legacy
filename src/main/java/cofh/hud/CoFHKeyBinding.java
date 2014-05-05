package cofh.hud;

import net.minecraft.client.settings.KeyBinding;

public class CoFHKeyBinding extends KeyBinding {

	String internalName;

	public CoFHKeyBinding(String localizedName, int keyCode, String category, String internalName) {

		super(localizedName, keyCode, category);
		this.internalName = internalName;
	}

	public String getName() {

		return internalName;
	}

}
