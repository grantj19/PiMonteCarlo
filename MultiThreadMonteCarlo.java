import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

public class MultiThreadMonteCarlo {

    static class Task implements Callable<Long> {

        private Long total_points;
        private int minX;
        private int minY;
        private int maxX;
        private int maxY;
        
        public Task (Long total_points, int minX, int maxX, int minY, int maxY){
            this.total_points = total_points;
            this.maxX = maxX;
            this.maxY = maxY;
            this.minX = minX;
            this.minY = minY;
        }

        public Long call(){
            long withinCircle = 0L;

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
                Instant startTime = Instant.now();
                long withinCircle = 0L;
                List<Future<Long>> futures = new ArrayList<>();
                
                Callable<Long> task1 = new Task(total_points / number_of_threads, -1, 0, 0, 1);
                Callable<Long> task2= new Task(total_points / number_of_threads, 0, 1, 0, 1);
                Callable<Long> task3 = new Task(total_points / number_of_threads, 0, 1, -1, 0);
                Callable<Long> task4 = new Task(total_points / number_of_threads, -1, 0, -1, 0);

                futures.add(es.submit(task1));
                futures.add(es.submit(task2));
                futures.add(es.submit(task3));
                futures.add(es.submit(task4));
                
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