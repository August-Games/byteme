package games.august.byteme.write

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Endian.*
import games.august.byteme.common.Transformation
import games.august.byteme.common.Transformation.*

fun bytes(block: WriteByteArrayDsl.WriteByteArrayBuilder.() -> Unit) = writeByteArray(block)
fun writeByteArray(block: WriteByteArrayDsl.WriteByteArrayBuilder.() -> Unit): ByteArray {
    val builder = WriteByteArrayDsl.WriteByteArrayBuilder()
    builder.block()
    return builder.build()
}

object WriteByteArrayDsl {

    @DslMarker
    annotation class WriteByteArrayDslMarker

    @WriteByteArrayDslMarker
    class WriteByteArrayBuilder {
        private val bytes = mutableListOf<Byte>()

        fun build(): ByteArray {
            return bytes.toByteArray()
        }

        fun put1(byte: Byte) {
            bytes.add(byte)
        }

        fun put1(
            int: Int,
            transformation: Transformation = None,
        ) {
            putNumber(int, 1, transformation, Little)
        }

        fun put2(
            int: Int,
            transformation: Transformation = None,
            endian: Endian = Little,
        ) = putNumber(int, 2, transformation, endian)

        fun put3(
            int: Int,
            transformation: Transformation = None,
            endian: Endian = Little,
        ) = putNumber(int, 3, transformation, endian)

        fun put4(
            int: Int,
            transformation: Transformation = None,
            endian: Endian = Little,
        ) = putNumber(int, 4, transformation, endian)

        fun putBytes(bytes: ByteArray, byteOrder: ByteOrder = ByteOrder.None) {
            this.bytes.addAll(
                when (byteOrder) {
                    ByteOrder.None -> bytes.toList()
                    ByteOrder.Reversed -> bytes.reversed()
                }
            )
        }

        private fun putNumber(
            value: Int,
            numBytes: Int,
            transformation: Transformation = None,
            endian: Endian = Little
        ) {
            val byteList = mutableListOf<Byte>()

            for (shift in (numBytes - 1) downTo 0) {
                // Only apply the transformation to the final byte.
                val transformedByte = if (shift == 0) applyTransformation(value, transformation) else value
                byteList.add((transformedByte shr (Byte.SIZE_BITS * shift)).toByte())
            }

            when (endian) {
                Big -> bytes.addAll(byteList)
                Little -> bytes.addAll(byteList.asReversed())
                Middle -> {
                    if (numBytes < 3 || numBytes > 4) error("Middle order requires between 3 and 4 bytes")
                    if (numBytes == 3) {
                        bytes.add(byteList[0]) // A
                        bytes.add(byteList[2]) // C
                        bytes.add(byteList[1]) // B
                    } else {
                        bytes.add(byteList[2]) // C
                        bytes.add(byteList[3]) // D
                        bytes.add(byteList[0]) // A
                        bytes.add(byteList[1]) // B
                    }
                }

                InverseMiddle -> {
                    if (numBytes < 3 || numBytes > 4) error("InverseMiddle order requires between 3 and 4 bytes")
                    if (numBytes == 3) {
                        bytes.add(byteList[2]) // C
                        bytes.add(byteList[0]) // A
                        bytes.add(byteList[1]) // B
                    } else {
                        bytes.add(byteList[1]) // B
                        bytes.add(byteList[0]) // A
                        bytes.add(byteList[3]) // D
                        bytes.add(byteList[2]) // C
                    }
                }
            }
        }

        private fun applyTransformation(value: Int, transformation: Transformation): Int {
            return when (transformation) {
                None -> value
                Add -> value + 128
                Negate -> -value
                Subtract -> 128 - value
            }
        }
    }
}
