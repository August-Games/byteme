package dylan.byteme.write

import com.google.common.truth.Truth.assertThat
import dylan.byteme.common.Endian
import dylan.byteme.common.Transformation
import dylan.byteme.write.WriteByteArrayDsl.writeByteArray
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class `WriteByteArrayDslTest+put1` {
    @Test
    fun `Test put1 (Transformation#None)`() {
        assertThat(
            writeByteArray {
                put1(0b1010_1111)
            }
        ).isEqualTo(
            byteArrayOf(0b1010_1111.toByte()),
        )
        assertThat(
            writeByteArray {
                put1(Int.MAX_VALUE)
            }
        ).isEqualTo(
            byteArrayOf(0b1111_1111.toByte()),
        )
        assertThat(
            writeByteArray {
                put1(Int.MAX_VALUE)
                put1(Int.MIN_VALUE)
                put1(Int.MAX_VALUE)
            }
        ).isEqualTo(
            byteArrayOf(
                0b1111_1111.toByte(),
                0b0000_0000.toByte(),
                0b1111_1111.toByte(),
            ),
        )
    }
}
