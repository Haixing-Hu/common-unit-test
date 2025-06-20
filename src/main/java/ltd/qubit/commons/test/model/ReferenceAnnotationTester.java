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

import ltd.qubit.commons.lang.ArrayUtils;
import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.reflect.BeanInfo;
import ltd.qubit.commons.reflect.ObjectGraphUtils;
import ltd.qubit.commons.reflect.Property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static ltd.qubit.commons.lang.StringUtils.isEmpty;

/**
 * 检查对象字段的{@code @Reference}注解参数是否合法。
 *
 * @param <T>
 *     待测试的领域对象模型的类型。
 * @author 胡海星
 */
public class ReferenceAnnotationTester<T> extends ModelTester<T> {

  /**
   * 构造一个 {@link ReferenceAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   */
  public ReferenceAnnotationTester(final Class<T> type) {
    super(type);
  }

  /**
   * 构造一个 {@link ReferenceAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param loops
   *     测试循环次数。
   */
  public ReferenceAnnotationTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  /**
   * 构造一个 {@link ReferenceAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public ReferenceAnnotationTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link ReferenceAnnotationTester} 对象。
   *
   * @param type
   *     待测试的领域对象模型的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public ReferenceAnnotationTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行对模型中 {@code @Reference} 注解的测试逻辑。
   * <p>
   * 此方法会遍历待测试类型的所有属性：
   * <ul>
   *   <li>忽略枚举类型、计算属性及非引用属性。</li>
   *   <li>对于标记了 {@code @Reference} 注解的属性，检查其 {@code entity} 和 {@code property} 参数的合法性。</li>
   *   <li>验证引用属性的实际类型（或其集合/映射的泛型参数类型）是否与 {@code @Reference} 注解中指定的实体及属性路径所解析出的类型一致。</li>
   * </ul>
   *
   * @throws Exception
   *     如果在测试过程中发生任何错误，例如属性不存在或类型不匹配。
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
      if (! prop.isReference()) {
        continue;   // 忽略非引用属性
      }
      final Class<?> refClass = prop.getReferenceEntity();
      if (refClass.equals(Object.class)) {
        continue;   // 忽略 "间接引用"
      }
      final String refPropPath = prop.getReferenceProperty();
      final Class<?> expectedReferencedType;
      if (isEmpty(refPropPath)) {
        // 所引用的是实体类本身
        expectedReferencedType = refClass;
      } else {
        // 获取引用的属性的类型，注意引用的属性值可能是个属性路径
        expectedReferencedType = ObjectGraphUtils.getPropertyType(refClass, refPropPath);
        assertNotNull(expectedReferencedType, "The property " + refClass.getSimpleName()
            + "." + refPropPath + " does not exist.");
      }
      if (prop.getActualComponentType() != null) {
        assertEquals(expectedReferencedType, prop.getActualComponentType(),
            "The component type of the property " + prop.getFullname()
            + " must be " + expectedReferencedType.getName());
      } else if (prop.getActualTypeArguments() != null) {
        final Class<?>[] actual = prop.getActualTypeArguments();
        assertTrue(ArrayUtils.contains(actual, expectedReferencedType),
            "The actual type arguments of the property " + prop.getFullname()
            + " must contain " + expectedReferencedType.getName());
      } else {
        assertEquals(expectedReferencedType, prop.getType(),
            "The type of the property " + prop.getFullname()
            + " must be " + expectedReferencedType.getName());
      }
      // TODO: 检查 path
    }
  }
}
