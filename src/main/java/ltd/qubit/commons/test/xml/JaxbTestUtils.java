////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.xml;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlunit.assertj.XmlAssert;

import ltd.qubit.commons.annotation.Scale;
import ltd.qubit.commons.datastructure.map.MapUtils;
import ltd.qubit.commons.io.IoUtils;
import ltd.qubit.commons.lang.ClassUtils;
import ltd.qubit.commons.reflect.ConstructorUtils;
import ltd.qubit.commons.text.xml.DomUtils;
import ltd.qubit.commons.text.xml.jaxb.JaxbUtils;
import ltd.qubit.commons.util.codec.BigDecimalCodec;
import ltd.qubit.commons.util.codec.IsoDateCodec;
import ltd.qubit.commons.util.codec.IsoInstantCodec;
import ltd.qubit.commons.util.codec.IsoLocalDateCodec;
import ltd.qubit.commons.util.codec.IsoLocalDateTimeCodec;
import ltd.qubit.commons.util.codec.IsoLocalTimeCodec;
import ltd.qubit.commons.util.codec.PeriodCodec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import static ltd.qubit.commons.reflect.AccessibleUtils.withAccessibleObject;
import static ltd.qubit.commons.reflect.FieldUtils.getAllFields;
import static ltd.qubit.commons.reflect.Option.ALL_ACCESS;
import static ltd.qubit.commons.reflect.Option.BEAN_FIELD;
import static ltd.qubit.commons.reflect.Option.NON_STATIC;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.assertXPathAbsent;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.assertXPathEquals;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.assertXmlEqual;
import static ltd.qubit.commons.test.xml.XmlUnitUtils.getXpathElement;

/**
 * 用于测试对象JAXB XML序列化的工具类。
 *
 * @author 胡海星
 */
public abstract class JaxbTestUtils {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(JaxbTestUtils.class);

  /**
   * 断言使用JAXB将对象编组（marshal）为XML后的结果与期望的XML字符串在语义上相同。
   *
   * @param <T>
   *     对象的类型。
   * @param cls
   *     对象的类，用于创建JAXBContext。
   * @param object
   *     待编组的对象。
   * @param expectedXml
   *     期望的XML字符串。
   * @throws Exception
   *     如果在编组或断言过程中发生错误。
   */
  public static <T> void assertXmlMarshalEquals(final Class<T> cls,
      final T object, final String expectedXml) throws Exception {
    final JAXBContext context = JAXBContext.newInstance(cls);
    final Marshaller mr = context.createMarshaller();
    mr.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
    mr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    final StringWriter writer = new StringWriter();
    mr.marshal(object, writer);
    final String actualXml = writer.toString();
    LOGGER.debug("Object is:\n{}", object);
    LOGGER.debug("Expected XML is:\n{}", expectedXml);
    LOGGER.debug("Actual XML is:\n{}\n", actualXml);
    XmlAssert.assertThat(actualXml).and(expectedXml)
             .ignoreComments()
             .ignoreChildNodesOrder()
             .ignoreWhitespace()
             .ignoreElementContentWhitespace()
             .areIdentical();
  }

  /**
   * 断言使用JAXB将对象列表编组（marshal）为XML后的结果与期望的XML字符串在语义上相同。
   *
   * @param <T>
   *     列表中对象的类型。
   * @param cls
   *     列表中对象的类，用于创建JAXBContext。
   * @param list
   *     待编组的对象列表。
   * @param rootName
   *     生成的XML文档的根元素名称。
   * @param expectedXml
   *     期望的XML字符串。
   * @throws Exception
   *     如果在编组或断言过程中发生错误。
   */
  public static <T> void assertXmlMarshalListEquals(final Class<T> cls,
      final List<T> list, final String rootName, final String expectedXml)
      throws Exception {
    final JAXBContext context = JAXBContext.newInstance(cls);
    final Marshaller mr = context.createMarshaller();
    mr.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
    mr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    final StringWriter writer = new StringWriter();
    marshalList(list, cls, rootName, writer);
    final String actualXml = writer.toString();
    LOGGER.debug("List is:\n{}", list);
    LOGGER.debug("Expected XML is:\n{}", expectedXml);
    LOGGER.debug("Actual XML is:\n{}\n", actualXml);
    XmlAssert.assertThat(actualXml).and(expectedXml)
             .ignoreComments()
             .ignoreChildNodesOrder()
             .ignoreWhitespace()
             .ignoreElementContentWhitespace()
             .areIdentical();
  }

