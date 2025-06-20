////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test;

import ltd.qubit.commons.lang.Equality;
import ltd.qubit.commons.lang.Hash;
import ltd.qubit.commons.text.tostring.ToStringBuilder;

/**
 * 提供DAO自动化测试的参数。
 *
 * @author 胡海星
 */
public class TestParameters {

  public static final int DEFAULT_LOOPS = 10;

  public static final int DEFAULT_COLLECTION_SIZE = 5;

  public static final int DEFAULT_TABLE_SIZE = 10;

  /**
   * 随机测试重复次数。
   */
  private int loops;

  /**
   * 随机生成的集合的大小上限。
   */
  private int collectionSize;

  /**
   * 随机生成的数据库表中元素的最大数目。
   */
  private int tableSize;

  /**
   * 构造一个 {@link TestParameters} 对象，并使用默认值初始化所有参数。
   */
  public TestParameters() {
    loops = DEFAULT_LOOPS;
    collectionSize = DEFAULT_COLLECTION_SIZE;
    tableSize = DEFAULT_TABLE_SIZE;
  }

  /**
   * 获取随机测试的重复次数。
   *
   * @return 随机测试的重复次数。
   */
  public final int getLoops() {
    return loops;
  }

  /**
   * 设置随机测试的重复次数。
   *
   * @param loops
   *     新的随机测试重复次数。
   */
  public final void setLoops(final int loops) {
    this.loops = loops;
  }

  /**
   * 获取随机生成的集合的大小上限。
   *
   * @return 随机生成的集合的大小上限。
   */
  public final int getCollectionSize() {
    return collectionSize;
  }

  /**
   * 设置随机生成的集合的大小上限。
   *
   * @param collectionSize
   *     新的随机生成的集合的大小上限。
   */
  public final void setCollectionSize(final int collectionSize) {
    this.collectionSize = collectionSize;
  }

  /**
   * 获取随机生成的数据库表中元素的最大数目。
   *
   * @return 随机生成的数据库表中元素的最大数目。
   */
  public final int getTableSize() {
    return tableSize;
  }

  /**
   * 设置随机生成的数据库表中元素的最大数目。
   *
   * @param tableSize
   *     新的随机生成的数据库表中元素的最大数目。
   */
  public final void setTableSize(final int tableSize) {
    this.tableSize = tableSize;
  }

  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final TestParameters other = (TestParameters) o;
    return Equality.equals(loops, other.loops)
        && Equality.equals(collectionSize, other.collectionSize)
        && Equality.equals(tableSize, other.tableSize);
  }

  public int hashCode() {
    final int multiplier = 7;
    int result = 3;
    result = Hash.combine(result, multiplier, loops);
    result = Hash.combine(result, multiplier, collectionSize);
    result = Hash.combine(result, multiplier, tableSize);
    return result;
  }

  public String toString() {
    return new ToStringBuilder(this)
        .append("loops", loops)
        .append("collectionSize", collectionSize)
        .append("tableSize", tableSize)
        .toString();
  }
}
