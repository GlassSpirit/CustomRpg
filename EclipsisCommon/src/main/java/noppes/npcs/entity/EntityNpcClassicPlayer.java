package noppes.npcs.entity;

import net.minecraft.world.World;

public class EntityNpcClassicPlayer extends EntityCustomNpc {
    public EntityNpcClassicPlayer(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
    }
}
