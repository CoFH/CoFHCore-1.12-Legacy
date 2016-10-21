package cofh.core.crash;

import com.google.common.collect.HashBiMap;

import java.util.Map;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CrashHelper {

	/*static final int range = 3;
	static final char[] validLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz!£$%^&*()`¬_+\"\\@'{}[]~/|<>,.?:;".toCharArray();
	static final char[] metaLetters = "0123456789ABCDEF".toCharArray();

	public static CrashReport makeDetailedCrashReport(Throwable throwable, String message, Object caller, Object... curState) {

		CrashReport crashReport = CrashReport.makeCrashReport(throwable, message);

		crashReport.makeCategory("Calling Thread").addCrashSectionCallable("Name", new Callable<String>() {

			@Override
			public String call() throws Exception {

				return Thread.currentThread().getName();
			}
		});

		if (caller != null) {
			addCallSection(caller, crashReport, "Calling Object");
		}

		for (int i = 0; i < curState.length; i += 2) {
			Object o = curState[i + 1];
			addCallSection(o, crashReport, "Additional - " + curState[i]);
		}

		return crashReport;
	}

	public static CrashReport addSurroundingDetails(CrashReport report, String sectionName, TileEntity tile) {

		if (tile == null) {
			CrashReportCategory cat = report.makeCategory("Surroundings" + (sectionName == null ? "" : sectionName));
			cat.addCrashSection("Tile?", "Null");
			return report;
		} else {
			return addSurroundingDetails(report, sectionName, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
		}
	}

	public static CrashReport addSurroundingDetails(CrashReport report, String sectionName, final World world, final int x, final int y, final int z) {

		CrashReportCategory cat = report.makeCategory("Surroundings" + (sectionName == null ? "" : sectionName));

		if (world == null) {
			cat.addCrashSection("World", "Null");
			cat.addCrashSection("Pos", x + "," + y + "," + z);
			return report;
		}

		cat.addCrashSectionCallable("Dim", new Callable<String>() {

			@Override
			public String call() throws Exception {

				return String.valueOf(world.provider.dimensionId);
			}
		});

		cat.addCrashSectionCallable("Dim_Name", new Callable<String>() {

			@Override
			public String call() throws Exception {

				return "" + world.provider.getDimensionName();
			}
		});

		cat.addCrashSection("Pos", x + "," + y + "," + z);

		cat.addCrashSectionCallable("NeighbourBlocks", new Callable<String>() {

			@Override
			public String call() throws Exception {

				HashBiMap<Block, String> map = HashBiMap.create();

				map.put(Blocks.air, " ");

				StringBuilder builder = new StringBuilder("\n\n");

				for (int dy = -range; dy <= range; dy++) {

					for (int dx = -range; dx <= range; dx++) {
						builder.append("\t\t");
						if (dx == -range) {
							builder.append("dy = ").append(dy);
						} else {
							builder.append("\t");
						}
						builder.append("[ ");
						for (int dz = -range; dz <= range; dz++) {
							int x2 = x + dx, y2 = y + dy, z2 = z + dz;

							if (world.blockExists(x2, y2, z2)) {
								Block block = world.getBlock(x2, y2, z2);
								builder.append(getNameForObject(block, map));

								int meta = world.getBlockMetadata(x2, y2, z2);
								if (meta < 0 || meta > 15) {
									builder.append('!');
								} else if (meta == 0 && block == Blocks.air) {
									builder.append(' ');
								} else {
									builder.append(metaLetters[meta]);
								}
							} else {
								map.put(null, "X");
								builder.append("XX");
							}

							builder.append(" ");
						}

						builder.append(']').append('\n');
					}
					builder.append('\n');
				}

				for (Map.Entry<Block, String> entry : map.entrySet()) {
					builder.append("\t\t'");
					builder.append(entry.getValue());
					builder.append("': ");
					Block block = entry.getKey();
					if (block == null) {
						builder.append("No Block Present");
					} else {
						builder.append(Block.blockRegistry.getNameForObject(block));
					}
					builder.append('\n');
				}

				return builder.toString();
			}

			public String getNameForObject(Block block, HashBiMap<Block, String> map) {

				String s = map.get(block);
				if (s != null) {
					return s;
				}

				String name = Block.blockRegistry.getNameForObject(block);
				if (name != null && name.length() > 0) {
					int i = name.indexOf(58);
					if (i >= 0) {
						name = name.substring(i + 1, name.length());
					}

					String t = name.substring(0, 1).toUpperCase();
					if (!map.containsValue(t)) {
						map.put(block, t);
						return t;
					}

					for (char c : name.toCharArray()) {
						if (Character.isUpperCase(c)) {
							if (!map.containsValue(String.valueOf(c))) {
								map.put(block, String.valueOf(c));
								return String.valueOf(c);
							}
						}
					}

					for (char c : name.toCharArray()) {
						if (!map.containsValue(String.valueOf(c))) {
							map.put(block, String.valueOf(c));
							return String.valueOf(c);
						}
					}
				}

				for (char c : validLetters) {
					if (!map.containsValue(String.valueOf(c))) {
						map.put(block, String.valueOf(c));
						return String.valueOf(c);
					}
				}

				s = "#" + map.size();
				map.put(block, s);
				return s;
			}
		});

		return report;
	}

	public static void addCallSection(final Object object, CrashReport crashReport, String sectionName) {

		CrashReportCategory category = crashReport.makeCategory(sectionName);
		if (object == null) {
			category.addCrashSection("Null?", "Null");
			return;
		}

		if (object instanceof Enum) {
			category.addCrashSection("Value", object.toString() + " . " + ((Enum<?>) object).ordinal());
			return;
		}

		if (object instanceof Throwable) {
			category.addCrashSectionThrowable("Throwable", (Throwable) object);
			return;
		}

		category.addCrashSectionCallable("Class", new Callable<Object>() {

			@Override
			public Object call() throws Exception {

				return object.getClass().getName();
			}
		});

		category.addCrashSectionCallable("ToString", new Callable<Object>() {

			@Override
			public Object call() throws Exception {

				return object.toString();
			}
		});

		if (object instanceof TileEntity) {
			final TileEntity tile = (TileEntity) object;
			tile.func_145828_a(category);
			category.addCrashSectionCallable("Tile Pos", new Callable<Object>() {

				@Override
				public Object call() throws Exception {

					return String.format("%d,%d,%d", tile.xCoord, tile.yCoord, tile.zCoord);
				}
			});
			category.addCrashSectionCallable("Tile NBT", new Callable<Object>() {

				@Override
				public Object call() throws Exception {

					NBTTagCompound tag = new NBTTagCompound();
					tile.writeToNBT(tag);
					return tag.toString();
				}
			});
		}

	}

	public static void addInventoryContents(CrashReport report, String categoryName, final IInventory inv) {

		CrashReportCategory category = report.makeCategory(categoryName);

		if (inv == null) {
			category.addCrashSection("Null?", "Null");
			return;
		}

		category.addCrashSectionCallable("InventoryContents", new Callable<String>() {

			@Override
			public String call() throws Exception {

				StringBuilder builder = new StringBuilder("\n\n");
				builder.append(inv.toString()).append(" - ").append(inv.getSizeInventory());
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					builder.append(i).append(" - ");
					ItemStack stackInSlot;
					try {
						stackInSlot = inv.getStackInSlot(i);
					} catch (Exception e) {
						builder.append("Errored - ").append(e.toString()).append("\n");
						continue;
					}

					builder.append(stackInSlot == null ? "Null" : stackInSlot.toString()).append("\n");
				}
				builder.append("\n\n");
				return builder.toString();
			}
		});
	}

	public static CrashReport makeDetailedCrashReport(Exception e, String inserting) {

		return makeDetailedCrashReport(e, inserting, null);
	}*/

}
