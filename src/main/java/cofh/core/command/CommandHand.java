package cofh.core.command;

import cofh.core.util.RegistrySocial;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import com.google.common.collect.Multimap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.translation.I18n;

import java.util.*;
import java.util.Map.Entry;

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

	private static HashMap<String, InfoType> infoMap = new HashMap<String, InfoType>();

	static {
		InfoType.values();
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		int l = args.length;
		int i = 0;
		EntityPlayerMP player = null;
		ItemStack itemstack = null;

		switch (l) {
			case 0:
				sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
				throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
			default:
			case 2:
			case 3:
				try {
					player = CommandBase.getPlayer(server, sender, args[++i]);
				} catch (CommandException t) {
					if (!infoMap.containsKey(args[i])) {
						throw t;
					}
					--i;
				}
				if (player != null && player != sender && !CommandHandler.canUseCommand(sender, 1, getCommandName()) && !RegistrySocial.playerHasAccess(sender.getName(), player.getGameProfile())) {
					throw new CommandException("commands.generic.permission");
				}
			case 1:
				if (player == null) {
					player = CommandBase.getCommandSenderAsPlayer(sender);
				}
				itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
				if (itemstack == null) {
					player.getHeldItem(EnumHand.OFF_HAND);
				}
		}

		if (itemstack == null) {
			sender.addChatMessage(new TextComponentTranslation("commands.enchant.noItem"));
			return;
		}

		ArrayList<InfoType> list = new ArrayList<InfoType>();

		if (++i == l) {
			list.add(InfoType.Name);
		} else {
			for (; i < l; ++i) {
				InfoType type = infoMap.get(args[i].toLowerCase());
				if (type == null) {
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
					throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
				}
				list.add(type);
			}
		}

		for (InfoType type : list) {
			ITextComponent[] data = type.getData(itemstack);
			ITextComponent msg = new TextComponentString(type.name() + ": ");
			if (data.length >= 1 && data[0] != null) {
				msg.appendSibling(data[0]);
			}
			sender.addChatMessage(msg);
			for (i = 1; i < data.length; ++i) {
				sender.addChatMessage(data[i]);
			}
		}
	}

	@SuppressWarnings ("unchecked")
	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
		}
		return CommandBase.getListOfStringsMatchingLastWord(args, infoMap.keySet());
	}

	private static enum InfoType {
		Name("generic") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				ITextComponent component = new TextComponentString("");
				component.appendSibling(stack.getTextComponent());
				if (stack.hasDisplayName()) {
					String s = stack.getItem().getUnlocalizedName(stack);
					if (!I18n.canTranslate(s)) {
						s += ".name";
					}
					ITextComponent component2 = new TextComponentTranslation(s);
					component2.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(s)));
					component.appendText(" (").appendSibling(component2).appendText(")");
				}
				return component;
			}
		}, Id {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				return new TextComponentString(Item.REGISTRY.getNameForObject(stack.getItem()).toString());
			}
		}, Size("amount", "count") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				return new TextComponentString(String.valueOf(stack.stackSize));
			}
		}, Metadata("damage", "alt") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				return new TextComponentString(String.valueOf(ItemHelper.getItemDamage(stack)));
			}
		}, toString("string", "text") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				return new TextComponentString(stack.toString());
			}
		}, Action("use") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				EnumAction action = stack.getItemUseAction();
				ITextComponent component = new TextComponentString(action.name());
				if (action != EnumAction.NONE) {
					component.appendText(" | Duration: " + stack.getMaxItemUseDuration());
				}
				return component;
			}
		}, Lore("flavorText") {
			@Override
			public ITextComponent[] getData(ItemStack stack) {

				LinkedList<ITextComponent> ret = new LinkedList<ITextComponent>();
				ret.add(null);

				if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display", 10)) {
					NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");

					if (nbttagcompound.getTagId("Lore") == 9) {
						NBTTagList nbttaglist1 = nbttagcompound.getTagList("Lore", 8);

						if (nbttaglist1.tagCount() > 0) {
							for (int j = 0; j < nbttaglist1.tagCount(); ++j) {
								ret.add(new TextComponentString(StringHelper.PURPLE + "      " + StringHelper.ITALIC + nbttaglist1.getStringTagAt(j)));
							}
						}
					}
				} else {
					ret.set(0, new TextComponentString("none"));
				}
				return ret.toArray(new ITextComponent[ret.size()]);
			}
		}, Enchants("enchant", "ench") {
			@Override
			public ITextComponent[] getData(ItemStack stack) {

				NBTTagList nbttaglist = stack.getEnchantmentTagList();

				LinkedList<ITextComponent> ret = new LinkedList<ITextComponent>();
				ret.add(null);
				if (nbttaglist != null && nbttaglist.tagCount() > 0) {
					int i = 0;
					for (; i < nbttaglist.tagCount(); ++i) {
						short short1 = nbttaglist.getCompoundTagAt(i).getShort("id");
						short short2 = nbttaglist.getCompoundTagAt(i).getShort("lvl");

						if (Enchantment.getEnchantmentByID(short1) != null) {
							ret.add(new TextComponentString(StringHelper.TEAL + "     " + I18n.translateToLocal(Enchantment.getEnchantmentByID(short1).getName()) + " " + StringHelper.toNumerals(short2)));
						} else {
							ret.add(new TextComponentString(StringHelper.RED + "     " + String.format("Invalid{id=%s,lvl=%s}", short1, short2)));
						}
					}
				} else {
					ret.set(0, new TextComponentString("none"));
				}
				return ret.toArray(new ITextComponent[ret.size()]);
			}
		}, NBT("tag", "stackTag", "compoundTag") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				return new TextComponentString((stack.getTagCompound() != null) ? stack.getTagCompound().toString() : null);
			}
		}, OreDict("oreNames", "oreName", "ores", "ore") {
			@Override
			public ITextComponent processStack(ItemStack stack) {

				ArrayList<String> ores = OreDictionaryArbiter.getAllOreNames(stack);
				int size = ores == null ? 0 : ores.size();
				String arr = StringHelper.toString(ores, "[null]");
				return new TextComponentString(size + "> " + arr.substring(1, arr.length() - 1));
			}
		}, Modifiers {
			protected final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

			@Override
			public ITextComponent[] getData(ItemStack stack) {

				LinkedList<ITextComponent> ret = new LinkedList<ITextComponent>();
				ret.add(null);

				Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers(EntityEquipmentSlot.HEAD);//TODO
				if (!multimap.isEmpty()) {

					for (Entry<String, AttributeModifier> entry : multimap.entries()) {
						AttributeModifier attributemodifier = entry.getValue();
						double d0 = attributemodifier.getAmount();

						if (field_111210_e.equals(attributemodifier.getID())) {
							d0 += EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
						}

						double d1;

						if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2) {
							d1 = d0;
						} else {
							d1 = d0 * 100.0D;
						}

						if (d0 > 0.0D) {
							ret.add(new TextComponentString("     " + StringHelper.LIGHT_BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + entry.getKey()))));
						} else if (d0 < 0.0D) {
							d1 = -d1;
							ret.add(new TextComponentString("     " + StringHelper.LIGHT_RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + entry.getKey()))));
						}
					}
				} else {
					ret.set(0, new TextComponentString("none"));
				}
				return ret.toArray(new ITextComponent[ret.size()]);
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

		public ITextComponent processStack(ItemStack stack) {

			return null;
		}

		public ITextComponent[] getData(ItemStack stack) {

			return new ITextComponent[] { processStack(stack) };
		}
	}

}
