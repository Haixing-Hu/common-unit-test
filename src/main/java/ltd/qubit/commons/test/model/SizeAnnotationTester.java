////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import java.util.List;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.reflect.BeanInfo;
import ltd.qubit.commons.reflect.Property;
import ltd.qubit.commons.util.range.CloseRange;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 检查对象字段的`@Size`注解参数是否正确。
 *
 * @param <T>
 *     待测试的领域对象模型的类型。
 * @author 胡海星
 */
public class SizeAnnotationTester<T> extends ModelTester<T> {

  /**
   * 构造一个 {@link SizeAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   */
  public SizeAnnotationTester(final Class<T> type) {
    super(type);
  }

  /**
   * 构造一个 {@link SizeAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param loops
   *     测试循环次数。
   */
  public SizeAnnotationTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  /**
   * 构造一个 {@link SizeAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public SizeAnnotationTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link SizeAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public SizeAnnotationTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行对模型中字符串类型属性的 {@code @Size} 注解的测试逻辑。
   * <p>
   * 此方法会遍历待测试类型的所有属性：
   * <ul>
   *   <li>忽略枚举类型、计算属性及JDK内置属性。</li>
   *   <li>仅关注 {@link String} 类型的属性。</li>
   *   <li>对于唯一的字符串字段 (unique string field)，断言其必须有 {@code @Size} 注解，且最小长度 (min) 至少为1。</li>
   *   <li>对于非空 (non-nullable) 且有 {@code @Size} 注解的字符串字段，断言其 {@code @Size} 注解的最小长度 (min) 至少为1。</li>
   *   <li>对于其他有 {@code @Size} 注解的字符串字段，断言其 {@code @Size} 注解的最小长度 (min) 至少为1。</li>
   * </ul>
   *
   * @throws Exception
   *     如果在测试过程中发生任何错误。
   */
  @Override
  protected void doTest() throws Exception {
    if (Enum.class.isAssignableFrom(type)) {
      return;     // 忽略枚举类型
    }
    final BeanInfo beanInfo = BeanInfo.of(type);
    final List<Property> properties = beanInfo.getProperties();
    for (final Property prop : properties) {
      if (prop.isComputed()) {
        continue;   // 忽略计算出的属性
      }
      if (prop.isJdkBuiltIn()) {
        continue;   // 忽略JDK内置属性
      }
      if (prop.getType() != String.class) {
        continue;  // 只考虑 String 类型的字段
      }
      final CloseRange<Integer> sizeRange = prop.getSizeRange();
      if (prop.isUnique()) {
        assertNotNull(sizeRange, "Unique string field " + prop.getFullname()
            + " must be annotated by a @Size annotation.");
        assertNotEquals(0, sizeRange.getMin(), "Unique string field " + prop.getFullname()
            + " must be annotated by a @Size annotation with a min value of at least 1.");
      } else if (!prop.isNullable() && sizeRange != null) {
        // assertNotNull(sizeRange, "Non-nullable string field " + prop.getFullname()
        //    + " must be annotated by a @Size annotation.");
        assertNotEquals(0, sizeRange.getMin(),
            "@Size annotated non-nullable string field " + prop.getFullname()
                + " must have a min value of at least 1.");
      } else if (sizeRange != null) {
        assertNotEquals(0, sizeRange.getMin(),
            "@Size annotated string field " + prop.getFullname()
                + " must specify a min value of at least 1.");
      }
    }
  }
}
