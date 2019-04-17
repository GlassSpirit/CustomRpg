package noppes.npcs.api.wrapper;

import net.minecraft.world.DimensionType;
import noppes.npcs.api.IDimension;

public class DimensionWrapper implements IDimension {

    private int id;

    private DimensionType type;

    public DimensionWrapper(int id, DimensionType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public String getSuffix() {
        return type.getSuffix();
    }

}
