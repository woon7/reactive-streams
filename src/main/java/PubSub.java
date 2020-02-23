import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PubSub {

    public static void main(String[] args) throws InterruptedException {
        Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);
        final ExecutorService executorService = Executors.newCachedThreadPool();

        Publisher<Integer> publisher = subscriber -> {
            Iterator<Integer> iterator = iterable.iterator();

            subscriber.onSubscribe(new Subscription() {
                public void request(long l) {
                    executorService.execute(() -> {
                        int i = 0;
                        while (i++ < l) {
                            if (iterator.hasNext()) {
                                subscriber.onNext(iterator.next());
                            } else {
                                subscriber.onComplete();
                                break;
                            }
                        }
                    });
                }

                public void cancel() {

                }
            });
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            Subscription subscription;

            public void onSubscribe(Subscription subscription) {
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            public void onNext(Integer integer) {
                System.out.println(Thread.currentThread().getName() + " onNext " + integer);
                this.subscription.request(1);
            }

            public void onError(Throwable throwable) {
                System.out.println("onError " + throwable.getMessage());
            }

            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        publisher.subscribe(subscriber);

        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdown();
    }

}