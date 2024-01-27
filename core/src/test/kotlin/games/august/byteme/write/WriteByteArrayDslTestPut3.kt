package games.august.byteme.write

import com.google.common.truth.Truth.assertThat
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class WriteByteArrayDslTestPut3 {
    private val a = 0b00110011.toByte()
    private val b = 0b10101111.toByte()
    private val c = 0b00000000.toByte()
    private val d = 0b11111111.toByte()
    private val packed3Byte = 3387136   // 00110011_10101111_00000000          (ABC)
    private val packed4Byte = 867107071 // 00110011_10101111_00000000_11111111 (ABCD)
    @TestFactory
    fun `Test put3 Endian ordering Transformation#None`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val inputs: List<Int>,
            val expectedBigEndianOutput: ByteArray,
            val expectedLittleEndianOutput: ByteArray,
            val expectedMiddleEndianOutput: ByteArray,
            val expectedInverseMiddleEndianOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "Three-byte int",
                inputs = listOf(packed3Byte),
                expectedBigEndianOutput = byteArrayOf(a, b, c),
                expectedLittleEndianOutput = byteArrayOf(c, b, a),
                expectedMiddleEndianOutput = byteArrayOf(a, c, b),
                expectedInverseMiddleEndianOutput = byteArrayOf(c, a, b),
            ),
            TestCase(
                name = "Four-byte int",
                inputs = listOf(packed4Byte),
                // NOTE: The first byte is ignored, because put3 only takes from the least significant 3 bytes.
                expectedBigEndianOutput = byteArrayOf(b, c, d),
                expectedLittleEndianOutput = byteArrayOf(d, c, b),
                expectedMiddleEndianOutput = byteArrayOf(b, d, c),
                expectedInverseMiddleEndianOutput = byteArrayOf(d, b, c),
            ),
        ).flatMap {
            listOf(
                dynamicTest("(BE): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put3(input, endian = Endian.Big) }
                        }
                    ).isEqualTo(it.expectedBigEndianOutput)
                },
                dynamicTest("(LE): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put3(input, endian = Endian.Little) }
                        }
                    ).isEqualTo(it.expectedLittleEndianOutput)
                },
                dynamicTest("(ME): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put3(input, endian = Endian.Middle) }
                        }
                    ).isEqualTo(it.expectedMiddleEndianOutput)
                },
                dynamicTest("(IME): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put3(input, endian = Endian.InverseMiddle) }
                        }
                    ).isEqualTo(it.expectedInverseMiddleEndianOutput)
                },
            )
        }
    }

    @TestFactory
    fun `Test put3 Transformation#Add`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )

        return listOf(
            TestCase(
                name = "$packed3Byte",
                input = packed3Byte,
                expectedOutput = byteArrayOf(
                    a,
                    b,
                    0b10000000.toByte(), // 0b00000000 + 128 == 0b10000000
                ),
            ),
            TestCase(
                name = "$packed4Byte",
                input = packed4Byte,
                expectedOutput = byteArrayOf(
                    // a, // Most significant byte is ignored because using put3
                    b,
                    c,
                    0b01111111.toByte(), // 0b11111111 + 128 == 383 == 0b00000001_01111111
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put3(it.input, transformation = Transformation.Add, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put3 Transformation#Negate`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "$packed3Byte",
                input = packed3Byte,
                expectedOutput = byteArrayOf(
                    a,
                    b,
                    0b00000000.toByte(), // negate 0b00000000 == 0b00000000
                ),
            ),
            TestCase(
                name = "$packed4Byte",
                input = packed4Byte,
                expectedOutput = byteArrayOf(
//                    a, // ignore most significant byte because using put3
                    b,
                    c,
                    0b00000001.toByte(), // negate 0b11111111 == 0b00000001
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put3(it.input, transformation = Transformation.Negate, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put3 Transformation#Subtract`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "$packed3Byte",
                input = packed3Byte,
                expectedOutput = byteArrayOf(
                    a,
                    b,
                    0b10000000.toByte(), // 128 - 0b00000000 == 0b10000000
                ),
            ),
            TestCase(
                name = "$packed4Byte",
                input = packed4Byte,
                expectedOutput = byteArrayOf(
//                    a, // ignore most significant byte because using put3
                    b,
                    c,
                    0b10000001.toByte(), // 128 - 0b11111111 == 0b10000001
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put3(it.input, transformation = Transformation.Subtract, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }
}
