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
 * The base class of test factories.
 *
 * @author Haixing Hu
 */
public abstract class TestGenerator {

  protected final Logger logger;
  protected TestParameters parameters;
  protected EasyRandom random;

  public TestGenerator(final EasyRandom random, final TestParameters parameters) {
    this.logger = LoggerFactory.getLogger(this.getClass());
    this.random = requireNonNull("random", random);
    this.parameters = requireNonNull("parameters", parameters);
  }

  public TestParameters getParameters() {
    return parameters;
  }

  public void setParameters(final TestParameters parameters) {
    this.parameters = requireNonNull("parameters", parameters);
  }

  public final EasyRandom getRandom() {
    return random;
  }

  public final TestGenerator setRandom(final EasyRandom random) {
    this.random = requireNonNull("random", random);
    return this;
  }

  protected <E> E createObject(final Class<E> type) {
    return random.nextObject(type);
  }

  public abstract List<DynamicNode> generate() throws Exception;
}
