package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Java6Assertions.assertThat;

@DisplayName("Flux.just() 테스트")
public class FluxBasicTest {

    @DisplayName("just 는 정보를 subscriber에게 그대로 전달해주고 아무런 설정이 없으면 동기적으로 이루어진다.")
    @Test
    void justTest() {
        Flux<String> flux = Flux.just("hello", "reactive", "stream").log();

        Consumer<String> subscriber = (data) -> System.out.println("구독한 정보는 : " + data);

        flux.subscribe(subscriber);
    }

    @DisplayName("onComplete는 모든 구독 정보를 처리하고 불린다.")
    @Test
    void justOnCompleteTest() {
        Flux<String> flux = Flux.just("hello", "reactive", "stream", "hello").log();

        Set<String> group = new HashSet<>();
        Consumer<String> subscriber = (data) -> {
            System.out.println("구독한 정보는 : " + data);
            group.add(data);
        };

        Runnable completeRunner = () -> {
            assertThat(group).hasSize(3);
            System.out.println("모든 작업을 마쳤습니다.");
        };

        flux.subscribe(subscriber, Throwable::printStackTrace, completeRunner);
    }

    @DisplayName("onError가 onNext 중에 터지면 에어를 처리해준다. 에러가 발생하면 onComplete 는 불리지 않는다.")
    @Test
    void justOnErrorTest() {
        Flux<String> flux = Flux.just("hello", null, "stream").log();
        Consumer<String> subscriber = (data) -> System.out.println("구독한 정보는 : " + data);
        Runnable completeRunner = () -> System.out.println("모든 작업을 마쳤습니다.");

        Consumer<Throwable> errorConsumer = (e) -> System.out.println("에러발생 : " + e.getMessage());

        flux.subscribe(subscriber, errorConsumer, completeRunner);
    }

    @DisplayName("concatMap 은 순서를 지켜서 데이터를 처리(flatmap처럼)한다음 새로운 Publisher 를 반환한다." +
            "작업은 동기적으로 먼저 들어온 구독자를 모두 처리하고 나서 뒤에 이어 새롭게 처리를 시작한다.")
    @Test
    void justConcatMapTest1() {
        Flux<String> flux = Flux.just("a1", "a2", "b3", "b4", "b5", "c6", "d7", "d8", "d9", "d1", "e2", "e3", "f4")
                .concatMap(data -> {
                    String number = data.split("")[1];
                    return Flux.just(number);
                }).log();

        Map<Integer, LocalDateTime> timeMap = new HashMap<>();
        flux.subscribe(
                (data) -> System.out.println(Thread.currentThread().getName() + " : " + data),
                (e) -> System.out.println(e.getMessage()),
                () -> timeMap.put(1, LocalDateTime.now())
        );

        flux.subscribe(
                (data) -> System.out.println(Thread.currentThread().getName() + " : " + data),
                (e) -> System.out.println(e.getMessage()),
                () -> timeMap.put(2, LocalDateTime.now())
        );

        assertThat(timeMap.get(1)).isLessThan(timeMap.get(2));
    }
}