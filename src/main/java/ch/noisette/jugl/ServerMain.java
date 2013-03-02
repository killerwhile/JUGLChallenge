package ch.noisette.jugl;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.noisette.jugl.http.HttpServer;

public class ServerMain implements Closeable {

    private final int port;

    private final HttpServer server;
    private final CountDownLatch initializationLatch = new CountDownLatch(1);

    public ServerMain() throws IOException {
        this(findFreePort());
    }

    public ServerMain(Integer port) throws IOException {

        if (port == null) {
            port = findFreePort();
        }

        this.port = port;
        
        server = new HttpServer(port, initializationLatch);

        runInSeparatedThread(server);

        try {
            initializationLatch.await();
        }
        catch (InterruptedException e) {
            throw new IllegalStateException("Initialization failed", e);
        }

    }

    public static void runInSeparatedThread(Runnable task) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.execute(task);

        executor.shutdown();
    }

    private static int findFreePort() throws IOException {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(0);

            return socket.getLocalPort();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        server.close();
    }

    public int getPort() {
        return port;
    }
    
}
