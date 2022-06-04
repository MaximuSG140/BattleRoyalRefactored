package Threadpool;

import java.util.*;

public class DynamicThreadPool implements IAsynchronousTaskExecutor
{
    private final Set<DynamicPoolWorker> threads = new HashSet<>();
    private final Queue<Task> taskQueue = new LinkedList<>();

    private Integer tasksExecuting = 0;
    private final int maxThreadCount;

    public DynamicThreadPool(int maxThreads)
    {
        maxThreadCount = maxThreads;
    }

    @Override
    public void executeTask(Runnable task)
    {
        executeTask(task, "NoName");
    }

    public void executeTask(Runnable task, String name)
    {
        synchronized (taskQueue)
        {
            taskQueue.add(new Task(task, name));
            synchronized (tasksExecuting)
            {
                synchronized (threads)
                {
                    if (tasksExecuting.compareTo(threads.size()) == 0 && threads.size() < maxThreadCount)
                    {
                        addNewThread();
                    }
                    else
                    {
                        taskQueue.notify();
                    }
                }
            }
        }
    }

    private void addNewThread()
    {
        var worker = new DynamicPoolWorker();
        threads.add(worker);
        worker.start();
    }

    private class DynamicPoolWorker extends Thread
    {
        public void run()
        {
            while(true)
            {
                Task task;
                synchronized (taskQueue)
                {
                    while(taskQueue.isEmpty())
                    {
                        try
                        {
                            taskQueue.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    synchronized (tasksExecuting)
                    {
                        tasksExecuting++;
                        task = taskQueue.poll();
                    }
                }
                try
                {
                    System.out.println(task.name + " started executing");
                    task.run();
                    System.out.println(task.name + " executed successfully");
                }
                catch (RuntimeException e)
                {
                    System.out.println(task.name + " execution stopped cause of runtime error: " + e.getClass().toString());
                }
                synchronized (tasksExecuting)
                {
                    tasksExecuting--;
                }
            }
        }
    }

    private class Task
    {
        private Runnable routine;
        private String name;

        public Task(Runnable r, String n)
        {
            routine = r;
            name = n;
        }

        public void run()
        {
            routine.run();
        }

        public String getName()
        {
            return name;
        }
    }
}


