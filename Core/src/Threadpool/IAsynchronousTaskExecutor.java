package Threadpool;

public interface IAsynchronousTaskExecutor
{
    void executeTask(Runnable task);
}
