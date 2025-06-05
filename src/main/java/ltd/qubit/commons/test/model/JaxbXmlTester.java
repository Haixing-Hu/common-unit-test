////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import ltd.qubit.commons.random.RandomBeanGenerator;

import static ltd.qubit.commons.test.xml.JaxbTestUtils.testXmlDeserialization;
import static ltd.qubit.commons.test.xml.JaxbTestUtils.testXmlSerialization;

/**
 * 用于测试类 JAXB XML 序列化/反序列化的测试器对象。
 *
 * @param <T>
 *     待测试对象的类型。
 * @author 胡海星
 */
public class JaxbXmlTester<T> extends ModelTester<T> {

  /**
   * 构造一个 {@link JaxbXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   */
  public JaxbXmlTester(final Class<T> type) {
    super(type);
  }

  /**
   * 构造一个 {@link JaxbXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param loops
   *     测试循环次数。
   */
  public JaxbXmlTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  /**
   * 构造一个 {@link JaxbXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public JaxbXmlTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link JaxbXmlTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public JaxbXmlTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行JAXB XML序列化和反序列化的测试逻辑。
   * <p>
   * 此方法会循环指定次数，在每次循环中：
   * <ol>
   *   <li>使用 {@link RandomBeanGenerator} 生成一个待测试类型的随机实例。</li>
   *   <li>调用 {@code JaxbTestUtils.testXmlSerialization} 测试该实例的JAXB XML序列化。</li>
   *   <li>调用 {@code JaxbTestUtils.testXmlDeserialization} 测试该实例的JAXB XML反序列化。</li>
   * </ol>
   *
   * @throws Exception
   *     如果在序列化或反序列化过程中发生错误。
   */
  @Override
  protected void doTest() throws Exception {
    for (int i = 0; i < loops; ++i) {
      final T obj = random.nextObject(type);
      testXmlSerialization(obj);
      testXmlDeserialization(obj);
    }
  }
}
