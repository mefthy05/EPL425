import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
	private static final int MIN_REQUESTS = 300;
	private static final int MAX_USERS = 10;
	private static class User implements Runnable{
		int n;
		String ip;
		public User(int n, String ip){
			this.n = n;
			this.ip = ip;
		}
		
		@Override
		public void run() {
			try {
				int requests = 0;
				long totalRTT = 0;
	            String message;
	            Socket socket = new Socket(ip, 80);
	            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	            BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            message = "HELLO " + socket.getLocalAddress() + " " + socket.getLocalPort() + " " + n + System.lineSeparator();
	            Random ran = new Random();
	            for(int i = 0; i < ran.nextInt(100) + MIN_REQUESTS; i++){
	            	long start = System.currentTimeMillis();
	            	output.writeBytes(message);
	            	output.flush();
	            	server.readLine();
	            	long end = System.currentTimeMillis();
	            	totalRTT += end - start;
	            	requests++;
	            }
	            double avgRTT = totalRTT / requests;
	            System.out.println("RTT = " + avgRTT);
	            socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
    public static void main(String args[]) {
    	Scanner inpt = new Scanner(System.in);
    	System.out.print("Give server IP: ");
    	String ip = inpt.nextLine();
    	ExecutorService userPool = Executors.newFixedThreadPool(10);
    	for (int i = 0; i < MAX_USERS; i++){
    		User user = new User(i, ip);
    		userPool.submit(user);
    	}
        inpt.close();
        userPool.shutdown();
    }
}
