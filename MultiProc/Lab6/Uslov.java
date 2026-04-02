import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ParkingLot {
    private int availableSpots;
    private final Lock lock = new ReentrantLock();
    private final Condition spotAvailable = lock.newCondition();

    public ParkingLot(int spots) {
        this.availableSpots = spots;
    }

    public void park(String carName) throws InterruptedException {
        lock.lock();
        try {
            System.out.println(carName + " wait for place");
            while (availableSpots == 0) {
                spotAvailable.await();
            }
            availableSpots--;
            System.out.println(carName + " on place");
        } finally {
            lock.unlock();
        }
    }

    public void leave(String carName) {
        lock.lock();
        try {
            availableSpots++;
            System.out.println(carName + " leave");
            spotAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}

class Car extends Thread {
    private final ParkingLot parkingLot;
    private final String carName;

    public Car(ParkingLot parkingLot, String carName) {
        this.parkingLot = parkingLot;
        this.carName = carName;
    }

    @Override
    public void run() {
        try {
            parkingLot.park(carName);
            Thread.sleep((long) (Math.random() * 2000 + 1000));
            parkingLot.leave(carName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Uslov {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(3);
        for (int i = 1; i <= 8; i++) {
            new Car(lot, "Avto " + i).start();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
