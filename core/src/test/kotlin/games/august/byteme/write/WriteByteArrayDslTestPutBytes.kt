package games.august.byteme.write

import com.google.common.truth.Truth.assertThat
import games.august.byteme.common.ByteOrder
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class WriteByteArrayDslTestPutBytes {
    @TestFactory
    fun `Test putBytes`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: ByteArray,
            val byteOrder: ByteOrder,
            val expectedOutput: ByteArray,
        )

        val a = 0b00110011.toByte()
        val b = 0b10101111.toByte()
        val c = 0b00000000.toByte()
        val d = 0b11111111.toByte()
        val e = 0b11001100.toByte()
        val f = 0b10000000.toByte()
        val g = 0b00000001.toByte()
        return listOf(
            TestCase(
                name = "ABCD",
                input = byteArrayOf(a, b, c, d),
                byteOrder = ByteOrder.None,
                expectedOutput = byteArrayOf(a, b, c, d),
            ),
            TestCase(
                name = "ABCD reversed",
                input = byteArrayOf(a, b, c, d),
                byteOrder = ByteOrder.Reversed,
                expectedOutput = byteArrayOf(d, c, b, a),
            ),
            TestCase(
                name = "DCBA",
                input = byteArrayOf(d, c, b, a),
                byteOrder = ByteOrder.None,
                expectedOutput = byteArrayOf(d, c, b, a),
            ),
            TestCase(
                name = "DCBA reversed",
                input = byteArrayOf(d, c, b, a),
                byteOrder = ByteOrder.Reversed,
                expectedOutput = byteArrayOf(a, b, c, d),
            ),
            TestCase(
                name = "ABCDEFG",
                input = byteArrayOf(a, b, c, d, e, f, g),
                byteOrder = ByteOrder.None,
                expectedOutput = byteArrayOf(a, b, c, d, e, f, g),
            ),
            TestCase(
                name = "ABCDEFG reversed",
                input = byteArrayOf(a, b, c, d, e, f, g),
                byteOrder = ByteOrder.Reversed,
                expectedOutput = byteArrayOf(g, f, e, d, c, b, a),
            ),
        ).map {
            dynamicTest(it.name) {
                assertThat(
                    writeByteArray {
                        putBytes(it.input, byteOrder = it.byteOrder)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }
}
