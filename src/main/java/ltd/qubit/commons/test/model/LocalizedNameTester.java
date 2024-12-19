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

public class LocalizedNameTester<E extends Enum<E>> extends ModelTester<E> {

  public LocalizedNameTester(final Class<E> type) {
    super(type);
  }

  public LocalizedNameTester(final Class<E> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  public LocalizedNameTester(final Class<E> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

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
