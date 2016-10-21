package cofh.core.util.nbt;

import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * This class is only for writing smarter ByteArrays; when it's read back it will be the standard NBTTagByteArray
 */
public final class NBTTagSmartByteArray extends NBTTagByteArray {

    private class _ByteArrayOutputStream extends ByteArrayOutputStream {

        _ByteArrayOutputStream(int initialSize) {
            super(initialSize);
        }

        byte[] getByteArray() {
            return this.buf;
        }
    }

    private _ByteArrayOutputStream arrayout;
    private DataOutputStream dataout;

    public NBTTagSmartByteArray() {

        this(64);
    }

    public NBTTagSmartByteArray(int initialSize) {

        super(null);

        arrayout = new _ByteArrayOutputStream(initialSize);
        dataout = new DataOutputStream(arrayout);
    }

    public NBTTagSmartByteArray addString(String theString) {

        try {
            dataout.writeUTF(theString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addUUID(UUID theUUID) {

        try {
            dataout.writeLong(theUUID.getMostSignificantBits());
            dataout.writeLong(theUUID.getLeastSignificantBits());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addLong(long theLong) {

        try {
            dataout.writeLong(theLong);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addInt(int theInteger) {

        try {
            dataout.writeInt(theInteger);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addVarInt(int theInteger) {

        try {
            int v = 0x00;
            if (theInteger < 0) {
                v |= 0x40;
                theInteger = ~theInteger;
            }
            if ((theInteger & ~0x3F) != 0) {
                v |= 0x80;
            }
            dataout.writeByte(v | (theInteger & 0x3F));
            theInteger >>>= 6;
            while (theInteger != 0) {
                dataout.writeByte((theInteger & 0x7F) | ((theInteger & ~0x7F) != 0 ? 0x80 : 0));
                theInteger >>>= 7;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addBool(boolean theBoolean) {

        try {
            dataout.writeBoolean(theBoolean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addByte(byte theByte) {

        try {
            dataout.writeByte(theByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addByte(int theByte) {

        return addByte((byte) theByte);
    }

    public NBTTagSmartByteArray addShort(short theShort) {

        try {
            dataout.writeShort(theShort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addShort(int theShort) {

        return addShort((short) theShort);
    }

    public NBTTagSmartByteArray addByteArray(byte theByteArray[]) {

        try {
            dataout.write(theByteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addFloat(float theFloat) {

        try {
            dataout.writeFloat(theFloat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addItemStack(ItemStack theStack) {

        try {
            if (theStack == null) {
                addShort(-1);
            } else {
                addShort(Item.getIdFromItem(theStack.getItem()));
                addByte(theStack.stackSize);
                addShort(ItemHelper.getItemDamage(theStack));
                addNBT(theStack.getTagCompound());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void addNBT(NBTTagCompound nbt) throws IOException {

        if (nbt == null) {
            addShort(-1);
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(nbt, baos);
            byte[] abyte = baos.toByteArray();
            addShort((short) abyte.length);
            addByteArray(abyte);
        }
    }

    public NBTTagSmartByteArray addFluidStack(FluidStack theStack) {

        try {
            FluidHelper.writeFluidStackToPacket(theStack, dataout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public NBTTagSmartByteArray addCoords(TileEntity theTile) {

        addInt(theTile.getPos().getX());
        addInt(theTile.getPos().getY());
        return addInt(theTile.getPos().getZ());
    }

    public NBTTagSmartByteArray addCoords(int x, int y, int z) {

        addInt(x);
        addInt(y);
        return addInt(z);
    }

    public void write(DataOutput output) throws IOException {

        output.writeInt(arrayout.size());
        output.write(arrayout.getByteArray(), 0, arrayout.size());
    }

    @Override
    public String toString() {

        return "[" + arrayout.size() + " bytes]";
    }

    @Override
    public NBTBase copy() {

        return new NBTTagByteArray(getByteArray());
    }

    @Override
    public boolean equals(Object o) {

        if (super.equals(o) && o.getClass() == NBTTagSmartByteArray.class) {
            NBTTagSmartByteArray other = (NBTTagSmartByteArray) o;
            return other.dataout.equals(dataout);
        }
        return false;
    }

    @Override
    public int hashCode() {

        return super.hashCode() ^ dataout.hashCode();
    }

    @Override
    public byte[] getByteArray() {

        return arrayout.toByteArray();
    }

}
