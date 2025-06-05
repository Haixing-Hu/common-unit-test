////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import java.io.Serializable;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.reflect.FieldUtils;
import ltd.qubit.commons.reflect.Option;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试实现了`java.io.Serializable`接口的类是否定义了`serialVersionUID`。
 *
 * @param <T>
 *     待测试的领域对象模型的类型。
 * @author 胡海星
 */
public class SerializableTester<T> extends ModelTester<T> {

  /**
   * 构造一个 {@link SerializableTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   */
  public SerializableTester(final Class<T> type) {
    super(type);
  }

  /**
   * 构造一个 {@link SerializableTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param loops
   *     测试循环次数。
   */
  public SerializableTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  /**
   * 构造一个 {@link SerializableTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public SerializableTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link SerializableTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public SerializableTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行对模型可序列化性的测试逻辑。
   * <p>
   * 此方法会检查待测试的类型（如果不是枚举类型）：
   * <ul>
   *   <li>是否实现了 {@link java.io.Serializable} 接口。</li>
   *   <li>是否定义了名为 {@code serialVersionUID} 的私有静态字段。</li>
   * </ul>
   *
   * @throws Exception
   *     如果在测试过程中发生任何错误，例如反射访问字段失败。
   */
  @Override
  protected void doTest() throws Exception {
    if (!type.isEnum()) {
      assertTrue(Serializable.class.isAssignableFrom(type));
      assertNotNull(FieldUtils.getField(type, Option.STATIC | Option.PRIVATE,
          "serialVersionUID"), "The serialVersionUID must be defined.");
    }
  }

}
