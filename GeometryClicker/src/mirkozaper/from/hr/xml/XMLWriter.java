/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.xml;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *
 * @author mirko
 */
public class XMLWriter {
    
    private static final String FILENAME = "shapes.xml";

    public static void CreateLevelXML(){
        
        try {
            Document document = createDocument("shapes");
            
            
            Element shape = document.createElement("shape");
            document.getDocumentElement().appendChild(shape);
            shape.appendChild(createElement(document, "name", "smallSquare"));
            shape.appendChild(createElement(document, "shape_value", "1"));
            shape.appendChild(createElement(document, "trap_value", "0"));
            
            shape = document.createElement("shape");
            document.getDocumentElement().appendChild(shape);
            shape.appendChild(createElement(document, "name", "mediumSquare"));
            shape.appendChild(createElement(document, "shape_value", "1"));
            shape.appendChild(createElement(document, "trap_value", "2"));
            
            shape = document.createElement("shape");
            document.getDocumentElement().appendChild(shape);
            shape.appendChild(createElement(document, "name", "bigSquare"));
            shape.appendChild(createElement(document, "shape_value", "1"));
            shape.appendChild(createElement(document, "trap_value", "0"));
            
            shape = document.createElement("shape");
            document.getDocumentElement().appendChild(shape);
            shape.appendChild(createElement(document, "name", "smallCircle"));
            shape.appendChild(createElement(document, "shape_value", "1"));
            shape.appendChild(createElement(document, "trap_value", "1"));
            
            shape = document.createElement("shape");
            document.getDocumentElement().appendChild(shape);
            shape.appendChild(createElement(document, "name", "mediumCircle"));
            shape.appendChild(createElement(document, "shape_value", "1"));
            shape.appendChild(createElement(document, "trap_value", "0"));
            
            shape = document.createElement("shape");
            document.getDocumentElement().appendChild(shape);
            shape.appendChild(createElement(document, "name", "bigCircle"));
            shape.appendChild(createElement(document, "shape_value", "1"));
            shape.appendChild(createElement(document, "trap_value", "0"));
            
            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(XMLWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
    }
    
    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        DocumentType documentType = domImplementation.createDocumentType("DOCTYPE", null, "shapes.dtd");
        return domImplementation.createDocument(null, element, documentType);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
        //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        //transformer.transform(new DOMSource(document), new StreamResult(System.out));
        transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));
    }
}
