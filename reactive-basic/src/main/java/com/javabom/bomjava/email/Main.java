package com.javabom.bomjava.email;

import com.javabom.bomjava.email.publisher.EmailPublisher;
import com.javabom.bomjava.email.subscriber.EmailSubscriber;

/**
 * Flow API 의 흐름 <br>
 * <br>
 * <img src="https://user-images.githubusercontent.com/13347548/104085503-58e4ec80-5293-11eb-8894-2affed7ecea7.png" alt="https://engineering.linecorp.com/ko/blog/reactive-streams-with-armeria-1/"/>
 */
public class Main {
    public static void main(String[] args) {
        EmailPublisher emailPublisher = new EmailPublisher();

        EmailSubscriber emailSubscriber = new EmailSubscriber(5);

        //1. subscribe
        emailPublisher.subscribe(emailSubscriber);
    }
}
