package dev.lightdream.lambda;

import dev.lightdream.lambda.lambda.LambdaExecutor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class ScheduleUtils {

    private static ScheduledExecutorService scheduledExecutor;
    private static ExecutorService threadExecutor;

    public static void init(Settings settings) {
        ScheduleUtils.scheduledExecutor = Executors.newScheduledThreadPool(settings.schedulePoolSize());
        ScheduleUtils.threadExecutor = Executors.newFixedThreadPool(settings.threadPoolSize());
    }

    public static void runTaskLater(LambdaExecutor task, long delay) {
        scheduledExecutor.schedule(new CancelableTimeTask() {
            @Override
            public void execute() {
                task.execute();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public static CancelableTimeTask runTaskLaterAsync(LambdaExecutor executor, long delay) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                runTaskAsync(executor);
            }
        };

        runTaskLaterAsync(task, delay);

        return task;
    }

    public static void runTaskLaterAsync(CancelableTimeTask task, long delay) {
        scheduledExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public static CancelableTimeTask runTaskTimer(LambdaExecutor executor, long timer) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                executor.execute();
            }
        };

        scheduledExecutor.scheduleAtFixedRate(task, 0, timer, TimeUnit.MILLISECONDS);

        return task;
    }

    public static void runTaskTimer(CancelableTimeTask task, long timer) {
        scheduledExecutor.scheduleAtFixedRate(task, 0, timer, TimeUnit.MILLISECONDS);
    }

    public static CancelableTimeTask runTaskTimerAsync(LambdaExecutor executor, long timer) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                runTaskAsync(executor);
            }
        };

        runTaskTimerAsync(task, timer);

        return task;
    }

    public static void runTaskTimerAsync(CancelableTimeTask task, long timer) {
        scheduledExecutor.scheduleAtFixedRate(task, 0, timer, TimeUnit.MILLISECONDS);
    }

    public static void runTaskAsync(LambdaExecutor task) {
        threadExecutor.execute(task::execute);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor
    public static class Settings {

        private int schedulePoolSize = 1;
        private int threadPoolSize = 1;

        public void build() {
            ScheduleUtils.init(this);
        }
    }
}
