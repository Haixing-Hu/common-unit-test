////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import java.util.Map;

import jakarta.validation.constraints.Size;

import ltd.qubit.commons.lang.Equality;
import ltd.qubit.commons.lang.Hash;
import ltd.qubit.commons.text.tostring.ToStringBuilder;

public class ObjectWithMapFieldNoAnnotation {

  @Size(min = 2, max = 5)
  private Map<Integer, String> intMap;

  private StringMap stringMap;

  public ObjectWithMapFieldNoAnnotation() {
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
    final ObjectWithMapFieldNoAnnotation other = (ObjectWithMapFieldNoAnnotation) o;
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
