package shouldersurfing.renderer

import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.util.vector.Vector2f

/**
 * @author Joshua Powers <jsh.powers></jsh.powers>@yahoo.com>
 * @version 1.0
 * @since 2013-01-14
 *
 *
 * Storage of various variables the injected code will use
 */
@SideOnly(Side.CLIENT)
object ShoulderRenderBin {
    /**
     * The last thing our line-of-sight collided with
     */
    var rayTraceHit: Vec3d? = null
    var rayTraceInReach = false

    /**
     * Holds the last projected coordinates
     */
    var projectedVector: Vector2f? = null

    /**
     * Whether or not to render the player on the next pass, used for when the
     * camera is inside the player.
     */
    var skipPlayerRender = false
}
