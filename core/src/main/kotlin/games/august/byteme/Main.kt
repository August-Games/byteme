package games.august.byteme

import games.august.byteme.common.ByteOrder
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation
import games.august.byteme.write.writeByteArray

fun main() {
    val bytes1: ByteArray = writeByteArray {
        put1(100)
        putBytes(byteArrayOf(1, 2, 3), ByteOrder.Reversed)
        put3(100, Transformation.Add, Endian.Big)
    }
    println(
        """
        1:
            ${bytes1.contentToString()}
            ${bytes1.joinToString { it.toBinaryString() }}
        """.trimIndent()
    )
    // Output:
    // 1:
    //    [100, 3, 2, 1, 0, 0, -28]
    //    01100100, 00000011, 00000010, 00000001, 00000000, 00000000, 11100100

    val bytes2: ByteArray = writeByteArray {
        // Consider Int.MAX_VALUE is [A, B, C, D]
        put1(Int.MAX_VALUE) // D
        put2(Int.MAX_VALUE) // C, D
        put3(Int.MAX_VALUE) // B, C, D
        put4(Int.MAX_VALUE) // A, B, C, D
    }
    println(
        """
        2:
            ${bytes2.contentToString()}
            ${bytes2.joinToString { it.toBinaryString() }}
        """.trimIndent()
    )
    // Output:
    // 2:
    //    [-1, -1, -1, -1, -1, -1, -1, -1, -1, 127]
    //    11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 01111111
}

private fun Byte.toBinaryString(): String {
    return String.format("%8s", Integer.toBinaryString(this.toInt() and 0xFF)).replace(' ', '0')
}
