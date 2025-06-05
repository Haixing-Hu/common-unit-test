////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test;

import java.util.List;

import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ltd.qubit.commons.random.EasyRandom;

import static ltd.qubit.commons.lang.Argument.requireNonNull;

/**
 * 测试工厂的基类。
 *
 * @author 胡海星
 */
public abstract class TestGenerator {

  protected final Logger logger;
  protected TestParameters parameters;
  protected EasyRandom random;

  /**
   * 构造一个 {@link TestGenerator} 对象。
   *
   * @param random
   *     用于生成随机数据的 {@link EasyRandom} 对象。
   * @param parameters
   *     测试参数。
   */
  public TestGenerator(final EasyRandom random, final TestParameters parameters) {
    this.logger = LoggerFactory.getLogger(this.getClass());
    this.random = requireNonNull("random", random);
    this.parameters = requireNonNull("parameters", parameters);
  }

  /**
   * 获取测试参数。
   *
   * @return 测试参数。
   */
  public TestParameters getParameters() {
    return parameters;
  }

  /**
   * 设置测试参数。
   *
   * @param parameters
   *     新的测试参数。
   */
  public void setParameters(final TestParameters parameters) {
    this.parameters = requireNonNull("parameters", parameters);
  }

  /**
   * 获取用于生成随机数据的 {@link EasyRandom} 对象。
   *
   * @return 用于生成随机数据的 {@link EasyRandom} 对象。
   */
  public final EasyRandom getRandom() {
    return random;
  }

  /**
   * 设置用于生成随机数据的 {@link EasyRandom} 对象。
   *
   * @param random
   *     新的 {@link EasyRandom} 对象。
   * @return 当前对象的引用，以便链式调用。
   */
  public final TestGenerator setRandom(final EasyRandom random) {
    this.random = requireNonNull("random", random);
    return this;
  }

  /**
   * 创建指定类型的对象实例。
   *
   * @param <E>
   *     要创建对象的类型。
   * @param type
   *     要创建对象的类。
   * @return 创建的对象实例。
   */
  protected <E> E createObject(final Class<E> type) {
    return random.nextObject(type);
  }

  /**
   * 生成动态测试节点列表。
   *
   * @return 动态测试节点列表。
   * @throws Exception
   *     如果在生成测试时发生错误。
   */
  public abstract List<DynamicNode> generate() throws Exception;
}
