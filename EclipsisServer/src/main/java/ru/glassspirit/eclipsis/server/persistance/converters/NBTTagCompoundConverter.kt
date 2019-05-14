package ru.glassspirit.eclipsis.server.persistance.converters

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class NBTTagCompoundConverter : AttributeConverter<NBTTagCompound, ByteArray> {
    override fun convertToDatabaseColumn(tag: NBTTagCompound?): ByteArray {
        tag?.let {
            val stream = ByteArrayOutputStream()
            CompressedStreamTools.writeCompressed(tag, stream)
            return stream.toByteArray()
        }
        return ByteArray(0)
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): NBTTagCompound {
        dbData?.let {
            val stream = ByteArrayInputStream(dbData)
            return CompressedStreamTools.readCompressed(stream)
        }
        return NBTTagCompound()
    }
}