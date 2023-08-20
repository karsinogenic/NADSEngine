package com.nads.nadsengine;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nads.nadsengine.Services.CustomBinarySearch;

@SpringBootApplication
public class NadsengineApplication {

	public static void main(String[] args) {
		SpringApplication.run(NadsengineApplication.class, args);
	}

	// public static void main(String[] args) {
	// String arr[] = { "Adi", "Andi", "Andre", "Anji", "Bagas", "Bagus", "Beni",
	// "Naufal", "Nofal", "Noval", "Rifai",
	// "Rifqi", "Rizqi", };
	// CustomBinarySearch cbs = new CustomBinarySearch();
	// List<Integer> list = cbs.binarySearchCoba(arr, "Bja", 5);
	// System.out.println(list.toString());
	// String[] newArray = Arrays.copyOfRange(arr, list.get(0), list.get(1));

	// System.out.println(Arrays.toString(newArray));
	// }

	// public static void main(String[] args) throws ExecutionException,
	// InterruptedException {
	// ExecutorService executor = Executors.newSingleThreadExecutor();
	// Future<String> future = executor.submit(() -> {
	// try {
	// Thread.sleep(1000);
	// return "Hello World!";
	// } catch (InterruptedException e) {
	// return "Interrupted";
	// }
	// });
	// future.cancel(true);
	// if (future.isCancelled()) {
	// System.out.println("Task was cancelled");
	// } else {
	// System.out.println(future.get());
	// }
	// executor.shutdown();
	// }
}
