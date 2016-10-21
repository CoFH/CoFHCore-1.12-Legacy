package cofh.core.sided;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Use CoFHCore.proxy.runServer() to run code only on a server.
 *
 * @author RWTema
 */
public interface IRunnableServer {

    /*
     * Note: Implementations of this method will ALSO need the @SideOnly annotation to ensure server-only code is removed on the client.
     */
    @SideOnly(Side.SERVER)
    public void runServer();
}
