package ru.myhomework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        // Создаем пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Список для хранения Future
        List<Future<Integer>> futures = new ArrayList<>();

        // Отправляем задачи на исполнение
        for (String text : texts) {
            Callable<Integer> task = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            futures.add(executor.submit(task));
        }

        // Получаем результаты и находим максимальный интервал
        int maxInterval = 0;
        for (Future<Integer> future : futures) {
            maxInterval = Math.max(maxInterval, future.get());
        }
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Max Interval: " + maxInterval);
        System.out.println("Time: " + (endTs - startTs) + "ms");

        // Завершаем пул потоков
        executor.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
