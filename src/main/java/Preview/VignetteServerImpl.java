package Preview;

import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VignetteServerImpl implements VignetterServer {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 6944;
    private HttpServer server = null;
    private StaticHttpHandler handler = null;
    private int port = -1;
    private String host = null;
    private String directoryName = null;
    private Logger logger =  LoggerFactory.getLogger(VignetteServerImpl.class);
    @Override
    public void start() throws VignetteServerException {
        logger.info("{VignetteServerImpl}:: start");
        try {
            server.start();
        } catch (IOException e) {
            logger.error("{VignetteServerImpl}:: start : Failed to start preview server ",e);
            throw new VignetteServerException("Failed to start preview server",e);
        }
    }

    @Override
    public void stop() throws VignetteServerException {
        logger.info("{VignetteServerImpl}:: stop");
        try {
            if(handler!=null){
                handler.removeDocRoot(handler.getDefaultDocRoot());
                handler.destroy();
            }
            if(server!=null){
                server.removeListener("sample-listener");
                server.shutdown();
            }
            handler = null;
            server = null;
        } catch (Exception e) {
            logger.error("{VignetteServerImpl}:: stop : Exception while stopping vignette server ",e);
            throw new VignetteServerException(
                    "Exception while stopping vignette server", e);
        }
    }

    @Override
    public void loadVignette(String directory, String host, int port) throws VignetteServerException {

        this.host = host == null ? DEFAULT_HOST : host;
        this.port = port == -1 ? DEFAULT_PORT : port;

        try {
            if (server != null && server.isStarted()) {
                handler.destroy();
                server.shutdownNow();
            }
            server = new HttpServer();
            NetworkListener networkListener = new NetworkListener(
                    "sample-listener", this.host, this.port);
            server.addListener(networkListener);
            handler = new StaticHttpHandler(directory);
            handler.setFileCacheEnabled(false);
            this.directoryName = directory;
            server.getServerConfiguration().addHttpHandler(handler);
            logger.info("{VignetteServerImpl}:: loadVignette : Starting Server ");
            server.start();
        }
        catch (BindException b){
            logger.error("{VignetteServerImpl}:: stop : Exception while stopping vignette server ",b);
            throw new VignetteServerException("Error starting preview:\nMake sure another instance of Vignette Studio is not also previewing.", b);
        }
        catch (Exception e) {
            logger.error("{VignetteServerImpl}:: stop : Failed to load vignette ",e);
            throw new VignetteServerException("Failed to load vignette", e);
        }
        //set the boolean back to false

    }

    @Override
    public URL getVignetteUrl() throws VignetteServerException {
        try {
            if(directoryName==null){
                logger.error("{VignetteServerImpl}:: getVignetteUrl : directoryName is null ");
                return null;
            }
            return new URL("http", host, port, "/main.html");
        } catch (MalformedURLException e) {
            logger.error("{VignetteServerImpl}:: getVignetteUrl : Could not get URL for vignette server ",e);

            throw new VignetteServerException(
                    "Could not get URL for vignette server", e);
        }
    }
}
