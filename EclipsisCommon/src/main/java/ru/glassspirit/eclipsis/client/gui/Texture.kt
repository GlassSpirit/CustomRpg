package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.FileResourcePack
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.SplashProgress
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.asm.FMLSanityChecker
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.commons.io.IOUtils
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_BGRA
import org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV
import org.lwjgl.util.glu.GLU
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.IntBuffer
import javax.imageio.ImageIO

@SideOnly(Side.CLIENT)
class Texture(val location: ResourceLocation) {

    private val name: Int
    private var width: Int
    private var height: Int
    private val size: Int
    val frames: Int

    init {
        var s: InputStream? = null
        try {
            s = open(location)
            val stream = ImageIO.createImageInputStream(s)
            val readers = ImageIO.getImageReaders(stream)
            if (!readers.hasNext()) throw IOException("No suitable reader found for image $location")
            val reader = readers.next()
            reader.input = stream
            var frames = reader.getNumImages(true)
            var images = arrayOfNulls<BufferedImage>(frames)
            for (i in 0 until frames) {
                images[i] = reader.read(i)
            }
            reader.dispose()
            width = images[0]!!.width
            height = images[0]!!.height
            // Animation strip
            if (height > width && height % width == 0) {
                frames = height / width
                val original = images[0]!!
                height = width
                images = arrayOfNulls(frames)
                for (i in 0 until frames) {
                    images[i] = original.getSubimage(0, i * height, width, height)
                }
            }
            this.frames = frames
            var size = 1
            while (size / width * (size / height) < frames) size *= 2
            this.size = size
            glEnable(GL_TEXTURE_2D)
            synchronized(SplashProgress::class.java) {
                name = glGenTextures()
                glBindTexture(GL_TEXTURE_2D, name)
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, null as IntBuffer?)
            checkGLError("Texture creation")
            var i = 0
            while (i * (size / width) < frames) {
                var j = 0
                while (i * (size / width) + j < frames && j < size / width) {
                    buf.clear()
                    val image = images[i * (size / width) + j]!!
                    for (k in 0 until height) {
                        for (l in 0 until width) {
                            buf.put(image.getRGB(l, k))
                        }
                    }
                    buf.position(0).limit(width * height)
                    glTexSubImage2D(GL_TEXTURE_2D, 0, j * width, i * height, width, height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buf)
                    checkGLError("Texture uploading")
                    j++
                }
                i++
            }
            glBindTexture(GL_TEXTURE_2D, 0)
            glDisable(GL_TEXTURE_2D)
        } catch (e: IOException) {
            FMLLog.log.error("Error reading texture from file: {}", location, e)
            throw RuntimeException(e)
        } finally {
            IOUtils.closeQuietly(s)
        }
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, name)
    }

    fun delete() {
        glDeleteTextures(name)
    }

    fun getU(frame: Int = 0, u: Float): Float {
        return width * (frame % (size / width) + u) / size
    }

    fun getV(frame: Int = 0, v: Float): Float {
        return height * (frame / (size / width) + v) / size
    }

    fun texCoord(frame: Int = 0, u: Float, v: Float) {
        glTexCoord2f(getU(frame, u), getV(frame, v))
    }

    @Throws(IOException::class)
    private fun open(loc: ResourceLocation): InputStream {
        if (miscPack.resourceExists(loc)) {
            return miscPack.getInputStream(loc)
        } else if (fmlPack.resourceExists(loc)) {
            return fmlPack.getInputStream(loc)
        }
        return mcPack.getInputStream(loc)
    }

    private fun checkGLError(where: String) {
        val err = glGetError()
        if (err != 0) {
            throw IllegalStateException(where + ": " + GLU.gluErrorString(err))
        }
    }

    companion object {
        private val buf = BufferUtils.createIntBuffer(4 * 1024 * 1024)
        private val mcPack = Minecraft().defaultResourcePack
        private val fmlPack = createResourcePack(FMLSanityChecker.fmlLocation)
        private val miscPack = createResourcePack(File("resourcepacks"))

        private fun createResourcePack(file: File): IResourcePack {
            return if (file.isDirectory) {
                FolderResourcePack(file)
            } else {
                FileResourcePack(file)
            }
        }
    }
}