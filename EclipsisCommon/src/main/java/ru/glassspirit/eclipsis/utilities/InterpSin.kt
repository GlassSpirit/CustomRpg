package ru.glassspirit.eclipsis.utilities

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import net.minecraft.util.math.Vec3d

class InterpSin(val point1: Vec3d, val point2: Vec3d, val period: Float = 1f, val amplitude: Float = 1f, val offset: Float = 0f) :
        InterpFunction<Vec3d> {
    private val disp = point2 - point1
    private val len = disp.length()


    override fun get(i: Float): Vec3d {
        return point1 + vec(disp.x * i, amplitude * Math.sin(offset + Math.PI * i.toDouble() / period), disp.z * i)
    }

}
