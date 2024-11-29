////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test;

import ltd.qubit.commons.lang.Assignable;
import ltd.qubit.commons.lang.CloneableEx;
import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.reflect.ConstructorUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * 测试实现了`CloneEx`和`Assignable`的类是否正确实现了`clone()`和`assign()`方法。
 *
 * @author 胡海星
 */
public class CloneTester extends Tester {

  public CloneTester() {
  }

  public CloneTester(final int loops) {
    super(loops);
  }

  public CloneTester(final RandomBeanGenerator random) {
    super(random);
  }

  public CloneTester(final RandomBeanGenerator random, final int loops) {
    super(random, loops);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected <T> void doTest(final Class<T> type) throws Exception {
    if (CloneableEx.class.isAssignableFrom(type)) {
      for (int i = 0; i < loops; ++i) {
        final T obj = random.nextObject(type);
        final T clonedCopy = ((CloneableEx<T>) obj).cloneEx();
        assertEquals(obj, clonedCopy,
            "The cloned copy must equals to the original object.");
        assertNotSame(obj, clonedCopy,
            "The cloned copy must not be the same as the original object.");
        if (obj instanceof Assignable) {
          final T assignedCopy = ConstructorUtils.newInstance(type);
          ((Assignable<T>) assignedCopy).assign(obj);
          assertEquals(obj, assignedCopy,
              "The assigned copy must equals to the original object.");
        }
      }
    }
  }
}
