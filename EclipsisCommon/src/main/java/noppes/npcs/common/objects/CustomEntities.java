package noppes.npcs.common.objects;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.common.CustomNpcsConfig;
import noppes.npcs.common.entity.*;
import noppes.npcs.common.entity.old.*;

public class CustomEntities {

    private int newEntityStartId = 0;

    @SubscribeEvent
    public void register(RegistryEvent.Register<EntityEntry> event) {

        EntityEntry[] entries = {
                registerNpc(EntityNPCHumanMale.class, "npchumanmale"),
                registerNpc(EntityNPCVillager.class, "npcvillager"),
                registerNpc(EntityNpcPony.class, "npcpony"),
                registerNpc(EntityNPCHumanFemale.class, "npchumanfemale"),
                registerNpc(EntityNPCDwarfMale.class, "npcdwarfmale"),
                registerNpc(EntityNPCFurryMale.class, "npcfurrymale"),
                registerNpc(EntityNpcMonsterMale.class, "npczombiemale"),
                registerNpc(EntityNpcMonsterFemale.class, "npczombiefemale"),
                registerNpc(EntityNpcSkeleton.class, "npcskeleton"),
                registerNpc(EntityNPCDwarfFemale.class, "npcdwarffemale"),
                registerNpc(EntityNPCFurryFemale.class, "npcfurryfemale"),
                registerNpc(EntityNPCOrcMale.class, "npcorcfmale"),
                registerNpc(EntityNPCOrcFemale.class, "npcorcfemale"),
                registerNpc(EntityNPCElfMale.class, "npcelfmale"),
                registerNpc(EntityNPCElfFemale.class, "npcelffemale"),
                registerNpc(EntityNpcCrystal.class, "npccrystal"),
                registerNpc(EntityNpcEnderchibi.class, "npcenderchibi"),
                registerNpc(EntityNpcNagaMale.class, "npcnagamale"),
                registerNpc(EntityNpcNagaFemale.class, "npcnagafemale"),
                registerNpc(EntityNpcSlime.class, "NpcSlime"),
                registerNpc(EntityNpcDragon.class, "NpcDragon"),
                registerNpc(EntityNPCEnderman.class, "npcEnderman"),
                registerNpc(EntityNPCGolem.class, "npcGolem"),
                registerNpc(EntityCustomNpc.class, "CustomNpc"),
                registerNpc(EntityNPC64x32.class, "CustomNpc64x32"),
                registerNpc(EntityNpcAlex.class, "CustomNpcAlex"),
                registerNpc(EntityNpcClassicPlayer.class, "CustomNpcClassic"),

                registerNewentity("CustomNpcChairMount", 64, 10, false).entity(EntityChairMount.class).build(),
                registerNewentity("CustomNpcProjectile", 64, 3, true).entity(EntityProjectile.class).build()

        };

        event.getRegistry().registerAll(entries);
    }


    private EntityEntry registerNpc(Class<? extends Entity> cl, String name) {
        if (CustomNpcsConfig.FixUpdateFromPre_1_12) {
            ForgeRegistries.ENTITIES.register(new EntityEntry(cl, name).setRegistryName(new ResourceLocation("customnpcs." + name)));
        }
        return registerNewentity(name, 64, 3, true).entity(cl).build();
    }

    private <E extends Entity> EntityEntryBuilder<E> registerNewentity(String name, int range, int update, boolean velocity) {
        EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
        ResourceLocation registryName = new ResourceLocation("customnpcs", name);
        return builder.id(registryName, newEntityStartId++).name(name).tracker(range, update, velocity);
    }
}
