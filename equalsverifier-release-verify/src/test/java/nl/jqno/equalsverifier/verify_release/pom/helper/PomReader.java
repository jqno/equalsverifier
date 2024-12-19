package nl.jqno.equalsverifier.verify_release.pom.helper;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PomReader {

    private final String filename;
    private final Document doc;
    private final XPath xpath;

    public PomReader(String filename) throws Exception {
        this.filename = filename;

        var stream = getClass().getClassLoader().getResourceAsStream(filename);
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();

        this.doc = builder.parse(stream);
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    public void assertNode(String expression, String value) {
        var actual = (String) getValue(expression, XPathConstants.STRING);
        assertThat(actual)
                .as(
                    "Expected: " + value + ", but was: " + actual + "\nFilename: " + filename + "; expression: ["
                            + expression + "]")
                .isEqualTo(value);
    }

    public void assertNodeSize(int expectedLength, String expression) {
        var nodes = (NodeList) getValue(expression, XPathConstants.NODESET);
        assertThat(nodes.getLength())
                .as(
                    "Expected node to have " + expectedLength + " nodes, but it has " + nodes.getLength()
                            + "!\nFilename: " + filename + "; expression: [" + expression + "]")
                .isEqualTo(expectedLength);
    }

    private Object getValue(String expression, QName qname) {
        try {
            var expr = xpath.compile(expression);
            return expr.evaluate(doc, qname);
        }
        catch (XPathException e) {
            throw new AssertionError("Failed to parse " + filename + " with expression [" + expression + "]", e);
        }
    }
}
