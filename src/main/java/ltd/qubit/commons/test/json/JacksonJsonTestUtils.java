////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.json.JsonMapper;

import ltd.qubit.commons.lang.ClassUtils;
import ltd.qubit.commons.text.jackson.module.TypeRegistrationModule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static ltd.qubit.commons.lang.StringUtils.endsWithChar;
import static ltd.qubit.commons.lang.StringUtils.nullToEmpty;
import static ltd.qubit.commons.reflect.AccessibleUtils.withAccessibleObject;
import static ltd.qubit.commons.reflect.FieldUtils.getAllFields;
import static ltd.qubit.commons.reflect.Option.BEAN_FIELD;
import static ltd.qubit.commons.test.json.JsonUnitUtils.assertJsonNodeAbsent;
import static ltd.qubit.commons.test.json.JsonUnitUtils.assertJsonNodeEquals;
import static ltd.qubit.commons.test.json.JsonUnitUtils.assertJsonNodeEqualsRaw;
import static ltd.qubit.commons.text.jackson.JacksonUtils.getPropertyName;
import static ltd.qubit.commons.text.jackson.JacksonUtils.serializeWithSerializer;

/**
 * 提供用于测试 Jackson JSON 序列化和反序列化的静态工具方法。
 *
 * @author 胡海星
 */
