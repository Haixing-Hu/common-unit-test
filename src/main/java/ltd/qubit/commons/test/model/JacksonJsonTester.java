////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import com.fasterxml.jackson.databind.json.JsonMapper;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.text.jackson.CustomizedJsonMapper;

import static ltd.qubit.commons.test.json.JacksonJsonTestUtils.testJsonDeserialization;
import static ltd.qubit.commons.test.json.JacksonJsonTestUtils.testJsonSerialization;

/**
 * A tester object used to test the JSON serialization/deserialization of
 * classes.
 *
 * @author Haixing Hu
 */
public class JacksonJsonTester<T> extends ModelTester<T> {

  private JsonMapper mapper;

  public JacksonJsonTester(final Class<T> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedJsonMapper());
  }

  public JacksonJsonTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedJsonMapper());
  }

  public JacksonJsonTester(final Class<T> type, final RandomBeanGenerator random, final int loops,
      final JsonMapper mapper) {
    super(type, random, loops);
    this.mapper = mapper;
  }

  public final JsonMapper getMapper() {
    return mapper;
  }

  public void setMapper(final JsonMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  protected void doTest() throws Exception {
    for (int i = 0; i < loops; ++i) {
      final T obj = random.nextObject(type);
      testJsonSerialization(mapper, obj);
      testJsonDeserialization(mapper, obj);
    }
  }
}
