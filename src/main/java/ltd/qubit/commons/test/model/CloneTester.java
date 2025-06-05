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

  /**
   * 构造一个 {@link CloneTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   */
  public CloneTester(final Class<T> type) {
    super(type);
  }

  /**
   * 构造一个 {@link CloneTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param loops
   *     测试循环次数。
   */
  public CloneTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  /**
   * 构造一个 {@link CloneTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public CloneTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link CloneTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public CloneTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行对模型克隆和赋值功能的测试逻辑。
   * <p>
   * 此方法会检查待测试的类型是否实现了 {@link CloneableEx} 接口。
   * 如果是，则会循环指定次数，在每次循环中：
   * <ol>
   *   <li>使用 {@link RandomBeanGenerator} 生成一个待测试类型的随机实例 ({@code obj})。</li>
   *   <li>调用 {@code obj} 的 {@code cloneEx()} 方法创建一个克隆副本 ({@code clonedCopy})。</li>
   *   <li>断言 {@code obj} 与 {@code clonedCopy} 相等 ({@code equals()} 返回 {@code true})。</li>
   *   <li>断言 {@code obj} 与 {@code clonedCopy} 不是同一个对象实例 ({@code ==} 返回 {@code false})。</li>
   *   <li>如果 {@code obj} 同时实现了 {@link Assignable} 接口：
   *     <ul>
   *       <li>创建一个新的待测试类型的实例 ({@code assignedCopy})。</li>
   *       <li>调用 {@code assignedCopy} 的 {@code assign(obj)} 方法。</li>
   *       <li>断言 {@code obj} 与 {@code assignedCopy} 相等。</li>
   *     </ul>
   *   </li>
   * </ol>
   *
   * @throws Exception
   *     如果在测试过程中发生任何错误，例如反射调用构造函数失败或断言失败。
   */
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
