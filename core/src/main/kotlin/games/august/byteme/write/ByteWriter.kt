package games.august.byteme.write

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation

/**
 * Performs the specific operations required to [putNumber] and [putBytes] to return whatever type [T] is with [build].
 *
 * e.g. a Netty `ByteBuf`, or a Kotlin `ByteArray`. They don't share any super type, so [T] will have to do.
 */
interface ByteWriter<T> {
    fun putNumber(
        value: Int,
        numBytes: Int,
        transformation: Transformation = Transformation.None,
        endian: Endian,
    )

    fun putBytes(bytes: ByteArray, byteOrder: ByteOrder = ByteOrder.None)
    fun build(): T
}