package exercise1;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




/**
 *
 * @author Tomasz Lelek
 */
public class test {
    
    public static void main(String[] args) {
        int standardDeviation = 1;
        int meanValue = 10;
        int bound = 10;
        ProducerConsumer producerConsumer = new ProducerConsumer(standardDeviation, meanValue,bound);
        producerConsumer.startProcess();
      
      }
    
}