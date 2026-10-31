import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

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
    static byte[] findfilebyte (byte[]requestbody,String boundary){
        String text = new String(requestbody, StandardCharsets.ISO_8859_1);

        int start = findfilestart(requestbody);
        if(start==-1){
            return new byte[0];
        }
        String ending = "\r\n--"+boundary;
        int end = text.indexOf(ending,start);
        if(end==-1){
            return new byte[0];
        }
        return Arrays.copyOfRange(requestbody,start,end);

    }
    private static int findfilestart (byte[] requestbody){
        String text = new String(requestbody, StandardCharsets.ISO_8859_1);

        int  position = text.indexOf("\r\n\r\n");

        if(position==-1){
            return -1;
        }
        return position+4;

    }
    private static int indexof(byte[]data,byte[]target){
        for(int i=0;i<=data.length-target.length;i++){
            boolean match  = true;
            for(int j=0;j<target.length;j++){
                if(data[i+j]!=target[j]){
                    match=false;
                    break;
                }
            }if(match){
                return i;
            }
        }
        return -1;

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

            if (requestline == null) {
                continue;
            }


            String header = "";
            String statusText = " ";
            int statusCode = 0;
            String body = "";
            String contentType = "text/html; charset=UTF-8";

            int contentLength = 0;
            while ((header = readhttpline(in)) != null && !header.isEmpty()) {
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.substring(header.indexOf(':') + 1).trim());

                }if(header.toLowerCase(Locale.ROOT).startsWith("content-type")){
                    contentType=header.substring(header.indexOf(':')+1).trim();
                }
                System.out.println(header);

            }  String boundary = "";

            for (String part : contentType.split(";")) {
                part = part.trim();

                if (part.startsWith("boundary=")) {
                    boundary = part.substring("boundary=".length());
                    break;
                }
            }if(boundary.isEmpty()){
                System.out.println("Multipart boundary not found");
            }
            String[] parts = requestline.split("\\s+");
            if (parts.length != 3) {
                statusCode = 400;
                statusText = "Bad Request ";
                body = "400 Bad Request";
            } else {
                String method = parts[0];
                String path = parts[1];

                if (method.equals("POST") && path.equals("/upload")) {
                    byte[] requestBody = new byte[contentLength];

                    int totalRead = 0;

                    while (totalRead < contentLength) {
                        int count = in.read(
                                requestBody,
                                totalRead,
                                contentLength - totalRead
                        );

                        if (count == -1) {
                            throw new IOException("Request body ended early");
                        }

                        totalRead += count;
                    }
                    String requestText = new String(
                            requestBody,
                            StandardCharsets.ISO_8859_1
                    );
                    int  filename = requestText.indexOf("filename=\"");

                    if(filename!=-1){
                        int start = filename+"filename=\"".length();
                        int end = requestText.indexOf("\"",start);

                        String fileName =requestText.substring(start,end);
                        System.out.println("File name: " + fileName);

                        int fileDataStart = requestText.indexOf("\r\n\r\n", end) + 4;
                        System.out.println("File data starts at: " + fileDataStart);

                        byte[] fileBytes = findfilebyte(requestBody, boundary);


                        Path uploaddir = Path.of("uploads").toAbsolutePath().normalize();

                        if (!Files.exists(uploaddir)) {
                            Files.createDirectories(uploaddir);
                        }

                        if (!Files.isDirectory(uploaddir)) {
                            throw new IOException("uploads is not a directory");
                        }

                        String SafeName = Path.of(fileName).getFileName().toString();
                        Path savePath  = uploaddir.resolve(SafeName);

                        Files.write(savePath,fileBytes);
                        System.out.println("FileName : "+savePath.toAbsolutePath());

                    }
                    statusCode = 200;
                    statusText = "OK";
                    body = "File request received! Bytes: " + totalRead;

                    System.out.println("Received bytes: " + totalRead);
                    System.out.println("Content-Type: " + contentType);
                    System.out.println("Boundary: " + boundary);

                    System.out.println("Request body received successfully!");

                }
                else if (!method.equals("GET")) {
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
