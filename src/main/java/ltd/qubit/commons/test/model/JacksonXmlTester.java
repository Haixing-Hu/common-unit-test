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

import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlDeserialization;
import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlSerialization;

/**
 * 用于测试类 XML 序列化/反序列化的测试器对象。
 *
 * @param <T>
 *     待测试对象的类型。
 * @author 胡海星
 */
public class JacksonXmlTester<T> extends ModelTester<T> {

  private final XmlMapper mapper;

  /**
   * 构造一个 {@link JacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public JacksonXmlTester(final Class<T> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link JacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public JacksonXmlTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedXmlMapper());
  }

  /**
   * 构造一个 {@link JacksonXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   * @param mapper
   *     用于XML序列化/反序列化的 {@link XmlMapper} 对象。
   */
  public JacksonXmlTester(final Class<T> type, final RandomBeanGenerator random, final int loops, final XmlMapper mapper) {
    super(type, random, loops);
    this.mapper = mapper;
  }

  /**
   * 执行XML序列化和反序列化的测试逻辑。
   * <p>
   * 此方法会循环指定次数，在每次循环中：
   * <ol>
   *   <li>使用 {@link RandomBeanGenerator} 生成一个待测试类型的随机实例。</li>
   *   <li>调用 {@code JacksonXmlTestUtils.testXmlSerialization} 测试该实例的XML序列化。</li>
   *   <li>调用 {@code JacksonXmlTestUtils.testXmlDeserialization} 测试该实例的XML反序列化。</li>
   * </ol>
   *
   * @throws Exception
   *     如果在序列化或反序列化过程中发生错误。
   */
  @Override
  protected void doTest() throws Exception {
    for (int i = 0; i < loops; ++i) {
      final T obj = random.nextObject(type);
      testXmlSerialization(mapper, obj);
      testXmlDeserialization(mapper, obj);
    }
  }
}
