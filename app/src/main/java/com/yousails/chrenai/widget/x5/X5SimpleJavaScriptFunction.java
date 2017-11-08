package com.yousails.chrenai.widget.x5;

/**
 * Use of 初始化腾讯x5内核
 * initX5Environment 内部会创建一个线程向后台查询当前可用内核版本号
 * ，这个函数内是异步执行所以不会阻塞 App 主线程，
 * 这个函数内是轻量级执行所以对 App 启动性能没有影响，
 * 当 App 后续创建 webview 时就可以首次加载 x5 内核了
 */

public interface X5SimpleJavaScriptFunction {
}
