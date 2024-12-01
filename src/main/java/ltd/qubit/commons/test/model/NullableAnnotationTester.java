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

  public NullableAnnotationTester(final Class<T> type) {
    super(type);
  }

  public NullableAnnotationTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  public NullableAnnotationTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  public NullableAnnotationTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

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
