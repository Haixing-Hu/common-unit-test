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

  protected EnumTestBase(final Class<E> type) {
    this(type, DEFAULT_TEST_LOOPS, new CustomizedJsonMapper(), new CustomizedXmlMapper());
  }

  protected EnumTestBase(final Class<E> type, final JsonMapper jsonMapper,
      final XmlMapper xmlMapper) {
    this(type, DEFAULT_TEST_LOOPS, jsonMapper, xmlMapper);
  }

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

  public final Class<E> getType() {
    return type;
  }

  public final int getLoops() {
    return loops;
  }

  public final ObjectMapper getJsonMapper() {
    return jsonMapper;
  }

  public final RandomBeanGenerator getRandom() {
    return random;
  }

  @Test
  public void testJsonSerializeDeserialize() throws Exception {
    jacksonJsonTester.test();
  }

  @Test
  public void testXmlSerializeDeserialize() throws Exception {
    xmlTester.test();
  }

  @Test
  public void testLocalizedName() throws Exception {
    localizedNameTester.test();
  }
}
