package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.EntityTameable;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IPixelmon;
import noppes.npcs.controllers.PixelmonHelper;

public class PixelmonWrapper<T extends EntityTameable> extends AnimalWrapper<T> implements IPixelmon {

    public PixelmonWrapper(T entity) {
        super(entity);
    }

    @Override
    public Object getPokemonData() {
        return PixelmonHelper.getPokemonData(entity);
    }

    @Override
    public int getType() {
        return EntityType.PIXELMON;
    }
}
