package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.common.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class RolePostman extends RoleInterface {

    public NpcMiscInventory inventory = new NpcMiscInventory(1);
    private List<EntityPlayer> recentlyChecked = new ArrayList<>();
    private List<EntityPlayer> toCheck;

    public RolePostman(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public boolean aiShouldExecute() {
        if (npc.ticksExisted % 20 != 0)
            return false;

        toCheck = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(10, 10, 10));
        toCheck.removeAll(recentlyChecked);

        List<EntityPlayer> listMax = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(20, 20, 20));
        recentlyChecked.retainAll(listMax);
        recentlyChecked.addAll(toCheck);

        for (EntityPlayer player : toCheck) {
            if (PlayerData.get(player).mailData.hasMail())
                player.sendMessage(new TextComponentTranslation("You've got mail"));
        }
        return false;
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setTag("PostInv", inventory.getToNBT());
        return nbttagcompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        inventory.setFromNBT(nbttagcompound.getCompoundTag("PostInv"));
    }


    @Override
    public void interact(EntityPlayer player) {
        player.openGui(CustomNpcs.INSTANCE, EnumGuiType.PlayerMailman.ordinal(), player.world, 1, 1, 0);
    }

}
