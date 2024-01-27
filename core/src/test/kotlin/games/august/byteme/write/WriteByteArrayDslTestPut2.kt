package games.august.byteme.write

import com.google.common.truth.Truth.assertThat
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class WriteByteArrayDslTestPut2 {

    @TestFactory
    fun `Test put2 Endian ordering Transformation#None`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val inputs: List<Int>,
            val expectedBigEndianOutput: ByteArray,
            val expectedLittleEndianOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "Two-byte int",
                inputs = listOf(0b10101111_00000000),
                expectedBigEndianOutput = byteArrayOf(
                    0b10101111.toByte(),
                    0b00000000.toByte(),
                ),
                expectedLittleEndianOutput = byteArrayOf(
                    0b00000000.toByte(),
                    0b10101111.toByte(),
                ),
            ),
            TestCase(
                name = "Three-byte int",
                inputs = listOf(0b00110011_10101111_00000000),
                // NOTE: The first byte is ignored, because put2 only takes from the least significant 2 bytes.
                expectedBigEndianOutput = byteArrayOf(
                    0b10101111.toByte(),
                    0b00000000.toByte(),
                ),
                expectedLittleEndianOutput = byteArrayOf(
                    0b00000000.toByte(),
                    0b10101111.toByte(),
                ),
            ),
            TestCase(
                name = "Max, min, min",
                inputs = listOf(Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE),
                expectedBigEndianOutput = byteArrayOf(
                    0b11111111.toByte(),
                    0b11111111.toByte(),
                    0b00000000.toByte(),
                    0b00000000.toByte(),
                    0b00000000.toByte(),
                    0b00000000.toByte(),
                ),
                // NOTE: Same output as Big Endian, because endianness applies to each number individually.
                expectedLittleEndianOutput = byteArrayOf(
                    0b11111111.toByte(),
                    0b11111111.toByte(),
                    0b00000000.toByte(),
                    0b00000000.toByte(),
                    0b00000000.toByte(),
                    0b00000000.toByte(),
                ),
            ),
        ).flatMap {
            listOf(
                dynamicTest("(BE): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input ->
                                put2(input, endian = Endian.Big)
                            }
                        }
                    ).isEqualTo(it.expectedBigEndianOutput)
                },
                dynamicTest("(LE): ${it.name}") {
                    assertThat(
                        writeByteArray {
                            it.inputs.forEach { input ->
                                put2(input, endian = Endian.Little)
                            }
                        }
                    ).isEqualTo(it.expectedLittleEndianOutput)
                }
            )
        }
    }

    @TestFactory
    fun `Test put2 Transformation#Add`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "31186 (0b01111001_11010010)",
                input = 0b01111001_11010010,
                expectedOutput = byteArrayOf(
                    0b01111001.toByte(), // 0b01111001
                    0b01010010.toByte(), // 0b11010010 + 128 == 338 == 0b00000001_01010010 (First byte gets dropped after transformation)
                ),
            ),
            TestCase(
                name = "175 (0b00000000_10101111)",
                input = 0b00000000_10101111,
                expectedOutput = byteArrayOf(
                    0b00000000.toByte(), // 0b00000000
                    0b00101111.toByte(), // 0b10101111 + 128 == 0b00000001_00101111 (First byte gets dropped after transformation)
                ),
            ),
            TestCase(
                name = "44800 (0b10101111_00000000)",
                input = 0b10101111_00000000,
                expectedOutput = byteArrayOf(
                    0b10101111.toByte(), // 0b10101111
                    0b10000000.toByte(), // 0b10000000 + 128 == 128 == 0b10101111_10000000 (First byte gets dropped after transformation)
                ),
            ),
            TestCase(
                name = "Int.MAX_VALUE (0b11111111_11111111)",
                input = Int.MAX_VALUE,
                expectedOutput = byteArrayOf(
                    0b11111111.toByte(), // 0b11111111
                    0b01111111.toByte(), // 0b11111111 + 128 == 383 == 0b00000001_01111111
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put2(it.input, transformation = Transformation.Add, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put2 Transformation#Negate`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "175 (0b00000000_10101111)",
                input = 0b00000000_10101111,
                expectedOutput = byteArrayOf(
                    0b00000000.toByte(),
                    0b01010001.toByte(), // negate 0b10101111 == -175 == 0b11111111_11111111_11111111_01010001
                ),
            ),
            TestCase(
                name = "44800 (0b10101111_00000000)",
                input = 0b10101111_00000000,
                expectedOutput = byteArrayOf(
                    0b10101111.toByte(),
                    0b00000000.toByte(), // negate 0b00000000 == 0b00000000
                ),
            ),
            TestCase(
                name = "Int.MAX_VALUE (0b11111111_11111111)",
                input = Int.MAX_VALUE,
                expectedOutput = byteArrayOf(
                    0b11111111.toByte(),
                    0b00000001.toByte(), // negate 0b11111111 == -255 == 0b11111111_11111111_11111111_00000001
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put2(it.input, transformation = Transformation.Negate, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put2 Transformation#Subtract`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "175 (0b00000000_10101111)",
                input = 0b00000000_10101111,
                expectedOutput = byteArrayOf(
                    0b00000000.toByte(),
                    0b11010001.toByte(), // 128 - 0b00000000_10101111 == 128 - 175 == -47 == 0b11111111_11010001
                ),
            ),
            TestCase(
                name = "44800 (0b10101111_00000000)",
                input = 0b10101111_00000000,
                expectedOutput = byteArrayOf(
                    0b10101111.toByte(),
                    0b10000000.toByte(), // 128 - 00000000 == 128 == 0b10000000
                ),
            ),
            TestCase(
                name = "Int.MAX_VALUE (0b11111111_11111111)",
                input = Int.MAX_VALUE,
                expectedOutput = byteArrayOf(
                    0b11111111.toByte(),
                    0b10000001.toByte(), // 128 - 0b11111111 == -127 == 0b11111111_11111111_11111111_10000001
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put2(it.input, transformation = Transformation.Subtract, endian = Endian.Big)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }
}
