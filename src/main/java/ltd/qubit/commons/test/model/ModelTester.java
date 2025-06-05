////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.model;

import ltd.qubit.commons.random.RandomBeanGenerator;

import static ltd.qubit.commons.lang.Argument.requireNonNull;
import static ltd.qubit.commons.lang.Argument.requirePositive;

/**
 * 执行领域模型单元测试的对象的基类。
 *
 * @param <T>
 *     待测试对象的类型。
 * @author 胡海星
 */
public abstract class ModelTester<T> {

  public static final int DEFAULT_LOOPS = 10;

  protected final Class<T> type;
  protected final int loops;
  protected final RandomBeanGenerator random;
  protected boolean enabled = true;

  /**
   * 构造一个 {@link ModelTester} 对象。
   *
   * @param type
   *     待测试对象的类。
   */
  public ModelTester(final Class<T> type) {
    this(type, new RandomBeanGenerator(), DEFAULT_LOOPS);
  }

  /**
   * 构造一个 {@link ModelTester} 对象。
   *
   * @param type
   *     待测试对象的类。
   * @param loops
   *     测试循环次数。
   */
  public ModelTester(final Class<T> type, final int loops) {
    this(type, new RandomBeanGenerator(), loops);
  }

  /**
   * 构造一个 {@link ModelTester} 对象。
   *
   * @param type
   *     待测试对象的类。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public ModelTester(final Class<T> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS);
  }

  /**
   * 构造一个 {@link ModelTester} 对象。
   *
   * @param type
   *     待测试对象的类。
   * @param random
   *     用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   * @param loops
   *     测试循环次数。
   */
  public ModelTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    this.type = type;
    this.random = requireNonNull("random", random);
    this.loops = requirePositive("loops", loops);
  }

  /**
   * 获取待测试对象的类。
   *
   * @return 待测试对象的类。
   */
  public Class<T> getType() {
    return type;
  }

  /**
   * 获取测试循环次数。
   *
   * @return 测试循环次数。
   */
  public final int getLoops() {
    return loops;
  }

  /**
   * 获取用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   *
   * @return 用于生成随机数据的 {@link RandomBeanGenerator} 对象。
   */
  public final RandomBeanGenerator getRandom() {
    return random;
  }

  /**
   * 检查此测试器当前是否启用。
   *
   * @return 如果此测试器已启用，则返回 {@code true}；否则返回 {@code false}。
   */
  public final boolean isEnabled() {
    return enabled;
  }

  /**
   * 设置此测试器的启用状态。
   *
   * @param enabled
   *     如果为 {@code true}，则启用此测试器；如果为 {@code false}，则禁用它。
   */
  public final void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * 执行测试。
   * <p>
   * 仅当此测试器已启用 ({@link #isEnabled()} 返回 {@code true}) 时，才会调用 {@link #doTest()} 方法。
   * </p>
   *
   * @throws Exception
   *     如果在测试执行过程中发生任何错误。
   */
  public final void test() throws Exception {
    if (enabled) {
      doTest();
    }
  }

  /**
   * 执行实际的测试逻辑。子类必须实现此方法以定义具体的测试行为。
   *
   * @throws Exception
   *     如果在测试执行过程中发生任何错误。
   */
  protected abstract void doTest() throws Exception;
}
