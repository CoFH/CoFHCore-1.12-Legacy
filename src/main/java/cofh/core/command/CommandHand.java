package cofh.core.command;

import cofh.core.RegistrySocial;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class CommandHand implements ISubCommand {

	public static ISubCommand instance = new CommandHand();

	@Override
	public String getCommandName() {

		return "hand";
	}

	@Override
	public int getPermissionLevel() {

		return 0;
	}

	private static HashMap<String, InfoType> infoMap = new HashMap<>();
	static {
		InfoType.values();
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		int l = args.length;
		int i = 0;
		EntityPlayerMP player = null;
		ItemStack itemstack = null;

		switch (l) {
		case 0:
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
		default:
		case 2:
		case 3:
			try {
				player = CommandBase.getPlayer(sender, args[++i]);
			} catch (CommandException t) {
				if (!infoMap.containsKey(args[i])) {
					throw t;
				}
				--i;
			}
			if (player != null && player != sender && !CommandHandler.canUseCommand(sender, 1, getCommandName()) &&
					!RegistrySocial.playerHasAccess(sender.getCommandSenderName(), player.getGameProfile())) {
				throw new CommandException("commands.generic.permission");
			}
		case 1:
			if (player == null) {
				player = CommandBase.getCommandSenderAsPlayer(sender);
			}
			itemstack = player.getCurrentEquippedItem();
		}

		if (itemstack == null) {
			sender.addChatMessage(new ChatComponentTranslation("commands.enchant.noItem"));
			return;
		}

		ArrayList<InfoType> list = new ArrayList<>();

		if (++i == l) {
			list.add(InfoType.Name);
		} else
			for (; i < l; ++i) {
				InfoType type = infoMap.get(args[i].toLowerCase());
				if (type == null) {
					sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
					throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
				}
				list.add(type);
			}

		for (InfoType type : list) {
			IChatComponent[] data = type.getData(itemstack);
			IChatComponent msg = new ChatComponentText(type.name() + ": ");
			if (data.length >= 1 && data[0] != null) {
				msg.appendSibling(data[0]);
			}
			sender.addChatMessage(msg);
			for (i = 1; i < data.length; ++i) {
				sender.addChatMessage(data[i]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, infoMap.keySet());
	}

	private static enum InfoType {
		Name("generic") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				IChatComponent component = new ChatComponentText("");
				component.appendSibling(stack.func_151000_E());
				if (stack.hasDisplayName()) {
					String s = stack.getItem().getUnlocalizedName(stack);
					if (!StatCollector.canTranslate(s)) {
						s += ".name";
					}
					IChatComponent component2 = new ChatComponentTranslation(s);
					component2.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(s)));
					component.appendText(" (").appendSibling(component2).appendText(")");
				}
				return component;
			}
		},
		Id {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				return new ChatComponentText(Item.itemRegistry.getNameForObject(stack.getItem()));
			}
		},
		Size("amount", "count") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				return new ChatComponentText(String.valueOf(stack.stackSize));
			}
		},
		Metadata("damage", "alt") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				return new ChatComponentText(String.valueOf(ItemHelper.getItemDamage(stack)));
			}
		},
		toString("string", "text") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				return new ChatComponentText(stack.toString());
			}
		},
		Action("use") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				EnumAction action = stack.getItemUseAction();
				IChatComponent component = new ChatComponentText(action.name());
				if (action != EnumAction.none) {
					component.appendText(" | Duration: " + stack.getMaxItemUseDuration());
				}
				return component;
			}
		},
		Lore("flavorText") {

			@Override
			public IChatComponent[] getData(ItemStack stack) {

				LinkedList<IChatComponent> ret = new LinkedList<>();
				ret.add(null);

				if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("display", 10)) {
					NBTTagCompound nbttagcompound = stack.stackTagCompound.getCompoundTag("display");

					if (nbttagcompound.func_150299_b("Lore") == 9) {
						NBTTagList nbttaglist1 = nbttagcompound.getTagList("Lore", 8);

						if (nbttaglist1.tagCount() > 0) {
							for (int j = 0; j < nbttaglist1.tagCount(); ++j) {
								ret.add(new ChatComponentText(StringHelper.PURPLE + "      " + StringHelper.ITALIC + nbttaglist1.getStringTagAt(j)));
							}
						}
					}
				} else {
					ret.set(0, new ChatComponentText("none"));
				}
				return ret.toArray(new IChatComponent[ret.size()]);
			}
		},
		Enchants("enchant", "ench") {

			@Override
			public IChatComponent[] getData(ItemStack stack) {

				NBTTagList nbttaglist = stack.getEnchantmentTagList();

				LinkedList<IChatComponent> ret = new LinkedList<>();
				ret.add(null);
				if (nbttaglist != null && nbttaglist.tagCount() > 0) {
					int i = 0;
					for (; i < nbttaglist.tagCount(); ++i) {
						short short1 = nbttaglist.getCompoundTagAt(i).getShort("id");
						short short2 = nbttaglist.getCompoundTagAt(i).getShort("lvl");

						if (Enchantment.enchantmentsList[short1] != null) {
							ret.add(new ChatComponentText(StringHelper.TEAL + "     " +
									StatCollector.translateToLocal(Enchantment.enchantmentsList[short1].getName()) +
									" " + StringHelper.toNumerals(short2)));
						} else {
							ret.add(new ChatComponentText(StringHelper.RED + "     " + String.format("Invalid{id=%s,lvl=%s}", short1, short2)));
						}
					}
				} else {
					ret.set(0, new ChatComponentText("none"));
				}
				return ret.toArray(new IChatComponent[ret.size()]);
			}
		},
		NBT("tag", "stackTag", "compoundTag") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				return new ChatComponentText(Objects.toString(stack.getTagCompound(), "null"));
			}
		},
		OreDict("oreNames", "oreName", "ores", "ore") {

			@Override
			public IChatComponent processStack(ItemStack stack) {

				ArrayList<String> ores = OreDictionaryArbiter.getAllOreNames(stack);
				int size = ores == null ? 0 : ores.size();
				String arr = Objects.toString(ores, "[null]");
				return new ChatComponentText(size + "> " + arr.substring(1, arr.length() - 1));
			}
		},
		Modifiers {
		    protected final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

			@Override
			public IChatComponent[] getData(ItemStack stack) {

				LinkedList<IChatComponent> ret = new LinkedList<>();
				ret.add(null);

		        Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers();
		        if (!multimap.isEmpty()) {

		            for (Entry<String, AttributeModifier> entry : multimap.entries()) {
		                AttributeModifier attributemodifier = entry.getValue();
		                double d0 = attributemodifier.getAmount();

		                if (field_111210_e.equals(attributemodifier.getID())) {
		                    d0 += EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED);
		                }

		                double d1;

		                if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2) {
		                    d1 = d0;
		                } else {
		                    d1 = d0 * 100.0D;
		                }

		                if (d0 > 0.0D) {
		                    ret.add(new ChatComponentText("     " + StringHelper.LIGHT_BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + entry.getKey()))));
		                } else if (d0 < 0.0D) {
		                    d1 = -d1;
		                    ret.add(new ChatComponentText("     " + StringHelper.LIGHT_RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + entry.getKey()))));
		                }
		            }
		        } else {
		        	ret.set(0, new ChatComponentText("none"));
		        }
				return ret.toArray(new IChatComponent[ret.size()]);
			}
		};

		InfoType() {

			infoMap.put(name().toLowerCase(), this);
		}

		InfoType(String... alts) {

			this();
			for (String alt : alts) {
				infoMap.put(alt.toLowerCase(), this);
			}
		}

		public IChatComponent processStack(ItemStack stack) {

			return null;
		}

		public IChatComponent[] getData(ItemStack stack) {

			return new IChatComponent[] { processStack(stack) };
		}
	}

}
