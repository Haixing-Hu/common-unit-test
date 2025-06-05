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
 * 提供 XML 单元测试相关的工具函数。
 *
 * @author 胡海星
 */
public class XmlUnitUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlUnitUtils.class);

  /**
   * 断言XML字符串中指定XPath表达式计算结果与期望值（转换为字符串）相等。
   *
   * @param xml
   *     待检查的XML字符串。
   * @param xpath
   *     XPath表达式。
   * @param value
   *     期望的值。如果为 {@code null}，则断言XPath表达式在XML中没有匹配项。
   */
  public static void assertXPathEquals(final String xml, final String xpath,
          @Nullable final Object value) {
    if (value == null) {
      assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
    } else {
      assertThat(xml, EvaluateXPathMatcher.hasXPath(xpath, equalTo(value.toString())));
    }
  }

  /**
   * 断言XML字符串中指定XPath表达式计算结果与期望的枚举常量名称相等。
   *
   * @param xml
   *     待检查的XML字符串。
   * @param xpath
   *     XPath表达式。
   * @param value
   *     期望的枚举常量。如果为 {@code null}，则断言XPath表达式在XML中没有匹配项。
   */
  public static void assertXPathEquals(final String xml, final String xpath,
      @Nullable final Enum<?> value) {
    if (value == null) {
      assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
    } else {
      assertThat(xml, EvaluateXPathMatcher.hasXPath(xpath, equalTo(value.name())));
    }
  }

  /**
   * 断言XML字符串中指定XPath表达式在XML中没有匹配项（通常用于检查节点是否为null或不存在）。
   *
   * @param xml
   *     待检查的XML字符串。
   * @param xpath
   *     XPath表达式。
   */
  public static void assertXPathNull(final String xml, final String xpath) {
    assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
  }

  /**
   * 断言XML字符串中指定XPath表达式在XML中没有匹配项（用于检查节点是否缺失）。
   *
   * @param xml
   *     待检查的XML字符串。
   * @param xpath
   *     XPath表达式。
   */
  public static void assertXPathAbsent(final String xml, final String xpath) {
    assertThat(xml, not(HasXPathMatcher.hasXPath(xpath)));
  }

  /**
   * 断言XML字符串中指定路径下的数组内容与期望的数组相等。
   *
   * @param <T>
   *     数组元素的类型。
   * @param xml
   *     待检查的XML字符串。
   * @param rootPath
   *     数组的根路径。
   * @param wrapperNode
   *     包裹数组元素的包装节点名称。
   * @param elementNode
   *     数组元素的节点名称。
   * @param array
   *     期望的数组内容。如果为 {@code null}，则断言包装节点不存在。
   */
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

  /**
   * 断言两个XML字符串在语义上是相同的，忽略注释、子节点顺序、空白和元素内容空白。
   *
   * @param obj
   *     与XML对应的原始Java对象，主要用于日志记录。
   * @param expected
   *     期望的XML字符串。
   * @param actual
   *     实际生成的XML字符串。
   * @throws Exception
   *     如果断言失败或比较过程中发生错误。
   */
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

  /**
   * 根据XPath表达式从XML字符串中获取匹配的元素列表。
   *
   * @param xml
   *     待解析的XML字符串。
   * @param xpath
   *     XPath表达式。
   * @return 匹配到的 {@link Element} 列表。如果不匹配则返回空列表。
   * @throws ParserConfigurationException
   *     如果配置解析器时发生错误。
   * @throws IOException
   *     如果读取XML字符串时发生IO错误。
   * @throws SAXException
   *     如果解析XML时发生SAX错误。
   * @throws XPathExpressionException
   *     如果XPath表达式编译或求值时发生错误。
   */
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
