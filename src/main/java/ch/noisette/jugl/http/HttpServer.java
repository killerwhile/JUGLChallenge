package ch.noisette.jugl.http;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author bperroud
 * 
 */
public class HttpServer implements Runnable, Closeable {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final int port;
    private final CountDownLatch initializationLatch;
    private ServerBootstrap bootstrap = null;
    private Channel serverChannel = null;
    
    public HttpServer(final int port, final CountDownLatch initializationLatch) {
        this.port = port;
        this.initializationLatch = initializationLatch;
    }



    public void run() {

        log.info("Starting " + HttpServer.class.getSimpleName() + " ...");

        final int c = 4;
        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(), Executors.newFixedThreadPool( c ), c));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new PipelineFactory());

        // Bind and start to accept incoming connections.
        serverChannel = bootstrap.bind(new InetSocketAddress(port));
        
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        
        log.info(HttpServer.class.getSimpleName() + " successfully started on port " + port + ".");

        initializationLatch.countDown();
    }

    public void close() throws IOException {

        log.info("Shutting down " + HttpServer.class.getSimpleName() + " ...");

        try {
            if (serverChannel != null) {
                final ChannelFuture f = serverChannel.close();
                f.awaitUninterruptibly();
            }
        }
        finally {
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
            }
        }

        log.info(HttpServer.class.getSimpleName() + " successfully shut down.");
    }

    public int getPort() {
        return port;
    }

}
