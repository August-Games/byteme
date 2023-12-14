package dylan.byteme.common

/**
 * Represents the ordering of bytes for a number.
 * Separate to [ByteOrder] because it applies to an individual number, and is used in different cases.
 */
enum class Endian {
    /**
     * Little-endian order: Least significant byte first.
     * For a 4-byte integer (ABCD), the order is: DCBA.
     */
    Little,

    /**
     * Big-endian order: Most significant byte first.
     * For a 4-byte integer (ABCD), the order is: ABCD.
     */
    Big,

    /**
     * Middle-endian order, also known as V1 order.
     * For a 3-byte number (ABC), the order is: ACB.
     * For a 4-byte integer (ABCD), the order is: CDAB.
     */
    Middle,

    /**
     * Inverse middle-endian order, also known as V2 order.
     * For a 3-byte number (ABC), the order is: CAB.
     * For a 4-byte integer (ABCD), the order is: BADC.
     */
    InverseMiddle,
}
