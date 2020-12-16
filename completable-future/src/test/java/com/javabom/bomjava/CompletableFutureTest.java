package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("동기 테스트")
class CompletableFutureTest {

    @DisplayName("complete 메서드를 통해 completed state 를 true로 변경한다.")
    @Test
    void completeTest() {
        //given
        CompletableFuture<String> future = new CompletableFuture<>();
        assertThat(future).isNotDone();

        //when
        future.complete("foo");

        //then
        assertThat(future).isDone();
    }

    @DisplayName("complete를 호출해서 thenRun으로 넘겨준 Runnable callback을 실행할 수 있다.")
    @Test
    void thenRunAndCompleteTest() throws InterruptedException {
        //given
        Set<String> threadNames = new HashSet<>();
        threadNames.add(Thread.currentThread().getName());

        CompletableFuture<String> future = new CompletableFuture<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        future.thenRun(() -> {
            try {
                System.out.println("get from complete : " + future.get());
                threadNames.add(Thread.currentThread().getName());
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //when
        future.complete("Runnable로 넘겨줄 값");
        countDownLatch.await();

        //then
        assertThat(future).isDone();
        assertThat(threadNames).hasSize(1);
    }

    @DisplayName("complete를 호출해서 thenAccept 에 넘긴 Consumer calllback을 호출할 수 있다.")
    @Test
    void thenAcceptAndCompleteTest() throws InterruptedException {
        //given
        Set<String> threadNames = new HashSet<>();
        threadNames.add(Thread.currentThread().getName());

        CompletableFuture<String> future = new CompletableFuture<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        future.thenAccept((string) -> {
            try {
                Thread.sleep(100);
                System.out.println("get from complete : " + string);
                threadNames.add(Thread.currentThread().getName());
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //when
        future.complete("Consumer로 넘겨줄 값");
        countDownLatch.await();

        //then
        assertThat(future).isDone();
        assertThat(threadNames).hasSize(1);
    }

}