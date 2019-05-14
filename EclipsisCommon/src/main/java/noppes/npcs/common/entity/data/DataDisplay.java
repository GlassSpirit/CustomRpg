package noppes.npcs.common.entity.data;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.BossInfo;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.INPCDisplay;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.util.ValueUtil;

import java.util.Random;

public class DataDisplay implements INPCDisplay {
    private EntityNPCInterface npc;

    private String name;
    private String title = "";

    public byte skinType = 0; //0:normal, 1:player, 2:url
    private String url = "";
    public GameProfile playerProfile;
    private String texture = "customnpcs:textures/entity/humanmale/steve.png";

    private String cloakTexture = "";
    private String glowTexture = "";

    private int visible = 0; //0:visible, 1:invisible, 2:semi-invisible

    private int modelSize = 5;

    private int showName = 0;
    private int skinColor = 0xFFFFFF;

    private boolean disableLivingAnimation = false;
    private boolean noHitbox = false;

    private byte showBossBar = 0;
    private BossInfo.Color bossColor = BossInfo.Color.PINK;

    public DataDisplay(EntityNPCInterface npc) {
        this.npc = npc;
        String[] names = {"Noppes", "Noppes", "Noppes", "Noppes", "Atesson",
                "Rothcersul", "Achdranys", "Pegato", "Chald", "Gareld",
                "Nalworche", "Ineald", "Tia'kim", "Torerod", "Turturdar",
                "Ranler", "Dyntan", "Oldrake", "Gharis", "Elmn", "Tanal",
                "Waran-ess", "Ach-aldhat", "Athi", "Itageray", "Tasr",
                "Ightech", "Gakih", "Adkal", "Qua'an", "Sieq", "Urnp", "Rods",
                "Vorbani", "Smaik", "Fian", "Hir", "Ristai", "Kineth", "Naif",
                "Issraya", "Arisotura", "Honf", "Rilfom", "Estz", "Ghatroth",
                "Yosil", "Darage", "Aldny", "Tyltran", "Armos", "Loxiku", "Burhat", "Tinlt", "Ightyd", "Mia",
                "Ken", "Karla", "Lily", "Carina", "SubPai", "Daniel", "Slater", "Zidane", "Valentine", "Eirina",
                "Carnow", "Grave", "Shadow", "Drakken", "Kaoz", "Silk", "Drake", "Oldam", "Lynxx", "Lenyx",
                "Winter", "Seth", "Apolitho", "Amethyst", "Ankin", "Seinkan", "Ayumu", "Sakamoto", "Divina",
                "Div", "Magia", "Magnus", "Tiakono", "Ruin", "Hailinx", "Ethan", "Wate", "Carter", "William",
                "Brion", "Sparrow", "Basrrelen", "Gyaku", "Claire", "Crowfeather", "Blackwell", "Raven", "Farcri",
                "Lucas", "Bangheart", "Kamoku", "Kyoukan", "Blaze", "Benjamin", "Larianne", "Kakaragon",
                "Melancholy", "Epodyno", "Thanato", "Mika", "Dacks", "Ylander", "Neve", "Meadow", "Cuero",
                "Embrera", "Eldamore", "Faolan", "Chim", "Nasu", "Kathrine", "Ariel", "Arei", "Demytrix",
                "Kora", "Ava", "Larson", "Leonardo", "Wyrl", "Sakiama", "Lambton", "Kederath", "Malus", "Riplette",
                "Andern", "Ezall", "Lucien", "Droco", "Cray", "Tymen", "Zenix", "Entranger",
                "Saenorath", "Chris", "Christine", "Marble", "Mable", "Ross", "Rose", "Xalgan ", "Kennet", "Aphmau"
        };
        name = names[new Random().nextInt(names.length)];
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Name", name);
        nbttagcompound.setString("Title", title);
        nbttagcompound.setString("SkinUrl", url);
        nbttagcompound.setString("Texture", texture);
        nbttagcompound.setString("CloakTexture", cloakTexture);
        nbttagcompound.setString("GlowTexture", glowTexture);
        nbttagcompound.setByte("UsingSkinUrl", skinType);

        if (this.playerProfile != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTUtil.writeGameProfile(nbttagcompound1, this.playerProfile);
            nbttagcompound.setTag("SkinUsername", nbttagcompound1);
        }

        nbttagcompound.setInteger("Size", modelSize);

        nbttagcompound.setInteger("ShowName", showName);
        nbttagcompound.setInteger("SkinColor", skinColor);
        nbttagcompound.setInteger("NpcVisible", visible);

        nbttagcompound.setBoolean("NoLivingAnimation", disableLivingAnimation);
        nbttagcompound.setBoolean("IsStatue", noHitbox);
        nbttagcompound.setByte("BossBar", showBossBar);
        nbttagcompound.setInteger("BossColor", bossColor.ordinal());

        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        setName(nbttagcompound.getString("Name"));
        title = nbttagcompound.getString("Title");

        int prevSkinType = skinType;
        String prevTexture = texture;
        String prevUrl = url;
        String prevPlayer = getSkinPlayer();

        url = nbttagcompound.getString("SkinUrl");
        skinType = nbttagcompound.getByte("UsingSkinUrl");
        texture = nbttagcompound.getString("Texture");
        cloakTexture = nbttagcompound.getString("CloakTexture");
        glowTexture = nbttagcompound.getString("GlowTexture");

        playerProfile = null;
        if (skinType == 1) {
            if (nbttagcompound.hasKey("SkinUsername", 10)) {
                this.playerProfile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkinUsername"));
            } else if (nbttagcompound.hasKey("SkinUsername", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkinUsername"))) {
                this.playerProfile = new GameProfile(null, nbttagcompound.getString("SkinUsername"));
            }
            this.loadProfile();
        }

        modelSize = ValueUtil.CorrectInt(nbttagcompound.getInteger("Size"), 1, 30);

        showName = nbttagcompound.getInteger("ShowName");

        if (nbttagcompound.hasKey("SkinColor"))
            skinColor = nbttagcompound.getInteger("SkinColor");

        visible = nbttagcompound.getInteger("NpcVisible");

        disableLivingAnimation = nbttagcompound.getBoolean("NoLivingAnimation");
        noHitbox = nbttagcompound.getBoolean("IsStatue");
        setBossbar(nbttagcompound.getByte("BossBar"));
        setBossColor(nbttagcompound.getInteger("BossColor"));

        if (prevSkinType != skinType || !texture.equals(prevTexture) || !url.equals(prevUrl) || !getSkinPlayer().equals(prevPlayer))
            npc.textureLocation = null;
        npc.textureGlowLocation = null;
        npc.textureCloakLocation = null;
        npc.updateHitbox();
    }

    public void loadProfile() {
        if (this.playerProfile != null && !StringUtils.isNullOrEmpty(this.playerProfile.getName()) && npc.getServer() != null) {
            if (!this.playerProfile.isComplete() || !this.playerProfile.getProperties().containsKey("textures")) {
                GameProfile gameprofile = npc.getServer().getPlayerProfileCache().getGameProfileForUsername(this.playerProfile.getName());

                if (gameprofile != null) {
                    Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

                    if (property == null) {
                        gameprofile = npc.getServer().getMinecraftSessionService().fillProfileProperties(gameprofile, true);
                    }

                    this.playerProfile = gameprofile;
                }
            }
        }
    }

    public boolean showName() {
        if (npc.isKilled())
            return false;
        return showName == 0 || (showName == 2 && npc.isAttacking());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (this.name.equals(name))
            return;
        this.name = name;
        npc.bossInfo.setName(npc.getDisplayName());
        npc.updateClient = true;
    }

    @Override
    public int getShowName() {
        return showName;
    }

    @Override
    public void setShowName(int type) {
        if (type == showName)
            return;
        this.showName = ValueUtil.CorrectInt(type, 0, 2);
        npc.updateClient = true;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        if (this.title.equals(title))
            return;
        this.title = title;
        npc.updateClient = true;
    }

    @Override
    public String getSkinUrl() {
        return url;
    }

    @Override
    public void setSkinUrl(String url) {
        if (this.url.equals(url))
            return;
        this.url = url;
        if (url.isEmpty())
            skinType = 0;
        else
            skinType = 2;
        npc.updateClient = true;
    }


    @Override
    public String getSkinPlayer() {
        return playerProfile == null ? "" : playerProfile.getName();
    }

    @Override
    public void setSkinPlayer(String name) {
        if (name == null || name.isEmpty()) {
            playerProfile = null;
            skinType = 0;
        } else {
            playerProfile = new GameProfile(null, name);
            skinType = 1;
        }
        npc.updateClient = true;
    }


    @Override
    public String getSkinTexture() {
        return texture;
    }

    @Override
    public void setSkinTexture(String texture) {
        if (this.texture.equals(texture))
            return;
        this.texture = texture.toLowerCase();
        npc.textureLocation = null;
        skinType = 0;
        npc.updateClient = true;
    }

    @Override
    public String getOverlayTexture() {
        return glowTexture;
    }

    @Override
    public void setOverlayTexture(String texture) {
        if (this.glowTexture.equals(texture))
            return;
        this.glowTexture = texture;
        npc.textureGlowLocation = null;
        npc.updateClient = true;
    }

    @Override
    public String getCapeTexture() {
        return cloakTexture;
    }

    @Override
    public void setCapeTexture(String texture) {
        if (this.cloakTexture.equals(texture))
            return;
        this.cloakTexture = texture.toLowerCase();
        npc.textureCloakLocation = null;
        npc.updateClient = true;
    }

    @Override
    public boolean getHasLivingAnimation() {
        return !disableLivingAnimation;
    }

    @Override
    public void setHasLivingAnimation(boolean enabled) {
        disableLivingAnimation = !enabled;
        npc.updateClient = true;
    }

    @Override
    public int getBossbar() {
        return showBossBar;
    }

    @Override
    public void setBossbar(int type) {
        if (type == showBossBar)
            return;
        showBossBar = (byte) ValueUtil.CorrectInt(type, 0, 2);
        npc.bossInfo.setVisible(showBossBar == 1);
        npc.updateClient = true;
    }

    @Override
    public int getBossColor() {
        return bossColor.ordinal();
    }

    @Override
    public void setBossColor(int color) {
        if (color < 0 || color >= BossInfo.Color.values().length)
            throw new CustomNPCsException("Invalid Boss Color: " + color);
        bossColor = BossInfo.Color.values()[color];
        npc.bossInfo.setColor(bossColor);
    }

    @Override
    public int getVisible() {
        return visible;
    }

    @Override
    public void setVisible(int type) {
        if (type == visible)
            return;
        visible = ValueUtil.CorrectInt(type, 0, 2);
        npc.updateClient = true;
    }

    @Override
    public int getSize() {
        return modelSize;
    }

    @Override
    public void setSize(int size) {
        if (modelSize == size)
            return;
        modelSize = ValueUtil.CorrectInt(size, 1, 30);
        npc.updateClient = true;
    }

    @Override
    public void setModelScale(int part, float x, float y, float z) {
        ModelData modeldata = ((EntityCustomNpc) npc).modelData;
        ModelPartConfig model = null;
        if (part == 0)
            model = modeldata.getPartConfig(EnumParts.HEAD);
        else if (part == 1)
            model = modeldata.getPartConfig(EnumParts.BODY);
        else if (part == 2)
            model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
        else if (part == 3)
            model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
        else if (part == 4)
            model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
        else if (part == 5)
            model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);

        if (model == null)
            throw new CustomNPCsException("Unknown part: " + part);

        model.setScale(x, y, z);
        npc.updateClient = true;
    }

