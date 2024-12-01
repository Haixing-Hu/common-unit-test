////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.testbed;

import java.util.Map;

import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import ltd.qubit.commons.lang.Equality;
import ltd.qubit.commons.lang.Hash;
import ltd.qubit.commons.text.tostring.ToStringBuilder;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@XmlRootElement(name = "object-with-map-field")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = ANY,
    getterVisibility = NONE,
    isGetterVisibility = NONE,
    setterVisibility = NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectWithMapField {

  @XmlElementWrapper(name = "int-map")
  @XmlElement(name="entry")
  @Size(min = 2, max = 5)
  private Map<Integer, String> intMap;

  @XmlElementWrapper(name = "string-map")
  @XmlElement(name="entry")
  private StringMap stringMap;

  public ObjectWithMapField() {
    // empty
  }

  public Map<Integer, String> getIntMap() {
    return intMap;
  }

  public void setIntMap(final Map<Integer, String> intMap) {
    this.intMap = intMap;
  }

  public StringMap getStringMap() {
    return stringMap;
  }

  public void setStringMap(final StringMap stringMap) {
    this.stringMap = stringMap;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final ObjectWithMapField other = (ObjectWithMapField) o;
    return Equality.equals(intMap, other.intMap)
        && Equality.equals(stringMap, other.stringMap);
  }

  @Override
  public int hashCode() {
    final int multiplier = 7;
    int result = 3;
    result = Hash.combine(result, multiplier, intMap);
    result = Hash.combine(result, multiplier, stringMap);
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("intMap", intMap)
        .append("stringMap", stringMap).toString();
  }
}
