package ru.glassspirit.eclipsis.client

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.BlendMode
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import java.awt.Color

object AccelerateAway {
    fun update(origin: Vec3d) {
        // details here aren't important. Just generate a list of points
        for (i in 0 until 50) {
            AccelerateAwaySystem.spawn(400.0, 1.0, origin.add(0.0, 2.0, 0.0),
                    Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1.0f),
                    Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1.0f)
            )
        }
    }
}

object AccelerateAwaySystem : ParticleSystem() {

    override fun configure() {
        val size = bind(1)
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val colorFrom = bind(4)
        val colorTo = bind(4)
        val color = bind(4)

        updateModules.add(BasicPhysicsUpdateModule(
                position,
                previousPosition,
                velocity = velocity,
                gravity = 0.05,
                enableCollision = true,
                bounciness = 0.8f,
                damping = 0.01f,
                friction = 0.01f
        ))
        updateModules.add(SetValueUpdateModule(color,
                EaseBinding(lifetime, age, easing = Easing.linear, bindingSize = 4, origin = colorFrom, target = colorTo)
        ))

        renderModules.add(SpriteRenderModule(
                sprite = ResourceLocation("minecraft", "textures/items/clay_ball.png"), // #balance,
                enableBlend = true,
                blendMode = BlendMode.ADDITIVE,
                previousPosition = previousPosition,
                position = position,
                color = color,
                size = size,
                depthMask = false
        ))
    }

    fun spawn(lifetime: Double, size: Double, pos: Vec3d, colorFrom: Color, colorTo: Color) {
        this.addParticle(lifetime,
                size,
                pos.x, pos.y, pos.z,
                pos.x, pos.y, pos.z,
                RandomUtils.nextDouble(0.0, 0.6) - 0.3, RandomUtils.nextDouble(0.25, 0.5), RandomUtils.nextDouble(0.0, 0.6) - 0.3, // velocity
                colorFrom.red / 255.0, colorFrom.green / 255.0, colorFrom.blue / 255.0, 1.0,
                colorTo.red / 255.0, colorTo.green / 255.0, colorTo.blue / 255.0, 0.0
        )
    }

}

