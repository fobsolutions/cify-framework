package io.cify.framework.http

import org.apache.http.HttpEntity
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

/**
 * Created by FOB Solutions
 *
 * Responsible for HTTP communications with external services
 */
class HttpConnector {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker("HTTP") as Marker
    private final static String STOP_VIDEO_RECORDING_RESOURCE = "stopVideoRecording"

    /**
     * Request device to stop video recording
     *
     * @param farmUrl - device farm url
     * @param sessionId - device session id
     * @param outputFile - output video file name
     */
    public static void requestDeviceStopRecording(String farmUrl, String sessionId, String outputFile) {
        LOG.debug(MARKER, "Make http communicating to stop device video recording...")
        try {
            URL url = new URL(farmUrl)
            String protocol = url.getProtocol()
            String authority = url.getAuthority()

            if (protocol && authority) {
                String requestStopVideo = "$protocol://$authority/$STOP_VIDEO_RECORDING_RESOURCE/$sessionId"
                CloseableHttpResponse response = httpGet(requestStopVideo)
                if (response?.getStatusLine()?.getStatusCode() == HttpStatus.SC_OK) {
                    saveFileFromResponse(response, outputFile)
                } else if (response) {
                    LOG.debug(MARKER, "http communicating failed cause " + response.getStatusLine().toString()
                            + EntityUtils.toString(response.getEntity()))
                }
            } else {
                LOG.debug(MARKER, "Missing or wrong parameter")
            }
        }
        catch (all) {
            LOG.debug(MARKER, "HttpConnector communicating to stop device video recording failed cause $all.message")
        }
    }

    /**
     * Send HTTP get communicating
     *
     * @param request - http get communicating String
     * @return response - CloseableHttpResponse from device farm
     */
    private static CloseableHttpResponse httpGet(String request) {
        LOG.debug(MARKER, "HttpConnector communicating get $request")
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build()
            HttpGet httpGet = new HttpGet(request);
            CloseableHttpResponse response = httpClient.execute(httpGet)
            return response
        } catch (all) {
            LOG.debug(MARKER, "HttpConnector communicating get failed cause $all.message")
        }
    }

    /**
     * Save response data into file
     *
     * @param response - CloseableHttpResponse from device farm
     * @param filename - file to save
     */
    private static void saveFileFromResponse(CloseableHttpResponse response, String filename) {
        LOG.debug(MARKER, "Save http response data to file $filename")
        try {
            HttpEntity entity = response.getEntity()
            if (entity != null) {
                File file = new File(filename)
                file.getParentFile().mkdirs()
                FileOutputStream outstream = new FileOutputStream(file)
                entity.writeTo(outstream)
            }
            LOG.debug(MARKER, "HttpConnector response code: " + response.getStatusLine().getStatusCode())
        }
        catch (all) {
            LOG.debug(MARKER, "Save http response data to file failed cause $all.message")
        }
    }

}
