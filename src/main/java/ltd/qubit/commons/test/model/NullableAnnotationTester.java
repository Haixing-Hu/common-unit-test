////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.reflect.BeanInfo;
import ltd.qubit.commons.reflect.Property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static ltd.qubit.commons.reflect.MethodUtils.isAnnotationDirectlyPresent;
import static ltd.qubit.commons.reflect.MethodUtils.isAnnotationDirectlyPresentInParameters;

/**
 * 测试字段上的`@Nullable`注解是否和对应的 Setter, Getter 上的`@Nullable`注解一致。
 *
 * @param <T>
 *     待测试的领域对象模型的类型。
 * @author 胡海星
 */
public class NullableAnnotationTester<T> extends ModelTester<T> {

  /**
   * 构造一个 {@link NullableAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   */
  public NullableAnnotationTester(final Class<T> type) {
    super(type);
  }

  /**
   * 构造一个 {@link NullableAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param loops
   *     测试循环次数。
   */
  public NullableAnnotationTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  /**
   * 构造一个 {@link NullableAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public NullableAnnotationTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link NullableAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public NullableAnnotationTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行对模型中属性及其对应的getter/setter方法上 {@code @Nullable} 注解一致性的测试逻辑。
   * <p>
   * 此方法会遍历待测试类型的所有属性：
   * <ul>
   *   <li>忽略枚举类型、计算属性、JDK内置属性以及transient字段。</li>
   *   <li>检查属性的 {@code @Nullable} 状态 ({@code prop.isNullable()})。</li>
   *   <li>验证getter方法存在。</li>
   *   <li>如果属性非只读，验证setter方法存在且只有一个参数。</li>
   *   <li>如果属性被标记为 {@code @Nullable}: 
   *     <ul>
   *       <li>断言该属性不能是基本类型。</li>
   *       <li>断言其getter方法必须直接标记 {@code @Nullable} 注解。</li>
   *       <li>如果存在setter方法，断言其参数必须直接标记 {@code @Nullable} 注解。</li>
   *     </ul>
   *   </li>
   *   <li>如果属性未被标记为 {@code @Nullable} (即非空):
   *     <ul>
   *       <li>断言其getter方法不能标记 {@code @Nullable} 注解。</li>
   *       <li>如果存在setter方法，断言其参数不能标记 {@code @Nullable} 注解。</li>
   *     </ul>
   *   </li>
   * </ul>
   *
   * @throws Exception
   *     如果在测试过程中发生任何错误，例如反射获取方法失败或断言失败。
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
      if (prop.isTransientField()) {
        continue;   // 忽略 transient 字段
      }
      final boolean nullable = prop.isNullable();
      final boolean primitive = prop.isPrimitive();
      final Method getter = prop.getReadMethod();
      final Method setter = prop.getWriteMethod();
      assertNotNull(getter, "The getter of the field " + prop.getFullname()
        + " must exist.");
      if (!prop.isReadonly()) {
        assertNotNull(setter, "The setter of the field " + prop.getFullname() + " must exist.");
        assertEquals(1, setter.getParameterCount(), "The setter of the field "
            + prop.getFullname() + " must have exactly one parameter.");
      }
      if (nullable) {
        assertFalse(primitive, "Primitive field " + prop.getFullname()
            + " cannot be annotated with @Nullable annotation.");
        assertTrue(isAnnotationDirectlyPresent(getter, Nullable.class),
            "The getter of the @Nullable field " + prop.getFullname()
                + " must be annotated with @Nullable.");
        if (setter != null) {
          assertTrue(isAnnotationDirectlyPresentInParameters(setter, Nullable.class),
              "The parameter of the setter of the @Nullable field "
                  + prop.getFullname() + " must be annotated with @Nullable.");
        }
      } else {
        assertFalse(isAnnotationDirectlyPresent(getter, Nullable.class),
            "The getter of the field " + prop.getFullname()
                + " must NOT be annotated with @Nullable.");
        if (setter != null) {
          assertFalse(isAnnotationDirectlyPresentInParameters(setter, Nullable.class),
              "The parameter of the setter of the field " + prop.getFullname()
                  + " must NOT be annotated with @Nullable.");
        }
      }
    }
  }
}
