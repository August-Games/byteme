package games.august.byteme.write

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation
import io.netty.buffer.ByteBuf

class ByteBufByteWriter(private val buf: ByteBuf): ByteWriter<ByteBuf> {

    override fun putNumber(value: Int, numBytes: Int, transformation: Transformation, endian: Endian) {
        if (numBytes == 1) {
            buf.writeByte(applyTransformation(value, transformation))
            return
        }
        when (endian) {
            Endian.Big -> {
                for (shift in (numBytes - 1) downTo 0) {
                    val transformedByte = if (shift == 0) applyTransformation(value, transformation) else value
                    buf.writeByte(transformedByte shr (Byte.SIZE_BITS * shift))
                }
            }
            Endian.Little -> {
                for (shift in 0..<numBytes) {
                    val transformedByte = if (shift == 0) applyTransformation(value, transformation) else value
                    buf.writeByte(transformedByte shr (Byte.SIZE_BITS * shift))
                }
            }
            Endian.Middle -> {
                if (numBytes < 3 || numBytes > 4) error("Middle order requires between 3 and 4 bytes")
                if (numBytes == 3) {
                    buf.writeByte(value shr (Byte.SIZE_BITS * 2)) // A
                    buf.writeByte(value shr (Byte.SIZE_BITS * 0)) // C
                    buf.writeByte(value shr (Byte.SIZE_BITS * 1)) // B
                } else {
                    buf.writeByte(value shr (Byte.SIZE_BITS * 1)) // C
                    buf.writeByte(value shr (Byte.SIZE_BITS * 0)) // D
                    buf.writeByte(value shr (Byte.SIZE_BITS * 3)) // A
                    buf.writeByte(value shr (Byte.SIZE_BITS * 2)) // B
                }
            }

            Endian.InverseMiddle -> {
                if (numBytes < 3 || numBytes > 4) error("InverseMiddle order requires between 3 and 4 bytes")
                if (numBytes == 3) {
                    buf.writeByte(value shr (Byte.SIZE_BITS * 0)) // C
                    buf.writeByte(value shr (Byte.SIZE_BITS * 2)) // A
                    buf.writeByte(value shr (Byte.SIZE_BITS * 1)) // B
                } else {
                    buf.writeByte(value shr (Byte.SIZE_BITS * 2)) // B
                    buf.writeByte(value shr (Byte.SIZE_BITS * 3)) // A
                    buf.writeByte(value shr (Byte.SIZE_BITS * 0)) // D
                    buf.writeByte(value shr (Byte.SIZE_BITS * 1)) // C
                }
            }
        }
    }

    override fun putBytes(bytes: ByteArray, byteOrder: ByteOrder) {
        when (byteOrder) {
            ByteOrder.None -> buf.writeBytes(bytes)
            ByteOrder.Reversed -> buf.writeBytes(bytes.reversedArray())
        }
    }

    override fun build(): ByteBuf = buf
}