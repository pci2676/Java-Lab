# Reactive Basic

이 프로젝트에서 리액티브 스트림의 기본적인 내부 구조에 대해 구현했다.  
그리고 리액티브 스트림을 공부하기 위해 약간의 학습테스트를 작성하였다.

---
**단편지식**  
Flux 와 Mono 는 Publisher 의 구현체이다.   
Publisher 는 구독할 정보(Subscription)를 Subscriber에게 전달한다.  
unbounded request 는 가능한 빨리 produce 해다라고 하는 것을 의미한다.

## Subscribe

subscribe() 에서 먼저 앞의 3개의 파라미터를 살펴보면  
첫번째 Consumer는 정보를 구독하는 Consumer, 두번째 Consumer 는 error 발생시 사용하는 Consumer, 세번째 Runnable 은 모든 작업이 끝났을때 행동을지정한다. 마지막 4번째
파라미터인 Consumer<Subscription> 는 Deprecated 되었으니 사용하지 말고 Context 를 사용한 부분은 추후 Context 를 학습한 이후 다시 알아보도록 한다.

## Disposable 을 이용한 subscribe() 의 중단

> [공식문서](https://projectreactor.io/docs/core/release/reference/#_cancelling_a_subscribe_with_its_disposable)

람다 기반의 subscribe() 메서드는 Disposable 타입으로 반환 값을 돌려준다. Disposable 인터페이스는 dispose() 메서드로 구독(subscription)을 중단할 수 있다. 그런데 이런
중단행위가 항상 보증되는 것은 아닌데 publisher 가 너무 빨리 구독정보를 발행하면 취소했음에도 구독 정보를 받을 수도 있다.

위와 같은 문제를 해결해주는 유틸성 클래스가 있는데 Disposables.swap() 으로 받는 인스턴스는 원자적인 행동을 해준다.    
특정 UI 시나리오에서 유용한데, 사용자가 취소 이벤트를 발생 요청 이벤트를 발생시킬 때 취소 이벤트 발행을 멈추는 것이 가능해진다.
> DisposablesTest.disposableScenarioTest 참고

Disposables.composite 는 Disposable 을 모아서 한번에 dispose 시킬 수 있고 한번 dispose 된 composite 에 새로운 Disposable 이 추가 되면 추가 된
Disposable 은 바로 dispose 된다. Disposable 의 상태를 일관되게 해주는 용도로 보면 될 것 같다.

## 람다의 대체제로 사용 가능 한 BaseSubscriber

> [공식문서](https://projectreactor.io/docs/core/release/reference/#_an_alternative_to_lambdas_basesubscriber)

BaseSubscriber 를 상속해서 클래스를 구현해 주면 되는데 이렇게 구현해서 만들어진 인스턴스는 반드시 한번에 하나만 구독해야한다. 그렇지 않으면 비동기로 동작하는 환경에서 onNext 에 여러 구독 정보가
들어오는 상황이 발생한다.  
즉, Pub:Sub 관계가 N:1 이 되어서는 안된다는 말이다. 이는 Reactive Stream 의 Rule 을 위배하는 것이다.

BaseSubscriber 는 커스텀한 요청을 처리하는데 더 유용하다. 최소한 구현해야할 것은 구독을 시작할 때 실행해서 첫 구독 정보를 가져오는데 사용되는 hookOnSubscribe(Subscription
subscription) 과 구독 정보를 다루는 hookOnNext(T value) 이다.

> BaseSubscriberTest.wrongUsageBaseSubscriberTest 참고

이외에도 hookOnComplete, hookOnError, hookOnCancel, hookFinally 을 제공한다.



