package noppes.npcs.client.fx;

import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.entity.EntityCustomNpc;

public class EntityEnderFX extends ParticlePortal {

    private float portalParticleScale;
    private int particleNumber;
    private EntityCustomNpc npc;
    private static final ResourceLocation resource = new ResourceLocation("textures/particle/particles.png");
    private final ResourceLocation location;
    private boolean move = true;
    private float startX = 0, startY = 0, startZ = 0;

    public EntityEnderFX(EntityCustomNpc npc, double par2, double par4,
                         double par6, double par8, double par10, double par12, ModelPartData data) {
        super(npc.world, par2, par4, par6, par8, par10, par12);

        this.npc = npc;
        particleNumber = npc.getRNG().nextInt(2);
        portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;

        particleRed = (data.color >> 16 & 255) / 255f;
        particleGreen = (data.color >> 8 & 255) / 255f;
        particleBlue = (data.color & 255) / 255f;

        if (npc.getRNG().nextInt(3) == 1) {
            move = false;
            this.startX = (float) npc.posX;
            this.startY = (float) npc.posY;
            this.startZ = (float) npc.posZ;
        }

        if (data.playerTexture)
            location = npc.textureLocation;
        else
            location = data.getResource();
    }

    @Override
    public void renderParticle(BufferBuilder renderer, Entity entity, float partialTicks, float par3, float par4, float par5, float par6, float par7) {

        if (move) {
            startX = (float) (npc.prevPosX + (npc.posX - npc.prevPosX) * (double) partialTicks);
            startY = (float) (npc.prevPosY + (npc.posY - npc.prevPosY) * (double) partialTicks);
            startZ = (float) (npc.prevPosZ + (npc.posZ - npc.prevPosZ) * (double) partialTicks);
        }
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.draw();
        float scale = ((float) particleAge + partialTicks) / (float) particleMaxAge;
        scale = 1.0F - scale;
        scale *= scale;
        scale = 1.0F - scale;
        particleScale = portalParticleScale * scale;
        ClientProxy.Companion.bindTexture(location);

        float f = 0.875f;
        float f1 = f + 0.125f;
        float f2 = 0.75f - (particleNumber * 0.25f);
        float f3 = f2 + 0.25f;
        float f4 = 0.1F * particleScale;
        float f5 = (float) (((prevPosX + (posX - prevPosX) * (double) partialTicks) - interpPosX) + startX);
        float f6 = (float) (((prevPosY + (posY - prevPosY) * (double) partialTicks) - interpPosY) + startY);
        float f7 = (float) (((prevPosZ + (posZ - prevPosZ) * (double) partialTicks) - interpPosZ) + startZ);

        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;

        GlStateManager.color(1, 1, 1, 1.0F);
        renderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        //tessellator.setBrightness(240);
        //tessellator.setColorOpaque_F(1, 1, 1);
        renderer.pos(f5 - par3 * f4 - par6 * f4, f6 - par4 * f4, f7 - par5 * f4 - par7 * f4).tex(f1, f3).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
        renderer.pos((f5 - par3 * f4) + par6 * f4, f6 + par4 * f4, (f7 - par5 * f4) + par7 * f4).tex(f1, f2).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
        renderer.pos(f5 + par3 * f4 + par6 * f4, f6 + par4 * f4, f7 + par5 * f4 + par7 * f4).tex(f, f2).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
        renderer.pos((f5 + par3 * f4) - par6 * f4, f6 - par4 * f4, (f7 + par5 * f4) - par7 * f4).tex(f, f3).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();

        tessellator.draw();
        ClientProxy.Companion.bindTexture(resource);
        renderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    @Override
    public int getFXLayer() {
        return 0;
    }
}
