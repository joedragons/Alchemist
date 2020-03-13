/*
 * Copyright (C) 2010-2020, Danilo Pianini and contributors
 * listed in the main project's alchemist/build.gradle.kts file.
 *
 * This file is part of Alchemist, and is distributed under the terms of the
 * GNU General Public License, with a linking exception,
 * as described in the file LICENSE in the Alchemist distribution's top directory.
 */

package it.unibo.alchemist.boundary.gui.view.property

import com.google.common.base.Charsets
import com.google.gson.reflect.TypeToken
import it.unibo.alchemist.boundary.gui.effects.json.EffectSerializer
import it.unibo.alchemist.boundary.gui.view.properties.*
import it.unibo.alchemist.test.TemporaryFile.create
import javafx.beans.property.Property
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.*
import java.lang.reflect.Type

class PropertySerializationTester<T: Property<E>, E: Any>(
        private val gsonType: Type,
        private val fromAny: (Any) -> T,
        vararg serializables: T
) {

    private val properties: List<T> = serializables.asList()

    companion object {
        private val GSON = EffectSerializer.getGSON()
    }

    fun testJavaSerialization() {
        val file = create()
        FileOutputStream(file).use { fout ->
            ObjectOutputStream(fout).use { oos ->
                FileInputStream(file).use { fin ->
                    ObjectInputStream(fin).use { ois ->
                        properties.forEach {
                            oos.writeObject(it)
                            val deserialized = fromAny(ois.readObject())
                            Assertions.assertEquals(it, deserialized, "Java serialization: ${message(it, deserialized)}")
                        }
                    }
                }
            }
        }
    }

    fun testGsonSerialization() {
        val file = create()
        FileWriter(file, Charsets.UTF_8).use { writer ->
            FileReader(file, Charsets.UTF_8).use { reader ->
                properties.forEach {
                    GSON.toJson(it, gsonType, writer)
                    val deserialized = GSON.fromJson<T>(reader, gsonType)
                    Assertions.assertEquals(it, deserialized, "Gson serialization: ${message(it, deserialized)}")
                }
            }
        }
    }

    private fun <T> message(origin: Property<T>?, deserialized: Property<T>?): String? {
        if (origin == null) {
            return "original property is null"
        }
        return if (deserialized == null) {
            "deserialized property is null"
        } else {
            "property \"${origin.name}: ${origin.value}\" is different from property \"${deserialized.name}: ${deserialized.value}\""
        }
    }
}

abstract class PropertySerializationTest {

    protected abstract val tester: PropertySerializationTester<*, *>

    @Test
    fun testJavaSerialization() {
        tester.testJavaSerialization()
    }

    @Test
    fun testJsonSerialization() {
        tester.testGsonSerialization()
    }
}

class RangedDoublePropertySerializationTest: PropertySerializationTest() {

    companion object {
        private const val DOUBLE_PROPERTY = "Test double property name"
        private const val DOUBLE_INITIAL_VALUE = 5.0
        private const val DOUBLE_LOWER_BOUND = 0.0
        private const val DOUBLE_UPPER_BOUND = 100.0
        private const val DOUBLE_PERCENT_NAME = "Percent test"
        private const val DOUBLE_PERCENT_INITIAL_VALUE = 33.0
        private const val DOUBLE_COLOR_NAME = "RED"
        private const val DOUBLE_COLOR_INITIAL_VALUE = 0.5
    }

    override val tester = PropertySerializationTester(
            object : TypeToken<RangedDoubleProperty>() {}.type,
            { e: Any -> e as RangedDoubleProperty },
            RangedDoubleProperty(DOUBLE_PROPERTY, DOUBLE_INITIAL_VALUE, DOUBLE_LOWER_BOUND, DOUBLE_UPPER_BOUND),
            PropertyFactory.getFXColorChannelProperty(DOUBLE_COLOR_NAME, DOUBLE_COLOR_INITIAL_VALUE),
            PropertyFactory.getPercentageRangedProperty(DOUBLE_PERCENT_NAME, DOUBLE_PERCENT_INITIAL_VALUE)
    )
}

class RangedIntegerPropertySerializationTest: PropertySerializationTest() {
    companion object {
        private const val INTEGER_PROPERTY = "Test integer property name"
        private const val INTEGER_INITIAL_VALUE = 5
        private const val INTEGER_LOWER_BOUND = 0
        private const val INTEGER_UPPER_BOUND = 100
    }

    override val tester = PropertySerializationTester(
            object : TypeToken<RangedIntegerProperty>() {}.type,
            { e: Any -> e as RangedIntegerProperty },
            RangedIntegerProperty(INTEGER_PROPERTY, INTEGER_INITIAL_VALUE, INTEGER_LOWER_BOUND, INTEGER_UPPER_BOUND)
    )
}

class SerializableBooleanPropertySerializationTest: PropertySerializationTest() {
    companion object {
        private const val BOOLEAN_PROPERTY = "Test boolean property name"
    }

    override val tester = PropertySerializationTester(
            object : TypeToken<SerializableBooleanProperty>() {}.type,
            { e: Any -> e as SerializableBooleanProperty },
            SerializableBooleanProperty(BOOLEAN_PROPERTY, true),
            SerializableBooleanProperty(BOOLEAN_PROPERTY, false)
    )
}

class SerializableEnumPropertySerializationTest: PropertySerializationTest() {
    companion object {
        private const val ENUM_PROPERTY = "Test enum property name"
    }

    @Suppress("unused")
    protected enum class TestEnum {
        FOO, BAR, TEST
    }

    @Suppress("unchecked_cast")
    override val tester = PropertySerializationTester(
        object : TypeToken<SerializableEnumProperty<TestEnum>>() {}.type,
        { e: Any -> e as SerializableEnumProperty<TestEnum> },
        SerializableEnumProperty(ENUM_PROPERTY, TestEnum.TEST)
    )
}

class SerializableStringPropertySerializationTest: PropertySerializationTest() {
    companion object {
        private const val STRING_PROPERTY = "Test string property name"
        private const val STRING_INITIAL_VALUE = "Test string property value"
    }

    override val tester = PropertySerializationTester(
        object : TypeToken<SerializableStringProperty>() {}.type,
        { e: Any -> e as SerializableStringProperty },
        SerializableStringProperty(STRING_PROPERTY, STRING_INITIAL_VALUE)
    )
}