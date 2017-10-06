import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class TCPClient {

    public static void main(String args[]) {
        Scanner inpt = new Scanner(System.in);
    	System.out.print("Give server IP: ");
    	String ip = inpt.nextLine();
        try {

            String message, response;
            Socket socket = new Socket(ip, 80);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader server = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            message = reader.readLine() + System.lineSeparator();


            output.writeBytes(message);
            response = server.readLine();

            System.out.println("[" + new Date() + "] Received: " + response);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
