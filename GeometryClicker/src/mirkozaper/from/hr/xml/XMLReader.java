/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.xml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mirkozaper.from.hr.model.LevelModel;
import static mirkozaper.from.hr.xml.XMLWriter.CreateLevelXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author mirko
 */
public class XMLReader {
    
    private static final String FILENAME = "shapes.xml";

    public static LevelModel GetLevelFromXML(){
        
        File f = new File(FILENAME);
        if (!f.exists())
            CreateLevelXML();
        
        LevelModel levelModel = new LevelModel();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDocument = builder.parse(new File(FILENAME));
            
            String rootNodeName=xmlDocument.getDocumentElement().getNodeName();
            
            System.out.println(rootNodeName);
            
            NodeList nodes=xmlDocument.getElementsByTagName("shape");
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Node shape = nodes.item(i);
                
                if(shape.getNodeType()==Node.ELEMENT_NODE){
                    Element shapeElement=(Element)shape;
                    String name=shapeElement.getElementsByTagName("name").item(0).getTextContent();
                    String shapeValue=shapeElement.getElementsByTagName("shape_value").item(0).getTextContent();
                    String trapValue=shapeElement.getElementsByTagName("trap_value").item(0).getTextContent();

                    switch (name) {
                      case "smallSquare":
                        levelModel.setSmallSquareShape(Integer.parseInt(shapeValue));
                        levelModel.setSmallSquareTrap(Integer.parseInt(trapValue));
                        break;
                      case "mediumSquare":
                        levelModel.setMediumSquareShape(Integer.parseInt(shapeValue));
                        levelModel.setMediumSquareTrap(Integer.parseInt(trapValue));
                        break;
                      case "bigSquare":
                        levelModel.setBigSquareShape(Integer.parseInt(shapeValue));
                        levelModel.setBigSquareTrap(Integer.parseInt(trapValue));
                        break;
                      case "smallCircle":
                        levelModel.setSmallCircleShape(Integer.parseInt(shapeValue));
                        levelModel.setSmallCircleTrap(Integer.parseInt(trapValue));
                        break;
                      case "mediumCircle":
                        levelModel.setMediumCircleShape(Integer.parseInt(shapeValue));
                        levelModel.setMediumCircleTrap(Integer.parseInt(trapValue));
                        break;
                      case "bigCircle":
                        levelModel.setBigCircleShape(Integer.parseInt(shapeValue));
                        levelModel.setBigCircleTrap(Integer.parseInt(trapValue));
                        break;
                      default:
                        System.err.println("XML contains invalid data");
                        break;
                    }
                    
                    System.out.println(name+"-"+shapeValue+"-"+trapValue);
                }
                
                
                
            }
            
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return levelModel;
    }

}
