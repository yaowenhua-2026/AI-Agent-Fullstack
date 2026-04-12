package org.example.week3_threadpool;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderFlowMyTest {

    static ExecutorService executor = Executors.newFixedThreadPool(3);

    // 1. 扣库存（你来写）
    public static CompletableFuture<Boolean> deductStock() {
        // TODO

        //返回一个异步的结果
        return CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread().getName()+"正在扣库存中...");
            //当前线程休息模拟业务
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("库存已扣除");
            return true;
        });
    }

    // 2. 创建订单（你来写）
    public static CompletableFuture<String> createOrder(boolean stockOk) {
        // TODO
        return CompletableFuture.supplyAsync(()->{
            //模仿处理业务
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //回复线程 记录 可能在这里出现意外
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            if (stockOk) {
                //创建订单编号
                String orderId = "ORDER_"+System.currentTimeMillis();
                //将订单返回去
                return orderId;
            }else  {
                throw new RuntimeException("库存不够,订单创建失败");
            }

        },executor);
    }

    // 3. 发送通知（你来写）
    public static CompletableFuture<Void> sendNotification(String orderId) {
        // TODO

        return CompletableFuture.runAsync(()->{
            System.out.println(Thread.currentThread().getName()+"发送通知中");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //恢复线程
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            System.out.println("发送通知成功,订单号:"+orderId);
        },executor);
    }
    public static CompletableFuture<Void> logOrder(String orderId) {
        return CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " 记录日志中...");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            System.out.println("日志记录成功，订单号: " + orderId);
        }, executor);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // 5. 编排流程（你来写）
        // deductStock()
        //     .thenCompose(...)
        //     ...
        //开始流程 先扣库存
        deductStock() //然后将结果传递给下一个 创建订单
                .thenCompose(stock->createOrder(stock))
                .thenCompose(orderId->sendNotification(orderId)
                .thenApply(V -> orderId))
        .whenComplete((orderId,ex)->{
           long end = System.currentTimeMillis();
           if(ex!=null){
               System.err.println("订单流程失败: " + ex.getMessage());
           }else {
               System.out.println("订单流程完成，订单号: " + orderId + "，耗时: " + (end-start) + "ms");
           }

        });

        // 等待完成
        try { Thread.sleep(4000); } catch (InterruptedException e) {}
        executor.shutdown();
    }
}