////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.test.model.JacksonJsonTester;
import ltd.qubit.commons.test.model.JacksonXmlTester;
import ltd.qubit.commons.test.model.LocalizedNameTester;
import ltd.qubit.commons.text.jackson.CustomizedJsonMapper;
import ltd.qubit.commons.text.jackson.CustomizedXmlMapper;

/**
 * 所有领域枚举类的单元测试的基类。
 *
 * <p>这个类设置为abstract，从而使得JUnit不会对这个类本身进行测试。</p>
 *
 * @param <E>
 *     待测试的领域枚举类的类型。
 * @author 胡海星
 */
public class EnumTestBase<E extends Enum<E>> {

  protected static final int DEFAULT_TEST_LOOPS = 10;

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected final Class<E> type;

  protected final int loops;

  protected final JsonMapper jsonMapper;

  protected final XmlMapper xmlMapper;

  protected final RandomBeanGenerator random;

  protected final JacksonJsonTester<E> jacksonJsonTester;

  protected final JacksonXmlTester<E> xmlTester;

  protected final LocalizedNameTester<E> localizedNameTester;

  /**
   * 构造一个 {@link EnumTestBase} 对象。
   *
   * @param type
   *     待测试的领域枚举类的类型。
   */
  protected EnumTestBase(final Class<E> type) {
    this(type, DEFAULT_TEST_LOOPS, new CustomizedJsonMapper(), new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link EnumTestBase} 对象。
   *
   * @param type
   *     待测试的领域枚举类的类型。
   * @param jsonMapper
   *     用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   * @param xmlMapper
   *     用于XML序列化/反序列化的 {@link XmlMapper} 对象。
   */
  protected EnumTestBase(final Class<E> type, final JsonMapper jsonMapper,
      final XmlMapper xmlMapper) {
    this(type, DEFAULT_TEST_LOOPS, jsonMapper, xmlMapper);
  }

  /**
   * 构造一个 {@link EnumTestBase} 对象。
   *
   * @param type
   *     待测试的领域枚举类的类型。
   * @param loops
   *     测试循环次数。
   * @param jsonMapper
   *     用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   * @param xmlMapper
   *     用于XML序列化/反序列化的 {@link XmlMapper} 对象。
   */
  protected EnumTestBase(final Class<E> type, final int loops,
      final JsonMapper jsonMapper, final XmlMapper xmlMapper) {
    this.type = type;
    this.loops = loops;
    this.jsonMapper = jsonMapper;
    this.xmlMapper = xmlMapper;
    this.random = new RandomBeanGenerator();
    this.jacksonJsonTester = new JacksonJsonTester<>(type, random, loops, jsonMapper);
    this.xmlTester = new JacksonXmlTester<>(type, random, loops, xmlMapper);
    this.localizedNameTester = new LocalizedNameTester<>(type, random, loops);
  }

  /**
   * 获取待测试的领域枚举类的类型。
   *
   * @return 待测试的领域枚举类的类型。
   */
  public final Class<E> getType() {
    return type;
  }

  /**
   * 获取测试循环次数。
   *
   * @return 测试循环次数。
   */
  public final int getLoops() {
    return loops;
  }

  /**
   * 获取用于JSON序列化/反序列化的 {@link ObjectMapper} 对象。
   *
   * @return 用于JSON序列化/反序列化的 {@link ObjectMapper} 对象。
   */
  public final ObjectMapper getJsonMapper() {
    return jsonMapper;
  }

  /**
   * 获取用于生成随机Java Bean的 {@link RandomBeanGenerator} 对象。
   *
   * @return 用于生成随机Java Bean的 {@link RandomBeanGenerator} 对象。
   */
  public final RandomBeanGenerator getRandom() {
    return random;
  }

  /**
   * 测试枚举的JSON序列化和反序列化。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testJsonSerializeDeserialize() throws Exception {
    jacksonJsonTester.test();
  }

  /**
   * 测试枚举的XML序列化和反序列化。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testXmlSerializeDeserialize() throws Exception {
    xmlTester.test();
  }

  /**
   * 测试枚举的本地化名称。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testLocalizedName() throws Exception {
    localizedNameTester.test();
  }
}
