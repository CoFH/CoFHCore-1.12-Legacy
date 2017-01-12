package cofh.asmhooks;

public class HooksCore {

	// { Forge hooks

	/*public static void preGenerateWorld(World world, int chunkX, int chunkZ) {

		MinecraftForge.EVENT_BUS.post(new ModPopulateChunkEvent.Pre(world, chunkX, chunkZ));
	}

	public static void postGenerateWorld(World world, int chunkX, int chunkZ) {

		MinecraftForge.EVENT_BUS.post(new ModPopulateChunkEvent.Post(world, chunkX, chunkZ));
	}

	// }

	// { Vanilla hooks
	public static boolean areItemsEqualHook(ItemStack held, ItemStack lastHeld) {

		if (held.getItem() != lastHeld.getItem()) {
			return false;
		}
		Item item = held.getItem();
		if (item instanceof IEqualityOverrideItem && ((IEqualityOverrideItem) item).isLastHeldItemEqual(held, lastHeld)) {
			return true;
		}
		if (held.isItemStackDamageable() && held.getItemDamage() != lastHeld.getItemDamage()) {
			return false;
		}

		return ItemStack.areItemStackTagsEqual(held, lastHeld);
	}

	public static void stackItems(EntityItem entity) {

		if (!CoFHProps.enableItemStacking) {
			return;
		}

		ItemStack stack = entity.getEntityItem();
		if (stack == null || stack.stackSize >= stack.getMaxStackSize()) {
			return;
		}

		@SuppressWarnings("rawtypes")
		Iterator iterator = entity.worldObj.getEntitiesWithinAABB(EntityItem.class, entity.boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

		while (iterator.hasNext()) {
			entity.combineItems((EntityItem) iterator.next());
		}
	}

	@SuppressWarnings("rawtypes")
	public static List getEntityCollisionBoxes(World world, Entity entity, AxisAlignedBB bb) {

		if (!entity.canBePushed()) {
			List collidingBoundingBoxes = world.collidingBoundingBoxes;
			if (collidingBoundingBoxes == null) {
				collidingBoundingBoxes = world.collidingBoundingBoxes = new ArrayList();
			}
			collidingBoundingBoxes.clear();
			int i = MathHelper.floor(bb.minX);
			int j = MathHelper.floor(bb.maxX + 1.0D);
			int k = MathHelper.floor(bb.minY);
			int l = MathHelper.floor(bb.maxY + 1.0D);
			int i1 = MathHelper.floor(bb.minZ);
			int j1 = MathHelper.floor(bb.maxZ + 1.0D);

			for (int x = i; x < j; ++x) {
				boolean xBound = x >= -30000000 & x < 30000000;
				for (int z = i1; z < j1; ++z) {
					boolean def = xBound & z >= -30000000 & z < 30000000;
					if (!world.blockExists(x, 64, z)) {
						continue;
					}
					if (def) {
						for (int y = k - 1; y < l; ++y) {
							world.getBlock(x, y, z).addCollisionBoxesToList(world, x, y, z, bb, collidingBoundingBoxes, entity);
						}
					} else {
						for (int y = k - 1; y < l; ++y) {
							Blocks.bedrock.addCollisionBoxesToList(world, x, y, z, bb, collidingBoundingBoxes, entity);
						}
					}
				}
			}

			return collidingBoundingBoxes;
		}
		return world.getCollidingBoundingBoxes(entity, bb);
	}

	@SideOnly(Side.CLIENT)
	public static void tickTextures(ITickable obj) {

		if (CoFHProps.enableAnimatedTextures) {
			obj.tick();
		}
	}

	public static boolean paneConnectsTo(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {

		Block block = world.getBlock(x, y, z);
		return block.func_149730_j() || block.getMaterial() == Material.glass || block instanceof BlockPane
				|| world.isSideSolid(x, y, z, dir.getOpposite(), false);
	}
	// }*/

}
