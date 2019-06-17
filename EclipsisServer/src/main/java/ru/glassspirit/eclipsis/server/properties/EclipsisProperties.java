package ru.glassspirit.eclipsis.server.properties;

import cz.neumimto.rpg.properties.Property;
import cz.neumimto.rpg.properties.PropertyContainer;

@PropertyContainer
public class EclipsisProperties {

    @Property(name = "accuracy", default_ = 100f)
    public static int accuracy;

    @Property(name = "dodge", default_ = 0)
    public static int dodge;

    @Property(name = "crit_chance", default_ = 0)
    public static int crit_chance;

    @Property(name = "crit_mult", default_ = 1f)
    public static int crit_mult;


}
