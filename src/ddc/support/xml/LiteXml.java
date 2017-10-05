package ddc.support.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author davidedc, 01/Agosto/2013
 */
public class LiteXml implements LiteXmlDocument {
	private Document doc = null;

	 public LiteXml( Document doc) {
		 this.doc=doc;
	 }


	@Override
	public Document getDocument() {
		return doc;
	}

	@Override
	public Element addRoot(String name) {
		Element e = doc.createElement(name);
		doc.appendChild(e);
		return e;
	}

	@Override
	public Element getRoot() {
		return doc.getDocumentElement();
	}

	@Override
	public Element addChild(Element element, String name) {
		Element e = doc.createElement(name);
		element.appendChild(e);
		return e;
	}

	@Override
	public Element addChild(Element element, String name, String value) {
		Element e = doc.createElement(name);
		e.setTextContent(value);
		element.appendChild(e);
		return e;
	}

	@Override
	public Element addChildCData(Element element, String name, String value) {
		Element CDataNode = addChild(element, name);
		CDATASection c = doc.createCDATASection(value);
		CDataNode.appendChild(c);
		return CDataNode;
	}

	@Override
	public CDATASection setCData(Element element, String name, String value) {
		CDATASection c = doc.createCDATASection(value);
		element.appendChild(c);
		return c;
	}

	@Override
	public Element addChild(Element element, String name, long value) {
		return addChild(element, name, String.valueOf(value));
	}

//	@Override
//	public boolean hasElementByXPath(String xpath) {
//		return getElementByXPath(xpath) != null;
//	}

//	@Override
//	public boolean removeByXPath(String xpath) {
//		Element e = getElementByXPath(xpath);
//		if (e != null && e.getParentNode() != null) {
//			e.getParentNode().removeChild(e);
//			return true;
//		}
//		return false;
//	}

	private static DateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Element addChild(Element element, String name, Date value) {
		return addChild(element, name, ISODateFormat.format(value));
	}

	@Override
	public Attr addAttribute(Element element, String name, String value) {
		Attr a = doc.createAttribute(name);
		a.setNodeValue(value);
		element.setAttributeNode(a);
		return a;
	}

//	@Override
//	public Element getElementByXPath(String xpath) {
//		XPathExpression xp;
//		try {
//			xp = compileXPath(xpath);
//			return getElement(xp);
//		} catch (LiteXmlException e) {
//			return null;
//		}
//	}

	/**
	 * Returns the first (index==0) elmenent of a NodeList of all the Elements
	 * in document order with a given tag name and are contained in the
	 * document.
	 */
	@Override
	public Element getElementByTagName(String name) {
		NodeList list = doc.getElementsByTagName(name);
		if (list.getLength() > 0) {
			Node n = list.item(0);
			if (n instanceof Element) {
				return (Element) n;
			}
		}
		return null;
	}

	/**
	 * Returns all the Elements in document with a given tag
	 */
	@Override
	public List<Element> getElementsByTagName(String name) {
		NodeList list = doc.getElementsByTagName(name);
		return LiteXmlUtil.toElements(list);
	}

//	@Override
//	public String getValueByXPath(String xpath) {
//		Element e = getElementByXPath(xpath);
//		return e == null ? null : e.getTextContent();
//	}

	@Override
	public String getValueByTagName(String name) {
		Element e = getElementByTagName(name);
		return e == null ? null : e.getTextContent();
	}

//	public String getValue(XPathExpression expr) throws LiteXmlException {
//		Element e = getElement(expr);
//		return e == null ? null : e.getTextContent();
//	}

	@Override
	public List<String> getValuesByTagName(String name) {
		NodeList list = doc.getElementsByTagName(name);
		ArrayList<String> a = new ArrayList<String>();
		if (list.getLength() > 0) {
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n instanceof Element) {
					a.add(((Element) n).getTextContent());
				}
			}
		}
		return a;
	}

	private void serialize(Document doc, OutputStream out) throws LiteXmlException {
		try {
			TransformerFactory f = TransformerFactory.newInstance();
			Transformer serializer;
			serializer = f.newTransformer();
			serializer.transform(new DOMSource(doc), new StreamResult(out));
		} catch (TransformerException e) {
			throw new LiteXmlException("serialize", e);
		}
	}

	@Override
	public String getXmlString(String charsetName) throws LiteXmlException {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			serialize(doc, bytes);
			return bytes.toString(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new LiteXmlException(e);
		}
	}

	public void write(ByteArrayOutputStream bytes) throws LiteXmlException {
		serialize(doc, bytes);
	}

	@Override
	public void write(File file) throws LiteXmlException {
		try {
			FileOutputStream f = new FileOutputStream(file);
			serialize(doc, f);
		} catch (FileNotFoundException e) {
			throw new LiteXmlException("write", e);
		}
	}

	@Override
	public void setValue(Element element, String value) {
		element.setNodeValue(value);
	}

	@Override
	public void setValue(String name, String value) {
		Element e = this.getElementByTagName(name);
		if (e != null)
			e.setTextContent(value);
	}

