////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.jsonunit.assertj.JsonAssert;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

/**
 * 提供 JSON 单元测试相关的工具函数。
 *
 * @author 胡海星
 */
public class JsonUnitUtils {

  /**
   * 断言JSON字符串中指定字段的数组内容与期望的数组对象相等。
   *
   * @param <T>
   *     数组元素的类型。
   * @param json
   *     待检查的JSON字符串。
   * @param field
   *     JSON中数组字段的路径。
   * @param expected
   *     期望的数组内容。如果为 {@code null}，则断言该字段不存在。
   * @param mapper
   *     用于将期望数组中的元素序列化为JSON字符串以便比较的 {@link ObjectMapper}。
   * @throws Exception
   *     如果序列化期望值或断言失败时发生错误。
   */
  public static <T> void assertJsonArrayEquals(final String json,
      final String field, @Nullable final T[] expected, final ObjectMapper mapper)
      throws Exception {
    if (expected == null) {
      assertThatJson(json).node(field).isAbsent();
    } else {
      final JsonAssert assertion = assertThatJson(json);
      assertion.node(field).isArray().hasSize(expected.length);
      for (int i = 0; i < expected.length; ++i) {
        final String str = mapper.writerWithDefaultPrettyPrinter()
                                 .writeValueAsString(expected[i]);
        assertion.node(field + "[" + i + "]").isEqualTo(str);
      }
    }
  }

  /**
   * 断言JSON字符串中指定字段的数组内容与期望的列表对象相等。
   *
   * @param <T>
   *     列表元素的类型。
   * @param json
   *     待检查的JSON字符串。
   * @param field
   *     JSON中数组字段的路径。
   * @param expected
   *     期望的列表内容。如果为 {@code null}，则断言该字段不存在。
   * @param mapper
   *     用于将期望列表中的元素序列化为JSON字符串以便比较的 {@link ObjectMapper}。
   * @throws Exception
   *     如果序列化期望值或断言失败时发生错误。
   */
  public static <T> void assertJsonArrayEquals(final String json,
      final String field, @Nullable final List<T> expected,
      final ObjectMapper mapper) throws Exception {
    if (expected == null) {
      assertThatJson(json).node(field).isAbsent();
    } else {
      final JsonAssert assertion = assertThatJson(json);
      assertion.node(field).isArray().hasSize(expected.size());
      for (int i = 0; i < expected.size(); ++i) {
        final String str = mapper.writerWithDefaultPrettyPrinter()
                                 .writeValueAsString(expected.get(i));
        assertion.node(field + "[" + i + "]").isEqualTo(str);
      }
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点为 {@code null}。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   */
  public static void assertJsonNodeNull(final String json,
      @Nullable final String path) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    ja.isNull();
  }

  /**
   * 断言JSON字符串中指定路径的节点不存在。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则断言根节点（通常无意义，除非JSON本身为空）。
   */
  public static void assertJsonNodeAbsent(final String json,
      @Nullable final String path) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    ja.isAbsent();
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的对象（序列化后）相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的对象。如果为 {@code null}，则断言该节点不存在。
   * @param mapper
   *     用于将期望对象序列化为JSON字符串以便比较的 {@link ObjectMapper}。
   * @throws Exception
   *     如果序列化期望对象或断言失败时发生错误。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Object expected,
      final ObjectMapper mapper) throws Exception {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      final String expectedJson = mapper.writerWithDefaultPrettyPrinter()
                                  .writeValueAsString(expected);
      ja.isEqualTo(expectedJson);
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的布尔值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的布尔值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Boolean expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isBoolean().isEqualTo(expected);
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的字符串相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的字符串。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final String expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isString().isEqualTo(expected);
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的字符（作为字符串）相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的字符。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Character expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isString().isEqualTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的长整型数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的长整型数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Long expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的整型数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的整型数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Integer expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的短整型数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的短整型数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Short expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的字节型数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的字节型数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Byte expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的单精度浮点型数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的单精度浮点型数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Float expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的双精度浮点型数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的双精度浮点型数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Double expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(expected.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的枚举常量（的名称）相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的枚举常量。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final Enum<?> expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isString().isEqualTo(expected.name());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的 {@link BigDecimal} 数值相等。
   * <p>
   * 注意：比较时会将 {@link BigDecimal} 转换为字符串以保证精度。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param value
   *     期望的 {@link BigDecimal} 数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final BigDecimal value) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (value == null) {
      ja.isAbsent();
    } else {
      // 必须把 BigDecimal 转换为字符串在比较，否则可能丧失精度，
      // 例如原来的值是 4.1230 若直接比较则会和 4.123 比较，导致不同
      ja.isNumber().isEqualByComparingTo(value.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的 {@link BigInteger} 数值相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param bigInteger
   *     期望的 {@link BigInteger} 数值。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEquals(final String json,
      @Nullable final String path, @Nullable final BigInteger bigInteger) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (bigInteger == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(bigInteger.toString());
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点（解释为浮点数）与期望的浮点数字符串表示相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param floatText
   *     期望的浮点数的字符串表示。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEqualsFloat(final String json,
      @Nullable final String path, @Nullable final String floatText) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (floatText == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(floatText);
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点（解释为双精度浮点数）与期望的双精度浮点数字符串表示相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param doubleText
   *     期望的双精度浮点数的字符串表示。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEqualsDouble(final String json,
      @Nullable final String path, @Nullable final String doubleText) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (doubleText == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(doubleText);
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点（解释为 {@link BigDecimal}）与期望的 {@link BigDecimal} 字符串表示相等。
   *
   * @param json
   *     待检查的JSON字符串。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param bigDecimalText
   *     期望的 {@link BigDecimal} 的字符串表示。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEqualsBigDecimal(final String json,
      @Nullable final String path, @Nullable final String bigDecimalText) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (bigDecimalText == null) {
      ja.isAbsent();
    } else {
      ja.isNumber().isEqualByComparingTo(bigDecimalText);
    }
  }

  /**
   * 断言JSON字符串中指定路径的节点与期望的原始JSON字符串片段相等。
   * <p>
   * 此方法用于比较节点与一个已经是JSON格式的字符串，而不是一个需要被序列化的Java对象。
   *
   * @param json
   *     待检查的JSON字符串（外层JSON）。
   * @param path
   *     JSON节点的路径。如果为 {@code null}，则检查根节点。
   * @param expected
   *     期望的原始JSON字符串片段。如果为 {@code null}，则断言该节点不存在。
   */
  public static void assertJsonNodeEqualsRaw(final String json,
      @Nullable final String path, @Nullable final String expected) {
    JsonAssert ja = assertThatJson(json);
    if (path != null) {
      ja = ja.node(path);
    }
    if (expected == null) {
      ja.isAbsent();
    } else {
      ja.isEqualTo(expected);
    }
  }
}
