package shouldersurfing.math

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import org.lwjgl.util.vector.Vector2f

import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * @author Joshua Powers <jsh.powers></jsh.powers>@yahoo.com>
 * @version 1.0
 * @since 2012-12-24
 */
@SideOnly(Side.CLIENT)
object VectorConverter {
    /**
     * Converts a Minecraft world coordinate to a screen coordinate
     *
     *
     * The world coordinate is the absolute location of a 3d vector in the
     * Minecraft world relative to the world origin.
     *
     *
     * Note that the return value will be scaled to match the current GUI
     * resolution of Minecraft.
     *
     * @param v3 [Vec3d] representing a coordinate in the Minecraft world
     * @return Returns a [Vector2f] representing a 2D location on the
     * screen, or null if the vector fails to be converted.
     */
    fun project2D(v3: Vec3d): Vector2f? {
        return project2D(v3.x.toFloat(), v3.y.toFloat(), v3.z.toFloat())
    }

    /**
     * Converts a Minecraft world coordinate to a screen coordinate
     *
     *
     * The world coordinate is the absolute location of a 3d vector in the
     * Minecraft world relative to the world origin.
     *
     *
     * Note that the return value will be scaled to match the current GUI
     * resolution of Minecraft.
     *
     * @param x X coordinate in the Minecraft world
     * @param y Y coordinate in the Minecraft world
     * @param z Z coordinate in the Minecraft world
     * @return Returns a [Vector2f] representing a 2D location on the
     * screen, or null if the vector fails to be converted.
     */
    fun project2D(x: Float, y: Float, z: Float): Vector2f? {
        /**
         * Buffer that will hold the screen coordinates
         */
        val screen_coords = GLAllocation.createDirectFloatBuffer(3)

        /**
         * Buffer that holds the transformation matrix of the view port
         */
        val viewport = GLAllocation.createDirectIntBuffer(16)

        /**
         * Buffer that holds the transformation matrix of the model view
         */
        val modelview = GLAllocation.createDirectFloatBuffer(16)

        /**
         * Buffer that holds the transformation matrix of the projection
         */
        val projection = GLAllocation.createDirectFloatBuffer(16)

        screen_coords.clear()
        modelview.clear()
        projection.clear()
        viewport.clear()

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview)
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)

        /**
         * the return value of the gluProject call
         */
        val ret = GLU.gluProject(x, y, z, modelview, projection, viewport, screen_coords)

        return if (ret) {
            Vector2f(screen_coords.get(0), screen_coords.get(1))
        } else null

    }
}
