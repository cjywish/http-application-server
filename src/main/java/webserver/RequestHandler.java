package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8")); // [home] worked version...
        	
        	String line = br.readLine();
        	if ( line == null){
        		return;
        	}
        	
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	/*String url = HttpRequestUtils.getUrl(line);
        	Map<String, String> headers = new HashMap<String, String>();
        	while(!"".equals(line)){
        		log.debug("header : {}", line);
        		line = br.readLine();
        		String[] headerTokens = line.split(": ");
        		if ( headerTokens.length == 2){
        			headers.put(headerTokens[0], headerTokens[1]);
        		}
        	}
        	
        	log.debug("Content-Length : {}", headers.get("Content-Length"));
        	
        	if (url.startsWith("/user/create")) {
        		String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
        		log.debug("RequestBody : {}", requestBody);
        		Map<String, String> params =
        				HttpRequestUtils.parseQeuryString(requestBody);
        		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        		log.debug("User : {}", user);
        		
        		url = "/index.html";
        	} */
        	
        	String[] tokens = line.split(" ");
        	int contentLength = 0;
        	while(!line.equals("")){
        		log.debug("header : {}", line);
        		line = br.readLine();
        		if (line.contains("Content-Length")){
        			contentLength = getContentLength(line);
        		}
        	}
        	String url = tokens[1];
        	if (("/user/create".equals(url))){
        		String body = IOUtils.readData(br, contentLength);
        		Map<String, String> params =
        				HttpRequestUtils.parseQeuryString(body);
        		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        		log.debug("User : {}", user);
        		DataOutputStream dos = new DataOutputStream(out);
        		response302Header(dos, "/index.html");
        	}
        	
        	DataOutputStream dos = new DataOutputStream(out);

            byte[] body = Files.readAllBytes(new File("./webapp"+ url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private int getContentLength(String line){
    	String[] headerTokens = line.split(":");
    	return Integer.parseInt(headerTokens[1].trim());
    }
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

   private void response302Header(DataOutputStream dos, String url){
	   try {
		   dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
		   dos.writeBytes("Location: " + url + " \r\n" );
		   dos.writeBytes("\r\n");
	   } catch(IOException e){
		   log.error(e.getMessage());
	   }
   }
    
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
