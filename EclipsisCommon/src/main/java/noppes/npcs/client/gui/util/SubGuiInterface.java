package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.GuiScreen;

public class SubGuiInterface extends GuiNPCInterface {
    public GuiScreen parent;

    public int id;

    @Override
    public void save() {

    }

    @Override
    public void close() {
        save();
        if (parent instanceof ISubGuiListener)
            ((ISubGuiListener) parent).subGuiClosed(this);

        if (parent instanceof GuiNPCInterface)
            ((GuiNPCInterface) parent).closeSubGui(this);
        else if (parent instanceof GuiContainerNPCInterface)
            ((GuiContainerNPCInterface) parent).closeSubGui(this);
        else
            super.close();

    }

    public GuiScreen getParent() {
        if (parent instanceof SubGuiInterface)
            return ((SubGuiInterface) parent).getParent();
        return parent;
    }

}
