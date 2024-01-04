package games.august.byteme.write

import com.google.common.truth.Truth.assertThat
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation
import games.august.byteme.write.WriteByteArrayDsl.writeByteArray
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class WriteByteArrayDslTestPut4 {
    @TestFactory
    fun `Test put4 Endian ordering Transformation#None`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val inputs: List<Int>,
            val expectedBigEndianOutput: ByteArray,
            val expectedLittleEndianOutput: ByteArray,
            val expectedMiddleEndianOutput: ByteArray,
            val expectedInverseMiddleEndianOutput: ByteArray,
        )
        val a = 0b00110011
        val b = 0b10101111
        val c = 0b00000000
        val d = 0b11111111
        val packed3Byte = (a shl 16) or (b shl 8) or c
        val packed4Byte = (a shl 24) or (b shl 16) or (c shl 8) or d
        return listOf(
            TestCase(
                name = "Three-byte int",
                inputs = listOf(packed3Byte),
                // NOTE: lettering below is offset by 1 because packed3Byte has 0b00000000 byte inserted at front for put4.
                // i.e. a.toByte() is actually 'B'
                expectedBigEndianOutput = byteArrayOf(
                    0b0,
                    a.toByte(),
                    b.toByte(),
                    c.toByte(),
                ),
                expectedLittleEndianOutput = byteArrayOf(
                    c.toByte(),
                    b.toByte(),
                    a.toByte(),
                    0b0,
                ),
                expectedMiddleEndianOutput = byteArrayOf(
                    b.toByte(), // C
                    c.toByte(), // D
                    0b0, // A
                    a.toByte(), // B
                ),
                expectedInverseMiddleEndianOutput = byteArrayOf(
                    a.toByte(), // B
                    0b0, // A
                    c.toByte(), // D
                    b.toByte(), // C
                ),
            ),
            TestCase(
                name = "Four-byte int",
                inputs = listOf(packed4Byte),
                // NOTE: The first byte is ignored, because put4 only takes from the least significant 3 bytes.
                expectedBigEndianOutput = byteArrayOf(
                    a.toByte(),
                    b.toByte(),
                    c.toByte(),
                    d.toByte(),
                ),
                expectedLittleEndianOutput = byteArrayOf(
                    d.toByte(),
                    c.toByte(),
                    b.toByte(),
                    a.toByte(),
                ),
                expectedMiddleEndianOutput = byteArrayOf(
                    c.toByte(),
                    d.toByte(),
                    a.toByte(),
                    b.toByte(),
                ),
                expectedInverseMiddleEndianOutput = byteArrayOf(
                    b.toByte(),
                    a.toByte(),
                    d.toByte(),
                    c.toByte(),
                ),
            ),
        ).flatMap {
            listOf(
                dynamicTest("(BE): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put4(input, endian = Endian.Big) }
                        }
                    ).isEqualTo(it.expectedBigEndianOutput)
                },
                dynamicTest("(LE): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put4(input, endian = Endian.Little) }
                        }
                    ).isEqualTo(it.expectedLittleEndianOutput)
                },
                dynamicTest("(ME): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put4(input, endian = Endian.Middle) }
                        }
                    ).isEqualTo(it.expectedMiddleEndianOutput)
                },
                dynamicTest("(IME): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input -> put4(input, endian = Endian.InverseMiddle) }
                        }
                    ).isEqualTo(it.expectedInverseMiddleEndianOutput)
                },
            )
        }
    }

    @TestFactory
    fun `Test put4 Transformation#Add`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )

        // a = 0b00110011
        // b = 0b10101111
        // c = 0b00000000
        // d = 0b11111111
        val packed3Byte = 3387136   // 00110011_10101111_00000000          (ABC)
        val packed4Byte = 464453887 // 00011011_10101111_00000000_11111111 (ABCD)
        return listOf(
            TestCase(
                name = "$packed3Byte",
                input = packed3Byte,
                expectedOutput = byteArrayOf(
                    0b0,
                    0b00110011.toByte(), // 3387136 + 128 == 3387264 == 0b00000000_00110011_10101111_10000000
                    0b10101111.toByte(),
                    0b10000000.toByte(),
                ),
            ),
            TestCase(
                name = "$packed4Byte",
                input = packed4Byte,
                expectedOutput = byteArrayOf(
                    0b00011011.toByte(), // 464453887 + 128 == 464454015 == 0b00011011_10101111_00000001_01111111
                    0b10101111.toByte(),
                    0b00000001.toByte(),
                    0b01111111.toByte(),
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put4(it.input, transformation = Transformation.Add, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put4 Transformation#Negate`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )

        // a = 0b00110011
        // b = 0b10101111
        // c = 0b00000000
        // d = 0b11111111
        val packed3Byte = 3387136   // 00110011_10101111_00000000          (ABC)
        val packed4Byte = 464453887 // 00011011_10101111_00000000_11111111 (ABCD)
        return listOf(
            TestCase(
                name = "$packed3Byte",
                input = packed3Byte,
                expectedOutput = byteArrayOf(
                    0b11111111.toByte(), // negate 3387136 == -3387136 == 0b11111111_11001100_01010001_00000000
                    0b11001100.toByte(),
                    0b01010001.toByte(),
                    0b00000000.toByte(),
                ),
            ),
            TestCase(
                name = "$packed4Byte",
                input = packed4Byte,
                expectedOutput = byteArrayOf(
                    0b11100100.toByte(), // negate 464453887 == -464453887 == 0b11100100_01010000_11111111_00000001
                    0b01010000.toByte(),
                    0b11111111.toByte(),
                    0b00000001.toByte(),
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put4(it.input, transformation = Transformation.Negate, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put4 Transformation#Subtract`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )

        // a = 0b00110011
        // b = 0b10101111
        // c = 0b00000000
        // d = 0b11111111
        val packed3Byte = 3387136   // 00110011_10101111_00000000          (ABC)
        val packed4Byte = 464453887 // 00011011_10101111_00000000_11111111 (ABCD)
        return listOf(
            TestCase(
                name = "$packed3Byte",
                input = packed3Byte,
                expectedOutput = byteArrayOf(
                    0b11111111.toByte(), // 128 - 3387136 == -3387008 == 0b11111111_11001100_01010001_10000000
                    0b11001100.toByte(),
                    0b01010001.toByte(),
                    0b10000000.toByte(),
                ),
            ),
            TestCase(
                name = "$packed4Byte",
                input = packed4Byte,
                expectedOutput = byteArrayOf(
                    0b11100100.toByte(), // 128 - 464453887 == -464453759 == 0b11100100_01010000_11111111_10000001
                    0b01010000.toByte(),
                    0b11111111.toByte(),
                    0b10000001.toByte(),
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put4(it.input, transformation = Transformation.Subtract, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }
}
