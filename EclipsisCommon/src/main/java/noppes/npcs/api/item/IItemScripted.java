package noppes.npcs.api.item;

public interface IItemScripted extends IItemStack {

    boolean hasTexture(int damage);

    /**
     * @param damage
     * @return Returns the texture associated with this damage value
     */
    String getTexture(int damage);

    /**
     * All scripted items with the same damage value have the same texture.
     * To change the actual texture of the item call setItemDamage afterwards with the same damage value
     *
     * @param damage  The damage value
     * @param texture Texture you want this damage value to have
     */
    void setTexture(int damage, String texture);

    void setMaxStackSize(int size);

    /**
     * @return Returns a value between 0 and 1, 0 is an empty durability bar and 1 a full one
     */
    double getDurabilityValue();

    /**
     * @param value A value between 0 and 1, 0 is an empty durability bar and 1 a full one
     */
    void setDurabilityValue(float value);

    /**
     * @return Returns whether the durability is visible or not
     */
    boolean getDurabilityShow();

    /**
     * @param bo Set whether the durability is visible
     */
    void setDurabilityShow(boolean bo);

    /**
     * @return Returns the customly set durability color for the bar. If no custom value is set it will return -1
     */
    int getDurabilityColor();

    /**
     * @param color Set a custom color hex value for durability bar.
     */
    void setDurabilityColor(int color);

    /**
     * @return Returns the color of the item. -1 for no color
     */
    int getColor();

    /**
     * @param color Set a custom color hex value for the item tint. -1 to remove the color
     */
    void setColor(int color);
}
