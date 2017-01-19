package cofh.core.util.nbt;

import net.minecraft.nbt.*;
import org.apache.commons.lang3.Validate;

public class NBTCache {
    private static final IntegerNumbersCache[] NUM_CACHE = new IntegerNumbersCache[256];

    private static final FloatingNumbersCache ZERO;
    private static final FloatingNumbersCache ONE;

    private static final int LOW = -128;
    private static final int HIGH = 127;

    static {
        for (int i = 0; i < (1 - LOW + HIGH); i++) {
            NUM_CACHE[i] = new IntegerNumbersCache(i + LOW);
        }
        ZERO = new FloatingNumbersCache(0);
        ONE = new FloatingNumbersCache(0);
    }

    public static NBTTagByte toCachedByte(NBTTagByte original) {
        int i = original.getInt();
        return i >= LOW && i <= HIGH ? NUM_CACHE[i - LOW].byteValue : original;
    }

    public static NBTTagByte getByte(byte b) {
        int i = (int) b;
        return i >= LOW && i <= HIGH ? NUM_CACHE[i - LOW].byteValue : new NBTTagByte(b);
    }

    public static NBTTagInt toCachedInt(NBTTagInt original) {
        int i = original.getInt();
        return i >= LOW && i <= HIGH ? NUM_CACHE[i - LOW].intValue : original;
    }

    public static NBTTagInt getInt(int i) {
        return i >= LOW && i <= HIGH ? NUM_CACHE[i - LOW].intValue : new NBTTagInt(i);
    }

    public static NBTTagShort toCachedShort(NBTTagShort original) {
        int i = original.getShort();
        return i >= LOW && i <= HIGH ? NUM_CACHE[i - LOW].shortValue : original;
    }

    public static NBTTagShort getShort(short i) {
        return i >= LOW && i <= HIGH ? NUM_CACHE[i - LOW].shortValue : new NBTTagShort(i);
    }

    public static NBTTagLong toCachedLong(NBTTagLong original) {
        long i = original.getLong();
        return i >= LOW && i <= HIGH ? NUM_CACHE[(int) (i - LOW)].longValue : original;
    }

    public static NBTTagLong getLong(long i) {
        return i >= LOW && i <= HIGH ? NUM_CACHE[(int) (i - LOW)].longValue : new NBTTagLong(i);
    }

    public static NBTTagFloat toCachedFloat(NBTTagFloat original) {
        float i = original.getFloat();
        if (i == 0) return ZERO.floatValue;
        else if (i == 1) return ONE.floatValue;
        else return original;
    }

    public static NBTTagFloat getFloat(float i) {
        if (i == 0) return ZERO.floatValue;
        else if (i == 1) return ONE.floatValue;
        else return new NBTTagFloat(i);
    }

    public static NBTTagDouble toCachedDouble(NBTTagDouble original) {
        double i = original.getDouble();
        if (i == 0) return ZERO.doubleValue;
        else if (i == 1) return ONE.doubleValue;
        else return original;
    }

    public static NBTTagDouble getDouble(double i) {
        if (i == 0) return ZERO.doubleValue;
        else if (i == 1) return ONE.doubleValue;
        else return new NBTTagDouble(i);
    }

    private static class IntegerNumbersCache {
        final NBTTagByte byteValue;
        final NBTTagShort shortValue;
        final NBTTagInt intValue;
        final NBTTagLong longValue;

        private IntegerNumbersCache(int n) {
            Validate.isTrue(n >= LOW && n <= HIGH);
            byteValue = new NBTTagByte((byte) n);
            shortValue = new NBTTagShort((short) n);
            intValue = new NBTTagInt(n);
            longValue = new NBTTagLong(n);
        }
    }

    private static class FloatingNumbersCache {
        final NBTTagFloat floatValue;
        final NBTTagDouble doubleValue;


        private FloatingNumbersCache(float val) {
            floatValue = new NBTTagFloat(val);
            doubleValue = new NBTTagDouble(val);
        }
    }
}
