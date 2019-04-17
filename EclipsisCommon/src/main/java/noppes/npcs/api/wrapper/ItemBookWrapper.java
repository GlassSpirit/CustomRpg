package noppes.npcs.api.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.item.IItemBook;

import java.util.ArrayList;
import java.util.List;

public class ItemBookWrapper extends ItemStackWrapper implements IItemBook {

    protected ItemBookWrapper(ItemStack item) {
        super(item);
    }

    @Override
    public String getTitle() {
        return getTag().getString("title");
    }

    @Override
    public void setTitle(String title) {
        getTag().setString("title", title);
    }

    @Override
    public String getAuthor() {
        return getTag().getString("author");
    }

    @Override
    public void setAuthor(String author) {
        getTag().setString("author", author);
    }

    @Override
    public String[] getText() {
        List<String> list = new ArrayList<String>();
        NBTTagList pages = getTag().getTagList("pages", 8);
        for (int i = 0; i < pages.tagCount(); ++i) {
            list.add(pages.getStringTagAt(i));
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void setText(String[] pages) {
        NBTTagList list = new NBTTagList();
        if (pages != null && pages.length > 0) {
            for (String page : pages) {
                list.appendTag(new NBTTagString(page));
            }
        }
        getTag().setTag("pages", list);
    }

    private NBTTagCompound getTag() {
        NBTTagCompound comp = item.getTagCompound();
        if (comp == null)
            item.setTagCompound(comp = new NBTTagCompound());
        return comp;
    }

    @Override
    public boolean isBook() {
        return true;
    }

    @Override
    public int getType() {
        return ItemType.BOOK;
    }
}
