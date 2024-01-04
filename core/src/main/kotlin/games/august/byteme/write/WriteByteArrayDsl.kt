package games.august.byteme.write

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Endian.*
import games.august.byteme.common.Transformation
import games.august.byteme.common.Transformation.*

object WriteByteArrayDsl {

    fun writeByteArray(block: WriteByteArrayBuilder.() -> Unit): ByteArray {
        val builder = WriteByteArrayBuilder()
        builder.block()
        return builder.build()
    }

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

        fun put1(int: Int) {
            bytes.add(int.toByte())
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

        fun putBytes(bytes: ByteArray, byteOrder: ByteOrder) {
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
            val transformedValue = applyTransformation(value, transformation)
            val byteList = mutableListOf<Byte>()

            for (shift in (numBytes - 1) downTo 0) {
                byteList.add((transformedValue shr (Byte.SIZE_BITS * shift)).toByte())
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
