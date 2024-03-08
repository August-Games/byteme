package games.august.byteme.write

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Endian.Little
import games.august.byteme.common.Transformation
import games.august.byteme.common.Transformation.*
import io.netty.buffer.ByteBuf

fun ByteBuf.write(block: WriteByteArrayDsl.WriteByteArrayBuilder<ByteBuf>.() -> Unit): ByteBuf {
    val builder = WriteByteArrayDsl.WriteByteArrayBuilder(ByteBufByteWriter(this))
    builder.block()
    return builder.build()
}

fun bytes(block: WriteByteArrayDsl.WriteByteArrayBuilder<ByteArray>.() -> Unit) = writeByteArray(block)
fun writeByteArray(block: WriteByteArrayDsl.WriteByteArrayBuilder<ByteArray>.() -> Unit): ByteArray {
    val builder = WriteByteArrayDsl.WriteByteArrayBuilder(ByteArrayByteWriter())
    builder.block()
    return builder.build()
}

object WriteByteArrayDsl {

    @DslMarker
    annotation class WriteByteArrayDslMarker

    @WriteByteArrayDslMarker
    class WriteByteArrayBuilder<T>(
        private val byteWriter: ByteWriter<T>,
    ) {
        fun build(): T = byteWriter.build()
        fun put1(
            int: Int,
            transformation: Transformation = None,
        ) {
            byteWriter.putNumber(int, 1, transformation, Little)
        }

        fun put2(
            int: Int,
            transformation: Transformation = None,
            endian: Endian,
        ) = byteWriter.putNumber(int, 2, transformation, endian)

        fun put3(
            int: Int,
            transformation: Transformation = None,
            endian: Endian,
        ) = byteWriter.putNumber(int, 3, transformation, endian)

        fun put4(
            int: Int,
            transformation: Transformation = None,
            endian: Endian,
        ) = byteWriter.putNumber(int, 4, transformation, endian)

        fun putBytes(bytes: ByteArray, byteOrder: ByteOrder) {
            byteWriter.putBytes(bytes, byteOrder)
        }
    }
}

internal fun applyTransformation(value: Int, transformation: Transformation): Int {
    return when (transformation) {
        None -> value
        Add -> value + 128
        Negate -> -value
        Subtract -> 128 - value
    }
}
