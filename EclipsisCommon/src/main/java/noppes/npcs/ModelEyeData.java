package noppes.npcs;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.Random;

public class ModelEyeData extends ModelPartData {
    private Random r = new Random();
    public boolean glint = true;

    public int browThickness = 4;
    public int eyePos = 1;

    public int skinColor = 0xB4846D;
    public int browColor = 0x5b4934;

    public long blinkStart = 0;

    public ModelEyeData() {
        super("eyes");
        this.color = (new Integer[]{8368696, 16247203, 10526975, 10987431, 10791096, 4210943, 14188339, 11685080, 6724056, 15066419,
                8375321, 15892389, 10066329, 5013401, 8339378, 3361970, 6704179, 6717235, 10040115, 16445005, 6085589, 4882687, 55610})[r.nextInt(23)];
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = super.writeToNBT();
        compound.setBoolean("Glint", glint);

        compound.setInteger("SkinColor", skinColor);
        compound.setInteger("BrowColor", browColor);

        compound.setInteger("PositionY", eyePos);
        compound.setInteger("BrowThickness", browThickness);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.isEmpty())
            return;
        super.readFromNBT(compound);
        glint = compound.getBoolean("Glint");

        skinColor = compound.getInteger("SkinColor");
        browColor = compound.getInteger("BrowColor");

        eyePos = compound.getInteger("PositionY");
        browThickness = compound.getInteger("BrowThickness");
    }

    public boolean isEnabled() {
        return this.type >= 0;
    }

    public void update(EntityNPCInterface npc) {
        if (!isEnabled() || !npc.isEntityAlive() || !npc.isServerWorld())
            return;
        if (blinkStart < 0) {
            blinkStart++;
        } else if (blinkStart == 0) {
            if (r.nextInt(140) == 1) {
                blinkStart = System.currentTimeMillis();
                if (npc != null) {
                    Server.sendAssociatedData(npc, EnumPacketClient.EYE_BLINK, npc.getEntityId());
                }
            }
        } else if (System.currentTimeMillis() - blinkStart > 300) {
            blinkStart = -20;
        }
    }
}
