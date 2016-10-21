package cofh.core.sided;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Use CoFHCore.proxy.apply() to run the appropriate code on client/server.
 *
 * @author RWTema
 */
public interface IFunctionSided<F, T> {

    /*
     * Note: Implementations of this method will ALSO need the @SideOnly annotation to ensure client-only code is removed on the server.
     */
    @SideOnly(Side.CLIENT)
    T applyClient(F input);

    /*
     * Note: Implementations of this method will ALSO need the @SideOnly annotation to ensure server-only code is removed on the client.
     */
    @SideOnly(Side.SERVER)
    T applyServer(F input);
}
