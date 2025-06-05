////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.text.jackson.CustomizedXmlMapper;
import static ltd.qubit.commons.text.CaseFormat.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * 用于测试枚举类 XML 序列化/反序列化的测试器对象。
 *
 * @param <E>
 *     待测试的枚举类型。
 * @author 胡海星
 */
public class EnumJacksonXmlTester<E extends Enum<E>> extends ModelTester<E> {

  private final XmlMapper mapper;

  /**
   * 构造一个 {@link EnumJacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   */
  public EnumJacksonXmlTester(final Class<E> type) {
    this(type, new RandomBeanGenerator(), DEFAULT_LOOPS, new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link EnumJacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public EnumJacksonXmlTester(final Class<E> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link EnumJacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public EnumJacksonXmlTester(final Class<E> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link EnumJacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   * @param mapper
   *     用于XML序列化/反序列化的 {@link XmlMapper} 对象。
   */
  public EnumJacksonXmlTester(final Class<E> type, final RandomBeanGenerator random, final int loops, final XmlMapper mapper) {
    super(type, random, loops);
    this.mapper = mapper;
  }

  /**
   * 执行枚举的Jackson XML序列化和反序列化测试。
   * <p>
   * 此方法会遍历枚举中的所有常量，对于每个常量：
   * <ol>
   *   <li>使用提供的 {@link XmlMapper} 将其序列化为XML字符串。</li>
   *   <li>验证序列化后的字符串是否符合预期格式，即 {@code <tag-name>ENUM_CONSTANT_NAME</tag-name>}，
   *       其中 {@code tag-name} 是由枚举类名按驼峰转连字符生成的。</li>
   *   <li>将该XML字符串反序列化回枚举实例。</li>
   *   <li>验证反序列化得到的实例与原始枚举常量是否相同。</li>
   * </ol>
   *
   * @throws Exception
   *     如果在序列化或反序列化过程中发生错误。
   */
  @Override
  protected void doTest() throws Exception {
    final String tagName = UPPER_CAMEL.to(LOWER_HYPHEN, type.getSimpleName());
    final String openTag = "<" + tagName + ">";
    final String closeTag = "</" + tagName + ">";
    for (final E e : type.getEnumConstants()) {
      final String str = mapper.writeValueAsString(e);
      assertEquals(openTag + e.name() + closeTag, str.strip(),
          "The XML serialization of the enumerator " + e + " must be its name, but it is '" + str + "'.");
      final E e2 = mapper.readValue(str, type);
      assertEquals(e, e2, "The XML deserialization of the enumerator " + e + " must be itself.");
    }
  }
}
