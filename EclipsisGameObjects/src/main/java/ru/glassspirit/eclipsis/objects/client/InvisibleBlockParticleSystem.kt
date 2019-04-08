package ru.glassspirit.eclipsis.objects.client

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.modules.DepthSortModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.glassspirit.eclipsis.kotlin.getBlocksWithinAABB
import ru.glassspirit.eclipsis.objects.EclipsisBlocks
import ru.glassspirit.eclipsis.objects.EclipsisGameObjectsMod


@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = EclipsisGameObjectsMod.MODID)
object InvisibleBlockParticleSystemListener {
    private var lastSpawnedTick = 0L

    @JvmStatic
    @SubscribeEvent
    fun renderTick(event: RenderWorldLastEvent) {
        if (Minecraft().world == null) return
        if (Minecraft().player == null) return
        if (Minecraft().world.totalWorldTime - lastSpawnedTick > 19) {
            val player = Minecraft().player
            if (player.heldItemMainhand.item == EclipsisBlocks.borderBlock.itemForm
                    || player.heldItemMainhand.item == EclipsisBlocks.borderLightBlock.itemForm
                    || player.heldItemMainhand.item == EclipsisBlocks.lightBlock.itemForm
                    || player.heldItemMainhand.item == EclipsisBlocks.emptyBlock.itemForm) {

                val map = Minecraft().world.getBlocksWithinAABB(player.entityBoundingBox.grow(10.0)).filterNot { it.value.material == Material.AIR }
                map.filter { it.value.block == EclipsisBlocks.borderBlock }.forEach {
                    BorderBlockParticleSystem.spawn(20.0, 1.0, vec(it.key.x, it.key.y, it.key.z))
                }
                map.filter { it.value.block == EclipsisBlocks.borderLightBlock }.forEach {
                    BorderLightBlockParticleSystem.spawn(20.0, 1.0, vec(it.key.x, it.key.y, it.key.z))
                }
                map.filter { it.value.block == EclipsisBlocks.lightBlock }.forEach {
                    LightBlockParticleSystem.spawn(20.0, 1.0, vec(it.key.x, it.key.y, it.key.z))
                }
                map.filter { it.value.block == EclipsisBlocks.emptyBlock }.forEach {
                    EmptyBlockParticleSystem.spawn(20.0, 1.0, vec(it.key.x, it.key.y, it.key.z))
                }
                lastSpawnedTick = Minecraft().world.totalWorldTime
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private abstract class InvisibleBlockParticleSystem(val rl: ResourceLocation) : ParticleSystem() {
        override fun configure() {
            val size = bind(1)
            val position = bind(3)
            val depth = bind(1)

            this.globalUpdateModules.add(DepthSortModule(
                    position,
                    depth
            ))
            this.renderModules.add(SpriteRenderModule(
                    sprite = rl,
                    enableBlend = true,
                    position = position,
                    size = size,
                    depthMask = true
            ))
        }

        fun spawn(lifetime: Double, size: Double, pos: Vec3d) {
            this.addParticle(lifetime, size, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
        }
    }

    @SideOnly(Side.CLIENT)
    private object BorderBlockParticleSystem : InvisibleBlockParticleSystem("eclipsis_objects:textures/items/border.png".toRl())

    @SideOnly(Side.CLIENT)
    private object BorderLightBlockParticleSystem : InvisibleBlockParticleSystem("eclipsis_objects:textures/items/border_light.png".toRl())

    @SideOnly(Side.CLIENT)
    private object LightBlockParticleSystem : InvisibleBlockParticleSystem("eclipsis_objects:textures/items/light.png".toRl())

    @SideOnly(Side.CLIENT)
    private object EmptyBlockParticleSystem : InvisibleBlockParticleSystem("eclipsis_objects:textures/items/empty.png".toRl())
}
