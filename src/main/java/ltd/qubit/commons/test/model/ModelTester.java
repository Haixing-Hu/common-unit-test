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
 * The base class of objects performing unit testing of a domain model.
 *
 * @param <T>
 *      the type of the objects to be tested.
 * @author Haixing Hu
 */
public abstract class ModelTester<T> {

  public static final int DEFAULT_LOOPS = 10;

  protected final Class<T> type;
  protected final int loops;
  protected final RandomBeanGenerator random;
  protected boolean enabled = true;

  public ModelTester(final Class<T> type) {
    this(type, new RandomBeanGenerator(), DEFAULT_LOOPS);
  }

  public ModelTester(final Class<T> type, final int loops) {
    this(type, new RandomBeanGenerator(), loops);
  }

  public ModelTester(final Class<T> type, final RandomBeanGenerator random) {
    this(type, random, DEFAULT_LOOPS);
  }

  public ModelTester(final Class<T> type, final RandomBeanGenerator random, final int loops) {
    this.type = type;
    this.random = requireNonNull("random", random);
    this.loops = requirePositive("loops", loops);
  }

  public Class<T> getType() {
    return type;
  }

  public final int getLoops() {
    return loops;
  }

  public final RandomBeanGenerator getRandom() {
    return random;
  }

  public final boolean isEnabled() {
    return enabled;
  }

  public final void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public final void test() throws Exception {
    if (enabled) {
      doTest();
    }
  }

  protected abstract void doTest() throws Exception;
}
