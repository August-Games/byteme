# byteme

[![PUBLISH](https://github.com/August-Games/byteme/actions/workflows/publish.yml/badge.svg)](https://github.com/August-Games/byteme/actions/workflows/publish.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/games.august/byteme.core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/games.august/byteme-core)

At this point, this just contains a DSL for creating a `ByteArray`, including options for specifying [Transformation](core/src/main/kotlin/games/august/byteme/common/Transformation.kt) or [Endian](core/src/main/kotlin/games/august/byteme/common/Endian.kt)ness when writing individual numbers, or [ByteOrder](core/src/main/kotlin/games/august/byteme/common/ByteOrder.kt) when writing the contents of an entire `ByteArray`.

## Example

See [Writing `ByteArray`s](#writing-bytearrays) and [Writing numbers](#writing-numbers) for more information. For further examples, refer to [the unit tests](core/src/test/kotlin/games/august/byteme/write).

Note: the invocations of `put1`/`put2`/etc. are using the default values for `Transformation` and `Endian` which are `Transformation.None` and `Endian.Little`.

```kotlin
val bytes1: ByteArray = writeByteArray {
    put1(100)
    putBytes(byteArrayOf(1, 2, 3), ByteOrder.Reversed)
    put3(100, Transformation.Add, Endian.Big)
}
println(
    """
    1:
        ${bytes1.contentToString()}
        ${bytes1.joinToString { it.toBinaryString() }}
    """.trimIndent()
)
// Output:
// 1:
//    [100, 3, 2, 1, 0, 0, -28]
//    01100100, 00000011, 00000010, 00000001, 00000000, 00000000, 11100100

val bytes2: ByteArray = writeByteArray {
    // Consider Int.MAX_VALUE is [A, B, C, D]
    put1(Int.MAX_VALUE) // D
    put2(Int.MAX_VALUE) // C, D
    put3(Int.MAX_VALUE) // B, C, D
    put4(Int.MAX_VALUE) // A, B, C, D
}
println(
    """
    2:
        ${bytes2.contentToString()}
        ${bytes2.joinToString { it.toBinaryString() }}
    """.trimIndent()
)
// Output:
// 2:
//    [-1, -1, -1, -1, -1, -1, -1, -1, -1, 127]
//    11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 11111111, 01111111
```

## Writing `ByteArray`s

### [ByteOrder](core/src/main/kotlin/games/august/byteme/common/ByteOrder.kt)

Specifying a `ByteOrder` when writing a `ByteArray` will affect the order in which the bytes are written to the `ByteArray`.

Consider the following `ByteArray`:
```
[0x12, 0x34, 0x56, 0x78, 0xAB, 0xCD]
```

---

`ByteOrder.None` will write the bytes in the same order:

```
[0x12, 0x34, 0x56, 0x78, 0xAB, 0xCD]
```

---

`ByteOrder.Reverse` will write the bytes in reverse order:

```
[0xCD, 0xAB, 0x78, 0x56, 0x34, 0x12]
```

## Writing numbers

### [Endian](core/src/main/kotlin/games/august/byteme/common/Endian.kt)ness

Specifying endianness when writing numbers will affect the order in which the bytes are written to the `ByteArray`. For example, the number `0x12345678` will be written as `12 34 56 78` in big endian, and `78 56 34 12` in little endian.

Refer to [Endian](core/src/main/kotlin/games/august/byteme/common/Endian.kt) KDoc for more information about the other options.

This can be used in combination with [Transformation](#transformation).

### [Transformation](core/src/main/kotlin/games/august/byteme/common/Transformation.kt).

Specifying a transformation when writing numbers will affect the value of the number before it is written to the `ByteArray`.

Consider a 4-byte number with the following bytes:
```
0b00110011
0b10101111
0b00000000
0b11111111
```

`Transformation.Add` will add `128` to the last byte of the number before writing the bytes to the `ByteArray`.

```
// The decimal value of the number is 464,453,887
[00110011, 10101111, 00000000, 11111111] = 464,453,887

// Add 128 to the final byte
[
    00110011,
    10101111,
    00000000,
    01111111, // 0b11111111 + 128 == 383 == 0b00000001_01111111
]

// The following bytes will be written to the ByteArray (Big Endian)
[00011011, 10101111, 00000000, 01111111]
```

Refer to [Transformation](core/src/main/kotlin/games/august/byteme/common/Transformation.kt) KDoc for more information about the other options.

This can be used in combination with [Endianness](#endianness).
