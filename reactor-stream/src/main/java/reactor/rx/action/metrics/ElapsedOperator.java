/*
 * Copyright (c) 2011-2014 Pivotal Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package reactor.rx.action.metrics;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.Publishers;
import reactor.core.subscriber.SubscriberBarrier;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;

/**
 * @author Stephane Maldini
 * @since 2.0, 2.1
 */
public final class ElapsedOperator<T> implements Publishers.Operator<T, Tuple2<Long, T>> {

	public static final ElapsedOperator INSTANCE = new ElapsedOperator();

	@Override
	public Subscriber<? super T> apply(Subscriber<? super Tuple2<Long, T>> subscriber) {
		return new ElapsedAction<>(subscriber);
	}

	static final class ElapsedAction<T> extends SubscriberBarrier<T, Tuple2<Long, T>> {

		private long lastTime;

		public ElapsedAction(Subscriber<? super Tuple2<Long, T>> subscriber) {
			super(subscriber);
		}

		@Override
		protected void doOnSubscribe(Subscription subscription) {
			lastTime = System.currentTimeMillis();
			subscriber.onSubscribe(subscription);
		}

		@Override
		protected void doNext(T ev) {
			long previousTime = lastTime;
			lastTime = System.currentTimeMillis();

			subscriber.onNext(Tuple.of(lastTime - previousTime, ev));
		}
	}
}
