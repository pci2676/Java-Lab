package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("dispose 로 구독을 취소할 수 있는데 이게 무조건 원자적으로 행동한다는 보장이없다. 이를 해결하기 위한 Disposables 유틸 클래스 테스트")
public class DisposablesTest {

    @DisplayName("그냥 swap을 호출하면 아무것도 없음")
    @Test
    void disposablesSwapTest1() {
        //given
        Disposable.Swap swap = Disposables.swap();

        //when
        Disposable disposable = swap.get();

        //then
        assertThat(disposable).isNull();
    }

    @DisplayName("replace 를 호출해서 disposable (subscribe 결과) 을 추가해넣을수 있다.")
    @Test
    void disposablesSwapTest2() {
        //given
        Disposable.Swap swap = Disposables.swap();

        //when
        Disposable subscribe = Flux.just("1")
                .subscribe(System.out::println);
        swap.replace(subscribe);

        Disposable disposable = swap.get();
        boolean disposed = swap.isDisposed();

        //then
        assertThat(disposable).isNotNull();
        assertThat(disposable).isEqualTo(subscribe);
        assertThat(disposed).isFalse();
    }

    @DisplayName("update 를 호출해서 disposable (subscribe 결과) 을 추가해넣을수 있다.")
    @Test
    void disposablesSwapTest22() {
        //given
        Disposable.Swap swap = Disposables.swap();

        //when
        Disposable subscribe = Flux.just("1")
                .subscribe(System.out::println);
        swap.update(subscribe);

        Disposable disposable = swap.get();
        boolean disposed = swap.isDisposed();

        //then
        assertThat(disposable).isNotNull();
        assertThat(disposable).isEqualTo(subscribe);
        assertThat(disposed).isFalse();
    }

    @DisplayName("replace 로 바꿔 끼어넣는다.")
    @Test
    void disposablesSwapTest3() {
        //given
        Disposable.Swap swap = Disposables.swap();

        //when
        Disposable subscribe1 = Flux.just("1")
                .subscribe(System.out::println);
        swap.replace(subscribe1);

        Disposable subscribe2 = Flux.just("2")
                .subscribe(System.out::println);
        boolean replace = swap.replace(subscribe2);

        Disposable disposable = swap.get();

        //then
        assertThat(disposable).isEqualTo(subscribe2);
        assertThat(replace).isTrue();
    }

    @DisplayName("update 로 바꿔넣으면 기존 subscribe 는 멈춤")
    @Test
    void disposablesSwapTest4() throws InterruptedException {
        //given
        Disposable.Swap swap = Disposables.swap();

        //when
        Flux<String> share = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .refCount(1);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Set<String> watchMovie = new HashSet<>();
        Disposable watcher1 = share.subscribe((item) -> {
            System.out.println(Thread.currentThread().getName() + " : 시청중");
            watchMovie.add(item);
            countDownLatch.countDown();
        });

        Disposable watcher2 = share.subscribe((item) -> System.out.println(Thread.currentThread().getName() + " : 시청중"));

        swap.replace(watcher1);
        countDownLatch.await();

        //when
        boolean update = swap.update(watcher2);

        //then
        assertAll("잘 바뀐걸 확인 할 수 있다.",
                () -> assertThat(update).isTrue(),
                () -> assertThat(swap.get()).isEqualTo(watcher2),
                () -> assertThat(watcher2.isDisposed()).isFalse()
        );

        assertAll("구독이 멈춰 버렸다.",
                () -> assertThat(watcher1.isDisposed()).isTrue(),
                () -> assertThat(watchMovie).hasSize(1)
        );
    }

    @DisplayName("replace 로 바꿔넣으면 기존 subscribe 는 멈추지 않는다.")
    @Test
    void disposablesSwapTest5() throws InterruptedException {
        //given
        Disposable.Swap swap = Disposables.swap();

        //when
        Flux<String> share = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .refCount(1);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Set<String> watchMovie = new HashSet<>();
        Disposable watcher1 = share.subscribe((item) -> {
            System.out.println(Thread.currentThread().getName() + " : 시청중");
            watchMovie.add(item);
            countDownLatch.countDown();
        });

        Disposable watcher2 = share.subscribe((item) -> System.out.println(Thread.currentThread().getName() + " : 시청중"));

        swap.replace(watcher1);
        countDownLatch.await();

        //when
        boolean update = swap.replace(watcher2);

        //then
        assertAll("잘 바뀐걸 확인 할 수 있다.",
                () -> assertThat(update).isTrue(),
                () -> assertThat(swap.get()).isEqualTo(watcher2),
                () -> assertThat(watcher2.isDisposed()).isFalse()
        );

        assertAll("구독도 멈추지 않음",
                () -> assertThat(watcher1.isDisposed()).isFalse()
        );
    }

    private Stream<String> getMovie() {
        return Stream.of(
                "01. 아이언맨 1(2008)",
                "02. 인크레더블 헐크 (2008)",
                "03. 아이언맨 2(2010)",
                "04. 토르: 천둥의 신(2011)",
                "05. 캡틴 아메리카: 퍼스트 어벤저스(2011)",
                "06. 어벤저스 1(2012)",
                "07. 아이언맨 3(2013)",
                "08. 토르 2: 다크월드(2013)",
                "09. 캡틴 아메리카2: 윈터솔저(2014)",
                "10. 가디언즈 오브 갤럭시 1(2014)",
                "11. 어벤저스 2: 에이지 오브 울트론(2015)",
                "12. 앤트맨 1(2015)",
                "13. 캡틴 아메리카3: 시빌 워(2016)",
                "14. 닥터 스트레인지 (2016)",
                "15. 가디언즈 오브 갤럭시 2(2017)",
                "16. 스파이더맨: 홈 커밍(2017)",
                "17. 토르 3: 라그나로크(2017)",
                "18. 블랙팬서 (2018)",
                "19. 어벤저스 3: 인피니티 워(2018)",
                "20. 앤트맨 2: 앤트맨과 와스프(2018)",
                "21. 캡틴 마블 (2019)",
                "22. 어벤저스 4: 엔드 게임(2019)",
                "23. 스파이더맨 2: 파 프롬 홈(2019)");
    }
}
