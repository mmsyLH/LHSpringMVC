package asia.lhweb.lhspringmvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * xml解析器
 * 用于解析spring配置文件
 *
 * @author 罗汉
 * @date 2023/09/05
 */
public class XMLParser {
    public static String getBasePackage(String xmlPaser) {
        // System.out.println("xmlPaser"+xmlPaser);
        SAXReader saxReader = new SAXReader();
        // 真正读的时候lhspringMVC.xml并不在resouces目录下
        /**
         * 默认资源文件是在resources下的
         * 只有运行后回到编译的class文件里
         * 所以需要先获取运行时候的真实目录
         */
        // 它是运行的时候再target目录下
        // 得到类的加载路径-》得到spring配置文件
        InputStream resourceAsStream
                = XMLParser.class.getClassLoader().getResourceAsStream(xmlPaser);
        try {
            //得到文档
            Document readDocument = saxReader.read(resourceAsStream);
            Element rootElement = readDocument.getRootElement();//<beans>
            Element componentScanElement =
                    rootElement.element("component-scan");
            // <component-scan base-package="asia.lhweb.controller"></component-scan>
            Attribute attribute = componentScanElement.attribute("base-package");//得到这个属性
            String basePackage = attribute.getText();
            return basePackage;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }

}
