import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args)throws IOException {

        //create new server
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server run at  port 8080");
        while (true) {
            Socket client = server.accept();
            System.out.println("client connected...");
            client.close();

        }
    }
}
