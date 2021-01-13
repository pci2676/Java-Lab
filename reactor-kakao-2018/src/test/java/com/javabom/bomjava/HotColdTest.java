package com.javabom.bomjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class HotColdTest {

    @DisplayName("Cold 는 구독을 하면 그때부터 퍼블리셔가 구독정보를 발행한다.")
    @Test
    void hotColdTest() {
        Flux<String> netFlix = Flux.just(getMovie().toArray(String[]::new))
                .doOnNext(subs -> System.out.println("next : " + subs));

        System.out.println("동기적으로 구독을 신청하면 그때 Subscription 을 처리하기 시작해서 새로운 데이터를 생성한다.");
        netFlix.subscribe(subs -> System.out.println("consume1 : " + subs));
        netFlix.subscribe(subs -> System.out.println("consume2 : " + subs));
    }

    @DisplayName("Hot 은 구독을 하고 있지 않아도 퍼블리셔는 계속 구독정보를 발행하고 있다.")
    @Test
    void hotColdTest2() throws InterruptedException {
        Flux<String> movieTheatre = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1)).share();


        // you start watching the movie
        CountDownLatch countDownLatch = new CountDownLatch(3);
        new Thread(
                () -> movieTheatre.subscribe(
                        scene -> {
                            System.out.println("Chan are watching " + scene);
                            countDownLatch.countDown();
                        },
                        System.out::println
                )
        ).start();

        countDownLatch.await();

        CountDownLatch completeCountDownLatch = new CountDownLatch(5);
        new Thread(
                () -> movieTheatre.subscribe(
                        scene -> {
                            System.out.println("In are watching " + scene);
                            completeCountDownLatch.countDown();
                        },
                        System.out::println
                )
        ).start();

        completeCountDownLatch.await();
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
