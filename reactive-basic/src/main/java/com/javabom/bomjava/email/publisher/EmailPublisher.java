package com.javabom.bomjava.email.publisher;

import com.javabom.bomjava.email.model.Email;
import com.javabom.bomjava.email.subscription.EmailSubscription;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

/**
 * Subscriber 의 구독을 받는다.
 * Publisher 는 Subscription 을 통해 Subscriber 의 onNext 에 데이터를 전달하고, 작업이 완료되면 onComplete, 에러가 발생하면 onError 시그널을 전달한다. <br>
 * <br>
 */
public class EmailPublisher implements Publisher<Email> {

    /**
     * @param subscriber subscribe 함수를 사용해서 Publisher 에게 구독을 요청한다. <br>
     *                   Publisher 는 Subscriber 의 onSubscribe 를 호출한다.
     */
    @Override
    public void subscribe(Subscriber<? super Email> subscriber) {
        EmailSubscription subscription = new EmailSubscription(subscriber);

        //2. onSubscribe
        subscriber.onSubscribe(subscription);


    }
}
