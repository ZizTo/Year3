import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tests {
    private static class Node<T> {
        T item;
        int key;
        Node next;
        Lock lock = new ReentrantLock();

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }

        Node(int key) {
            this.key = key;
        }

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }

    static class SequentialList {
        private Node head = new Node(Integer.MIN_VALUE);
        private Node tail = new Node(Integer.MAX_VALUE);

        public SequentialList() {
            head.next = tail;
        }

        public boolean contains(int key) {
            Node curr = head.next;
            while (curr.key < key)
                curr = curr.next;
            return curr.key == key;
        }

        public boolean add(int key) {
            Node pred = head;
            Node curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            if (curr.key == key) {
                return false;
            }

            Node newNode = new Node(key);
            newNode.next = curr;
            pred.next = newNode;
            return true;
        }

        public boolean remove(int key) {
            Node pred = head;
            Node curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            if (curr.key != key) {
                return false;
            }

            pred.next = curr.next;
            return true;
        }

    }

    static class FineList<T> {
        private final Node<T> head = new Node<T>(Integer.MIN_VALUE);
        private final Node<T> tail = new Node<T>(Integer.MAX_VALUE);

        public FineList() {
            head.next = tail;
        }

        public boolean add(T item) {
            int key = item.hashCode();
            head.lock();
            Node<T> pred = head;
            try {
                Node<T> curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key == key)
                        return false;
                    Node<T> newNode = new Node<T>(item);
                    newNode.next = curr;
                    pred.next = newNode;
                    return true;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }

        public boolean remove(T item) {
            int key = item.hashCode();
            head.lock();
            Node<T> pred = head;
            try {
                Node<T> curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key != key)
                        return false;
                    pred.next = curr.next;
                    return true;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }

        public boolean contains(T item) {
            int key = item.hashCode();
            head.lock();
            Node<T> pred = head;
            try {
                Node<T> curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    return curr.key == key;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    static class OptimisticList<T> {
        private Node<T> head = new Node<T>(Integer.MIN_VALUE);
        private Node<T> tail = new Node<T>(Integer.MAX_VALUE);

        public OptimisticList() {
            head.next = tail;
        }

        private boolean validate(Node<T> pred, Node<T> curr) {
            Node<T> node = head;
            while (node.key <= pred.key) {
                if (node == pred)
                    return pred.next == curr;
                node = node.next;
            }
            return false;
        }

        public boolean add(T item) {
            int key = item.hashCode();
            while (true) {
                Node<T> pred = head;
                Node<T> curr = pred.next;
                while (curr.key < key) {
                    pred = curr;
                    curr = curr.next;
                }
                pred.lock();
                try {
                    curr.lock();
                    try {
                        if (validate(pred, curr)) {
                            if (curr.key == key)
                                return false;
                            Node<T> newNode = new Node<T>(item);
                            newNode.next = curr;
                            pred.next = newNode;
                            return true;
                        }
                    } finally {
                        curr.unlock();
                    }
                } finally {
                    pred.unlock();
                }
            }
        }

        public boolean remove(T item) {
            int key = item.hashCode();
            while (true) {
                Node<T> pred = head;
                Node<T> curr = pred.next;
                while (curr.key < key) {
                    pred = curr;
                    curr = curr.next;
                }
                pred.lock();
                try {
                    curr.lock();
                    try {
                        if (validate(pred, curr)) {
                            if (curr.key != key)
                                return false;
                            pred.next = curr.next;
                            return true;
                        }
                    } finally {
                        curr.unlock();
                    }
                } finally {
                    pred.unlock();
                }
            }
        }

        public boolean contains(T item) {
            int key = item.hashCode();
            while (true) {
                Node<T> pred = head;
                Node<T> curr = pred.next;
                while (curr.key < key) {
                    pred = curr;
                    curr = curr.next;
                }
                pred.lock();
                try {
                    curr.lock();
                    try {
                        if (validate(pred, curr)) {
                            return (curr.key == key);
                        }
                    } finally {
                        curr.unlock();
                    }
                } finally {
                    pred.unlock();
                }
            }
        }
    }

    static final int INITIAL_SIZE = 10000;
    static final int TOTAL_OPS = 1000000;
    static final int RANGE = 1000;

    public static void runTest(String name, Object list, int threadCount) throws InterruptedException {
        Random rand = new Random();

        for (int i = 0; i < INITIAL_SIZE; i++) {
            int val = rand.nextInt(RANGE);
            if (list instanceof OptimisticList)
                ((OptimisticList<Integer>) list).add(val);
            else if (list instanceof FineList)
                ((FineList<Integer>) list).add(val);
            else
                ((SequentialList) list).add(val);
        }

        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        long startTime = System.currentTimeMillis();

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                Random localRand = new Random();
                try {
                    int opsPerThread = TOTAL_OPS / threadCount;
                    for (int i = 0; i < opsPerThread; i++) {
                        double choice = localRand.nextDouble();
                        int val = localRand.nextInt(RANGE);

                        if (choice < 0.9) {
                            if (list instanceof OptimisticList)
                                ((OptimisticList<Integer>) list).contains(val);
                            else if (list instanceof FineList)
                                ((FineList<Integer>) list).contains(val);
                            else
                                ((SequentialList) list).contains(val);
                        } else if (choice < 0.95) {
                            if (list instanceof OptimisticList)
                                ((OptimisticList<Integer>) list).add(val);
                            else if (list instanceof FineList)
                                ((FineList<Integer>) list).add(val);
                            else
                                ((SequentialList) list).add(val);
                        } else {
                            if (list instanceof OptimisticList)
                                ((OptimisticList<Integer>) list).remove(val);
                            else if (list instanceof FineList)
                                ((FineList<Integer>) list).remove(val);
                            else
                                ((SequentialList) list).remove(val);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        doneLatch.await();
        long endTime = System.currentTimeMillis();
        executor.shutdown();

        System.out.println(name + ": Threads - " + threadCount + ", Time - " + (endTime - startTime));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting");
        int[] threads = { 2, 4, 6 };

        SequentialList seq = new SequentialList();
        runTest("Sequential ", seq, 1);
        System.out.println();

        for (int t : threads) {
            OptimisticList<Integer> optimistic = new OptimisticList<>();
            runTest("Optimistic", optimistic, t);

            FineList<Integer> fine = new FineList<>();
            runTest("Fine", fine, t);
            System.out.println();
        }
    }
}
