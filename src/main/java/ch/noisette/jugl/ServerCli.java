package ch.noisette.jugl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerCli {

    protected static final Logger log = LoggerFactory.getLogger(ServerCli.class);

    private final ServerMain server;

    public ServerCli() throws IOException {
        this(null);
    }
    
    public ServerCli(Integer port) throws IOException {
        server = new ServerMain(port);
    }

    public void close() throws IOException {
        server.close();
    }


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        
        Integer port = null;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        final ServerCli streamer = new ServerCli(port);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    streamer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    // Can't do much more than that...
                }
            }
        });
    }
}
