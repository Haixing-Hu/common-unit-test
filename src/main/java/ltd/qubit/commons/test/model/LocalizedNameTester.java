/// /////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import java.util.Locale;

import ltd.qubit.commons.lang.EnumUtils;
import ltd.qubit.commons.lang.LocalizedEnum;
import ltd.qubit.commons.random.RandomBeanGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 测试实现了 {@link ltd.qubit.commons.lang.LocalizedEnum} 接口的枚举的本地化名称功能。
 *
 * @param <E>
 *     待测试的枚举类型，该枚举必须实现 {@link ltd.qubit.commons.lang.LocalizedEnum}。
 * @author 胡海星
 */
public class LocalizedNameTester<E extends Enum<E>> extends ModelTester<E> {

  /**
   * 构造一个 {@link LocalizedNameTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   */
  public LocalizedNameTester(final Class<E> type) {
    super(type);
  }

  /**
   * 构造一个 {@link LocalizedNameTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public LocalizedNameTester(final Class<E> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  /**
   * 构造一个 {@link LocalizedNameTester} 对象。
   *
   * @param type
   *     待测试的枚举类的类型。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public LocalizedNameTester(final Class<E> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  /**
   * 执行本地化名称的测试逻辑。
   * <p>
   * 此方法会检查待测试的枚举类型是否实现了 {@link LocalizedEnum} 接口。
   * 如果是，则针对每个枚举常量，使用不同的 {@link Locale}（英语、简体中文以及一个会触发回退的区域设置如意大利语）
   * 来测试其本地化名称的获取以及通过本地化名称反查枚举实例的功能。
   * </p>
   */
  @Override
  protected void doTest() {
    if (LocalizedEnum.class.isAssignableFrom(type)) {
      for (final E e : type.getEnumConstants()) {
        testLocalizedName(e, Locale.ENGLISH, false);
        testLocalizedName(e, Locale.SIMPLIFIED_CHINESE, false);
        testLocalizedName(e, Locale.ITALY, true);
      }
    }
  }

  /**
   * 测试单个枚举常量在指定区域设置下的本地化名称相关功能。
   *
   * @param e
   *     待测试的枚举常量。
   * @param locale
   *     用于获取本地化名称的 {@link Locale} 对象。
   * @param fallback
   *     一个布尔值，指示在此测试场景中是否预期本地化名称会回退到枚举常量的默认名称 ({@link Enum#name()})。
   *     如果为 {@code true}，则断言本地化名称与默认名称相同；
   *     如果为 {@code false}，则断言本地化名称与默认名称不同（除非默认名称本身就是该区域的本地化名称）。
   */
  private void testLocalizedName(final E e, final Locale locale, final boolean fallback) {
    final String name = e.name();
    final String localizedName = ((LocalizedEnum) e).getLocalizedNameFor(locale);
    final E e2 = EnumUtils.forLocalizedName(type, locale, localizedName);
    assertNotNull(e2);
    final String localizedName2 = ((LocalizedEnum) e2).getLocalizedNameFor(locale);
    final String message = String.format("The localized name of the value '%s' in the local %s is: '%s'",
        name, locale, localizedName);
    System.out.println(message);
    assertEquals(e, e2, message);
    assertEquals(localizedName, localizedName2, message);
    if (fallback) {
      assertEquals(localizedName, e.name(), "The localized name of the value '" + name + "' in the local " + locale
          + " must fallback to the default name of the enumerator.");
    } else {
      assertNotEquals(localizedName, e.name(), "The localized name of the value '" + name + "' in the local " + locale
          + " must not equal to the default name of the enumerator.");
    }
  }
}
