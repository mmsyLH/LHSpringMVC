package asia.lhweb.lhspringmvc.context;

import asia.lhweb.lhspringmvc.servlet.annotation.Controller;
import asia.lhweb.lhspringmvc.servlet.annotation.Service;
import asia.lhweb.lhspringmvc.xml.XMLParser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    // 定义属性ioc 存放反射后生成的bean对象  比如controller service  目前放入的都是单例的 多例一般是动态生成
    public ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<String, Object>();
    private String coonfigLocation;    // 表示psring 容器配置文件


    public LhWebApplicationContext() {
    }

    public LhWebApplicationContext(String contextConfigLocation) {
        this.coonfigLocation = contextConfigLocation;
    }

    // 编写方法，完成自己的spring容器的初始化
    public void init() {
        String basePackage = XMLParser.getBasePackage(coonfigLocation.split(":")[1]);// springMVC.xml
        String[] basePackages = basePackage.split(",");
        if (basePackages.length > 0) {// 传入的包要>0
            for (String aPackage : basePackages) {
                scanPackage(aPackage);
            }
        }
        System.out.println("classFullPathList:" + classFullPathList);
        // 将扫描到的类反射到Ioc容器
        executeInstance();
        System.out.println("扫描后的Ioc：" + ioc);
    }

    /**
     * 扫描包
     * 创建方法完成对包的扫描 io/容器 java基础
     * 比如asia.lhweb.controller
     *
     * @param packageStr 包str
     */
    public void scanPackage(String packageStr) {
        // 要扫描的是这个传入的包路径下的文件
        // 此时要拿到的真实路径下的   把asia.lhweb.controller换成/asia/lhweb/controller
        URL url = this.getClass().getClassLoader().getResource("/" + packageStr.replaceAll("\\.", "/"));
        // url=file:/F:/JavaWorksparce/lh-springMVC/target/lh-springMVC/WEB-INF/classes/asia/lhweb/controller/
        // System.out.println("url=" + url);
        // 根据得到的路径，对其扫描，把类的全路径保存在classFullPathList钟
        String path = url.getFile();
        File dir = new File(path);// io中  目录也视为一个文件来处理 可能是个多级目录
        // 遍历目录 dir
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {// 如果是一个目录，需要递归处理
                scanPackage(packageStr + "." + file.getName());// 当前这个包名+.再拼接一个子目录名
            } else {
                // 这时，扫描到的文件可能是.class文件也有可能是其他文件
                // 就算是.class文件 也要判断是否需要注入到容器的问题
                // 目前先把文件的全路径都保存在集合中，后面在注入对象到容器时再处理
                String classFullPath = packageStr + "." + file.getName().replaceAll(".class", "");
                classFullPathList.add(classFullPath);
            }

        }


    }

    // 将扫描到的类，在满足条件的情况下反射到Ioc容器  这个条件指的是是否有对应注解 例如Controller
    public void executeInstance() {
        if (classFullPathList.size() == 0) {// 没有扫描到这个类 不需要反射对象到ioc容器
            return;
        }
        try {
            // 遍历
            for (String classFullPath : classFullPathList) {
                Class<?> aClazz = Class.forName(classFullPath);
                // 判断是否有注解  有的话就进行反射添加到容器中
                if (aClazz.isAnnotationPresent(Controller.class)) {
                    Object instance = aClazz.newInstance();
                    // 得到类名首字母小写的beanName
                    /**
                     *
                     * aClazz.getSimpleName().substring(1) 表示从第二个开始到后面全部
                     */
                    // BeanName ：studentController
                    String beanName = aClazz.getSimpleName().substring(0, 1).toLowerCase() + aClazz.getSimpleName().substring(1);
                    ioc.put(beanName, instance);
                }// 如果有其他的注解可以扩展
                else if (aClazz.isAnnotationPresent(Service.class)) {// 处理Service注解
                    Service serviceAnnotation = aClazz.getAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();


                    if ("".equals(beanName)) {// 没有指定就使用默认的机制，即类名小写
                        // beanName = aClazz.getSimpleName().substring(0, 1).toLowerCase() + aClazz.getSimpleName().substring(1);
                        // 1 得到所有的接口名称
                        Class<?>[] interfaces = aClazz.getInterfaces();
                        Object instance = aClazz.newInstance();// 这里有点压力！！！！！！
                        // 我的理解：如果在后面New的话就有可能导致出现多个beanName对应不同的对象实例到Ioc容器中
                        // 2 遍历接口，然后通过多个接口名来注入
                        for (Class<?> anInterface : interfaces) {
                            String beanName2 = anInterface.getSimpleName().substring(0, 1).toLowerCase() + aClazz.getSimpleName().substring(1);
                            ioc.put(beanName2, instance);
                        }
                        // 3 留一个作业，使用类名的首字母小写来注入
                    } else {// 如果有指定名称，就使用这个名称对象
                        ioc.put(beanName, aClazz.newInstance());
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
