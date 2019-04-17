package shouldersurfing.math

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.vecmath.Vector2f

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
     * The world coordinate is the absolute location of a 3d vector in the
     * Minecraft world relative to the world origin.
     *
     *
     * Note that the return value will be scaled to match the current GUI
     * resolution of Minecraft.
     *
     * @param v3
     * [Vec3] representing a coordinate in the Minecraft world
     * @return Returns a [Vector2f] representing a 2D location on the
     * screen, or null if the vector fails to be converted.
     */
    fun project2D(v3: Vec3d): Vec2f? {
        return project2D(v3.x.toFloat(), v3.y.toFloat(), v3.z.toFloat())
    }

    /**
     * Converts a Minecraft world coordinate to a screen coordinate
     *
     * The world coordinate is the absolute location of a 3d vector in the
     * Minecraft world relative to the world origin.
     *
     *
     * Note that the return value will be scaled to match the current GUI
     * resolution of Minecraft.
     *
     * @param x
     * X coordinate in the Minecraft world
     * @param y
     * Y coordinate in the Minecraft world
     * @param z
     * Z coordinate in the Minecraft world
     * @return Returns a [Vector2f] representing a 2D location on the
     * screen, or null if the vector fails to be converted.
     */
    fun project2D(x: Float, y: Float, z: Float): Vec2f? {
        /**
         * Buffer that will hold the screen coordinates
         */
        val screenCoords = GLAllocation.createDirectFloatBuffer(3)

        /**
         * Buffer that holds the transformation matrix of the view port
         */
        val viewport = GLAllocation.createDirectByteBuffer(64).asIntBuffer()

        /**
         * Buffer that holds the transformation matrix of the model view
         */
        val modelview = GLAllocation.createDirectFloatBuffer(16)

        /**
         * Buffer that holds the transformation matrix of the projection
         */
        val projection = GLAllocation.createDirectFloatBuffer(16)

        screenCoords.clear()
        modelview.clear()
        projection.clear()
        viewport.clear()

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview)
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)

        if (gluProject(x, y, z, modelview, projection, viewport, screenCoords)) {
            val screenX = screenCoords.get(0)
            val screenY = screenCoords.get(1)

            if (!java.lang.Float.isInfinite(screenX) && !java.lang.Float.isInfinite(screenY)) {
                return Vec2f(screenX, screenY)
            }
        }

        return null
    }

    private fun multMatrixVecf(m: FloatBuffer, inArray: FloatArray, outArray: FloatArray) {
        for (i in 0..3) {
            outArray[i] = (inArray[0] * m.get(m.position() + i)
                    + inArray[1] * m.get(m.position() + 1 * 4 + i)
                    + inArray[2] * m.get(m.position() + 2 * 4 + i)
                    + inArray[3] * m.get(m.position() + 3 * 4 + i))

        }
    }

    private fun gluProject(objx: Float, objy: Float, objz: Float, modelMatrix: FloatBuffer, projMatrix: FloatBuffer, viewport: IntBuffer, win_pos: FloatBuffer): Boolean {
        val inArray = FloatArray(4)
        val outArray = FloatArray(4)

        inArray[0] = objx
        inArray[1] = objy
        inArray[2] = objz
        inArray[3] = 1.0f

        multMatrixVecf(modelMatrix, inArray, outArray)
        multMatrixVecf(projMatrix, outArray, inArray)

        if (inArray[3].toDouble() == 0.0) {
            return false
        }

        inArray[3] = 1.0f / inArray[3] * 0.5f

        // Map x, y and z to range 0-1
        inArray[0] = inArray[0] * inArray[3] + 0.5f
        inArray[1] = inArray[1] * inArray[3] + 0.5f
        inArray[2] = inArray[2] * inArray[3] + 0.5f

        // Map x,y to viewport
        win_pos.put(0, inArray[0] * viewport.get(viewport.position() + 2) + viewport.get(viewport.position() + 0))
        win_pos.put(1, inArray[1] * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1))
        win_pos.put(2, inArray[2])

        return true
    }
}