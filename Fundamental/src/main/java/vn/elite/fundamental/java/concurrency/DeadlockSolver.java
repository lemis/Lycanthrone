package vn.elite.fundamental.java.concurrency;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockSolver extends ReentrantLock {
    private static List<DeadlockSolver> deadlockLocksRegistry = new ArrayList<>();

    private static Lock a = new DeadlockSolver(false, true);
    private static Lock b = new DeadlockSolver(false, true);
    private static Lock c = new DeadlockSolver(false, true);

    private static Condition wa = a.newCondition();
    private static Condition wb = b.newCondition();
    private static Condition wc = c.newCondition();

    private List<Thread> hardwaitingThreads = new ArrayList<>();
    private boolean debugging;

    public DeadlockSolver() {
        this(false, false);
    }

    public DeadlockSolver(boolean fair) {
        this(fair, false);
    }

    public DeadlockSolver(boolean fair, boolean debug) {
        super(fair);
        debugging = debug;
        registerLock(this);
    }

    private static synchronized void registerLock(DeadlockSolver ddl) {
        if (!deadlockLocksRegistry.contains(ddl)) {
            deadlockLocksRegistry.add(ddl);
        }
    }

    private static synchronized void unregisterLock(DeadlockSolver ddl) {
        deadlockLocksRegistry.remove(ddl);
    }

    private static synchronized void markAsHardwait(List<Thread> l, Thread t) {
        if (!l.contains(t)) {
            l.add(t);
        }
    }

    private static synchronized void freeIfHardwait(List<Thread> l, Thread t) {
        l.remove(t);
    }

    private static Iterator<DeadlockSolver> getAllLocksOwned(Thread t) {
        ArrayList<DeadlockSolver> results = new ArrayList<>();

        for (DeadlockSolver current : deadlockLocksRegistry) {
            if (current.getOwner() == t) {
                results.add(current);
            }
        }
        return results.iterator();
    }

    private static Iterator<Thread> getAllThreadsHardwaiting(DeadlockSolver solver) {
        return solver.hardwaitingThreads.iterator();
    }

    private static synchronized boolean canThreadWaitOnLock(Thread t, DeadlockSolver l) {
        Iterator<DeadlockSolver> locksOwned = getAllLocksOwned(t);
        while (locksOwned.hasNext()) {
            DeadlockSolver current = locksOwned.next();
            if (current == l) {
                return false;
            }
            Iterator<Thread> waitingThreads = getAllThreadsHardwaiting(current);

            while (waitingThreads.hasNext()) {
                Thread otherThread = waitingThreads.next();
                if (!canThreadWaitOnLock(otherThread, l)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void delaySeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static void awaitSeconds(Condition c, int seconds) {
        try {
            c.await(seconds, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static void testOne() {
        new Thread(() -> {
            System.out.println("thread one grab a");
            a.lock();
            delaySeconds(2);
            System.out.println("thread one grab b");
            b.lock();
            delaySeconds(2);
            a.unlock();
            b.unlock();
        }).start();
        new Thread(() -> {
            System.out.println("thread two grab b");
            b.lock();
            delaySeconds(2);
            System.out.println("thread two grab a");
            a.lock();
            delaySeconds(2);
            a.unlock();
            b.unlock();
        }).start();
    }

    private static void testTwo() {
        new Thread(() -> {
            System.out.println("thread one grab a");
            a.lock();
            delaySeconds(2);
            System.out.println("thread one grab b");
            b.lock();
            delaySeconds(10);
            a.unlock();
            b.unlock();
        }).start();
        new Thread(() -> {
            System.out.println("thread two grab b");
            b.lock();
            delaySeconds(2);
            System.out.println("thread two grab c");
            c.lock();
            delaySeconds(10);
            b.unlock();
            c.unlock();
        }).start();

        new Thread(() -> {
            System.out.println("thread three grab c");
            c.lock();
            delaySeconds(4);
            System.out.println("thread three grab a");
            a.lock();
            delaySeconds(10);
            c.unlock();
            a.unlock();
        }).start();
    }

    private static void testThree() {
        new Thread(() -> {
            System.out.println("thread one grab b");
            b.lock();
            System.out.println("thread one grab a");
            a.lock();
            delaySeconds(2);
            System.out.println("thread one waits on b");
            awaitSeconds(wb, 10);
            a.unlock();
            b.unlock();
        }).start();
        new Thread(() -> {
            delaySeconds(1);
            System.out.println("thread two grab b");
            b.lock();
            System.out.println("thread two grab a");
            a.lock();
            delaySeconds(10);
            b.unlock();
            c.unlock();
        }).start();
    }

    public void lock() {
        if (isHeldByCurrentThread()) {
            if (debugging) {
                System.out.println("Already Own Lock");
            }
            super.lock();
            freeIfHardwait(
                hardwaitingThreads,
                Thread.currentThread());
            return;
        }
        markAsHardwait(hardwaitingThreads, Thread.currentThread());
        if (canThreadWaitOnLock(Thread.currentThread(), this)) {
            if (debugging) {
                System.out.println("Waiting For Lock");
            }
            super.lock();
            freeIfHardwait(hardwaitingThreads, Thread.currentThread());

            if (debugging) {
                System.out.println("Got New Lock");
            }
        } else {
            throw new DeadlockDetectedException("DEADLOCK DETECTED");
        }
    }

    public void lockInterruptibly() {
        lock();
    }

    public Condition newCondition() {
        return new DeadlockDetectingCondition(this, super.newCondition());
    }

    public class DeadlockDetectingCondition implements Condition {

        Condition embedded;

        protected DeadlockDetectingCondition(ReentrantLock lock, Condition embedded) {
            this.embedded = embedded;
        }

        public void await() throws InterruptedException {
            try {
                markAsHardwait(hardwaitingThreads, Thread.currentThread());
                embedded.await();
            } finally {
                freeIfHardwait(hardwaitingThreads, Thread.currentThread());
            }
        }

        public void awaitUninterruptibly() {
            markAsHardwait(hardwaitingThreads, Thread.currentThread());
            embedded.awaitUninterruptibly();
            freeIfHardwait(hardwaitingThreads, Thread.currentThread());
        }

        public long awaitNanos(long nanosTimeout) throws InterruptedException {
            try {
                markAsHardwait(hardwaitingThreads, Thread.currentThread());
                return embedded.awaitNanos(nanosTimeout);
            } finally {
                freeIfHardwait(hardwaitingThreads, Thread.currentThread());
            }
        }

        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            try {
                markAsHardwait(hardwaitingThreads, Thread.currentThread());
                return embedded.await(time, unit);
            } finally {
                freeIfHardwait(hardwaitingThreads, Thread.currentThread());
            }
        }

        public boolean awaitUntil(Date deadline) throws InterruptedException {
            try {
                markAsHardwait(hardwaitingThreads, Thread.currentThread());
                return embedded.awaitUntil(deadline);
            } finally {
                freeIfHardwait(hardwaitingThreads, Thread.currentThread());
            }
        }

        public void signal() {
            embedded.signal();
        }

        public void signalAll() {
            embedded.signalAll();
        }
    }

    static class DeadlockDetectedException extends RuntimeException {
        public DeadlockDetectedException(String s) {
            super(s);
        }
    }

    public static void main(String[] args) {
        int test = 1;
        if (args.length > 0) {
            test = Integer.parseInt(args[0]);
        }
        switch (test) {
            case 1:
                testOne();
                break;
            case 2:
                testTwo();
                break;
            case 3:
                testThree();
                break;
            default:
                System.err.println("usage: java DeadlockDetectingLock [ test# ]");
        }
        delaySeconds(60);
        System.out.println("--- End Program ---");
        System.exit(0);
    }
}
