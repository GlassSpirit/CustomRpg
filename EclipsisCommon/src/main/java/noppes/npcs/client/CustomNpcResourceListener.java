package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.common.CustomNpcs;

public class CustomNpcResourceListener implements IResourceManagerReloadListener {

    public static int DefaultTextColor = 0x404040;

    @Override
    public void onResourceManagerReload(IResourceManager var1) {
        if (var1 instanceof SimpleReloadableResourceManager) {
            createTextureCache();

            SimpleReloadableResourceManager simplemanager = (SimpleReloadableResourceManager) var1;

            FolderResourcePack pack = new FolderResourcePack(CustomNpcs.INSTANCE.getDir());
            simplemanager.reloadResourcePack(pack);

            try {
                DefaultTextColor = Integer.parseInt(I18n.translateToLocal("customnpcs.defaultTextColor"), 16);
            } catch (NumberFormatException e) {
                DefaultTextColor = 0x404040;
            }
        }
    }

    private void createTextureCache() {
        enlargeTexture("planks_oak");
        enlargeTexture("planks_big_oak");
        enlargeTexture("planks_birch");
        enlargeTexture("planks_jungle");
        enlargeTexture("planks_spruce");
        enlargeTexture("planks_acacia");
        enlargeTexture("iron_block");
        enlargeTexture("diamond_block");
        enlargeTexture("stone");
        enlargeTexture("gold_block");
        enlargeTexture("wool_colored_white");
    }

    private void enlargeTexture(String texture) {
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        if (manager == null)
            return;
        ResourceLocation location = new ResourceLocation("customnpcs:textures/cache/" + texture + ".png");
        ITextureObject ob = manager.getTexture(location);
        if (ob == null || !(ob instanceof TextureCache)) {
            ob = new TextureCache(location);
            manager.loadTexture(location, ob);
        }
        ((TextureCache) ob).setImage(new ResourceLocation("textures/blocks/" + texture + ".png"));
    }

}
