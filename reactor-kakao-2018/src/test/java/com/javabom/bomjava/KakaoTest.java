package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KakaoTest {

    @DisplayName("과일 종류(L), 과일(K)별 갯수(V) 구하기")
    @Test
    void concatTest1() throws InterruptedException {
        final List<String> basket1 = Arrays.asList("kiwi", "orange", "lemon", "orange", "lemon", "kiwi");
        final List<String> basket2 = Arrays.asList("banana", "lemon", "lemon", "kiwi");
        final List<String> basket3 = Arrays.asList("strawberry", "orange", "lemon", "grape", "strawberry");
        final List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
        final Flux<List<String>> basketFlux = Flux.fromIterable(baskets);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        basketFlux.concatMap(basket -> {
            //work 1 - 과일의 종류를 뽑아내는 작업
            final Mono<List<String>> distinctFruits = Flux.fromIterable(basket)
                    .log()
                    .distinct()
                    .collectList()
                    .subscribeOn(Schedulers.parallel());
            //work 2 - 과일-갯수 맵으로 묶어내는 작업
            final Mono<Map<String, Long>> countFruitsMono = Flux.fromIterable(basket)
                    .log()
                    .groupBy(fruit -> fruit) // 바구니로 부터 넘어온 과일 기준으로 group을 묶는다.
                    .concatMap(groupedFlux ->
                            groupedFlux.count()
                                    .map(count -> {
                                        final Map<String, Long> fruitCount = new LinkedHashMap<>();
                                        fruitCount.put(groupedFlux.key(), count);
                                        return fruitCount;
                                    }) // 각 과일별로 개수를 Map으로 리턴
                    ) // concatMap으로 순서보장
                    .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<String, Long>() {
                        {
                            putAll(accumulatedMap);
                            putAll(currentMap);
                            // 두개를 전부 putAll 한게 accumulatedMap 으로 다음 reduce 작업에 쓰이고 마지막에 합쳐진 Mono<맵>이 반환된다.
                        }
                    })// 그동안 누적된 accumulatedMap에 현재 넘어오는 currentMap을 합쳐서 새로운 Map을 만든다. // map끼리 putAll하여 하나의 Map으로 만든다.
                    .subscribeOn(Schedulers.parallel());
            // zip 으로 하나의 Flux로 만들어서 내보내기
            return Flux.zip(distinctFruits, countFruitsMono, FruitInfo::new);
        })
                .subscribe(
                        System.out::println,
                        err -> {
                            System.err.println(err);
                            countDownLatch.countDown();
                        },
                        () -> {
                            System.out.println("complete");
                            countDownLatch.countDown();
                        }
                );
        countDownLatch.await(2, TimeUnit.SECONDS);
    }

    static class FruitInfo {
        private final List<String> distinctFruits;
        private final Map<String, Long> countFruits;

        public FruitInfo(List<String> distinctFruits, Map<String, Long> countFruits) {
            this.distinctFruits = distinctFruits;
            this.countFruits = countFruits;
        }

        @Override
        public String toString() {
            return "FruitInfo{" +
                    "distinctFruits=" + distinctFruits +
                    ", countFruits=" + countFruits +
                    '}';
        }
    }
}
