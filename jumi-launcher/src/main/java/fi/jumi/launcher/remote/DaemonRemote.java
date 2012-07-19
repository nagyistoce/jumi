// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.launcher.remote;

import fi.jumi.actors.ActorRef;
import fi.jumi.launcher.SuiteOptions;
import fi.jumi.launcher.network.DaemonConnectionListener;

public interface DaemonRemote {

    void connectToDaemon(SuiteOptions suiteOptions, ActorRef<DaemonConnectionListener> response);
}