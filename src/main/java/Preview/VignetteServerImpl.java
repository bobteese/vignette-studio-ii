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
    private StaticHttpHandler handler = new StaticHttpHandler();
    private int port = -1;
    private String host = null;
    private String directoryName = null;
    private Logger logger =  LoggerFactory.getLogger(VignetteServerImpl.class);
    @Override
    public void start() throws VignetteServerException {
        try {
            server.start();
        } catch (IOException e) {
            logger.error("{Failed to start preview server}", e);
            e.printStackTrace();
            System.err.println("Failed to start preview server" + e.getMessage());
            throw new VignetteServerException("Failed to start preview server",
                    e);

        }
    }

    @Override
    public void stop() throws VignetteServerException {
        try {
            if(handler!=null)
                handler.destroy();
            if(server!=null)
                server.shutdownNow();

        } catch (Exception e) {
            logger.error("{Exception while stopping vignette server}", e);
            e.printStackTrace();
            System.err.println("Exception while stopping vignette server" + e.getMessage());
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
            HttpHandler handler = new Handler(directory);
            server.addListener(networkListener);
            StaticHttpHandler staticHttpHandler = new StaticHttpHandler(directory);
            this.directoryName = directory;
            server.getServerConfiguration().addHttpHandler(staticHttpHandler);
            server.start();
        }
        catch (BindException b){
            logger.error("{Error starting preview}", b);
            b.printStackTrace();
            System.err.println("Error starting preview" + b.getMessage());
            throw new VignetteServerException("Error starting preview:\nMake sure another instance of Vignette Studio is not also previewing.", b);
        }
        catch (Exception e) {
            logger.error("{Failed to load vignette}", e);
            e.printStackTrace();
            System.err.println("Error starting preview" + e.getMessage());
            throw new VignetteServerException("Failed to load vignette", e);
        }
        //set the boolean back to false

    }

    @Override
    public URL getVignetteUrl() throws VignetteServerException {
        try {
            Path file = Paths.get(directoryName);
            String dir = file.getFileName().toString();
            return new URL("http", host, port, "/main.html");
        } catch (MalformedURLException e) {
            logger.error("{Failed to load vignette}", e);
            e.printStackTrace();
            System.err.println("Failed to load vignette" + e.getMessage());
            throw new VignetteServerException(
                    "Could not get URL for vignette server", e);
        }
    }
    private static class Handler extends StaticHttpHandler {

        private final String directory;
        private static final Pattern rangePat = Pattern.compile("([0-9]+)\\-([0-9]*)");

        public Handler(String directory) {
            super(directory);
            this.directory = directory;
            setFileCacheEnabled(false);
        }

        @Override
        public void service(final Request request, Response response)
                throws Exception {
            String url = request.getRequestURL().toString();
            if (url.toLowerCase().endsWith(".mp4")) {
                response.setContentType("video/mp4");
            } else if (url.toLowerCase().endsWith(".srt")) {
                response.setContentType("application/x-subrip");
            }
            if (url.toLowerCase().endsWith(".mp4")) {
                response.setHeader(Header.AcceptRanges, "bytes");
                String range = request
                        .getHeader(Header.Range);
                if (range != null) {
                    Matcher matchy = rangePat.matcher(range);
                    if(matchy.find()) {
                        int start = Integer.parseInt(matchy.group(1));
                        String file = getRelativeURI(request);
                        String path = directory + File.separator + file;
                        File fileToServe = new File(path);
                        int end = (int) (fileToServe.length() - 1);
                        if (!"".equals(matchy.group(2))) {
                            end = Integer.parseInt(matchy.group(2));
                        }
                        RandomAccessFile raf = new RandomAccessFile(
                                fileToServe, "r");
                        raf.seek(start);
                        byte[] buffer = new byte[end - start + 1];
                        raf.read(buffer);
                        response.setHeader(Header.ContentRange,
                                "bytes " + start + "-" + end + "/"
                                        + fileToServe.length());
                        response.setHeader(Header.ContentLength,
                                Long.toString(end - start + 1));
                        response.setStatus(HttpStatus.PARTIAL_CONTENT_206);
                        response.getOutputBuffer().write(buffer);
                    }
                }
            }
            super.service(request, response);
        }
    }
}
