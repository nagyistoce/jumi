// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.ipc;

import fi.jumi.core.api.SuiteListener;
import fi.jumi.core.config.SuiteConfiguration;
import fi.jumi.core.events.RequestListenerEventizer;
import fi.jumi.core.ipc.api.*;
import fi.jumi.core.ipc.channel.*;
import fi.jumi.core.ipc.dirs.CommandDir;
import fi.jumi.core.ipc.encoding.RequestListenerEncoding;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Closeable;

@NotThreadSafe
public class IpcCommandSender implements CommandListener, Closeable {

    private final IpcWriter<RequestListener> requestWriter;
    private final RequestListener requestProxy;

    public IpcCommandSender(CommandDir dir) {
        requestWriter = IpcChannel.writer(dir.getRequestPath(), RequestListenerEncoding::new);
        requestProxy = new RequestListenerEventizer().newFrontend(requestWriter);
    }

    @Override
    public void close() {
        requestWriter.close();
    }

    @Override
    public void runTests(SuiteConfiguration suiteConfiguration, SuiteListener suiteListener) {
        requestProxy.runTests(suiteConfiguration);
    }

    @Override
    public void shutdown() {
        requestProxy.shutdown();
    }
}