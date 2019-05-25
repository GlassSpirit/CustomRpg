package noppes.npcs.client.gui.mainmenu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.*;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataStats;

public class GuiNpcStats extends GuiNPCInterface2 implements ITextfieldListener, IGuiData {
    private DataStats stats;

    public GuiNpcStats(EntityNPCInterface npc) {
        super(npc, 2);
        stats = npc.stats;
        Client.sendData(EnumPacketServer.MainmenuStatsGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 10;
        addLabel(new GuiNpcLabel(0, "stats.health", guiLeft + 5, y + 5));
        addTextField(new GuiNpcTextField(0, this, guiLeft + 85, y, 50, 18, stats.getMaxHealth() + ""));
        getTextField(0).numbersOnly = true;
        getTextField(0).setMinMaxDefault(0, Integer.MAX_VALUE, 20);
        addLabel(new GuiNpcLabel(1, "stats.aggro", guiLeft + 140, y + 5));
        addTextField(new GuiNpcTextField(1, this, fontRenderer, guiLeft + 220, y, 50, 18, stats.getAggroRange() + ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(1, 64, 2);
        addLabel(new GuiNpcLabel(34, "stats.creaturetype", guiLeft + 275, y + 5));
        addButton(new GuiNpcButton(8, guiLeft + 355, y, 56, 20, new String[]{"stats.normal", "stats.undead", "stats.arthropod"}, stats.getCreatureType()));

        y += 22;
        addLabel(new GuiNpcLabel(2, "stats.respawn", guiLeft + 5, y + 5));
        addButton(new GuiNpcButton(0, guiLeft + 82, y, 56, 20, "selectServer.edit"));
        addLabel(new GuiNpcLabel(69, "stats.level", guiLeft + 275, y + 5));
        addTextField(new GuiNpcTextField(69, this, fontRenderer, guiLeft + 355, y, 56, 20, stats.getLevel() + ""));
        getTextField(69).numbersOnly = true;
        getTextField(69).setMinMaxDefault(0, Integer.MAX_VALUE, 1);

        y += 22;
        addLabel(new GuiNpcLabel(5, "stats.meleeproperties", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(2, guiLeft + 82, y, 56, 20, "selectServer.edit"));

        y += 22;
        addLabel(new GuiNpcLabel(6, "stats.rangedproperties", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(3, guiLeft + 82, y, 56, 20, "selectServer.edit"));
        addLabel(new GuiNpcLabel(7, "stats.projectileproperties", guiLeft + 140, y + 5));
        this.addButton(new GuiNpcButton(9, guiLeft + 217, y, 56, 20, "selectServer.edit"));


        y += 34;
        addLabel(new GuiNpcLabel(15, "effect.resistance", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(15, guiLeft + 82, y, 56, 20, "selectServer.edit"));

        y += 34;
        addLabel(new GuiNpcLabel(10, "stats.fireimmune", guiLeft + 5, y + 5));
        addButton(new GuiNpcButton(4, guiLeft + 82, y, 56, 20, new String[]{"gui.no", "gui.yes"}, npc.isImmuneToFire() ? 1 : 0));
        addLabel(new GuiNpcLabel(11, "stats.candrown", guiLeft + 140, y + 5));
        addButton(new GuiNpcButton(5, guiLeft + 217, y, 56, 20, new String[]{"gui.no", "gui.yes"}, stats.getCanDrown() ? 1 : 0));
        addLabel(new GuiNpcLabel(14, "stats.regenhealth", guiLeft + 275, y + 5));
        addTextField(new GuiNpcTextField(14, this, guiLeft + 355, y, 56, 20, stats.getHealthRegen() + "").setNumbersOnly());

        y += 22;
        addLabel(new GuiNpcLabel(16, "stats.combatregen", guiLeft + 275, y + 5));
        addTextField(new GuiNpcTextField(16, this, guiLeft + 355, y, 56, 20, stats.getCombatRegen() + "").setNumbersOnly());
        addLabel(new GuiNpcLabel(12, "stats.burninsun", guiLeft + 5, y + 5));
        addButton(new GuiNpcButton(6, guiLeft + 82, y, 56, 20, new String[]{"gui.no", "gui.yes"}, stats.getBurnInSun() ? 1 : 0));
        addLabel(new GuiNpcLabel(13, "stats.nofalldamage", guiLeft + 140, y + 5));
        addButton(new GuiNpcButton(7, guiLeft + 217, y, 56, 20, new String[]{"gui.no", "gui.yes"}, stats.getNoFallDamage() ? 1 : 0));

        y += 22;
        addLabel(new GuiNpcLabel(17, "stats.potionimmune", guiLeft + 5, y + 5));
        addButton(new GuiNpcButtonYesNo(17, guiLeft + 82, y, 56, 20, stats.getPotionImmune()));
        addLabel(new GuiNpcLabel(22, "ai.cobwebAffected", guiLeft + 140, y + 5));
        addButton(new GuiNpcButton(22, guiLeft + 217, y, 56, 20, new String[]{"gui.no", "gui.yes"}, stats.getIgnoreCobweb() ? 0 : 1));
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0) {
            stats.setMaxHealth(textfield.getInteger());
            npc.heal(stats.getMaxHealth());
        } else if (textfield.id == 1) {
            stats.setAggroRange(textfield.getInteger());
        } else if (textfield.id == 14) {
            stats.setHealthRegen(textfield.getInteger());
        } else if (textfield.id == 16) {
            stats.setCombatRegen(textfield.getInteger());
        } else if (textfield.id == 69) {
            stats.setLevel(textfield.getInteger());
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            setSubGui(new SubGuiNpcRespawn(this.stats));
        } else if (button.id == 2) {
            setSubGui(new SubGuiNpcMeleeProperties(this.stats.getMelee()));
        } else if (button.id == 3) {
            setSubGui(new SubGuiNpcRangeProperties(this.stats));
        } else if (button.id == 4) {
            npc.setImmuneToFire(button.getValue() == 1);
        } else if (button.id == 5) {
            stats.setCanDrown(button.getValue() == 1);
        } else if (button.id == 6) {
            stats.setBurnInSun(button.getValue() == 1);
        } else if (button.id == 7) {
            stats.setNoFallDamage(button.getValue() == 1);
        } else if (button.id == 8) {
            stats.setCreatureType(button.getValue());
        } else if (button.id == 9) {
            setSubGui(new SubGuiNpcProjectiles(this.stats.getRanged()));
        } else if (button.id == 15) {
            setSubGui(new SubGuiNpcResistanceProperties(this.stats.getResistances()));
        } else if (button.id == 17) {
            stats.setPotionImmune(((GuiNpcButtonYesNo) guibutton).getBoolean());
        } else if (button.id == 22) {
            stats.setIgnoreCobweb((button.getValue() == 0));
        }
    }

    @Override
    public void save() {
        Client.sendData(EnumPacketServer.MainmenuStatsSave, stats.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        stats.readToNBT(compound);
        initGui();
    }
}
