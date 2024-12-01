////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.xml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ltd.qubit.commons.random.RandomBeanGenerator;
import ltd.qubit.commons.test.testbed.App;
import ltd.qubit.commons.test.testbed.BadBean;
import ltd.qubit.commons.test.testbed.BeanWithPhone;
import ltd.qubit.commons.test.testbed.Category;
import ltd.qubit.commons.test.testbed.Info;
import ltd.qubit.commons.test.testbed.Location;
import ltd.qubit.commons.test.testbed.ObjectWithArrayField;
import ltd.qubit.commons.test.testbed.ObjectWithEnumField;
import ltd.qubit.commons.test.testbed.ObjectWithListField;
import ltd.qubit.commons.test.testbed.ObjectWithMapField;
import ltd.qubit.commons.test.testbed.ObjectWithMapFieldNoAnnotation;
import ltd.qubit.commons.text.jackson.CustomizedXmlMapper;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlDeserialization;
import static ltd.qubit.commons.test.xml.JacksonXmlTestUtils.testXmlSerialization;

public class JacksonXmlTestUtilsTest {

  private final RandomBeanGenerator generator = new RandomBeanGenerator();
  private final XmlMapper mapper = new CustomizedXmlMapper();

  @Test
  public void testTestXmlSerialization() throws Exception {
    @SuppressWarnings("unchecked")
    final Info info = generator.nextObject(Info.class);
    testXmlSerialization(mapper, info);
    testXmlDeserialization(mapper, info);

    final Category category = generator.nextObject(Category.class);
    testXmlSerialization(mapper, category);
    testXmlDeserialization(mapper, category);

    final App app = generator.nextObject(App.class);
    testXmlSerialization(mapper, app);
    testXmlDeserialization(mapper, app);

    final ObjectWithEnumField obj1 = generator.nextObject(ObjectWithEnumField.class);
    testXmlSerialization(mapper, obj1);
    testXmlDeserialization(mapper, obj1);

    final ObjectWithListField obj2 = generator.nextObject(ObjectWithListField.class);
    testXmlSerialization(mapper, obj2);
    testXmlDeserialization(mapper, obj2);

    final Location obj4 = generator.nextObject(Location.class);
    testXmlSerialization(mapper, obj4);
    testXmlDeserialization(mapper, obj4);

    final BeanWithPhone obj5 = generator.nextObject(BeanWithPhone.class);
    testXmlSerialization(mapper, obj5);
    testXmlDeserialization(mapper, obj5);
  }

  @Disabled("FIXME: Not passed")
  @Test
  public void testTestXmlSerializationObjectWithArrayField() throws Exception {
    final ObjectWithArrayField obj3 = generator.nextObject(ObjectWithArrayField.class);
    testXmlSerialization(mapper, obj3);
    testXmlDeserialization(mapper, obj3);
  }

  @Test
  public void testTestXmlSerializationObjectWithMapFieldNoAnnotation() throws Exception {
    final ObjectWithMapFieldNoAnnotation obj = generator.nextObject(ObjectWithMapFieldNoAnnotation.class);
    testXmlSerialization(mapper, obj);
    testXmlDeserialization(mapper, obj);
  }

  @Disabled("FIXME: Not PASS")
  @Test
  public void testTestXmlSerializationObjectWithMapField() throws Exception {
    final ObjectWithMapField obj = generator.nextObject(ObjectWithMapField.class);
    testXmlSerialization(mapper, obj);
    testXmlDeserialization(mapper, obj);
  }

  @Test
  public void testTestXmlSerializationBadBean() throws Exception {
    final BadBean bean = generator.nextObject(BadBean.class);
    try {
      testXmlSerialization(mapper, bean);
      fail("should throw");
    } catch (final AssertionError e) {
      e.printStackTrace();
      assertNotEquals("should throw", e.getMessage());
      // pass
    }
    testXmlDeserialization(mapper, bean);
  }

}
