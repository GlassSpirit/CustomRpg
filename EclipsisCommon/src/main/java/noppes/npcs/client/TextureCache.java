package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TextureCache extends SimpleTexture {
    private BufferedImage bufferedImage;
    private boolean textureUploaded;

    public TextureCache(ResourceLocation location) {
        super(location);
    }

    @Override
    public int getGlTextureId() {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    private void checkTextureUploaded() {
        if (!this.textureUploaded && this.bufferedImage != null) {
            if (this.textureLocation != null && this.glTextureId != -1) {
                TextureUtil.deleteTexture(this.glTextureId);
                this.glTextureId = -1;
            }

            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
            this.textureUploaded = true;
        }
    }

    public void setImage(ResourceLocation location) {
        try {
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            BufferedImage bufferedimage = ImageIO.read(manager.getResource(location).getInputStream());
            int i = bufferedimage.getWidth();
            int j = bufferedimage.getHeight();

            bufferedImage = new BufferedImage(i * 4, j * 2, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.drawImage(bufferedimage, 0, 0, null);
            g.drawImage(bufferedimage, i, 0, null);
            g.drawImage(bufferedimage, i * 2, 0, null);
            g.drawImage(bufferedimage, i * 3, 0, null);
            g.drawImage(bufferedimage, 0, i, null);
            g.drawImage(bufferedimage, i, j, null);
            g.drawImage(bufferedimage, i * 2, j, null);
            g.drawImage(bufferedimage, i * 3, j, null);

            textureUploaded = false;
        } catch (Exception e) {
            LogWriter.error("Failed caching texture: " + location, e);
        }
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {

    }
}
