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

import ltd.qubit.commons.test.testbed.CredentialType;

public class LocalizedNameTesterTest {

  @Test
  public void shouldWork() throws Exception {
    final LocalizedNameTester<CredentialType> tester = new LocalizedNameTester<>(CredentialType.class);
    tester.test();
  }
}
