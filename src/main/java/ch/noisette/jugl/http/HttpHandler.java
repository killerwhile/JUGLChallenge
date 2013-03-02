package ch.noisette.jugl.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpMethod.POST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.noisette.jugl.knapsack.Knap;
import ch.noisette.jugl.miner.Miner;

/**
 * 
 * @author bperroud
 */
public class HttpHandler extends SimpleChannelUpstreamHandler
{
    protected static final Logger log = LoggerFactory.getLogger(HttpHandler.class);

    public static final String QUERY1 = "?q=Quelle+est+ton+adresse+email";
    public static final ChannelBuffer PAYLOAD_EMAIL = ChannelBuffers.copiedBuffer("benoit@noisette.ch", CharsetUtil.UTF_8);

    public static final String QUERY2 = "?q=Es+tu+abonne+a+la+mailing+list";
    public static final ChannelBuffer PAYLOAD_YES = ChannelBuffers.copiedBuffer("OUI", CharsetUtil.UTF_8);

    public static final String QUERY3 = "minesweeper/resolve";

    public static final String QUERY4 = "diet/resolve";

    public static final String QUERY5 = "?q=Veux+tu+tenter+ta+chance+pour+gagner+un+des+prix";
    
    public static final ChannelBuffer PAYLOAD_DEFAULT = ChannelBuffers.copiedBuffer("DONTKNOW", CharsetUtil.UTF_8);

    private static final String MY_CONTENT_TYPE = "text/plain; charset=UTF-8";
    private static final String MY_SERVER = "NoisetteAtWork2012";
    
    public static String lastQuestion = null;
    public static int lastAnswer = 0;
    
    private JSONParser parser = new JSONParser();
    private Knap knap = new Knap();
    private static final ChannelBuffer PAYLOAD_NO_SOLUTION = ChannelBuffers.copiedBuffer( JSONArray.toJSONString( Arrays.asList( new String[] { "no solution" } ) ), CharsetUtil.UTF_8);
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
    {
        HttpRequest request = (HttpRequest) e.getMessage();
        if (request.getMethod() != GET && request.getMethod() != POST)
        {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }
        
        final Channel ch = e.getChannel();

        // too lazy to use Uri.decode...
        final String query = request.getUri().substring( 1 ).replace( "%20", "+" ).replace( "%28", "(" ).replace( "%2F", "/").replace( "%29", ")" ); // removing heading slash

        log.info( query );
        
        if (request.getMethod() == POST)
        {
            ChannelBuffer content = request.getContent();
            log.warn( content.toString( CharsetUtil.UTF_8 ) );
        }
        
        final ChannelBuffer payload;
        
        if (QUERY4.equals(query))
        {
            final JSONArray data = (JSONArray)parser.parse( request.getContent().toString( CharsetUtil.UTF_8 ) );
            
            final Set<Knap.Sac> items = new HashSet<Knap.Sac>();

            for (ListIterator<JSONObject> it = data.listIterator(); it.hasNext(); )
            {
                JSONObject o = it.next();
                final String name = (String)o.get( "name" );
                int weight = ((Long)o.get( "value" )).intValue();
                items.add( new Knap.Sac(name, weight) );
            }
            
            final List<String> results = knap.solve( items, 0 );
            if (results != null)
            {
                final String jsonArray = JSONArray.toJSONString( results );
                payload = ChannelBuffers.copiedBuffer(jsonArray, CharsetUtil.UTF_8);
            }
            else
            {
                payload = PAYLOAD_NO_SOLUTION;
            }
        }
        else if (QUERY1.equals(query))
        {
            payload = PAYLOAD_EMAIL;
        }
        else if (query.startsWith( QUERY2 ) || query.startsWith( QUERY5 ))
        {
            payload = PAYLOAD_YES;
        }
        else if (query.contains( "(" ) && query.contains( ")" )) 
        {
            
            int pos1 = query.indexOf( '(' );
            int pos2 = query.indexOf( ')' );
            
            String[] pos = query.substring( pos1 + 1, pos2 ).split( "/" );
            
            if (lastQuestion == null || !lastQuestion.equals( query ))
            {
                payload = ChannelBuffers.copiedBuffer( pos[0], CharsetUtil.UTF_8);
                lastAnswer = 1;
                lastQuestion = query;
            }
            else
            {
                if (lastAnswer >= pos.length)
                {
                    lastAnswer = 0;
                    payload = PAYLOAD_DEFAULT;
                }
                else 
                {
                    payload = ChannelBuffers.copiedBuffer( pos[lastAnswer], CharsetUtil.UTF_8);
                    lastAnswer++;
                }
            }
            
        }
        else if (QUERY3.equals(query))
        {
            final String content = request.getContent().toString( CharsetUtil.UTF_8 );
            
            final String[] s = content.split( "\\s+" );
            
            final int rows = Integer.parseInt( s[0] );
            final int columns = Integer.parseInt( s[1] );
            
            final List<String> rowsData = new ArrayList<String>(s.length - 2);
            
            for (int i = 2; i < s.length; i++)
            {
                rowsData.add( s[i] );
            }
            
            final Miner miner = new Miner( rows, columns, rowsData );
            
            payload = ChannelBuffers.copiedBuffer( miner.toString(), CharsetUtil.UTF_8 );
        }
        else
        {
            if (request.getMethod() == POST)
            {
                payload = PAYLOAD_DEFAULT;
            }
            else
            {
                payload = null;
            }
        }
        
        if (payload == null)
        {
            sendError(ctx, BAD_REQUEST);
            return;
        }
        
        final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        setResponseHeaders( response );
        
        response.setContent(payload);
        
        final ChannelFuture cf = ch.write(response);
        
        cf.addListener(ChannelFutureListener.CLOSE);
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
    {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();

        if (cause instanceof TooLongFrameException)
        {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        log.error( "Exception caught", cause );
        
        if (ch.isConnected())
        {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }
    
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
    {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        setResponseHeaders( response );

        response.setContent(ChannelBuffers.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private void setResponseHeaders(final HttpResponse response)
    {
        response.setHeader(CONTENT_TYPE, MY_CONTENT_TYPE);
        response.setHeader(SERVER, MY_SERVER);
    }
}
