package top.e404.keepaccounts.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import top.e404.keepaccounts.util.primitive
import java.math.BigDecimal

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor = primitive(PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = BigDecimal(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: BigDecimal) =
        encoder.encodeString(value.toString())
}