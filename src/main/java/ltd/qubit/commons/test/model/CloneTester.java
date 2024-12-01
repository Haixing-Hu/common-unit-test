////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import ltd.qubit.commons.lang.Assignable;
import ltd.qubit.commons.lang.CloneableEx;
import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.reflect.ConstructorUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * 测试实现了`CloneEx`和`Assignable`的类是否正确实现了`clone()`和`assign()`方法。
 *
 * @param <T>
 *     待测试的领域对象模型的类型。
 * @author 胡海星
 */
public class CloneTester<T> extends ModelTester<T> {

  public CloneTester(final Class<T> type) {
    super(type);
  }

  public CloneTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  public CloneTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  public CloneTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void doTest() throws Exception {
    if (CloneableEx.class.isAssignableFrom(type)) {
      for (int i = 0; i < loops; ++i) {
        final T obj = random.nextObject(type);
        final T clonedCopy = ((CloneableEx<T>) obj).cloneEx();
        assertEquals(obj, clonedCopy, "The cloned copy must equals to the original object.");
        assertNotSame(obj, clonedCopy, "The cloned copy must not be the same as the original object.");
        if (obj instanceof Assignable) {
          final T assignedCopy = ConstructorUtils.newInstance(type);
          ((Assignable<T>) assignedCopy).assign(obj);
          assertEquals(obj, assignedCopy, "The assigned copy must equals to the original object.");
        }
      }
    }
  }
}
