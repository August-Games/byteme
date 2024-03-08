package games.august.byteme.write

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation

class ByteArrayByteWriter: ByteWriter<ByteArray> {
    private val bytes = mutableListOf<Byte>()
    override fun putNumber(value: Int, numBytes: Int, transformation: Transformation, endian: Endian) {
        val byteList = mutableListOf<Byte>()

        for (shift in (numBytes - 1) downTo 0) {
            // Only apply the transformation to the final byte.
            val transformedByte = if (shift == 0) applyTransformation(value, transformation) else value
            byteList.add((transformedByte shr (Byte.SIZE_BITS * shift)).toByte())
        }

        when (endian) {
            Endian.Big -> bytes.addAll(byteList)
            Endian.Little -> bytes.addAll(byteList.asReversed())
            Endian.Middle -> {
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

            Endian.InverseMiddle -> {
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

    override fun putBytes(bytes: ByteArray, byteOrder: ByteOrder) {
        this.bytes.addAll(
            when (byteOrder) {
                ByteOrder.None -> bytes.toList()
                ByteOrder.Reversed -> bytes.reversed()
            }
        )
    }

    override fun build(): ByteArray = bytes.toByteArray()
}