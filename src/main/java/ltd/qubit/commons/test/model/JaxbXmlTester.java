////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import ltd.qubit.commons.random.RandomBeanGenerator;

import static ltd.qubit.commons.test.xml.JaxbTestUtils.testXmlDeserialization;
import static ltd.qubit.commons.test.xml.JaxbTestUtils.testXmlSerialization;

/**
 * A tester object used to test the XML serialization/deserialization of
 * classes.
 *
 * @param <T>
 *      the type of the objects to be tested.
 * @author Haixing Hu
 */
public class JaxbXmlTester<T> extends ModelTester<T> {

  public JaxbXmlTester(final Class<T> type) {
    super(type);
  }

  public JaxbXmlTester(final Class<T> type, final int loops) {
    super(type, loops);
  }

  public JaxbXmlTester(final Class<T> type, final RandomBeanGenerator random) {
    super(type, random);
  }

  public JaxbXmlTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    super(type, random, loops);
  }

  @Override
  protected void doTest() throws Exception {
    for (int i = 0; i < loops; ++i) {
      final T obj = random.nextObject(type);
      testXmlSerialization(obj);
      testXmlDeserialization(obj);
    }
  }
}
