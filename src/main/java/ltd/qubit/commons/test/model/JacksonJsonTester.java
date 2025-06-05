////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import com.fasterxml.jackson.databind.json.JsonMapper;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.text.jackson.CustomizedJsonMapper;

import static ltd.qubit.commons.test.json.JacksonJsonTestUtils.testJsonDeserialization;
import static ltd.qubit.commons.test.json.JacksonJsonTestUtils.testJsonSerialization;

/**
 * 用于测试类 JSON 序列化/反序列化的测试器对象。
 *
 * @param <T>
 *     待测试的类的类型。
 * @author 胡海星
 */
public class JacksonJsonTester<T> extends ModelTester<T> {

  private JsonMapper mapper;

  /**
   * 构造一个 {@link JacksonJsonTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public JacksonJsonTester(final Class<T> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedJsonMapper());
  }

  /**
   * 构造一个 {@link JacksonJsonTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public JacksonJsonTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedJsonMapper());
  }

  /**
   * 构造一个 {@link JacksonJsonTester} 对象。
   *
   * @param type
   *     待测试的类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   * @param mapper
   *     用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   */
  public JacksonJsonTester(final Class<T> type, final RandomBeanGenerator random, final int loops,
      final JsonMapper mapper) {
    super(type, random, loops);
    this.mapper = mapper;
  }

  /**
   * 获取用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   *
   * @return 当前使用的 {@link JsonMapper} 对象。
   */
  public final JsonMapper getMapper() {
    return mapper;
  }

  /**
   * 设置用于JSON序列化/反序列化的 {@link JsonMapper} 对象。
   *
   * @param mapper
   *     新的 {@link JsonMapper} 对象。
   */
  public void setMapper(final JsonMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * 执行JSON序列化和反序列化的测试逻辑。
   * <p>
   * 此方法会循环指定次数，在每次循环中：
   * <ol>
   *   <li>使用 {@link RandomBeanGenerator} 生成一个待测试类型的随机实例。</li>
   *   <li>调用 {@code JacksonJsonTestUtils.testJsonSerialization} 测试该实例的JSON序列化。</li>
   *   <li>调用 {@code JacksonJsonTestUtils.testJsonDeserialization} 测试该实例的JSON反序列化。</li>
   * </ol>
   *
   * @throws Exception
   *     如果在序列化或反序列化过程中发生错误。
   */
  @Override
  protected void doTest() throws Exception {
    for (int i = 0; i < loops; ++i) {
      final T obj = random.nextObject(type);
      testJsonSerialization(mapper, obj);
      testJsonDeserialization(mapper, obj);
    }
  }
}
