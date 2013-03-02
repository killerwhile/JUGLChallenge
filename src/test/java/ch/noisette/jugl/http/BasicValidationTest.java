package ch.noisette.jugl.http;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.noisette.jugl.ServerMain;
import ch.noisette.jugl.http.HttpHandler;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Part;
import com.ning.http.client.Response;
import com.ning.http.multipart.StringPart;

public class BasicValidationTest {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected ServerMain server;

    protected String tempDir;

    @Before
    public void before() throws IOException {

        server = new ServerMain();

    }

    @After
    public void after() throws IOException {

        server.close();

    }
    
    protected int getPort()
    {
        return server.getPort();
    }
    
    
    @Test
    public void getQuery1() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/" + HttpHandler.QUERY1;

        final AsyncHttpClient client = new AsyncHttpClient();
        
        final ListenableFuture<Response> f = client.prepareGet(url).execute();
        
        final Response response = f.get();
        
        client.close();

        Assert.assertEquals(StringUtils.trim( new String(HttpHandler.PAYLOAD_EMAIL.array() ) ), StringUtils.trim( response.getResponseBody() ) ); 
    }
    
    @Test
    public void getQuery2() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/" + HttpHandler.QUERY2;

        final AsyncHttpClient client = new AsyncHttpClient();
        
        final ListenableFuture<Response> f = client.prepareGet(url).execute();
        
        final Response response = f.get();
        
        Assert.assertEquals(StringUtils.trim( new String(HttpHandler.PAYLOAD_YES.array() ) ), StringUtils.trim( response.getResponseBody() ) );
        
        client.close();
    }
    
    @Test
    public void getQueryGeneric() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/?q=Test+Qchose(OUI/NON)";

        final AsyncHttpClient client = new AsyncHttpClient();
        
        final ListenableFuture<Response> f1 = client.prepareGet(url).execute();
        
        final Response response1 = f1.get();
        
        Assert.assertEquals("OUI", StringUtils.trim( response1.getResponseBody() ) ); 
        
        final ListenableFuture<Response> f2 = client.prepareGet(url).execute();
        
        final Response response2 = f2.get();
        
        Assert.assertEquals("NON", StringUtils.trim( response2.getResponseBody() ) ); 
        
        final ListenableFuture<Response> f3 = client.prepareGet(url).execute();
        
        final Response response3 = f3.get();
        
        Assert.assertEquals("DONTKNOW", StringUtils.trim( response3.getResponseBody() ) ); 
        
        final ListenableFuture<Response> f4 = client.prepareGet(url).execute();
        
        final Response response4 = f4.get();
        
        Assert.assertEquals("OUI", StringUtils.trim( response4.getResponseBody() ) ); 
        
        final ListenableFuture<Response> f5 = client.prepareGet(url).execute();
        
        final Response response5 = f5.get();
        
        Assert.assertEquals("NON", StringUtils.trim( response5.getResponseBody() ) ); 

        client.close();
    }
    
    @Test
    public void getQueryPost() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/" + HttpHandler.QUERY2;

        final AsyncHttpClient client = new AsyncHttpClient();
        
        Part part = new StringPart( "name1", "value1" );
        
        final ListenableFuture<Response> f = client.preparePost( url ).addBodyPart( part ).execute();
        
        final Response response = f.get();
        
        Assert.assertEquals(StringUtils.trim( new String(HttpHandler.PAYLOAD_YES.array() ) ), StringUtils.trim( response.getResponseBody() ) );
        
        client.close();
    }
    
    @Test
    public void getQueryMine() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/" + HttpHandler.QUERY3;

        final AsyncHttpClient client = new AsyncHttpClient();
        
        final ListenableFuture<Response> f = client.preparePost( url ).setBody( "4 4\r\n.*.*\r\n....\r\n..**\r\n*...\r\n" ).execute();
        
        final Response response = f.get();
        
        Assert.assertEquals( "1*2*\n1243\n12**\n*222", response.getResponseBody() );
        
        client.close();
    }
    
    
    @Test
    public void getQueryDiet() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/" + HttpHandler.QUERY4;

        final AsyncHttpClient client = new AsyncHttpClient();
        
        final ListenableFuture<Response> f1 = client.preparePost( url ).setBody( 
"[\n{\n\"name\" : \"coca-light\", \"value\" : 1 }, { \"name\" : \"coca\", \"value\" : 138 }, { \"name\" : \"au-travail-a-velo\", \"value\" : -113 }, { \"name\" : \"guitar-hero\", \"value\" : -181 } ]"
            ).execute();
        
        final Response response1 = f1.get();
        
        Assert.assertEquals( "[\"no solution\"]", response1.getResponseBody() ); 
        
        final ListenableFuture<Response> f2 = client.preparePost( url ).setBody( 
"[\n{\n\"name\" : \"coca-light\", \"value\" : 1 }, { \"name\" : \"coca\", \"value\" : 180 }, { \"name\" : \"au-travail-a-velo\", \"value\" : -113 }, { \"name\" : \"guitar-hero\", \"value\" : -181 } ]"
            ).execute();
        
        final Response response2 = f2.get();

        Assert.assertEquals( "[\"guitar-hero\",\"coca-light\",\"coca\"]", response2.getResponseBody() ); 

        client.close();
    }
    
    @Test
    public void getQuery5() throws Throwable {

        final String url = "http://127.0.0.1:" + server.getPort() + "/" + HttpHandler.QUERY5;

        final AsyncHttpClient client = new AsyncHttpClient();
        
        final ListenableFuture<Response> f = client.prepareGet(url).execute();
        
        final Response response = f.get();
        
        Assert.assertEquals(StringUtils.trim( new String(HttpHandler.PAYLOAD_YES.array() ) ), StringUtils.trim( response.getResponseBody() ) );
        
        client.close();
    }
    
}
