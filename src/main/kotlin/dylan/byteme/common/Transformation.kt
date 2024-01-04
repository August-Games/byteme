package dylan.byteme.common

enum class Transformation {
    /**
     * No transformation is applied.
     * The value is written and read as-is without any modification.
     */
    None,

    /**
     * Adds 128 to the value when written, subtracts 128 from the value when read.
     * Also known as type-A transformation.
     * This transformation shifts the value range, typically used to adjust the data's sign.
     * For example, a value of 0 becomes 128, and a value of 128 becomes 256 when written.
     */
    Add,

    /**
     * Negates the value.
     * Also known as type-C transformation.
     * This transformation inverts the value, turning positive values to negative and vice versa.
     * For example, a value of 100 becomes -100, and a value of -100 becomes 100.
     */
    Negate,

    /**
     * Subtracts the value from 128.
     * Also known as type-S transformation.
     * This transformation creates a mirrored value around the midpoint of 128.
     * For example, a value of 100 becomes 28 (128 - 100), and a value of 50 becomes 78 (128 - 50).
     */
    Subtract,
}
