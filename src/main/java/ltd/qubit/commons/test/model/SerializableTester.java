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

  public SerializableTester(final Class<T> type) {
    super(type);
  }

  public SerializableTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  public SerializableTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  public SerializableTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  @Override
  protected void doTest() throws Exception {
    if (!type.isEnum()) {
      assertTrue(Serializable.class.isAssignableFrom(type));
      assertNotNull(FieldUtils.getField(type, Option.STATIC | Option.PRIVATE,
          "serialVersionUID"), "The serialVersionUID must be defined.");
    }
  }

}
