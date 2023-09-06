package asia.lhweb.lhspringmvc.context;

import asia.lhweb.lhspringmvc.xml.XMLParser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * web应用程序上下文
 * 自己的spring容器
 *
 * @author 罗汉
 * @date 2023/09/05
 */
public class LhWebApplicationContext {
    // 定义属性 保存要扫描的包的全路径
    private List<String> classFullPathList = new ArrayList<String>();

    /**
     * 扫描包
     * 创建方法完成对包的扫描 io/容器 java基础
     * 比如asia.lhweb.controller
     * @param packageStr 包str
     */
    public void scanPackage(String packageStr) {
        //要扫描的是这个传入的包路径下的文件
        //此时要拿到的真实路径下的   把asia.lhweb.controller换成/asia/lhweb/controller
        URL url =
                this.getClass().getClassLoader()
                        .getResource("/" + packageStr.replaceAll("\\.", "/"));
        //url=file:/F:/JavaWorksparce/lh-springMVC/target/lh-springMVC/WEB-INF/classes/asia/lhweb/controller/
        // System.out.println("url=" + url);
        //根据得到的路径，对其扫描，把类的全路径保存在classFullPathList钟
        String path = url.getFile();
        File dir = new File(path);//io中  目录也视为一个文件来处理 可能是个多级目录
        //遍历目录 dir
        for (File file : dir.listFiles()) {
            if (file.isDirectory()){//如果是一个目录，需要递归处理
                scanPackage(packageStr+"."+file.getName());//当前这个包名+.再拼接一个子目录名
            }else {
                //这时，扫描到的文件可能是.class文件也有可能是其他文件
                //就算是.class文件 也要判断是否需要注入到容器的问题
                //目前先把文件的全路径都保存在集合中，后面在注入对象到容器时再处理
                String classFullPath =
                        packageStr + "." + file.getName().replaceAll(".class", "");
                classFullPathList.add(classFullPath);
            }

        }



    }
    //编写方法，完成自己的spring容器的初始化
    public void init(){
        String basePackage = XMLParser.getBasePackage("lhspringMVC.xml");
        String[] basePackages = basePackage.split(",");
        if (basePackages.length>0){//传入的包要>0
            for (String aPackage : basePackages) {
                scanPackage(aPackage);
            }
        }
        System.out.println("classFullPathList:"+classFullPathList);
    }

}
