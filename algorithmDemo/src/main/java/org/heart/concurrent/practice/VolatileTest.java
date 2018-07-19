package org.heart.concurrent.practice;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class VolatileTest {
    public volatile int inc = 0;

    public AtomicInteger inc1 = new AtomicInteger();

    private final  Lock lock = new ReentrantLock(false);

    /**
     * 自增操作不是原子性操作，而且volatile也无法保证对变量的任何操作都是原子性的,
     * 需要使用synchronized 使方法同步
     * 或者 采用Lock同步
     * 或者 采用AtomicInteger：
     */
   /* public void increase() {
        inc++;
    }*/

    /*public synchronized void increase() {
        inc++;
    }*/

    public  void increase() {
        inc++;
        inc1.getAndAdd(2);
    }
    /*public  void increase(){
        lock.lock();

        try {
            inc++;
        }finally {
            lock.unlock();
        }
    }*/


    public static void main(String[] args) {

        // 获取java线程的管理MXBean
        ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
        // 不需要获取同步的Monitor和synchronizer信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = tmxb.dumpAllThreads(false, false);
        // 遍历线程信息，打印出ID和名称
        for (ThreadInfo info : threadInfos) {
            System.out.println("[" + info.getThreadId() + "] " + info.getThreadName());
        }


        System.out.println(Thread.activeCount());


        final VolatileTest test = new VolatileTest();
        for(int i=0;i<100;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
//                    System.out.println(test.inc);
                };
            }.start();
        }
//        System.out.println(Thread.activeCount());
        while(Thread.activeCount()>2){
            //保证前面的线程都执行完
            Thread.yield();
        }
        System.out.println(test.inc);
        System.out.println(test.inc1);
    }
}