  /**
   * 断言使用JAXB将XML字符串解组（unmarshal）为对象后的结果与期望的对象相等。
   *
   * @param <T>
   *     对象的类型。
   * @param cls
   *     对象的类，用于创建JAXBContext和确定返回类型。
   * @param xml
   *     待解组的XML字符串。
   * @param expectedObject
   *     期望得到的对象实例。
   * @throws Exception
   *     如果在解组或断言过程中发生错误。
   */
  public static <T> void assertXmlUnmarshalEquals(final Class<T> cls,
      final String xml, final T expectedObject) throws Exception {
    final JAXBContext context = JAXBContext.newInstance(cls);
    final Unmarshaller umr = context.createUnmarshaller();
    final StringReader reader = new StringReader(xml);
    @SuppressWarnings("unchecked")
    final T actualObject = (T) umr.unmarshal(reader);
    LOGGER.debug("XML is:\n{}", xml);
    LOGGER.debug("Expected object is:\n{}", expectedObject);
    LOGGER.debug("Actual object is:\n{}\n", actualObject);
    assertEquals(expectedObject, actualObject);
  }

  /**
   * 断言使用JAXB将XML字符串解组（unmarshal）为对象列表后的结果与期望的对象列表相等。
   *
   * @param <T>
   *     列表中对象的类型。
   * @param cls
   *     列表中对象的类，用于创建JAXBContext和确定返回类型。
   * @param xml
   *     待解组的XML字符串，代表一个对象列表。
   * @param expectedList
   *     期望得到的对象列表。
   * @throws Exception
   *     如果在解组或断言过程中发生错误。
   */
  public static <T> void assertXmlUnmarshalListEquals(final Class<T> cls,
      final String xml, final List<T> expectedList) throws Exception {
    final StringReader reader = new StringReader(xml);
    final List<T> actualList = unmarshalList(reader, cls);
    LOGGER.debug("XML is:\n{}", xml);
    LOGGER.debug("Expected object is:\n{}", expectedList);
    LOGGER.debug("Actual object is:\n{}\n", actualList);
    assertEquals(expectedList, actualList);
  }

  /**
   * 将对象列表存储到XML文档中。
   *
   * <p><b>注意：</b>此函数<b>不会</b>关闭写入器。调用者必须自行关闭它。
   *
   * @param <T>
   *     要存储的对象的类型。
   * @param list
   *     要存储的对象列表。
   * @param cls
   *     要存储的对象的类。
   * @param rootName
   *     存储的XML文档的根元素的名称。
   * @param writer
   *     用于写入编组后的XML的写入器。
   * @throws JAXBException
   *     如果发生任何错误。
   */
  private static <T> void marshalList(final List<T> list, final Class<T> cls,
      final String rootName,
      final Writer writer) throws JAXBException {
    final QName qname = new QName(rootName);
    final JaxbListWrapper<T> wrapper = new JaxbListWrapper<>(list);
    @SuppressWarnings("rawtypes")
    final JAXBElement<JaxbListWrapper> element = new JAXBElement<>(qname,
        JaxbListWrapper.class, wrapper);
    final JAXBContext context = JAXBContext.newInstance(JaxbListWrapper.class, cls);
    final Marshaller mr = context.createMarshaller();
    mr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    mr.marshal(element, writer);
  }

