package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{

    private final int nThreads;

    public MultiThreadedSumMatrix(int nThreads) {
        this.nThreads = nThreads;
    }

    public static class Worker extends Thread {
        private final List<Double> list;
        private final int startpos;
        private final int nelem;
        private long res;

        Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        public long getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {

        List<Double> l = new ArrayList<>();
        
        for (double[] ds : matrix) {
            for (double d : ds) {
                l.add(d);   
            }
        }

        final int size = l.size() % nThreads + l.size() / nThreads;

        final List<Worker> workers = new ArrayList<>(nThreads);
        for (int i = 0; i < l.size(); i += size) {
            workers.add(new Worker(l, i, size));
        }

        for (final Worker w: workers) {
            w.start();
        }

        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }
}