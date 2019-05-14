package noppes.npcs.api.wrapper;

import net.minecraft.nbt.*;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.NBTJsonUtil;

public class NBTWrapper implements INbt {

    private NBTTagCompound compound;

    public NBTWrapper(NBTTagCompound compound) {
        this.compound = compound;
    }

    @Override
    public void remove(String key) {
        compound.removeTag(key);
    }

    @Override
    public boolean has(String key) {
        return compound.hasKey(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.getBoolean(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        compound.setBoolean(key, value);
    }

    @Override
    public short getShort(String key) {
        return compound.getShort(key);
    }

    @Override
    public void setShort(String key, short value) {
        compound.setShort(key, value);
    }

    @Override
    public int getInteger(String key) {
        return compound.getInteger(key);
    }

    @Override
    public void setInteger(String key, int value) {
        compound.setInteger(key, value);
    }

    @Override
    public byte getByte(String key) {
        return compound.getByte(key);
    }

    @Override
    public void setByte(String key, byte value) {
        compound.setByte(key, value);
    }

    @Override
    public long getLong(String key) {
        return compound.getLong(key);
    }

    @Override
    public void setLong(String key, long value) {
        compound.setLong(key, value);
    }

    @Override
    public double getDouble(String key) {
        return compound.getDouble(key);
    }

    @Override
    public void setDouble(String key, double value) {
        compound.setDouble(key, value);
    }

    @Override
    public float getFloat(String key) {
        return compound.getFloat(key);
    }

    @Override
    public void setFloat(String key, float value) {
        compound.setFloat(key, value);
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public void setString(String key, String value) {
        compound.setString(key, value);
    }

    @Override
    public byte[] getByteArray(String key) {
        return compound.getByteArray(key);
    }

    @Override
    public void setByteArray(String key, byte[] value) {
        compound.setByteArray(key, value);
    }

    @Override
    public int[] getIntegerArray(String key) {
        return compound.getIntArray(key);
    }

    @Override
    public void setIntegerArray(String key, int[] value) {
        compound.setIntArray(key, value);
    }

    @Override
    public Object[] getList(String key, int type) {
        NBTTagList list = compound.getTagList(key, type);
        Object[] nbts = new Object[list.tagCount()];

        for (int i = 0; i < list.tagCount(); i++) {
            if (list.getTagType() == 10)
                nbts[i] = NpcAPI.instance().getINbt(list.getCompoundTagAt(i));
            else if (list.getTagType() == 8)
                nbts[i] = list.getStringTagAt(i);
            else if (list.getTagType() == 6)
                nbts[i] = list.getDoubleAt(i);
            else if (list.getTagType() == 5)
                nbts[i] = list.getFloatAt(i);
            else if (list.getTagType() == 3)
                nbts[i] = list.getIntAt(i);
            else if (list.getTagType() == 11)
                nbts[i] = list.getIntArrayAt(i);
        }
        return nbts;
    }

    @Override
    public int getListType(String key) {
        NBTBase b = compound.getTag(key);
        if (b == null)
            return 0;
        if (b.getId() != 9)
            throw new CustomNPCsException("NBT tag " + key + " isn't a list");
        return ((NBTTagList) b).getTagType();
    }

    @Override
    public void setList(String key, Object[] value) {
        NBTTagList list = new NBTTagList();
        for (Object nbt : value) {
            if (nbt instanceof INbt)
                list.appendTag(((INbt) nbt).getMCNBT());
            else if (nbt instanceof String)
                list.appendTag(new NBTTagString((String) nbt));
            else if (nbt instanceof Double)
                list.appendTag(new NBTTagDouble((Double) nbt));
            else if (nbt instanceof Float)
                list.appendTag(new NBTTagFloat((Float) nbt));
            else if (nbt instanceof Integer)
                list.appendTag(new NBTTagInt((Integer) nbt));
            else if (nbt instanceof int[])
                list.appendTag(new NBTTagIntArray((int[]) nbt));
        }
        compound.setTag(key, list);
    }

    @Override
    public INbt getCompound(String key) {
        return NpcAPI.instance().getINbt(compound.getCompoundTag(key));
    }

    @Override
    public void setCompound(String key, INbt value) {
        if (value == null)
            throw new CustomNPCsException("Value cant be null");
        compound.setTag(key, value.getMCNBT());
    }

    @Override
    public String[] getKeys() {
        return compound.getKeySet().toArray(new String[compound.getKeySet().size()]);
    }

    @Override
    public int getType(String key) {
        return compound.getTagId(key);
    }

    @Override
    public NBTTagCompound getMCNBT() {
        return compound;
    }

    @Override
    public String toJsonString() {
        return NBTJsonUtil.Convert(compound);
    }

    @Override
    public boolean isEqual(INbt nbt) {
        if (nbt == null)
            return false;
        return compound.equals(nbt.getMCNBT());
    }

    @Override
    public void clear() {
        for (String name : compound.getKeySet()) {
            compound.removeTag(name);
        }
    }

    @Override
    public void merge(INbt nbt) {
        compound.merge(nbt.getMCNBT());
    }
}
