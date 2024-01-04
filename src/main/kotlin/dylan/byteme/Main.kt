package dylan.byteme

import dylan.byteme.common.ByteOrder
import dylan.byteme.common.Endian
import dylan.byteme.common.Transformation
import dylan.byteme.write.WriteByteArrayDsl.writeByteArray

fun main() {
    val bytes = writeByteArray {
        put1(100)
        putBytes(byteArrayOf(1, 2, 3), ByteOrder.Reversed)
        put3(100, Transformation.Add, Endian.Big)
    }
}
