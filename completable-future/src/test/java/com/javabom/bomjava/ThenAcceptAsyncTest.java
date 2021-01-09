package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html
 */
@DisplayName("비동기 테스트")
public class ThenAcceptAsyncTest {

    @DisplayName("thenAcceptAsync는 제어권을 넘기지 않고 비동기로 동작한다.")
    @Test
    void thenAcceptAsyncTest1() throws InterruptedException {
        //given
        Set<String> threadNames = new HashSet<>();
        threadNames.add(Thread.currentThread().getName());

        CompletableFuture<String> future = new CompletableFuture<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        future.thenAcceptAsync((string) -> {
            System.out.println(string);
            threadNames.add(Thread.currentThread().getName());
            countDownLatch.countDown();
        });

        //when
        future.complete("비동기 콜백에게 넘겨준 값");
        countDownLatch.await();

        //then
        assertThat(threadNames).hasSize(2);
    }

    @DisplayName("Excuter를 람다를 이용해서 정의하면 thenAcceptAsync는 메인스레드로 동기로 동작한다.")
    @Test
    void thenAcceptAsyncTest2() throws InterruptedException {
        //given
        Set<String> threadNames = new HashSet<>();
        String mainThreadName = Thread.currentThread().getName();
        threadNames.add(mainThreadName);

        Executor executor = command -> {
            threadNames.add(Thread.currentThread().getName());
            command.run();
        };

        CompletableFuture<String> future = new CompletableFuture<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        future.thenAcceptAsync((string) -> {
            threadNames.add(Thread.currentThread().getName());
            countDownLatch.countDown();
        }, executor);

        //when
        future.complete("비동기 콜백에게 넘겨준 값");
        countDownLatch.await();

        //then
        assertThat(threadNames).containsOnly(mainThreadName);
    }

    @DisplayName("thenAcceptAsync는 완료후 새로운 스레드를 이용해서 다음 작업을 한다.")
    @Test
    void thenAcceptAsyncTest3() throws InterruptedException {
        //given
        Set<String> threadNames = new HashSet<>();
        threadNames.add(Thread.currentThread().getName());

        Executor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        CompletableFuture<String> future = new CompletableFuture<>();
        CountDownLatch countDownLatch = new CountDownLatch(2);

        CompletableFuture<Void> nextFuture = future.thenAcceptAsync((string) -> {
            threadNames.add(Thread.currentThread().getName());
            System.out.println("처음 : " + threadNames);
            countDownLatch.countDown();
        }, executor);

        //future 가 complete 되면 연쇄적으로 실행된다.
        nextFuture.thenAcceptAsync((voidObject) -> {
            threadNames.add(Thread.currentThread().getName());
            System.out.println("다음 : " + threadNames);
            countDownLatch.countDown();
        });


        //when
        future.complete("Hello Future");
        countDownLatch.await();

        //then
        System.out.println(threadNames);
        assertThat(threadNames).hasSize(3);
    }

}
