package com.yousails.chrenai.widget.x5;

import android.content.Context;

import java.util.Map;

/**
 * Use of 初始化腾讯x5内核
 * initX5Environment 内部会创建一个线程向后台查询当前可用内核版本号
 * ，这个函数内是异步执行所以不会阻塞 App 主线程，
 * 这个函数内是轻量级执行所以对 App 启动性能没有影响，
 * 当 App 后续创建 webview 时就可以首次加载 x5 内核了
 */
public abstract class X5SecurityJsBridgeBundle {


    ///////////////////////////////////////////////////////////////////////////////
    //add js
    private final static String DEFAULT_JS_BRIDGE ="JsBridge";
    public static final String METHOD = "method";
    public static final String BLOCK = "block";
    public static final String CALLBACK = "callback";

    public static final String PROMPT_START_OFFSET = "local_js_bridge::";

    private Context mContext;
    private String mJsBlockName ;
    private String mMethodName;


    public abstract void onCallMethod();

    public X5SecurityJsBridgeBundle(String JsBlockName, String methodName) throws Exception {
        if(methodName == null){
            throw new Exception("methodName can not be null!");
        }

        if(JsBlockName!=null){
            this.mJsBlockName=JsBlockName;
        }else{
            this.mJsBlockName = DEFAULT_JS_BRIDGE;
        }


    }


    public String getMethodName(){
        return this.mMethodName;
    }

    public String getJsBlockName(){
        return this.mJsBlockName;
    }


    private void injectJsMsgPipecode(Map<String,Object> data){
        if(data==null){
            return ;
        }
        String injectCode = "javascript:(function JsAddJavascriptInterface_(){ "+
                "if (typeof(window.jsInterface)!='undefined') {"+
                "console.log('window.jsInterface_js_interface_name is exist!!');}   "+
                "else {"+
                data.get(BLOCK)+data.get(METHOD)+
                "window.jsBridge = {"+
                "onButtonClick:function(arg0) {"+
                "return prompt('MyApp:'+JSON.stringify({obj:'jsInterface',func:'onButtonClick',args:[arg0]}));"+
                "},"+

                "onImageClick:function(arg0,arg1,arg2) {"+
                "prompt('MyApp:'+JSON.stringify({obj:'jsInterface',func:'onImageClick',args:[arg0,arg1,arg2]}));"+
                "},"+
                "};"+
                "}"+
                "}"+
                ")()";
    }


    private static String getStandardMethodSignature(){
        return null;
    }
}
