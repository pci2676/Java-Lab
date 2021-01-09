package com.javabom.bomjava.email.subscriber;

import com.javabom.bomjava.email.model.Email;

import java.util.Random;
import java.util.concurrent.Flow.Subscriber;

import static java.util.concurrent.Flow.Subscription;

public class EmailSubscriber implements Subscriber<Email> {

    private final long requestCount;

    public EmailSubscriber(long requestCount) {
        this.requestCount = requestCount;
    }

    /**
     * 매개 변수로 Subscription을 받는 onSubscribe
     *
     * @param subscription Publisher 가 onSubscribe 를 호출해서 Subscription 을 전달한다.
     */
    @Override
    public void onSubscribe(Subscription subscription) {
        //3. request
        subscription.request(requestCount);
    }

    /**
     * 받은 데이터를 처리하는 onNext
     *
     * @param item
     */
    @Override
    public void onNext(Email item) {
        int random = new Random().nextInt(2);
        if (random == 0) {
            throw new RuntimeException("이메일 전송 실패 !!!");
        }
        System.out.println(item);
    }

    /**
     * 에러를 처리하는 onError
     *
     * @param throwable
     */
    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    /**
     * 작업 완료 시 사용하는 onComplete
     */
    @Override
    public void onComplete() {
        System.out.println("email send complete.");
    }
}
