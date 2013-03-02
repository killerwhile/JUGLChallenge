package ch.noisette.jugl.http;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.noisette.jugl.ServerCli;

@Ignore
public class RunTheServerTest {

    protected static final Logger log = LoggerFactory.getLogger(RunTheServerTest.class);

    @Test
    public void test() throws IOException, InterruptedException {

        final ServerCli server = new ServerCli( );
        
        Runtime.getRuntime().removeShutdownHook( new Thread() {

            @Override
            public void run()
            {
                try
                {
                    server.close();
                }
                catch ( IOException e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        });
    }
}
