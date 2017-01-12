package cofh.core.sided;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Use CoFHCore.proxy.runClient() to run code only on a client instance.
 *
 * @author RWTema
 */
public interface IRunnableClient {

	/*
	 * Note: Implementations of this method will ALSO need the @SideOnly annotation to ensure client-only code is removed on the server.
	 */
	@SideOnly (Side.CLIENT)
	public void runClient();
}
