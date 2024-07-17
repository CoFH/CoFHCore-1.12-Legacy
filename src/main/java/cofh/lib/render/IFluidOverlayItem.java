package cofh.lib.render;

/**
 * Simple fluid containers that implement this can be assigned the FactoryFluidOverlayItem. The mask for the fluid is assumed to be for render pass 1, with the
 * base icon render pass 0. {@link getRenderPasses(int)} is called to see if the item needs an overlay (return 2)
 *
 * @author skyboy
 */
public interface IFluidOverlayItem {

}
