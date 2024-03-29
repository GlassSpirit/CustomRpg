package noppes.npcs.client.gui.mainmenu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.containers.ContainerNPCInv;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Map;

public class GuiNPCInv extends GuiContainerNPCInterface2 implements ITextfieldListener, IGuiData {
    private Map<Integer, Float> chances = new HashMap<>();
    private ContainerNPCInv container;
    private ResourceLocation slot;

    public GuiNPCInv(EntityNPCInterface npc, ContainerNPCInv container) {
        super(npc, container, 3);
        this.setBackground("npcinv.png");
        this.container = container;
        ySize = 200;
        slot = getResource("slot.png");
        Client.sendData(EnumPacketServer.MainmenuInvGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        addLabel(new GuiNpcLabel(0, "inv.minExp", guiLeft + 118, guiTop + 18));
        addTextField(new GuiNpcTextField(0, this, fontRenderer, guiLeft + 108, guiTop + 29, 60, 20, npc.inventory.getExpMin() + ""));
        getTextField(0).numbersOnly = true;
        getTextField(0).setMinMaxDefault(0, Short.MAX_VALUE, 0);

        addLabel(new GuiNpcLabel(1, "inv.maxExp", guiLeft + 118, guiTop + 52));
        addTextField(new GuiNpcTextField(1, this, fontRenderer, guiLeft + 108, guiTop + 63, 60, 20, npc.inventory.getExpMax() + ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, Short.MAX_VALUE, 0);

        addButton(new GuiNpcButton(10, guiLeft + 88, guiTop + 88, 80, 20, new String[]{"stats.normal", "inv.auto"}, npc.inventory.lootMode));

        addLabel(new GuiNpcLabel(2, "inv.npcInventory", guiLeft + 191, guiTop + 5));
        addLabel(new GuiNpcLabel(3, "inv.inventory", guiLeft + 8, guiTop + 101));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                float chance = 0;
                if (npc.inventory.dropChance.containsKey(i * 9 + j)) {
                    chance = npc.inventory.dropChance.get(i * 9 + j);
                }
                if (chance < 0 || chance > 100) chance = 0;
                chances.put(i * 9 + j, chance);
                GuiNpcTextField field = new GuiNpcTextField(50 + i * 9 + j, this, guiLeft + 211 + i * 75, guiTop + 14 + j * 21, 50, 20, chance + "");
                addTextField(field);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 10) {
            npc.inventory.lootMode = ((GuiNpcButton) guibutton).getValue();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(slot);

        for (int id = 4; id <= 6; id++) {
            Slot slot = container.getSlot(id);
            if (slot.getHasStack())
                drawTexturedModalRect(guiLeft + slot.xPos - 1, guiTop + slot.yPos - 1, 0, 0, 18, 18);
        }
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        int showname = npc.display.getShowName();
        npc.display.setShowName(1);
        drawNpc(50, 84);
        npc.display.setShowName(showname);

        super.drawScreen(i, j, f);
    }

    @Override
    public void save() {
        npc.inventory.dropChance = chances;
        npc.inventory.setExp(getTextField(0).getInteger(), getTextField(1).getInteger());
        Client.sendData(EnumPacketServer.MainmenuInvSave, npc.inventory.writeEntityToNBT(new NBTTagCompound()));
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        npc.inventory.readEntityFromNBT(compound);
        initGui();
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id >= 50) {
            int slot = textfield.id - 50;
            float chance = 0;
            try {
                chance = Float.parseFloat(textfield.getText());
                if (chance < 0) chance = 0;
                if (chance > 100) chance = 100;
            } catch (Exception ignored) {
            }
            textfield.setText(chance + "");
            chances.put(slot, chance);
        }
    }
}
