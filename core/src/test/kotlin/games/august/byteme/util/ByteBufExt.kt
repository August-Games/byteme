package games.august.byteme.util

import io.netty.buffer.ByteBuf

fun ByteBuf.toByteArraySafe(): ByteArray {
    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.readerIndex(), bytes)

    return bytes
}
