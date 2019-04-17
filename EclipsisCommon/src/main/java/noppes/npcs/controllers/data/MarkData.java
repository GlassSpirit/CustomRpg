package noppes.npcs.controllers.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.Server;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.constants.EnumPacketClient;

import java.util.ArrayList;
import java.util.List;

public class MarkData implements ICapabilityProvider {

    @CapabilityInject(MarkData.class)
    public static Capability<MarkData> MARKDATA_CAPABILITY = null;
    private static final String NBTKEY = "cnpcmarkdata";
    private static final ResourceLocation CAPKEY = new ResourceLocation("customnpcs", "markdata");
    private EntityLivingBase entity;

    public List<Mark> marks = new ArrayList<Mark>();

    public void setNBT(NBTTagCompound compound) {
        List<Mark> marks = new ArrayList<Mark>();
        NBTTagList list = compound.getTagList("marks", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            Mark m = new Mark();
            m.type = c.getInteger("type");
            m.color = c.getInteger("color");
            m.availability.readFromNBT(c.getCompoundTag("availability"));
            marks.add(m);
        }
        this.marks = marks;
    }

    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Mark m : marks) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("type", m.type);
            c.setInteger("color", m.color);
            c.setTag("availability", m.availability.writeToNBT(new NBTTagCompound()));
            list.appendTag(c);
        }
        compound.setTag("marks", list);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == MARKDATA_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing))
            return (T) this;
        return null;
    }

    public static void register(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(CAPKEY, new MarkData());
    }

    public void save() {
        entity.getEntityData().setTag(NBTKEY, getNBT());
    }

    public IMark addMark(int type) {
        Mark m = new Mark();
        m.type = type;
        marks.add(m);
        if (!entity.world.isRemote)
            syncClients();
        return m;
    }

    public IMark addMark(int type, int color) {
        Mark m = new Mark();
        m.type = type;
        m.color = color;
        marks.add(m);
        if (!entity.world.isRemote)
            syncClients();
        return m;
    }

    public static MarkData get(EntityLivingBase entity) {
        MarkData data = entity.getCapability(MARKDATA_CAPABILITY, null);
        if (data.entity == null) {
            data.entity = entity;
            data.setNBT(entity.getEntityData().getCompoundTag(NBTKEY));
        }
        return data;
    }

    public void syncClients() {
        Server.sendToAll(entity.getServer(), EnumPacketClient.MARK_DATA, entity.getEntityId(), getNBT());
    }

    public class Mark implements IMark {
        public int type = MarkType.NONE;

        public Availability availability = new Availability();

        public int color = 0xFFED51;

        @Override
        public IAvailability getAvailability() {
            return availability;
        }

        @Override
        public int getColor() {
            return color;
        }

        @Override
        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public void setType(int type) {
            this.type = type;
        }

        @Override
        public void update() {
            MarkData.this.syncClients();
        }
    }
}
