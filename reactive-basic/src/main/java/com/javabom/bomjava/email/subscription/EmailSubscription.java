package com.javabom.bomjava.email.subscription;

import com.javabom.bomjava.email.model.Email;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

/**
 * Subscriber 와 Publisher 간 통신의 매개체이다.
 */
public class EmailSubscription implements Subscription {
    private Flow.Subscriber<? super Email> emailSubscriber;
    private boolean subscribe;

    public EmailSubscription(Flow.Subscriber<? super Email> subscriber) {
        this.emailSubscriber = subscriber;
        this.subscribe = true;
    }

    /**
     * Subscriber 는 request 함수를 통해 Publisher 에게 데이터를 전달한다.
     *
     * @param n Subscriber 가 n개의 데이터를 요청(back pressure)
     */
    @Override
    public void request(long n) {
        if (hasNotSubscriber()) {
            return;
        }

        for (int request = 0; request < n; request++) {
            Email email = new Email("제목", "내용");
            new Thread(() -> sendEmail(email)).start();
        }
    }

    private void sendEmail(Email email) {
        try {
            //4. onNext
            emailSubscriber.onNext(email);
            //5. onComplete
            emailSubscriber.onComplete();
        } catch (Exception e) {
            //5. onError
            emailSubscriber.onError(e);
        }
    }

    private boolean hasNotSubscriber() {
        return !this.subscribe;
    }

    /**
     * 구독을 취소한다.
     */
    @Override
    public void cancel() {
        this.subscribe = false;
        this.emailSubscriber = null;
    }

}
