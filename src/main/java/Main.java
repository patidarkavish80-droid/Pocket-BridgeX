import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static String readhttpline(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;

        while ((b = in.read()) != -1) {
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                buffer.write(b);
            }
        }
        if (b == -1 && buffer.size() == 0) {
            return null;
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException {

        //create new server
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server run at http://localhost:8080/");

        while (true) {
            Socket client = server.accept();
            System.out.println("client connected...");

            InputStream in = client.getInputStream();
            String requestline = readhttpline(in);
            System.out.println(requestline);

            String header = "";
            String statusText = " ";
            int statusCode = 0;
            String body = "";
            String contentType = "";

            int contentLength = 0;
            while ((header = readhttpline(in)) != null && !header.isEmpty()) {
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.substring(header.indexOf(':') + 1).trim());

                }
                System.out.println(header);

            }
            byte[] requestBody = new byte[contentLength];
            int totalread = 0;

            while (totalread < contentLength) {
                int count = in.read(
                        requestBody,
                        totalread,
                        contentLength - totalread
                );

                if (count == -1) {
                    break;
                }

                totalread += count;
            }
            System.out.println("Total bytes received: " + totalread);
            String[] parts = requestline.split("\\s+");
            if (parts.length != 3) {
                statusCode = 400;
                statusText = "Bad Request ";
                body = "400 Bad Request";
            } else {
                String method = parts[0];
                String path = parts[1];

                if (!method.equals("GET")) {
                    statusCode = 405;
                    statusText = "Method Not Allowed";
                    body = "405 Method Not Allowed";
                } else if (path.equals("/")) {
                    Path filePath = Path.of("src/main/resources/index.html");

                    if (Files.exists(filePath)) {
                        statusCode = 200;
                        statusText = "OK";
                        contentType = "text/html; charset=UTF-8";
                        body = Files.readString(filePath);
                    } else {
                        statusCode = 404;
                        statusText = "Not Found";
                        body = "404 Not Found";
                    }
                } else if (path.equals("/style.css")) {
                    Path filePath = Path.of("src/main/resources/style.css");

                    if (Files.exists(filePath)) {
                        statusCode = 200;
                        statusText = "OK";
                        contentType = "text/css; charset=UTF-8";
                        body = Files.readString(filePath);
                    } else {
                        statusCode = 404;
                        statusText = "Not Found";
                        body = "404 Not Found";
                    }
                } else {
                    statusCode = 404;
                    statusText = "Not Found";
                    body = "404 Not Found";
                }

                String response =
                        "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                                + "Content-Length:" + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                                + "Content-Type: " + contentType + "\r\n"
                                + "Connection: close\r\n"
                                + "\r\n"
                                + body;

                OutputStream out = client.getOutputStream();
                out.write(response.getBytes(StandardCharsets.UTF_8));
                out.flush();
                client.close();
            }
        }
    }
}
