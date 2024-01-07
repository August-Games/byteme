package games.august.byteme.write

import com.google.common.truth.Truth.assertThat
import games.august.byteme.common.Endian
import games.august.byteme.common.Transformation
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

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

    @TestFactory
    fun `Test put1 Transformation#Add`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "175 (0b1010_1111)",
                input = 0b1010_1111,
                expectedOutput = byteArrayOf(
                    0b00101111.toByte(), // 0b00000000_10101111 + 128 == 303 == 0b00101111
                ),
            ),
            TestCase(
                name = "44800 (0b10101111_00000000)",
                input = 0b10101111_00000000,
                expectedOutput = byteArrayOf(
                    0b10000000.toByte(), // 0b10101111_00000000 + 128 == 44928 == 0b10000000
                ),
            ),
            TestCase(
                name = "Int.MAX_VALUE (0b11111111_11111111)",
                input = Int.MAX_VALUE,
                expectedOutput = byteArrayOf(
                    0b01111111.toByte(), // 0b11111111_11111111 + 128 == (overflow) 65663 == 0b01111111
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put1(it.input, transformation = Transformation.Add)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put1 Transformation#Negate`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "175 (0b1010_1111)",
                input = 0b1010_1111,
                expectedOutput = byteArrayOf(
                    0b01010001.toByte(), // -(0b10101111) == -175 == 0b01010001
                ),
            ),
            TestCase(
                name = "44800 (0b10101111_00000000)",
                input = 0b00000000,
                expectedOutput = byteArrayOf(
                    0b00000000.toByte(), // -(0b10101111_00000000) == -44800 == 0b00000000
                ),
            ),
            TestCase(
                name = "Int.MAX_VALUE (0b11111111_11111111)",
                input = Int.MAX_VALUE,
                expectedOutput = byteArrayOf(
                    0b00000001.toByte(), // -(0b11111111_11111111) == -65535 == 0b00000001
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put1(it.input, transformation = Transformation.Negate)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }

    @TestFactory
    fun `Test put1 Transformation#Subtract`(): List<DynamicTest> {
        class TestCase(
            val name: String,
            val input: Int,
            val expectedOutput: ByteArray,
        )
        return listOf(
            TestCase(
                name = "175 (0b1010_1111)",
                input = 0b1010_1111,
                expectedOutput = byteArrayOf(
                    0b11010001.toByte(), // // 128 - 0b00000000_10101111 == 128 - 175 == -47 == 0b11010001
                ),
            ),
            TestCase(
                name = "44800 (0b10101111_00000000)",
                input = 0b10101111_00000000,
                expectedOutput = byteArrayOf(
                    0b10000000.toByte(), // 128 - 0b10101111_00000000 == 128 - 44800 == -44672 == 0b10000000
                ),
            ),
            TestCase(
                name = "Int.MAX_VALUE (0b11111111_11111111)",
                input = Int.MAX_VALUE,
                expectedOutput = byteArrayOf(
                    0b10000001.toByte(), // 128 - 0b11111111_11111111 == 128 - 65535 == -65407 == 0b10000001
                ),
            ),
        ).map {
            dynamicTest("(BE): ${it.name}") {
                assertThat(
                    writeByteArray {
                        put1(it.input, transformation = Transformation.Subtract)
                    }
                ).isEqualTo(it.expectedOutput)
            }
        }
    }
}
