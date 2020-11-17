package Preview;

import java.net.URL;

public interface VignetterServer {

    void start() throws VignetteServerException;

    void stop() throws VignetteServerException;

    void loadVignette(String directory, String host, int port)
            throws VignetteServerException;

    URL getVignetteUrl() throws VignetteServerException;
}
