package games.august.byteme.write

import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation

/**
 * If [int] is less than 128, it will be written as a single byte.
 * If [int] is greater than 128, it will be written as a short with 0x8000 added to it.
 */
fun WriteByteArrayDsl.WriteByteArrayBuilder.putSmart(
    int: Int,
    transformation: Transformation = Transformation.None,
    endian: Endian = Endian.Big,
) {
    if (int < 128) {
        put1(int, transformation = transformation)
    } else {
        put2(int + 0x8000, transformation = transformation, endian = endian)
    }
}

/**
 * Writes a string to the byte array, ending with a null byte.
 */
fun WriteByteArrayDsl.WriteByteArrayBuilder.putString(string: String) {
    val chars = string.toCharArray()
    for (c in chars) {
        put1(c.code)
    }
    put1(0)
}
