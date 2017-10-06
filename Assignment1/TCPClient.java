import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
　
public class TCPclient {
	static final int MIN_REQUESTS = 300;
	static String ip = "";
	private static class User implements Runnable{
		@Override
		public void run() {
			try {
	            String message, response;
	            Socket socket = new Socket(ip, 80);
	            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	            BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            message = "HELLO " + socket.getLocalAddress() + " " + socket.getLocalPort() + " " /*+ n*/ + System.lineSeparator();
	            System.out.println(message);
	            output.writeBytes(message);
	            response = server.readLine();
	            Random ran = new Random();
	            for(int i = 0; i < ran.nextInt(100) + MIN_REQUESTS; i++){
	            	output.writeBytes(message);
	            	response = server.readLine();
	                System.out.println("[" + new Date() + "] Received: " + response);
	            }
	            output.writeBytes("FIN" + System.lineSeparator());
	            socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	//public static ExecutorService USER_POOL = Executors.newFixedThreadPool(10);
	
    public static void main(String args[]) {
    	Scanner inpt = new Scanner(System.in);
    	System.out.print("Give server IP: ");
    	ip = inpt.nextLine();
    	int n = 0;
        
        inpt.close();
    }
}
　
