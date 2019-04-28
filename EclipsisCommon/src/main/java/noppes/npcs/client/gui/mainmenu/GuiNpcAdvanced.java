package noppes.npcs.client.gui.mainmenu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.client.Client;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.advanced.*;
import noppes.npcs.client.gui.roles.*;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiNpcAdvanced extends GuiNPCInterface2 implements IGuiData {
    private boolean hasChanges = false;

    public GuiNpcAdvanced(EntityNPCInterface npc) {
        super(npc, 4);
        Client.sendData(EnumPacketServer.MainmenuAdvancedGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 8;
        this.addButton(new GuiNpcButton(3, guiLeft + 85 + 160, y, 52, 20, "selectServer.edit"));
        this.addButton(new GuiButtonBiDirectional(8, guiLeft + 85, y, 155, 20, new String[]{"role.none", "role.trader", "role.follower", "role.bank", "role.transporter", "role.mailman", NoppesStringUtils.translate("role.companion", "(WIP)"), "dialog.dialog"}, npc.advanced.role));
        getButton(3).setEnabled(npc.advanced.role != RoleType.NONE && npc.advanced.role != RoleType.MAILMAN);

        this.addButton(new GuiNpcButton(4, guiLeft + 85 + 160, y += 22, 52, 20, "selectServer.edit"));
        this.addButton(new GuiButtonBiDirectional(5, guiLeft + 85, y, 155, 20, new String[]{"job.none", "job.bard", "job.healer", "job.guard", "job.itemgiver", "role.follower", "job.spawner", "job.conversation", "job.chunkloader", "job.puppet", "job.builderBlock", "job.farmer"}, npc.advanced.job));

        getButton(4).setEnabled(npc.advanced.job != JobType.NONE && npc.advanced.job != JobType.CHUNKLOADER && npc.advanced.job != JobType.BUILDER);

        this.addButton(new GuiNpcButton(7, guiLeft + 15, y += 22, 190, 20, "advanced.lines"));
        this.addButton(new GuiNpcButton(9, guiLeft + 208, y, 190, 20, "menu.factions"));
        this.addButton(new GuiNpcButton(10, guiLeft + 15, y += 22, 190, 20, "dialog.dialogs"));
        this.addButton(new GuiNpcButton(11, guiLeft + 208, y, 190, 20, "advanced.sounds"));
        this.addButton(new GuiNpcButton(12, guiLeft + 15, y += 22, 190, 20, "advanced.night"));
        this.addButton(new GuiNpcButton(13, guiLeft + 208, y, 190, 20, "global.linked"));
        this.addButton(new GuiNpcButton(14, guiLeft + 15, y += 22, 190, 20, "advanced.scenes"));
        this.addButton(new GuiNpcButton(15, guiLeft + 208, y, 190, 20, "advanced.marks"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 3) {
            save();
            Client.sendData(EnumPacketServer.RoleGet);
        }
        if (button.id == 8) {
            hasChanges = true;
            npc.advanced.setRole(button.getValue());

            getButton(3).setEnabled(npc.advanced.role != RoleType.NONE && npc.advanced.role != RoleType.MAILMAN);
        }
        if (button.id == 4) {
            save();
            Client.sendData(EnumPacketServer.JobGet);
        }
        if (button.id == 5) {
            hasChanges = true;
            npc.advanced.setJob(button.getValue());

            getButton(4).setEnabled(npc.advanced.job != JobType.NONE && npc.advanced.job != JobType.CHUNKLOADER && npc.advanced.job != JobType.BUILDER);
        }
        if (button.id == 9) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCFactionSetup(npc));
        }
        if (button.id == 10) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCDialogNpcOptions(npc, this));
        }
        if (button.id == 11) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCSoundsMenu(npc));
        }
        if (button.id == 7) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCLinesMenu(npc));
        }
        if (button.id == 12) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCNightSetup(npc));
        }
        if (button.id == 13) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCAdvancedLinkedNpc(npc));
        }
        if (button.id == 14) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCScenes(npc));
        }
        if (button.id == 15) {
            save();
            NoppesUtil.openGUI(player, new GuiNPCMarks(npc));
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (compound.hasKey("RoleData")) {
            if (npc.roleInterface != null)
                npc.roleInterface.readFromNBT(compound);

            if (npc.advanced.role == RoleType.TRADER)
                NoppesUtil.requestOpenGUI(EnumGuiType.SetupTrader);
            else if (npc.advanced.role == RoleType.FOLLOWER)
                NoppesUtil.requestOpenGUI(EnumGuiType.SetupFollower);
            else if (npc.advanced.role == RoleType.BANK)
                NoppesUtil.requestOpenGUI(EnumGuiType.SetupBank);
            else if (npc.advanced.role == RoleType.TRANSPORTER)
                displayGuiScreen(new GuiNpcTransporter(npc));
            else if (npc.advanced.role == RoleType.COMPANION)
                displayGuiScreen(new GuiNpcCompanion(npc));
            else if (npc.advanced.role == RoleType.DIALOG)
                NoppesUtil.openGUI(player, new GuiRoleDialog(npc));
        } else if (compound.hasKey("JobData")) {
            if (npc.jobInterface != null)
                npc.jobInterface.readFromNBT(compound);

            if (npc.advanced.job == JobType.BARD)
                NoppesUtil.openGUI(player, new GuiNpcBard(npc));
            else if (npc.advanced.job == JobType.HEALER)
                NoppesUtil.openGUI(player, new GuiNpcHealer(npc));
            else if (npc.advanced.job == JobType.GUARD)
                NoppesUtil.openGUI(player, new GuiNpcGuard(npc));
            else if (npc.advanced.job == JobType.ITEMGIVER)
                NoppesUtil.requestOpenGUI(EnumGuiType.SetupItemGiver);
            else if (npc.advanced.job == JobType.FOLLOWER)
                NoppesUtil.openGUI(player, new GuiNpcFollowerJob(npc));
            else if (npc.advanced.job == JobType.SPAWNER)
                NoppesUtil.openGUI(player, new GuiNpcSpawner(npc));
            else if (npc.advanced.job == JobType.CONVERSATION)
                NoppesUtil.openGUI(player, new GuiNpcConversation(npc));
            else if (npc.advanced.job == JobType.PUPPET)
                NoppesUtil.openGUI(player, new GuiNpcPuppet(this, (EntityCustomNpc) npc));
            else if (npc.advanced.job == JobType.FARMER)
                NoppesUtil.openGUI(player, new GuiJobFarmer(npc));
        } else {
            npc.advanced.readToNBT(compound);
            initGui();
        }
    }

    @Override
    public void save() {
        if (hasChanges) {
            Client.sendData(EnumPacketServer.MainmenuAdvancedSave, npc.advanced.writeToNBT(new NBTTagCompound()));
            hasChanges = false;
        }
    }


}
