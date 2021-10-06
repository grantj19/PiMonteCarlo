import java.text.BreakIterator;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

public class MultiThreadMonteCarlo {

    static class Task implements Callable<Long> {

        private Long total_points;
        private int quadrant;

        public Task (Long total_points, int quad){
            this.total_points = total_points;
            this.quadrant = quad;
        }

        public Long call(){
            long withinCircle = 0L;
            int minX;
            int maxX;
            int minY;
            int maxY;

            switch(quadrant){
                case 1: minX = -1;
                        maxX = 0;
                        minY = 0;
                        maxY = 1;
                        break;
                case 2: minX = 0;
                        maxX = 1;
                        minY = 0;
                        maxY = 1;
                        break;
                case 3: minX = 0;
                        maxX = 1;
                        minY = -1;
                        maxY = 0;
                        break;
                case 4: minX = -1;
                        maxX = 0;
                        minY = -1;
                        maxY = 0;
                        break;
                default: minX = 0;
                         maxX = 0;
                         minY = 0;
                         maxY = 0;
                         break;
            }
    
            for(int i=0; i<total_points; i++){

                double x = ThreadLocalRandom.current().nextDouble(minX, maxX);
                double y = ThreadLocalRandom.current().nextDouble(minY, maxY);

                if (x*x + y*y <= 1){
                    withinCircle++;
                }
            }

            return withinCircle;
        }
    }
    public static void main(String[] args) {
        try { 
            long total_points = 200_000L;
            int number_of_threads = 4;

            ExecutorService es = Executors.newFixedThreadPool(number_of_threads);

            for (int i=0; i<3; i++) {
                long withinCircle = 0L;
                Instant startTime = Instant.now();
                List<Future<Long>> futures = new ArrayList<>();
                
                for (int j = 1; j <= number_of_threads; j++){
                    Callable<Long> task = new Task(total_points / number_of_threads, j);
                    futures.add(es.submit(task));
                }
                
                try { 
                    for (Future<Long> f : futures){
                        withinCircle += f.get();
                    }
                }
                catch (InterruptedException | ExecutionException e) { }

                double pi = withinCircle / (double)total_points * 4;

                Instant finishTime = Instant.now();
                long timeElapsed = Duration.between(startTime, finishTime).toMillis();

                System.out.println("The calculated pi value is: " + pi);
                System.out.println("Number of points used: " + total_points);
                System.out.println("Time taken: "+timeElapsed+" milliseconds.\n");

                total_points = total_points*100;
            }

            es.shutdown();
        }
        catch(Exception ex) {
            System.out.println(ex.getStackTrace().toString());
        }
    }
}