public class JacksonJsonTestUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonTestUtils.class);

  /**
   * 测试给定对象的JSON反序列化过程。
   * <p>
   * 该方法首先使用提供的 {@link JsonMapper} 将对象序列化为JSON字符串（带美化格式），
   * 然后将该JSON字符串反序列化回原始对象的类型，并断言原始对象与反序列化后的对象相等。
   * 测试过程中的关键步骤和结果会通过SLF4J日志记录下来。
   *
   * @param <T>
   *     待测试对象的类型。
   * @param mapper
   *     用于JSON序列化和反序列化的 {@link JsonMapper} 实例。
   * @param obj
   *     待测试的对象实例。
   * @throws Exception
   *     如果在序列化或反序列化过程中发生任何错误。
   */
  public static <T> void testJsonDeserialization(final JsonMapper mapper, final T obj)
      throws Exception {
    LOGGER.info("Testing JSON deserialization for the object:\n{}", obj);
    final String json = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(obj);
    LOGGER.info("The object is serialized to:\n{}", json);
    final Object result = mapper.readValue(json, obj.getClass());
    LOGGER.info("The JSON is deserialized to:\n{}", result);
    assertEquals(obj, result);
    LOGGER.info("Test finished successfully.");
  }

  /**
   * 测试给定对象的JSON序列化过程。
   * <p>
   * 该方法首先使用提供的 {@link JsonMapper} 将对象序列化为JSON字符串（带美化格式）。
   * 然后，它会调用 {@code assertJsonNodeEqualsObject} 方法来递归地比较JSON字符串中的每个节点
   * 是否与原始对象中相应字段的值匹配。测试过程中的关键步骤和结果会通过SLF4J日志记录下来。
   *
   * @param <T>
   *     待测试对象的类型。
   * @param mapper
   *     用于JSON序列化的 {@link JsonMapper} 实例。
   * @param obj
   *     待测试的对象实例。
   * @throws Exception
   *     如果在序列化或断言过程中发生任何错误。
   */
  public static <T> void testJsonSerialization(final JsonMapper mapper, final T obj)
      throws Exception {
    LOGGER.info("Testing JSON serialization for the object:\n{}", obj);
    final String json = mapper.writerWithDefaultPrettyPrinter()
                              .writeValueAsString(obj);
    LOGGER.info("The object is serialized to:\n{}", json);
    assertJsonNodeEqualsObject(mapper, json, null, null, obj);
    LOGGER.info("Test finished successfully.");
  }

  private static void assertJsonNodeEqualsObject(final JsonMapper mapper,
      final String json, @Nullable final String path, @Nullable final Field field,
      @Nullable final Object fieldValue)
      throws Exception {
    if (fieldValue == null) {
      assertJsonNodeAbsent(json, path);
    } else if (field != null && field.isAnnotationPresent(JsonSerialize.class)) {
      final JsonSerialize annotation = field.getAnnotation(JsonSerialize.class);
      final String expected = serializeWithSerializer(mapper, annotation, field, fieldValue);
      assertJsonNodeEqualsRaw(json, path, expected);
    } else {
      final Class<?> type = fieldValue.getClass();
      final JsonSerializer<?> serializer = TypeRegistrationModule.getSerializer(type);
      if (serializer != null) {
        final String expected = serializeWithSerializer(mapper, serializer, field, fieldValue);
        assertJsonNodeEqualsRaw(json, path, expected);
      } else {
        assertJsonNodeEqualsNonNullObject(mapper, json, path, field, fieldValue);
      }
    }
  }

  private static void assertJsonNodeEqualsNonNullObject(final JsonMapper mapper,
      final String json, final String path, @Nullable final Field field,
      final Object fieldValue) throws Exception {
    assert fieldValue != null;
    final Class<?> type = fieldValue.getClass();
    if (type == Boolean.class) {
      assertJsonNodeEquals(json, path, (Boolean) fieldValue);
    } else if (type == Character.class) {
      assertJsonNodeEquals(json, path, (Character) fieldValue);
    } else if (type == Byte.class) {
      assertJsonNodeEquals(json, path, (Byte) fieldValue);
    } else if (type == Short.class) {
      assertJsonNodeEquals(json, path, (Short) fieldValue);
    } else if (type == Integer.class) {
      assertJsonNodeEquals(json, path, (Integer) fieldValue);
    } else if (type == Long.class) {
      assertJsonNodeEquals(json, path, (Long) fieldValue);
    } else if (type == Float.class) {
      assertJsonNodeEquals(json, path, (Float) fieldValue);
    } else if (type == Double.class) {
      assertJsonNodeEquals(json, path, (Double) fieldValue);
    } else if (type == String.class) {
      assertJsonNodeEquals(json, path, (String) fieldValue);
    } else if (ClassUtils.isEnumType(type)) {
      assertJsonNodeEquals(json, path, (Enum<?>) fieldValue);
    } else if (ClassUtils.isArrayType(type)) {
      assertJsonNodeEqualsArray(mapper, json, path, field, fieldValue);
    } else if (ClassUtils.isCollectionType(type)) {
      final Collection<?> collection = (Collection<?>) fieldValue;
      assertJsonNodeEqualsCollection(mapper, json, path, field, collection);
    } else if (ClassUtils.isMapType(type)) {
      final Map<?, ?> map = (Map<?, ?>) fieldValue;
      assertJsonNodeEqualsMap(mapper, json, path, field, map);
    } else {
      final List<Field> subfields = getAllFields(fieldValue.getClass(), BEAN_FIELD);
      for (final Field subfield : subfields) {
        assertJsonNodeEqualsField(mapper, json, path, fieldValue, subfield);
      }
    }
  }

  private static void assertJsonNodeEqualsField(final JsonMapper mapper,
      final String json, @Nullable final String rootPath, final Object obj,
      final Field field) throws Exception {
    final PropertyName propertyName = getPropertyName(mapper, field);
    final String path = (rootPath == null ? propertyName.getSimpleName()
                                          : rootPath + "." + propertyName.getSimpleName());
    if (field.isAnnotationPresent(JsonIgnore.class)
        || Modifier.isTransient(field.getModifiers())) {
      LOGGER.debug("Ignore the JSON node '{}' ...", path);
    } else {
      LOGGER.debug("Testing the JSON node '{}' ...", path);
      final Object fieldValue = withAccessibleObject(field, f -> f.get(obj), true);
      final Class<?> fieldType = field.getType();
      if (fieldType == boolean.class) {
        assertJsonNodeEquals(json, path, (Boolean) fieldValue);
      } else if (fieldType == char.class) {
        assertJsonNodeEquals(json, path, (Character) fieldValue);
      } else if (fieldType == byte.class) {
        assertJsonNodeEquals(json, path, (Byte) fieldValue);
      } else if (fieldType == short.class) {
        assertJsonNodeEquals(json, path, (Short) fieldValue);
      } else if (fieldType == int.class) {
        assertJsonNodeEquals(json, path, (Integer) fieldValue);
      } else if (fieldType == long.class) {
        assertJsonNodeEquals(json, path, (Long) fieldValue);
      } else if (fieldType == float.class) {
        assertJsonNodeEquals(json, path, (Float) fieldValue);
      } else if (fieldType == double.class) {
        assertJsonNodeEquals(json, path, (Double) fieldValue);
      } else {
        assertJsonNodeEqualsObject(mapper, json, path, field, fieldValue);
      }
    }
  }

  private static void assertJsonNodeEqualsCollection(final JsonMapper mapper,
      final String json, final String path, final Field field,
      final Collection<?> collection) throws Exception {
    int i = 0;
    for (final Object element : collection) {
      // json路径不能有连续的数组方框，例如 rows[0][0].key 是无法识别的，应该写成 rows[0].[0].key
      final String previousPath = nullToEmpty(path);
      final String indexPath = "[" + i + "]";
      final String subpath;
      if (endsWithChar(previousPath, ']')) {
        subpath = previousPath + '.' + indexPath;
      } else {
        subpath = previousPath + indexPath;
      }
      assertJsonNodeEqualsObject(mapper, json, subpath, field, element);
      ++i;
    }
  }

  private static void assertJsonNodeEqualsMap(final JsonMapper mapper,
      final String json, final String path, final Field field, final Map<?, ?> map)
      throws Exception {
    for (final Object key : map.keySet()) {
      final String subPath = nullToEmpty(path) + "." + key;
      assertJsonNodeEqualsObject(mapper, json, subPath, field, map.get(key));
    }
  }

  private static void assertJsonNodeEqualsArray(final JsonMapper mapper,
      final String json, final String path, final Field field, final Object array)
      throws Exception {
    assert (array != null && array.getClass().isArray());
    final int n = Array.getLength(array);
    final Class<?> elementType = array.getClass().getComponentType();
    if (elementType == boolean.class) {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEquals(json, subpath, Array.getBoolean(array, i));
      }
    } else if (elementType == char.class) {
      // NOTE: a byte array was serialized as a string in JACKSON
      final String expected = new String((char[]) array);
      assertJsonNodeEquals(json, path, expected);
    } else if (elementType == byte.class) {
      // NOTE: a byte array was serialized as a BASE64 encoded string in JACKSON
      final Base64.Encoder encoder = Base64.getEncoder();
      final String expected = encoder.encodeToString((byte[]) array);
      assertJsonNodeEquals(json, path, expected);
    } else if (elementType == short.class) {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEquals(json, subpath, Array.getShort(array, i));
      }
    } else if (elementType == int.class) {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEquals(json, subpath, Array.getInt(array, i));
      }
    } else if (elementType == long.class) {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEquals(json, subpath, Array.getLong(array, i));
      }
    } else if (elementType == float.class) {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEquals(json, subpath, Array.getFloat(array, i));
      }
    } else if (elementType == double.class) {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEquals(json, subpath, Array.getDouble(array, i));
      }
    } else {
      for (int i = 0; i < n; ++i) {
        final String subpath = nullToEmpty(path) + "[" + i + "]";
        assertJsonNodeEqualsObject(mapper, json, subpath, field, Array.get(array, i));
      }
    }
  }
}
