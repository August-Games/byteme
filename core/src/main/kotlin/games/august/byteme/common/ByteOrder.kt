package games.august.byteme.common

/**
 * Represents the ordering of bytes in a sequence.
 * A separate concept to [Endian] because it applies to the entire sequence as a whole,
 * whereas [Endian] applies to each individual number.
 */
enum class ByteOrder {
    /**
     * None: No reordering.
     */
    None,

    /**
     * Reversed order: Every byte in reversed order.
     * For a 4-byte integer (ABCD), the order is: DCBA.
     * For a big mish-mash of bytes (ABCDEFGH), the order is: HGFEDCBA.
     *
     * Note: This is similar to Little-endian but applies to the entire sequence as a whole.
     * Little-endian is usually used for ordering of bytes for a single number.
     */
    Reversed,
}