  /**
   * 从XML文件解组对象列表。
   *
   * @param <T>
   *     要加载的对象的类型。
   * @param reader
   *     用于读取XML片段的读取器。
   * @param cls
   *     要加载的对象的类。
   * @return 从XML文件反序列化得到的对象列表。
   * @throws JAXBException
   *     如果发生任何错误。
   */
  private static <T> List<T> unmarshalList(final Reader reader,
      final Class<T> cls) throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(JaxbListWrapper.class, cls);
    final Unmarshaller umr = context.createUnmarshaller();
    final StreamSource source = new StreamSource(reader);
    @SuppressWarnings("unchecked")
    final JaxbListWrapper<T> wrapper = umr.unmarshal(source, JaxbListWrapper.class).getValue();
    return wrapper.getList();
  }

  static class JaxbListWrapper<T> {

    private final List<T> list;

    public JaxbListWrapper() {
      list = new ArrayList<>();
    }

    public JaxbListWrapper(final List<T> list) {
      this.list = list;
    }

    @XmlAnyElement(lax = true)
    public List<T> getList() {
      return list;
    }
  }

  /**
   * 测试给定对象的JAXB XML反序列化（解组）过程。
   * <p>
   * 该方法首先使用 {@link ltd.qubit.commons.text.xml.jaxb.JaxbUtils#marshal(Object, Class)} 将对象序列化为XML字符串，
   * 然后使用 {@link ltd.qubit.commons.text.xml.jaxb.JaxbUtils#unmarshal(java.io.Reader, Class)} 将该XML字符串反序列化回原始对象的类型，
   * 并断言原始对象与反序列化后的对象相等。
   * 测试过程中的关键步骤和结果会通过SLF4J日志记录下来。
   *
   * @param <T>
   *     待测试对象的类型。
   * @param obj
   *     待测试的对象实例。
   * @throws Exception
   *     如果在序列化或反序列化过程中发生任何错误。
   */
  public static <T> void testXmlDeserialization(final T obj)
      throws Exception {
    LOGGER.debug("Testing XML deserialization for the object:\n{}", obj);
    @SuppressWarnings("unchecked")
    final Class<T> type = (Class<T>) obj.getClass();
    final String xml = JaxbUtils.marshal(obj, type);
    LOGGER.debug("The object is serialized to:\n{}", xml);
    final Object result = JaxbUtils.unmarshal(new StringReader(xml), type);
    LOGGER.debug("The XML is deserialized to:\n{}", result);
    assertEquals(obj, result);
    LOGGER.debug("Test finished successfully.");
  }

  private static void assertXmlNodeEqualsField(final String xml,
      @Nullable final String rootPath, final Object obj, final Field field)
      throws Exception {
    final String xmlFieldName = toXmlName(field);
    final String path = (rootPath == null ? xmlFieldName
                                          : rootPath + "/" + xmlFieldName);
    if (field.isAnnotationPresent(XmlTransient.class)
        || Modifier.isTransient(field.getModifiers())) {
      LOGGER.debug("Ignore the XML node '{}' ...", path);
    } else {
      LOGGER.debug("Testing the XML node '{}' ...", path);
      final Object fieldValue = withAccessibleObject(field, f -> f.get(obj), true);
      final Class<?> fieldType = field.getType();
      if (fieldType.isPrimitive()) {
        assertXPathEquals(xml, path, fieldValue);
      } else {
        assertXmlNodeEqualsObject(xml, path, fieldValue, field);
      }
    }
  }

  private static void assertXmlNodeEqualsObject(final String xml,
      final String path, @Nullable final Object fieldValue, @Nullable final Field field)
      throws Exception {
    if (fieldValue == null) {
      assertXPathAbsent(xml, path);
    } else if ((field != null) && field.isAnnotationPresent(XmlJavaTypeAdapter.class)) {
      assertXmlNodeEqualsObjectWithAdapter(xml, path, fieldValue,
          field.getAnnotation(XmlJavaTypeAdapter.class));
    } else {
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
      } else if (type == Date.class) {
        final IsoDateCodec codec = new IsoDateCodec();
        assertXPathEquals(xml, path, codec.encode((Date) fieldValue));
      } else if (type == LocalDate.class) {
        final IsoLocalDateCodec codec = new IsoLocalDateCodec();
        assertXPathEquals(xml, path, codec.encode((LocalDate) fieldValue));
      } else if (type == LocalTime.class) {
        final IsoLocalTimeCodec codec = new IsoLocalTimeCodec();
        assertXPathEquals(xml, path, codec.encode((LocalTime) fieldValue));
      } else if (type == LocalDateTime.class) {
        final IsoLocalDateTimeCodec codec = new IsoLocalDateTimeCodec();
        assertXPathEquals(xml, path, codec.encode((LocalDateTime) fieldValue));
      } else if (type == Instant.class) {
        final IsoInstantCodec codec = new IsoInstantCodec();
        assertXPathEquals(xml, path, codec.encode((Instant) fieldValue));
      } else if (type == Period.class) {
        final PeriodCodec codec = new PeriodCodec();
        assertXPathEquals(xml, path, codec.encode((Period) fieldValue));
      } else if (type == BigInteger.class) {
        assertXPathEquals(xml, path, fieldValue.toString());
      } else if (type == BigDecimal.class) {
        int scale = BigDecimalCodec.DEFAULT_SCALE;
        if (field != null && field.isAnnotationPresent(Scale.class)) {
          scale = field.getAnnotation(Scale.class).value();
        }
        final BigDecimalCodec codec = new BigDecimalCodec(scale);
        assertXPathEquals(xml, path, codec.encode((BigDecimal) fieldValue));
      } else if (ClassUtils.isEnumType(type)) {
        assertXPathEquals(xml, path, (Enum<?>) fieldValue);
      } else if (ClassUtils.isArrayType(type)) {
        assertXmlNodeEqualsArray(xml, path, fieldValue, field);
      } else if (ClassUtils.isCollectionType(type)) {
        final Collection<?> collection = (Collection<?>) fieldValue;
        assertXmlNodeEqualsCollection(xml, path, collection, field);
      } else if (ClassUtils.isMapType(type)) {
        final Map<?, ?> map = (Map<?, ?>) fieldValue;
        assertXmlNodeEqualsMap(xml, path, map, field);
      } else {
        final int options = NON_STATIC | ALL_ACCESS;
        final List<Field> subfields = getAllFields(fieldValue.getClass(), options);
        for (final Field subfield : subfields) {
          assertXmlNodeEqualsField(xml, path, fieldValue, subfield);
        }
      }
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void assertXmlNodeEqualsObjectWithAdapter(final String xml,
      final String path, @Nullable final Object obj, final XmlJavaTypeAdapter annotation)
      throws Exception {
    if (obj == null) {
      assertXPathAbsent(xml, path);
    } else {
      final Class<? extends XmlAdapter> adapterClass = annotation.value();
      final XmlAdapter adapter = ConstructorUtils.newInstance(adapterClass);
      final String str = (String) adapter.marshal(obj);
      assertXPathEquals(xml, path, str);
    }
  }

  private static void assertXmlNodeEqualsCollection(final String xml,
      final String path,
      @Nullable final Collection<?> collection, @Nullable final Field field)
      throws Exception {
    if (collection == null) {
      assertXPathAbsent(xml, path);
    } else {
      String elementPath = path;
      if (field != null) {
        if (!field.isAnnotationPresent(XmlElement.class)) {
          fail("The XmlElement annotation must be presented at the "
              + "Collection field.");
        }
        final String elementName = field.getAnnotation(XmlElement.class).name();
        if (field.isAnnotationPresent(XmlElementWrapper.class)) {
          elementPath = path + "/" + elementName;
        } else {
          elementPath = getXmlPathParent(path) + "/" + elementName;
        }
      }
      int i = 1;
      for (final Object obj : collection) {
        assertXmlNodeEqualsObject(xml, elementPath + "[" + i + "]", obj, null);
        ++i;
      }
    }
  }

  private static void assertXmlNodeEqualsMap(final String xml, final String path,
      @Nullable final Map<?, ?> map, @Nullable final Field field) throws Exception {
    if (map == null) {
      assertXPathAbsent(xml, path);
    } else {
      String elementPath = path;
      if (field != null) {
        if (!field.isAnnotationPresent(XmlElement.class)) {
          fail("The XmlElement annotation must be presented at the map field.");
        }
        final String elementName = field.getAnnotation(XmlElement.class).name();
        if (field.isAnnotationPresent(XmlElementWrapper.class)) {
          elementPath = path + "/" + elementName;
        } else {
          elementPath = getXmlPathParent(path) + "/" + elementName;
        }
      }
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
        assertXmlNodeEqualsObject(xml, valuePath, value, null);
      }
    }
  }

  private static String getXmlPathParent(final String path) {
    final int i = path.lastIndexOf('/');
    if (i >= 0) {
      return path.substring(0, i);
    } else {
      return "";
    }
  }

  private static void assertXmlNodeEqualsArray(final String xml, final String path,
      @Nullable final Object array, @Nullable final Field field) throws Exception {
    if (array == null) {
      assertXPathAbsent(xml, path);
    } else {
      assert array.getClass().isArray();
      final Class<?> elementType = array.getClass().getComponentType();
      String elementPath = path;
      if (field != null && elementType != byte.class) {
        // byte[] should be serialized to BASE64 encoded string
        if (!field.isAnnotationPresent(XmlElementWrapper.class)) {
          fail("The XmlElementWrapper annotation must be presented at "
              + "the array field.");
        }
        if (!field.isAnnotationPresent(XmlElement.class)) {
          fail("The XmlElement annotation must be presented at "
              + "the array field.");
        }
        final String wrapperName = field.getAnnotation(XmlElementWrapper.class).name();
        final String elementName = field.getAnnotation(XmlElement.class).name();
        elementPath = path + "/" + elementName;
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
          assertXmlNodeEqualsObject(xml, elementPath + "[" + (i + 1) + "]",
              Array.get(array, i), null);
        }
      }
    }
  }

  /**
   * 根据Java字段对象推断其对应的XML元素名称。
   * <p>
   * 优先使用 {@link jakarta.xml.bind.annotation.XmlElement @XmlElement} 或 {@link jakarta.xml.bind.annotation.XmlAttribute @XmlAttribute} 注解的 {@code name} 属性。
   * 如果这些注解不存在或 {@code name} 为默认值 ({@code "##default"})，则使用字段自身的名称。
   *
   * @param field
   *     Java字段对象。
   * @return 推断出的XML元素或属性名称。
   */
  public static String toXmlName(final Field field) {
    final String fieldName = field.getName();
    final String result = toXmlName(fieldName);
    if (field.isAnnotationPresent(XmlAttribute.class)) {
      return "@" + result;
    } else {
      return result;
    }
  }

  /**
   * 根据Java字段名称直接返回其作为XML元素名称（无特殊处理）。
   * <p>
   * 注意：此方法不考虑JAXB注解，仅返回原始字段名。
   * 如需更准确的XML名称推断（考虑注解），请使用 {@link #toXmlName(Field)}。
   *
   * @param fieldName
   *     Java字段的名称。
   * @return 字段名称本身，作为XML名称。
   */
  public static String toXmlName(final String fieldName) {
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < fieldName.length(); ++i) {
      final char ch = fieldName.charAt(i);
      if (ch >= 'A' && ch <= 'Z') {
        if (i > 0) {
          builder.append('-');
        }
        builder.append(Character.toLowerCase(ch));
      } else {
        builder.append(ch);
      }
    }
    return builder.toString();
  }

  /**
   * 获取指定类对应的JAXB XML根元素名称。
   * <p>
   * 该方法查找类上的 {@link jakarta.xml.bind.annotation.XmlRootElement @XmlRootElement} 注解，并返回其 {@code name} 属性。
   * 如果注解不存在或名称为默认值 ({@code "##default"})，则返回 {@code null}。
   *
   * @param type
   *     目标类。
   * @return JAXB XML根元素名称，如果未定义则返回 {@code null}。
   */
  public static String getXmlRootElement(final Class<?> type) {
    if (!type.isAnnotationPresent(XmlRootElement.class)) {
      fail("No XmlRootElement annotation presented in the class.");
    }
    final XmlRootElement rootElement = type.getAnnotation(XmlRootElement.class);
    return rootElement.name();
  }

  /**
   * 测试给定对象的JAXB XML序列化（编组）过程的正确性。
   * <p>
   * 该方法首先使用 {@link ltd.qubit.commons.text.xml.jaxb.JaxbUtils#marshal(Object, Class)} 将对象序列化为XML字符串。
   * 然后，它会调用此类内部的 {@code assertXmlNodeEqualsField} 方法来递归地比较XML字符串中的每个节点
   * 是否与原始对象中相应字段的值匹配。
   * 测试过程中的关键步骤和结果会通过SLF4J日志记录下来。
   *
   * @param <T>
   *     待测试对象的类型。
   * @param obj
   *     待测试的对象实例。
   * @throws Exception
   *     如果在序列化或断言过程中发生任何错误。
   */
  public static <T> void testXmlSerialization(final T obj)
      throws Exception {
    LOGGER.debug("Testing XML serialization for the object:\n{}", obj);
    @SuppressWarnings("unchecked")
    final Class<T> type = (Class<T>) obj.getClass();
    final String xml = JaxbUtils.marshal(obj, type);
    LOGGER.debug("The object is serialized to:\n{}", xml);
    final String rootElement = getXmlRootElement(type);
    if (obj instanceof Enum<?>) {
      // 对枚举类型特殊处理
      final String value = ((Enum<?>) obj).name();
      assertXPathEquals(xml, rootElement, value);
    } else {
      final List<Field> fields = getAllFields(obj.getClass(), BEAN_FIELD);
      for (final Field field : fields) {
        assertXmlNodeEqualsField(xml, rootElement, obj, field);
      }
    }
    LOGGER.debug("Test finished successfully.");
  }

  /**
   * 将给定的XML字符串反序列化为指定类型的对象，然后再次序列化，并验证结果的一致性。
   * <p>
   * 此方法执行以下操作：
   * <ol>
   *   <li>将输入XML字符串 ({@code xml}) 反序列化为 {@code cls} 类型的对象 ({@code obj})。</li>
   *   <li>将 {@code obj} 序列化回XML字符串 ({@code marshaledXml})。</li>
   *   <li>断言原始输入XML ({@code xml}) 与 {@code marshaledXml} 在语义上相等。</li>
   *   <li>将 {@code marshaledXml} 反序列化回 {@code cls} 类型的对象 ({@code unmarshaledObj})。</li>
   *   <li>断言初次反序列化的 {@code obj} 与再次反序列化的 {@code unmarshaledObj} 相等。</li>
   * </ol>
   *
   * @param <T>
   *     目标对象的类型。
   * @param xml
   *     待测试的XML字符串。
   * @param cls
   *     期望反序列化成的对象的类。
   * @throws Exception
   *     如果在序列化、反序列化或断言过程中发生错误。
   */
  public static <T> void testXmlSerialization(final String xml, final Class<T> cls)
      throws Exception {
    LOGGER.debug("Expected XML is:\n{}", xml);
    final T obj = JaxbUtils.unmarshal(new StringReader(xml), cls);
    final String marshaledXml = JaxbUtils.marshal(obj, cls);
    LOGGER.debug("Actual XML is:\n{}", marshaledXml);
    assertXmlEqual(obj, xml, marshaledXml);
    final T unmarshaledObj = JaxbUtils.unmarshal(new StringReader(marshaledXml), cls);
    LOGGER.debug("Expected object is: {}", obj);
    LOGGER.debug("Actual object is:   {}", unmarshaledObj);
    assertEquals(obj, unmarshaledObj);
  }

  /**
   * 从给定的URL加载XML，反序列化为指定类型的对象，然后再次序列化，并验证结果的一致性。
   * <p>
   * 此方法首先从URL读取XML内容，然后调用 {@link #testXmlSerialization(String, Class)} 执行核心测试逻辑。
   * 操作包括：
   * <ol>
   *   <li>从URL读取XML内容。</li>
   *   <li>将XML反序列化为 {@code cls} 类型的对象。</li>
   *   <li>将该对象序列化回XML字符串。</li>
   *   <li>断言原始从URL加载的XML与再次序列化的XML在语义上相等。</li>
   *   <li>将再次序列化的XML反序列化回对象。</li>
   *   <li>断言初次反序列化的对象与再次反序列化的对象相等。</li>
   * </ol>
   *
   * @param <T>
   *     目标对象的类型。
   * @param url
   *     包含XML数据的URL。
   * @param cls
   *     期望反序列化成的对象的类。
   * @throws Exception
   *     如果在IO操作、序列化、反序列化或断言过程中发生错误。
   */
  public static <T> void testXmlSerialization(final URL url, final Class<T> cls) throws Exception {
    final String xml = IoUtils.toString(url, StandardCharsets.UTF_8);
    testXmlSerialization(xml, cls);
  }
}
