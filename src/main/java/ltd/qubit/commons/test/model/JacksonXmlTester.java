////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.text.jackson.CustomizedXmlMapper;

import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlDeserialization;
import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlSerialization;

/**
 * A tester object used to test the XML serialization/deserialization of
 * classes.
 *
 * @param <T>
 *      the type of the objects to be tested.
 * @author Haixing Hu
 */
public class JacksonXmlTester<T> extends ModelTester<T> {

  private final XmlMapper mapper;

  public JacksonXmlTester(final Class<T> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedXmlMapper());
  }

  public JacksonXmlTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedXmlMapper());
  }

  public JacksonXmlTester(final Class<T> type, final RandomBeanGenerator random, final int loops, final XmlMapper mapper) {
    super(type, random, loops);
    this.mapper = mapper;
  }

  @Override
  protected void doTest() throws Exception {
    for (int i = 0; i < loops; ++i) {
      final T obj = random.nextObject(type);
      testXmlSerialization(mapper, obj);
      testXmlDeserialization(mapper, obj);
    }
  }
}
