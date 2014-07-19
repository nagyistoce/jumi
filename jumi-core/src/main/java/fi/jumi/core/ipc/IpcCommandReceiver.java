// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.ipc;

import fi.jumi.core.api.SuiteListener;
import fi.jumi.core.config.SuiteConfiguration;
import fi.jumi.core.events.*;
import fi.jumi.core.events.suiteListener.OnSuiteFinishedEvent;
import fi.jumi.core.ipc.api.*;
import fi.jumi.core.ipc.channel.*;
import fi.jumi.core.ipc.dirs.*;
import fi.jumi.core.ipc.encoding.*;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.file.Path;

@NotThreadSafe
public class IpcCommandReceiver implements Runnable {

    private final DaemonDir daemonDir;
    private final CommandListener commandListener;
    private final IpcReader<RequestListener> requestReader;
    private final IpcWriter<ResponseListener> responseWriter;
    private final ResponseListener response;

    public IpcCommandReceiver(DaemonDir daemonDir, CommandDir commandDir, CommandListener commandListener) {
        this.daemonDir = daemonDir;
        this.commandListener = commandListener;
        this.requestReader = IpcChannel.reader(commandDir.getRequestPath(), RequestListenerEncoding::new);
        this.responseWriter = IpcChannel.writer(commandDir.getResponsePath(), ResponseListenerEncoding::new);
        this.response = new ResponseListenerEventizer().newFrontend(responseWriter);
    }

    @Override
    public void run() {
        try {
            IpcReaders.decodeAll(requestReader, new RequestReader());
        } catch (InterruptedException e) {
            System.err.println(this + " interrupted");
            Thread.currentThread().interrupt();
        }
        responseWriter.close();
    }


    @NotThreadSafe
    private class RequestReader implements RequestListener {

        @Override
        public void runTests(SuiteConfiguration suiteConfiguration) {
            Path suiteResults = newSuiteResultsFile();
            // TODO: the suite writer should run as an actor
            IpcWriter<SuiteListener> suiteWriter = IpcChannel.writer(suiteResults, SuiteListenerEncoding::new);

            SuiteListener suiteListener = new SuiteListenerEventizer().newFrontend(message -> {
                suiteWriter.send(message);
                if (message instanceof OnSuiteFinishedEvent) { // XXX
                    suiteWriter.close();
                }
            });

            response.onSuiteStarted(suiteResults);

            // TODO: send ActorRef to suiteListener
            commandListener.runTests(suiteConfiguration, suiteListener);
        }

        private Path newSuiteResultsFile() {
            try {
                return daemonDir.createSuiteDir().getSuiteResultsPath();
            } catch (IOException e) {
                // TODO: write a failure to results file?
                throw new RuntimeException(e);
            }
        }

        @Override
        public void shutdown() {
            commandListener.shutdown();
        }
    }
}
