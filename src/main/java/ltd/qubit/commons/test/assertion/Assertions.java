////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.assertion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class Assertions {

  public static <T> void assertCollectionEquals(@Nullable final Collection<T> col1,
      @Nullable final Collection<T> col2) {
    assertCollectionEquals(col1, col2, null);
  }

  public static <T> void assertCollectionEquals(@Nullable final Collection<T> col1,
      @Nullable final Collection<T> col2, @Nullable final String message) {
    if (col1 == null) {
      assertNull(col2, message);
    } else if (col2 == null) {
      assertNull(col1, message);
    } else {
      final Set<T> set1 = new HashSet<>(col1);
      final Set<T> set2 = new HashSet<>(col2);
      assertEquals(set1, set2, message);
    }
  }

  public static <T> void assertNotEmpty(@Nullable final Collection<T> col) {
    assertNotNull(col);
    assertFalse(col.isEmpty());
  }

  public static void assertNotEmpty(@Nullable final String str) {
    assertNotNull(str);
    assertFalse(str.isEmpty());
  }
}
