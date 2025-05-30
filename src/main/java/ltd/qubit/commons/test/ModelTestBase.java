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

  protected ModelTestBase(final Class<T> type) {
    this(type, DEFAULT_TEST_LOOPS, new CustomizedJsonMapper(), new CustomizedXmlMapper());
  }

  protected ModelTestBase(final Class<T> type, final JsonMapper jsonMapper,
      final XmlMapper xmlMapper) {
    this(type, DEFAULT_TEST_LOOPS, jsonMapper, xmlMapper);
  }

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

  public final Class<T> getType() {
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
  public void testClone() throws Exception {
    cloneTester.test();
  }

  @Test
  public void testSerializable() throws Exception {
    serializableTester.test();
  }

  @Test
  public void testNullableAnnotation() throws Exception {
    nullableAnnotationTester.test();
  }

  @Test
  public void testSizeAnnotation() throws Exception {
    sizeAnnotationTester.test();
  }

  @Test
  public void testReferenceAnnotation() throws Exception {
    referenceAnnotationTester.test();
  }
}
