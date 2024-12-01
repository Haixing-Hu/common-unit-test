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
import static ltd.qubit.commons.text.CaseFormat.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlSerialization;

/**
 * A tester object used to test the XML serialization/deserialization of enumeration
 * classes.
 *
 * @param <E>
 *      the type of the enumeration to be tested.
 * @author Haixing Hu
 */
public class EnumJacksonXmlTester<E extends Enum<E>> extends ModelTester<E> {

  private final XmlMapper mapper;

  public EnumJacksonXmlTester(final Class<E> type) {
    this(type, new RandomBeanGenerator(), DEFAULT_LOOPS, new CustomizedXmlMapper());
  }

  public EnumJacksonXmlTester(final Class<E> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS, new CustomizedXmlMapper());
  }

  public EnumJacksonXmlTester(final Class<E> type, final RandomBeanGenerator random, final int loops) {
    this(type, random, loops, new CustomizedXmlMapper());
  }

  public EnumJacksonXmlTester(final Class<E> type, final RandomBeanGenerator random, final int loops, final XmlMapper mapper) {
    super(type, random, loops);
    this.mapper = mapper;
  }

  @Override
  protected void doTest() throws Exception {
    final String tagName = UPPER_CAMEL.to(LOWER_HYPHEN, type.getSimpleName());
    final String openTag = "<" + tagName + ">";
    final String closeTag = "</" + tagName + ">";
    for (final E e : type.getEnumConstants()) {
      final String str = mapper.writeValueAsString(e);
      assertEquals(openTag + e.name() + closeTag, str.strip(),
          "The XML serialization of the enumerator " + e + " must be its name, but it is '" + str + "'.");
      final E e2 = mapper.readValue(str, type);
      assertEquals(e, e2, "The XML deserialization of the enumerator " + e + " must be itself.");
    }
  }
}
