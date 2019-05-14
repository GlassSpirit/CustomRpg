package noppes.npcs.util;

import com.mojang.authlib.GameProfile;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.UUID;

public class GameProfileNpc extends GameProfile {
    private static final UUID id = UUID.fromString("c9c843f8-4cb1-4c82-aa61-e264291b7bd6");
    public EntityNPCInterface npc;

    public GameProfileNpc() {
        super(id, "[customnpcs]");
    }

    @Override
    public String getName() {
        if (npc == null)
            return super.getName();
        return npc.getName();
    }

    @Override
    public UUID getId() {
        if (npc == null)
            return id;
        return npc.getPersistentID();
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
