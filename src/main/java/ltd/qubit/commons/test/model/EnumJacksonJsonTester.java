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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A tester object used to test the JSON serialization/deserialization of
 * enumeration classes.
 *
 * @param <E>
 *      the type of the enumeration to be tested.
 * @author Haixing Hu
 */
public class EnumJacksonJsonTester<E extends Enum<E>> extends ModelTester<E> {

  private JsonMapper mapper;

  public EnumJacksonJsonTester(final Class<E> type) {
    this(type, new RandomBeanGenerator(), DEFAULT_LOOPS, new CustomizedJsonMapper());
  }

  public EnumJacksonJsonTester(final Class<E> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedJsonMapper());
  }

  public EnumJacksonJsonTester(final Class<E> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedJsonMapper());
  }

  public EnumJacksonJsonTester(final Class<E> type, final RandomBeanGenerator random, final int loops,
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
    for (final E e : type.getEnumConstants()) {
      final String str = mapper.writeValueAsString(e);
      assertEquals("\"" + e.name() + "\"", str, "The JSON serialization of the enumerator "
          + e + " must be its name, but it is '" + str + "'.");
      final E e2 = mapper.readValue(str, type);
      assertEquals(e, e2, "The JSON deserialization of the enumerator " + e + " must be itself.");
    }
  }
}
