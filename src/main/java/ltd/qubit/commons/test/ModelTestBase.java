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
import ltd.qubit.commons.test.model.CloneTester;
import ltd.qubit.commons.test.model.JacksonJsonTester;
import ltd.qubit.commons.test.model.JacksonXmlTester;
import ltd.qubit.commons.test.model.NullableAnnotationTester;
import ltd.qubit.commons.test.model.ReferenceAnnotationTester;
import ltd.qubit.commons.test.model.SerializableTester;
import ltd.qubit.commons.test.model.SizeAnnotationTester;
import ltd.qubit.commons.text.jackson.CustomizedJsonMapper;
import ltd.qubit.commons.text.jackson.CustomizedXmlMapper;

/**
 * 所有领域对象模型单元测试的基类。
 *
 * <p>这个类设置为abstract，从而使得JUnit不会对这个类本身进行测试。</p>
 *
 * @param <T>
 *     待测试的领域对象模型的类型。
 */
public abstract class ModelTestBase<T> {

  protected static final int DEFAULT_TEST_LOOPS = 10;

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected final Class<T> type;

  protected final int loops;

  protected final JsonMapper jsonMapper;

  protected final XmlMapper xmlMapper;

  protected final RandomBeanGenerator random;

  protected final JacksonJsonTester<T> jacksonJsonTester;

  protected final JacksonXmlTester<T> xmlTester;

  protected final CloneTester<T> cloneTester;

  protected final SerializableTester<T> serializableTester;

  protected final NullableAnnotationTester<T> nullableAnnotationTester;

  protected final SizeAnnotationTester<T> sizeAnnotationTester;

  protected final ReferenceAnnotationTester<T> referenceAnnotationTester;

  /**
   * 构造一个 {@link ModelTestBase} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   */
  protected ModelTestBase(final Class<T> type) {
    this(type, DEFAULT_TEST_LOOPS, new CustomizedJsonMapper(), new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link ModelTestBase} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param jsonMapper
   *     用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   * @param xmlMapper
   *     用于XML序列化/反序列化的 {@link XmlMapper} 对象。
   */
  protected ModelTestBase(final Class<T> type, final JsonMapper jsonMapper,
      final XmlMapper xmlMapper) {
    this(type, DEFAULT_TEST_LOOPS, jsonMapper, xmlMapper);
  }

  /**
   * 构造一个 {@link ModelTestBase} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param loops
   *     测试循环次数。
   * @param jsonMapper
   *     用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   * @param xmlMapper
   *     用于XML序列化/反序列化的 {@link XmlMapper} 对象。
   */
  protected ModelTestBase(final Class<T> type, final int loops,
      final JsonMapper jsonMapper, final XmlMapper xmlMapper) {
    this.type = type;
    this.loops = loops;
    this.jsonMapper = jsonMapper;
    this.xmlMapper = xmlMapper;
    this.random = new RandomBeanGenerator();
    this.jacksonJsonTester = new JacksonJsonTester<>(type, random, loops, jsonMapper);
    this.xmlTester = new JacksonXmlTester<>(type, random, loops, xmlMapper);
    this.cloneTester = new CloneTester<>(type, random, loops);
    this.serializableTester = new SerializableTester<>(type, random, loops);
    this.nullableAnnotationTester = new NullableAnnotationTester<>(type, random, loops);
    this.sizeAnnotationTester = new SizeAnnotationTester<>(type, random, loops);
    this.referenceAnnotationTester = new ReferenceAnnotationTester<>(type, random, loops);
  }

  /**
   * 获取待测试的领域对象模型的类型。
   *
   * @return 待测试的领域对象模型的类型。
   */
  public final Class<T> getType() {
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
   * 测试模型的JSON序列化和反序列化。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testJsonSerializeDeserialize() throws Exception {
    jacksonJsonTester.test();
  }

  /**
   * 测试模型的XML序列化和反序列化。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testXmlSerializeDeserialize() throws Exception {
    xmlTester.test();
  }

  /**
   * 测试模型的克隆功能。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testClone() throws Exception {
    cloneTester.test();
  }

  /**
   * 测试模型的可序列化性。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testSerializable() throws Exception {
    serializableTester.test();
  }

  /**
   * 测试模型的 Nullable 注解。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testNullableAnnotation() throws Exception {
    nullableAnnotationTester.test();
  }

  /**
   * 测试模型的 Size 注解。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testSizeAnnotation() throws Exception {
    sizeAnnotationTester.test();
  }

  /**
   * 测试模型的 Reference 注解。
   *
   * @throws Exception
   *     如果发生任何错误。
   */
  @Test
  public void testReferenceAnnotation() throws Exception {
    referenceAnnotationTester.test();
  }
}
