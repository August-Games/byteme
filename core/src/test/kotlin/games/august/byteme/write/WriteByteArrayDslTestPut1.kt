package games.august.byteme.write

import games.august.byteme.write.WriteByteArrayDsl.writeByteArray
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class WriteByteArrayDslTestPut1 {
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
