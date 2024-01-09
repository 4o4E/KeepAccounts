package top.e404.keepaccounts.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor

inline fun <reified T> KSerializer<T>.primitive(kind: PrimitiveKind) =
    PrimitiveSerialDescriptor(T::class.java.name, kind)