//	@Override
//	public List<Element> filterElement(NodeList nodes) {
//		ArrayList<Element> a = new ArrayList<Element>();
//		for (int i = 0; i < nodes.getLength(); i++) {
//			Node n = nodes.item(i);
//			if (n instanceof Element) {
//				a.add((Element) n);
//			}
//		}
//		return a;
//	}
//
//	private List<Element> toListElement(Node node) {
//		ArrayList<Element> a = new ArrayList<Element>();
//		if (node instanceof Element) {
//			a.add((Element) node);
//		}
//		return a;
//	}

//	@Override
//	public void setValueByXPath(String xpath, String value) {
//		Element e = getElementByXPath(xpath);
//		if (e != null) {
//			e.setTextContent(value);
//		}
//	}
//
//	@Override
//	public void setValue(XPathExpression expr, String value) throws LiteXmlException {
//		Element e;
//		e = getElement(expr);
//		if (e != null)
//			setValue(e, value);
//	}
//
//	@Override
//	public XPathExpression compileXPath(String xpath) throws LiteXmlException {
//		try {
//			XPathFactory factory = XPathFactory.newInstance();
//			XPath xp = factory.newXPath();
//			return xp.compile(xpath);
//		} catch (XPathExpressionException e) {
//			throw new LiteXmlException("select", e);
//		}
//	}
//
//	@Override
//	public List<Element> getElementsByXPath(String xpath) throws LiteXmlException {
//		return getElements(compileXPath(xpath));
//	}
//
//
//	@Override
//	public List<Element> getElements(XPathExpression expr) {
//		try {
//			Object result = expr.evaluate(doc, XPathConstants.NODESET);
//			if (result instanceof NodeList) {
//				NodeList nodes = (NodeList) result;
//				return filterElement(nodes);
//			} else if (result instanceof Node) {
//				Node node = (Node) result;
//				return LiteXmlUtil.toElements(node);
//			}
//		} catch (XPathExpressionException e) {
//		}
//		return new ArrayList<Element>();
//	}
//
//	@Override
//	public List<Node> getNodesByXPath(String xpath) throws LiteXmlException {
//		return getNodes(compileXPath(xpath));
//	}
//
//	@Override
//	public List<Node> getNodes(XPathExpression expr) throws LiteXmlException {
//		try {
//			ArrayList<Node> a = new ArrayList<>();
//			Object result = expr.evaluate(doc, XPathConstants.NODESET);
//			if (result instanceof NodeList) {
//				NodeList nodes = (NodeList) result;
//				for (int i = 0; i < nodes.getLength(); i++) {
//					a.add(nodes.item(i));
//				}
//			} else if (result instanceof Node) {
//				a.add((Node) result);
//			}
//			return a;
//		} catch (XPathExpressionException e) {
//			throw new LiteXmlException("getNodes()", e);
//		}
//		
//	}
//
//	@Override
//	public Element getElement(XPathExpression expr) throws LiteXmlException {
//		try {
//			Object result = expr.evaluate(doc, XPathConstants.NODESET);
//			if (result instanceof NodeList) {
//				NodeList nodes = (NodeList) result;
//				List<Element> list = filterElement(nodes);
//				if (list.size() > 0)
//					return list.get(0);
//			}
//		} catch (XPathExpressionException e) {
//			throw new LiteXmlException(e);
//		}
//		return null;
//	}

	@Override
	public Map<String, String> getAttributes(Element elem) {
		Map<String, String> map = new TreeMap<>();
		NamedNodeMap nodeMap = elem.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);
			map.put(node.getNodeName(), node.getNodeValue());
		}
		return map;
	}

	@Override
	public String getAttribute(Element elem, String attrName) {
		NamedNodeMap nodeMap = elem.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);
			if (node.getNodeName().equals(attrName))
				return node.getNodeValue();
		}
		return null;
	}

}
