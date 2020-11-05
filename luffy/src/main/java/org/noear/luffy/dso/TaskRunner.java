package org.noear.luffy.dso;

import org.noear.luffy.task.IJtTask;
import org.noear.luffy.task.IJtTaskRunner;

import java.util.Date;

/**
 * 任务运行工具
 * */
public class TaskRunner implements IJtTaskRunner {
    public static final TaskRunner g = new TaskRunner();

    private TaskRunner(){

    }

    public  void run(IJtTask task) {
        System.out.print("run::" + task.getName() + "\r\n");

        new Thread(() -> {
            doRun(task);
        }).start();
    }

    private  void doRun(IJtTask task){
        while (true) {
            try {
                Date time_start = new Date();
//                System.out.print(task.getName() + "::time_start::" + time_start.toString() + "\r\n");

                task.exec();

                Date time_end = new Date();
//                System.out.print(task.getName() + "::time_end::" + time_end.toString() + "\r\n");

                if(task.getInterval() == 0){
                    return;
                }

                if (time_end.getTime() - time_start.getTime() < task.getInterval()) {
                    Thread.sleep(task.getInterval());//0.5s
                }

            } catch (Exception ex) {
                ex.printStackTrace();

                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
}
