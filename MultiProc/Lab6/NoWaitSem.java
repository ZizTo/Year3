import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class ParkingLot2 {
    private final Semaphore parkingSpots;

    public ParkingLot2(int spots) {
        this.parkingSpots = new Semaphore(spots, true);
    }

    public Semaphore getParkingSpots() {
        return parkingSpots;
    }
}

class Car2 extends Thread {
    private final ParkingLot2 parkingLot;
    private final String carName;
    private final int waitTimeSeconds;

    public Car2(ParkingLot2 parkingLot, String carName, int waitTimeSeconds) {
        this.parkingLot = parkingLot;
        this.carName = carName;
        this.waitTimeSeconds = waitTimeSeconds;
    }

    @Override
    public void run() {
        System.out.println(carName + " wait");
        try {
            boolean parked = parkingLot.getParkingSpots().tryAcquire(waitTimeSeconds, TimeUnit.SECONDS);

            if (parked) {
                System.out.println(carName + " parked");

                Thread.sleep((long) (Math.random() * 2000 + 2000));

                System.out.println(carName + " leave");
                parkingLot.getParkingSpots().release();
            } else {
                System.out.println(carName + " !!! waited for " + waitTimeSeconds + " sec and leave");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class NoWaitSem {
    public static void main(String[] args) {
        ParkingLot2 lot = new ParkingLot2(2);
        for (int i = 1; i <= 7; i++) {
            new Car2(lot, "Avto " + i, 2).start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
