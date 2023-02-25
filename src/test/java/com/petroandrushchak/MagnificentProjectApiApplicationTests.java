package com.petroandrushchak;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;


@SpringBootTest
class MagnificentProjectApiApplicationTests {

	@Test
	void contextLoads() throws InterruptedException, ExecutionException {

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		long startTime = System.nanoTime();
		Future<String> future = executorService.submit(() -> {
			AtomicInteger i = new AtomicInteger();
			await().atMost(Duration.ofSeconds(60))
				   .pollInSameThread()
				   .pollInterval(Duration.ofSeconds(1))
				   .until(() -> {
					   System.out.println("Waiting for Log in button to be visible ... i = " + i.get());
					   i.getAndIncrement();
					   if (i.get() == 55) {
						   return true;
					   } else {
						   return false;
					   }
				   });
			return "Hello from Callable";
		});

		while(!future.isDone()) {
			System.out.println("Task is still not done...");
			Thread.sleep(200);
			double elapsedTimeInSec = (System.nanoTime() - startTime)/1000000000.0;

			if(elapsedTimeInSec > 20) {
				future.cancel(true);
			}
		}

		System.out.println("Task completed! Retrieving the result");
		String result = future.get();
		System.out.println(result);

		executorService.shutdown();


	}

}
