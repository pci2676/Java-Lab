# Reactive Basic

이 프로젝트에서 리액티브 스트림의 기본적인 내부 구조에 대해 구현했다.  
그리고 리액티브 스트림을 공부하기 위해 약간의 학습테스트를 작성하였다.

---
**단편지식**  
Flux 와 Mono 는 Publisher 의 구현체이다.   
Publisher 는 구독할 정보(Subscription)를 Subscriber에게 전달한다.

## Subscribe

subscribe() 에서 먼저 앞의 3개의 파라미터를 살펴보면  
첫번째 Consumer는 정보를 구독하는 Consumer, 두번째 Consumer 는 error 발생시 사용하는 Consumer, 세번째 Runnable 은 모든 작업이 끝났을때 행동을지정한다. 마지막 4번째
파라미터인 Consumer<Subscription> 는 Deprecated 되었으니 사용하지 말고 Context 를 사용한 부분은 추후 Context 를 학습한 이후 다시 알아보도록 한다.

## Disposable 을 이용한 subscribe() 의 중단

람다 기반의 subscribe() 메서드는 Disposable 타입으로 반환 값을 돌려준다. Disposable 인터페이스는 dispose() 메서드로 구독(subscription)을 중단할 수 있다. 그런데 이런
중단행위가 항상 보증되는 것은 아닌데 publisher 가 너무 빨리 구독정보를 발행하면 취소했음에도 구독 정보를 받을 수도 있다.

위와 같은 문제를 해결해주는 유틸성 클래스가 있는데 Disposables.swap() 으로 받는 인스턴스는 원자적인 행동을 해준다.  


