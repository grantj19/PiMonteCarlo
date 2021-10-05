import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public class SingleThreadMonteCarlo {

    public static void main(String[] args) {
        long total_points = 200_000_000_000L;

        for (int i=0; i<3; i++) {
            Instant startTime = Instant.now();

            double pi = simulation(total_points);

            Instant finishTime = Instant.now();

            long timeElapsed = Duration.between(startTime, finishTime).toMillis();

            System.out.println("The calculated pi value is: " + pi);
            System.out.println("Number of points used: " + total_points);
            System.out.println("Time taken: "+timeElapsed+" milliseconds.\n");
            total_points = total_points*100;
        }
    }

    private static double simulation(long total_points){
        long withinCircle = 0;

        for(int i=0; i<total_points; i++){
            double x = ThreadLocalRandom.current().nextDouble(-1, 1);
            double y = ThreadLocalRandom.current().nextDouble(-1, 1);
            if (x*x + y*y <= 1){
                withinCircle++;
            }
        }

        double pi = withinCircle / (double)total_points * 4;
        return pi;

    }

}
