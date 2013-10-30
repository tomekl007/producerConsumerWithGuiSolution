package exercise1;

import javax.swing.*;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: tomaszlelek
 * Date: 10/28/13
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProducerConsumer extends SwingWorker<Void, Void>{
    final AtomicInteger progress ;
    Random normalDistribution = new Random();
    final int standardDeviation;
    final int meanValue;
    final int bound;
    final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();
    Thread[] consumers = new Thread[N_CONSUMERS];
    Thread producer;
    public final int valueToIncrementDecrement;

    public ProducerConsumer(int standardDeviation, int meanValue, int bound) {
        this.standardDeviation = standardDeviation;
        this.meanValue = meanValue;
        this.bound = bound;
        valueToIncrementDecrement = 100/bound;
        progress = new AtomicInteger(0);
    }

    public void startProcess() {
        BlockingQueue<Date> queue = new LinkedBlockingQueue<Date>(bound);



        //start consumers
        for (int i = 0; i < N_CONSUMERS; i++){
            System.out.println("new consumer");
            Thread consumer = new Thread(new Consumer(queue));
            consumers[i] = consumer;
            consumer.start();
        }
        //start producer
        System.out.println("new producer");
        producer = new Thread(new Producer(queue));
        producer.start();
    }

    @Override
    protected Void doInBackground() throws Exception {
        setProgress(progress.get());
        startProcess();
        //Initialize progress property.

        while (!isCancelled()) {
                setProgress(progress.get());
        }
        return null;
    }

    class Producer implements Runnable{
        private final BlockingQueue<Date> queue;
        Producer(BlockingQueue<Date> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                while(!isCancelled()){
                Date dateToAdd = new Date();
                System.out.println("producer -> "+ Thread.currentThread().getName()  + " try to add date to queue : " + dateToAdd.getTime());

                    progress.getAndIncrement();
                    queue.offer(dateToAdd, getTimeToWait(), TimeUnit.SECONDS);


                }
                return;
            } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
            }



        }
    }




    /**
     * to change the maen (average) of the distribution, we add the required value;
     * to change the standard deviation, we multiply the value.
     */
    private long getTimeToWait() {

        return (long)Math.abs((normalDistribution.nextGaussian() * standardDeviation + meanValue));
    }

    class Consumer implements Runnable{
        private final BlockingQueue<Date> queue;
        Consumer(BlockingQueue<Date> queue) {
            this.queue = queue;
        }

        public void run() {
            try {

                while (!isCancelled()){

                    Date current = queue.poll(getTimeToWait(), TimeUnit.SECONDS); //wait getTimeToWait() seconds before giving up
                    if(current == null){
                        throw new TimeoutException();
                    }
                    System.out.println("consumer -> "+ Thread.currentThread().getName() + ", time in millisenconds taken : " + current.getTime());
                    progress.decrementAndGet();

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (TimeoutException e) {
                System.out.println("timeout for consumer end, probably producer thread die, try to start again producer");
                return;
            }
        }



    }
}

