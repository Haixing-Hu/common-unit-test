////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ltd.qubit.commons.datastructure.map.MapUtils;
import ltd.qubit.commons.io.IoUtils;
import ltd.qubit.commons.lang.ClassUtils;
import ltd.qubit.commons.text.jackson.JacksonUtils;
import ltd.qubit.commons.text.jackson.module.TypeRegistrationModule;
import ltd.qubit.commons.text.xml.DomUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static ltd.qubit.commons.reflect.AccessibleUtils.withAccessibleObject;
import static ltd.qubit.commons.reflect.FieldUtils.getAllFields;
import static ltd.qubit.commons.reflect.Option.BEAN_FIELD;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.assertXPathAbsent;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.assertXPathEquals;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.assertXmlEqual;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.getXpathElement;
import static ltd.qubit.commons.text.jackson.JacksonUtils.getPropertyName;
import static ltd.qubit.commons.text.jackson.JacksonUtils.getRootName;
import static ltd.qubit.commons.text.jackson.JacksonUtils.serializeWithAdapter;
import static ltd.qubit.commons.text.jackson.JacksonUtils.serializeWithSerializer;

/**
 * The utility class for testing the XML serialization of a object.
 *
 * @author Haixing Hu
 */
public abstract class JacksonXmlTestUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JacksonXmlTestUtils.class);

  public static <T> void assertXmlSerializeEquals(final XmlMapper mapper,
      final Class<T> cls, final T object, final String expectedXml)
      throws Exception {
    final String actualXml = mapper.writeValueAsString(object);
    assertXmlEqual(object, expectedXml, actualXml);
  }

  public static <T> void assertXmlDeserializeEquals(final XmlMapper mapper,
      final Class<T> cls, final String xml, final T expectedObject)
      throws Exception {
    final T actualObject = mapper.readValue(xml, cls);
    LOGGER.debug("XML is:\n{}", xml);
    LOGGER.debug("Expected object is:\n{}", expectedObject);
    LOGGER.debug("Actual object is:\n{}\n", actualObject);
    assertEquals(expectedObject, actualObject);
  }

  @SuppressWarnings("unchecked")
  public static <T> void testXmlDeserialization(final XmlMapper mapper, final T obj)
      throws Exception {
    LOGGER.info("Testing XML deserialization for the object:\n{}", obj);
    final String xml = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(obj);
    LOGGER.info("The object is serialized to:\n{}", xml);
    final T result = mapper.readValue(xml, (Class<T>) obj.getClass());
    LOGGER.info("The XML is deserialized to:\n{}", result);
    assertEquals(obj, result);
    LOGGER.info("Test finished successfully.");
  }

  public static <T> void testXmlSerialization(final XmlMapper mapper, final T obj)
      throws Exception {
    LOGGER.info("Testing XML serialization for the object:\n{}", obj);
    @SuppressWarnings("unchecked")
    final Class<T> type = (Class<T>) obj.getClass();
    final String xml = mapper.writeValueAsString(obj);
    LOGGER.info("The object is serialized to:\n{}", xml);
    final PropertyName root = getRootName(mapper, type);
    assertXmlNodeEqualsObject(mapper, xml, root.getSimpleName(), null, obj);
    LOGGER.info("Test finished successfully.");
  }

  public static <T> void testXmlSerialization(final XmlMapper mapper,
      final URL url, final Class<T> cls) throws Exception {
    final String xml = IoUtils.toString(url, StandardCharsets.UTF_8);
    testXmlSerialization(mapper, xml, cls);
  }

  public static <T> void testXmlSerialization(final XmlMapper mapper,
      final String xml, final Class<T> cls) throws Exception {
    LOGGER.info("Expected XML is:\n{}", xml);
    final T obj = mapper.readValue(xml, cls);
    final String marshaledXml = mapper.writeValueAsString(obj);
    LOGGER.info("Actual XML is:\n{}", marshaledXml);
    assertXmlEqual(obj, xml, marshaledXml);
    final T unmarshaledObj = mapper.readValue(marshaledXml, cls);
    LOGGER.info("Expected object is: {}", obj);
    LOGGER.info("Actual object is:   {}", unmarshaledObj);
    assertEquals(obj, unmarshaledObj);
  }

  @SuppressWarnings("rawtypes")
  public static void assertXmlNodeEqualsObject(final XmlMapper mapper,
      final String xml, final String path, @Nullable final Field field,
      @Nullable final Object fieldValue) throws Exception {
    if (fieldValue == null) {
      assertXPathAbsent(xml, path);
    } else if (field != null && field.isAnnotationPresent(XmlJavaTypeAdapter.class)) {
      final XmlJavaTypeAdapter annotation = field.getAnnotation(XmlJavaTypeAdapter.class);
      final String expected = serializeWithAdapter(mapper, annotation, field, fieldValue);
      assertXPathEquals(xml, path, expected);
    } else if (field != null && field.isAnnotationPresent(JsonSerialize.class)) {
      final JsonSerialize annotation = field.getAnnotation(JsonSerialize.class);
      final String expected = serializeWithSerializer(mapper, annotation, field, fieldValue);
      assertXPathEquals(xml, path, expected);
    } else {
      final Class<?> type = fieldValue.getClass();
      final JsonSerializer serializer = TypeRegistrationModule.getSerializer(type);
      if (serializer != null) {
        final String expected = serializeWithSerializer(mapper, serializer, field, fieldValue);
        assertXPathEquals(xml, path, expected);
      } else {
        assertXmlNodeEqualsNonNullObject(mapper, xml, path, field, fieldValue);
      }
    }
  }

  private static void assertXmlNodeEqualsNonNullObject(final XmlMapper mapper,
      final String xml, final String path, @Nullable final Field field,
      final Object fieldValue) throws Exception {
    assert fieldValue != null;
    final Class<?> type = fieldValue.getClass();
    if (type == Boolean.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Character.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Byte.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Short.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Integer.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Long.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Float.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == Double.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (type == String.class) {
      assertXPathEquals(xml, path, fieldValue);
    } else if (ClassUtils.isEnumType(type)) {
      assertXPathEquals(xml, path, (Enum<?>) fieldValue);
    } else if (ClassUtils.isArrayType(type)) {
      assertXmlNodeEqualsArray(mapper, xml, path, fieldValue, field);
    } else if (ClassUtils.isCollectionType(type)) {
      final Collection<?> collection = (Collection<?>) fieldValue;
      assertXmlNodeEqualsCollection(mapper, xml, path, collection, field);
    } else if (ClassUtils.isMapType(type)) {
      final Map<?, ?> map = (Map<?, ?>) fieldValue;
      assertXmlNodeEqualsMap(mapper, xml, path, map, field);
    } else {
      final List<Field> subfields = getAllFields(fieldValue.getClass(), BEAN_FIELD);
      for (final Field subfield : subfields) {
        assertXmlNodeEqualsField(mapper, xml, path, fieldValue, subfield);
      }
    }
  }

  private static void assertXmlNodeEqualsField(final XmlMapper mapper,
      final String xml, @Nullable final String rootPath, final Object obj,
      final Field field) throws Exception {
    final Class<?> fieldType = field.getType();
    final PropertyName propertyName;
    if (ClassUtils.isArrayType(fieldType)
        || ClassUtils.isCollectionType(fieldType)
        || ClassUtils.isMapType(fieldType)) {
      propertyName = JacksonUtils.getWrapperPropertyName(mapper, field);
    } else {
      propertyName = getPropertyName(mapper, field);
    }
    final String path = (rootPath == null ? propertyName.getSimpleName()
                                          : rootPath + "/" + propertyName.getSimpleName());
    if (field.isAnnotationPresent(XmlTransient.class)
        || field.isAnnotationPresent(JsonIgnore.class)
        || Modifier.isTransient(field.getModifiers())) {
      LOGGER.debug("Ignore the XML node '{}' ...", path);
    } else {
      LOGGER.debug("Testing the XML node '{}' ...", path);
      final Object fieldValue = withAccessibleObject(field, f -> f.get(obj), true);
      if (fieldType.isPrimitive()) {
        assertXPathEquals(xml, path, fieldValue);
      } else {
        assertXmlNodeEqualsObject(mapper, xml, path, field, fieldValue);
      }
    }
  }

  private static void assertXmlNodeEqualsCollection(final XmlMapper mapper,
      final String xml, final String path,
      @Nullable final Collection<?> collection, @Nullable final Field field)
      throws Exception {
    if (collection == null) {
      assertXPathAbsent(xml, path);
    } else {
      String elementPath = path;
      if (field != null) {
        final PropertyName elementName = JacksonUtils.getWrappedPropertyName(mapper, field);
        elementPath = path + "/" + elementName;
      }
      int i = 1;
      for (final Object obj : collection) {
        assertXmlNodeEqualsObject(mapper, xml, elementPath + "[" + i + "]", field, obj);
        ++i;
      }
    }
  }

  private static void assertXmlNodeEqualsMap(final XmlMapper mapper,
      final String xml, final String path, @Nullable final Map<?, ?> map,
      @Nullable final Field field) throws Exception {
    if (map == null) {
      assertXPathAbsent(xml, path);
    } else {
      final String elementPath = path + "/entry";
      final List<Element> nodes = getXpathElement(xml, elementPath);
      assertEquals(map.size(), nodes.size(),
          "The size of the map is not equal to the number of XML nodes: " + elementPath);
      final int n = map.size();
      for (int i = 0; i < n; ++i) {
        final Element node = nodes.get(i);
        final String childPath = elementPath + "[" + (i + 1) + "]";
        final String keyPath = childPath + "/key";
        final String valuePath = childPath + "/value";
        final Node keyNode = DomUtils.getFirstChild(node, "key");
        assertNotNull(keyNode, "The key node is not found in the map: " + keyPath);
        final String keyString = keyNode.getTextContent();
        final Object value = MapUtils.getByKeyString(map, keyString);
        assertNotNull(value, "The key '" + keyString + "' is not found in the map: " + keyPath);
        assertXmlNodeEqualsObject(mapper, xml, valuePath, field, value);
      }
    }
  }

  private static void assertXmlNodeEqualsArray(final XmlMapper mapper,
      final String xml, final String path, @Nullable final Object array,
      @Nullable final Field field) throws Exception {
    if (array == null) {
      assertXPathAbsent(xml, path);
    } else {
      assert array.getClass().isArray();
      final Class<?> elementType = array.getClass().getComponentType();
      String elementPath = path;
      if (field != null && elementType != byte.class) {
        // byte[] should be serialized to BASE64 encoded string
        final PropertyName elementName = JacksonUtils.getWrappedPropertyName(mapper, field);
        elementPath = path + "/" + elementName.getSimpleName();
      }
      final int n = Array.getLength(array);
      if (elementType == boolean.class) {
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              Array.getBoolean(array, i));
        }
      } else if (elementType == char.class) {
        // NOTE: a char array was serialized as a int array in JAXB
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              (int) Array.getChar(array, i));
        }
      } else if (elementType == byte.class) {
        // NOTE: a byte array was serialized as a BASE64 encoded string in JAXB
        final Base64.Encoder encoder = Base64.getEncoder();
        final String expected = encoder.encodeToString((byte[]) array);
        assertXPathEquals(xml, path, expected);
      } else if (elementType == short.class) {
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              Array.getShort(array, i));
        }
      } else if (elementType == int.class) {
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              Array.getInt(array, i));
        }
      } else if (elementType == long.class) {
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              Array.getLong(array, i));
        }
      } else if (elementType == float.class) {
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              Array.getFloat(array, i));
        }
      } else if (elementType == double.class) {
        for (int i = 0; i < n; ++i) {
          assertXPathEquals(xml, elementPath + "[" + (i + 1) + "]",
              Array.getDouble(array, i));
        }
      } else {
        for (int i = 0; i < n; ++i) {
          assertXmlNodeEqualsObject(mapper, xml, elementPath + "[" + (i + 1) + "]",
              field, Array.get(array, i));
        }
      }
    }
  }
}
