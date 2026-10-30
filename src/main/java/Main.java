import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    private static String readhttpline(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b ;

        while((b= in.read())!=-1){
            if(b=='\n'){
                break;
            }if(b!='\r'){
                buffer.write(b);
            }
        }if(b==-1&&buffer.size()==0){
            return null;
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
    public static void main(String[] args)throws IOException {

        //create new server
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server run at  port 8080");
        while (true) {
            Socket client = server.accept();
            System.out.println("client connected...");

            InputStream in = client.getInputStream();
            String requestline = readhttpline(in);
            System.out.println(requestline);









            client.close();
        }
    }
}