    @Override
    public float[] getModelScale(int part) {
        ModelData modeldata = ((EntityCustomNpc) npc).modelData;
        ModelPartConfig model = null;
        if (part == 0)
            model = modeldata.getPartConfig(EnumParts.HEAD);
        else if (part == 1)
            model = modeldata.getPartConfig(EnumParts.BODY);
        else if (part == 2)
            model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
        else if (part == 3)
            model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
        else if (part == 4)
            model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
        else if (part == 5)
            model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);

        if (model == null)
            throw new CustomNPCsException("Unknown part: " + part);

        return new float[]{model.scaleX, model.scaleY, model.scaleZ};
    }

    @Override
    public int getTint() {
        return skinColor;
    }

    @Override
    public void setTint(int color) {
        if (color == skinColor)
            return;
        this.skinColor = color;
        npc.updateClient = true;
    }

    @Override
    public void setModel(String id) {
        ModelData modeldata = ((EntityCustomNpc) npc).modelData;
        if (id == null) {
            if (modeldata.entityClass == null)
                return;
            modeldata.entityClass = null;
            npc.updateClient = true;
        } else {
            ResourceLocation resource = new ResourceLocation(id);
            Entity entity = EntityList.createEntityByIDFromName(resource, this.npc.world);
            if (entity == null)
                throw new CustomNPCsException("Failed to create an entity from given id: " + id);

            modeldata.setEntityName(entity.getClass().getCanonicalName());
            npc.updateClient = true;
        }
    }

    @Override
    public String getModel() {
        ModelData modeldata = ((EntityCustomNpc) npc).modelData;
        if (modeldata.entityClass == null)
            return null;

        String name = modeldata.entityClass.getCanonicalName();
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValuesCollection()) {
            Class<? extends Entity> c = ent.getEntityClass();
            if (c.getCanonicalName().equals(name) && EntityLivingBase.class.isAssignableFrom(c)) {
                return ent.getRegistryName().toString();
            }
        }
        return null;
    }

    @Override
    public boolean getHasHitbox() {
        return !noHitbox;
    }

    @Override
    public void setHasHitbox(boolean bo) {
        if (noHitbox != bo)
            return;
        this.noHitbox = !bo;
        npc.updateClient = true;
    }


}
