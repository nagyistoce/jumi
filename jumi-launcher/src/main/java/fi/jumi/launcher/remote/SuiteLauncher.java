// Copyright © 2011-2013, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.launcher.remote;

import fi.jumi.actors.eventizers.Event;
import fi.jumi.actors.queue.MessageSender;
import fi.jumi.core.api.SuiteListener;
import fi.jumi.core.config.*;

public interface SuiteLauncher {

    void runTests(SuiteConfiguration suiteConfiguration,
                  DaemonConfiguration daemonConfiguration,
                  MessageSender<Event<SuiteListener>> suiteListener);

    void shutdownDaemon();
}
