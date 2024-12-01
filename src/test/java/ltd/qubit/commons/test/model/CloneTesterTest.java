/// /////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import org.junit.jupiter.api.Test;

import ltd.qubit.commons.test.testbed.App;

public class CloneTesterTest {

  @Test
  public void testCloneTester() throws Exception {
    final CloneTester<App> tester = new CloneTester<>(App.class);
    tester.test();
  }
}
