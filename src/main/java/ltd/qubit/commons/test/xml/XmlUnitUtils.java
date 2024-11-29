////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.test.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.matchers.EvaluateXPathMatcher;
import org.xmlunit.matchers.HasXPathMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

/**
 * Provide utilities functions for XML Unit.
 *
 * @author Haixing Hu
 */
public class XmlUnitUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlUnitUtils.class);

  public static void assertXPathEquals(final String xml, final String xpath,
          @Nullable final Object value) {
    if (value == null) {
      assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
    } else {
      assertThat(xml, EvaluateXPathMatcher.hasXPath(xpath, equalTo(value.toString())));
    }
  }

  public static void assertXPathEquals(final String xml, final String xpath,
      @Nullable final Enum<?> value) {
    if (value == null) {
      assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
    } else {
      assertThat(xml, EvaluateXPathMatcher.hasXPath(xpath, equalTo(value.name())));
    }
  }

  public static void assertXPathNull(final String xml, final String xpath) {
    assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
  }

  public static void assertXPathAbsent(final String xml, final String xpath) {
    assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
  }

  public static <T> void assertXPathArrayEquals(final String xml,
      final String rootPath, final String wrapperNode, final String elementNode,
      @Nullable final T[] array) {
    if (array == null) {
      assertXPathNull(xml, rootPath + "/" + wrapperNode);
    } else {
      final int n = array.length;
      final String prefix = rootPath + "/" + wrapperNode + "/" + elementNode;
      for (int i = 0; i < n; ++i) {
        assertXPathEquals(xml, prefix + "[" + (i + 1) + "]", array[i]);
      }
    }
  }

  public static void assertXmlEqual(final Object obj, final String expected, final String actual)
      throws Exception {
    LOGGER.debug("Object is:\n{}", obj);
    LOGGER.debug("Expected XML is:\n{}", expected);
    LOGGER.debug("Actual XML is:\n{}", actual);
    XmlAssert.assertThat(actual).and(expected)
             .ignoreComments()
             .ignoreChildNodesOrder()
             .ignoreWhitespace()
             .ignoreElementContentWhitespace()
             .areIdentical();
    // final Diff diff = DiffBuilder.compare(expected)
    //                              .withTest(actual)
    //                              .ignoreComments()
    //                              .ignoreWhitespace()
    //                              .checkForIdentical()
    //                              .build();
    // assertFalse("XML identical " + diff.toString(), diff.hasDifferences());
  }

  public static List<Element> getXpathElement(final String xml, final String xpath)
      throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.parse(new InputSource(new StringReader(xml)));
    final XPath xPath = XPathFactory.newInstance().newXPath();
    final NodeList nodes = (NodeList) xPath.compile(xpath).evaluate(doc, XPathConstants.NODESET);
    final List<Element> result = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); ++i) {
      final Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        result.add((Element) node);
      }
    }
    return result;
  }
}
