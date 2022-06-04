package Threadpool;

import java.util.LinkedList;
import java.util.Queue;

public class StaticThreadPool implements IAsynchronousTaskExecutor
{
    private final int threadsNumber;
    private final PoolWorker[] threads;
    private final Queue<Runnable> queue = new LinkedList<>();

    public StaticThreadPool(int nThreads)
    {
        this.threadsNumber = nThreads;
        threads = new PoolWorker[nThreads];

        for (int i = 0; i < nThreads; i++)
        {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }
    @Override
    public void executeTask(Runnable task)
    {
        synchronized (queue)
        {
            queue.add(task);
            queue.notify();
        }
    }

    private class PoolWorker extends Thread
    {
        public void run()
        {
            Runnable task;

            while (true)
            {
                synchronized (queue)
                {
                    while (queue.isEmpty())
                    {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException e)
                        {
                            System.out.println("An error occurred while queue is waiting: " + e.getMessage());
                        }
                    }
                    task = queue.poll();
                }
                try
                {
                    task.run();
                }
                catch (RuntimeException e)
                {
                    System.out.println("Thread interrupted due to an issue: " + e.getMessage());
                }
            }
        }
    }
}
