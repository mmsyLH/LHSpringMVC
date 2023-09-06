package asia.lhweb.lhspringmvc.handler;

import java.lang.reflect.Method;

/**
 * 记录请求的url和控制器方法的映射关系
 *
 * @author 罗汉
 * @date 2023/09/06
 */
public class LhHandler {
    private String url;
    private Object controller;
    private Method method;

    public LhHandler() {
    }

    public LhHandler(String url, Object controller, Method method) {
        this.url = url;
        this.controller = controller;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "LhHandler{" +
                "url='" + url + '\'' +
                ", controller=" + controller +
                ", method=" + method +
                '}';
    }
}
