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
import ltd.qubit.commons.test.testbed.ObjectWithSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import static ltd.qubit.commons.test.xml.JaxbTestUtils.testXmlDeserialization;
import static ltd.qubit.commons.test.xml.JaxbTestUtils.testXmlSerialization;

public class JaxbTestUtilsTest {

  private final RandomBeanGenerator generator = new RandomBeanGenerator();

  @Test
  public void testGetXmlFieldName() throws Exception {
    assertEquals("id", JaxbTestUtils.toXmlName("id"));
    assertEquals("organization", JaxbTestUtils.toXmlName("organization"));
    assertEquals("security-key", JaxbTestUtils.toXmlName("securityKey"));
    assertEquals("create-time", JaxbTestUtils.toXmlName("createTime"));
    assertEquals("token-create-time",
        JaxbTestUtils.toXmlName("tokenCreateTime"));
  }

  @Test
  public void testTestXmlSerialization() throws Exception {
    @SuppressWarnings("unchecked")
    final Info info = generator.nextObject(Info.class);
    testXmlSerialization(info);
    testXmlDeserialization(info);

    final Category category = generator.nextObject(Category.class);
    testXmlSerialization(category);
    testXmlDeserialization(category);

    final App app = generator.nextObject(App.class);
    testXmlSerialization(app);
    testXmlDeserialization(app);

    final ObjectWithEnumField obj1 = generator.nextObject(ObjectWithEnumField.class);
    testXmlSerialization(obj1);
    testXmlDeserialization(obj1);

    final ObjectWithListField obj2 = generator.nextObject(ObjectWithListField.class);
    testXmlSerialization(obj2);
    testXmlDeserialization(obj2);

    final ObjectWithArrayField obj3 = generator.nextObject(ObjectWithArrayField.class);
    testXmlSerialization(obj3);
    testXmlDeserialization(obj3);

    final Location obj4 = generator.nextObject(Location.class);
    testXmlSerialization(obj4);
    testXmlDeserialization(obj4);

    final BeanWithPhone obj5 = generator.nextObject(BeanWithPhone.class);
    testXmlSerialization(obj5);
    testXmlDeserialization(obj5);
  }

  @Test
  public void testTestXmlSerializationObjectWithMapField() throws Exception {
    final ObjectWithMapField obj = generator.nextObject(ObjectWithMapField.class);
    testXmlSerialization(obj);
    testXmlDeserialization(obj);
  }

  @Disabled("FIXME: set is not supported yet")
  @Test
  public void testTestXmlSerializationObjectWithSetField() throws Exception {
    final ObjectWithSet obj = generator.nextObject(ObjectWithSet.class);
    testXmlSerialization(obj);
    testXmlDeserialization(obj);
  }

  @Test
  public void testTestXmlSerializationBadBean() throws Exception {
    final BadBean bean = generator.nextObject(BadBean.class);
    try {
      testXmlSerialization(bean);
      fail("should throw");
    } catch (final AssertionError e) {
      e.printStackTrace();
      assertNotEquals("should throw", e.getMessage());
      // pass
    }
    testXmlDeserialization(bean);
  }

}
