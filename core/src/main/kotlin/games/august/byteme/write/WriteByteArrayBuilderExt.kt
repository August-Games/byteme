package games.august.byteme.write

/**
 * If [int] is less than 128, it will be written as a single byte.
 * If [int] is greater than 128, it will be written as a short with 0x8000 added to it.
 */
fun WriteByteArrayDsl.WriteByteArrayBuilder.putSmart(int: Int) {
    if (int < 128) {
        put1(int)
    } else {
        put2(int + 0x8000)
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
