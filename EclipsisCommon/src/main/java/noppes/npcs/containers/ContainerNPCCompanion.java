package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;

public class ContainerNPCCompanion extends ContainerNpcInterface {
    public InventoryNPC currencyMatrix;
    public RoleCompanion role;

    public ContainerNPCCompanion(EntityNPCInterface npc, EntityPlayer player) {
        super(player);
        role = (RoleCompanion) npc.roleInterface;
        for (int k = 0; k < 3; k++) {
            for (int j1 = 0; j1 < 9; j1++) {
                addSlotToContainer(new Slot(player.inventory, j1 + k * 9 + 9, 6 + j1 * 18, 87 + k * 18));
            }
        }

        for (int l = 0; l < 9; l++) {
            addSlotToContainer(new Slot(player.inventory, l, 6 + l * 18, 145));
        }

        if (role.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
            int size = (role.getTalentLevel(EnumCompanionTalent.INVENTORY) + 1) * 2;
            for (int i = 0; i < size; i++) {
                addSlotToContainer(new Slot(role.inventory, i, 114 + i % 3 * 18, 8 + i / 3 * 18));
            }
        }
        if (role.getTalentLevel(EnumCompanionTalent.ARMOR) > 0) {
            addSlotToContainer(new SlotCompanionArmor(role, npc.inventory, 0, 6, 8, EntityEquipmentSlot.HEAD));
            addSlotToContainer(new SlotCompanionArmor(role, npc.inventory, 1, 6, 26, EntityEquipmentSlot.CHEST));
            addSlotToContainer(new SlotCompanionArmor(role, npc.inventory, 2, 6, 44, EntityEquipmentSlot.LEGS));
            addSlotToContainer(new SlotCompanionArmor(role, npc.inventory, 3, 6, 62, EntityEquipmentSlot.FEET));
        }
        if (role.getTalentLevel(EnumCompanionTalent.SWORD) > 0) {
            addSlotToContainer(new SlotCompanionWeapon(role, npc.inventory, 4, 79, 17));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        role.setStats();
    }
}
