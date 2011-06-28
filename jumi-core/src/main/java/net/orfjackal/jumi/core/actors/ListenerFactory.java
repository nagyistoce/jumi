// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package net.orfjackal.jumi.core.actors;

public interface ListenerFactory<T> {

    Class<T> getType();

    T newFrontend(MessageSender<Event<T>> target);

    MessageSender<Event<T>> newBackend(T target);
}
