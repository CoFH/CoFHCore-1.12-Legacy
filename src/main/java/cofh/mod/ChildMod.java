package cofh.mod;

import net.minecraftforge.fml.common.Mod;

public @interface ChildMod {

    /**
     * The {@link Mod} instance defining this ChildMod
     */
    Mod[] mod();

    /**
     * The parent {@link Mod}
     */
    String parent();
}
