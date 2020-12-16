package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

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
        threadNames.add(Thread.currentThread().getName());

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
        assertThat(threadNames).hasSize(1);
        assertThat(threadNames).contains("main");
    }
}
