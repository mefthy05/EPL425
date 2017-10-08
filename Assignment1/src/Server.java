import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.OperatingSystemMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private static final boolean HIGH_LOAD = false;
	private static class TCPWorker implements Runnable {
		private Socket client;
		private String clientbuffer;
		private static String load = null;

		public TCPWorker(Socket client) {
			this.client = client;
			this.clientbuffer = "";
			StringBuilder s = new StringBuilder();
			if (!HIGH_LOAD)
				for (int i = 0; i < 300000; i++)
					s.append('c');
			else
				for (int i = 0; i < 2000000; i++)
					s.append('c');
			load = s.toString();
		}

		@Override
		public void run() {
			try {
				int totalRequests = 0;
				long start = System.currentTimeMillis();
				System.out.println("Client connected with: " + this.client.getInetAddress());
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
				double CPUtotal =  0;
				double memUtil = 0;
				OperatingSystemMXBean os = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
				while (client.isConnected() && !client.isClosed()){
					this.clientbuffer = reader.readLine();
					if (this.clientbuffer == null){
						break;
					}
					int userID = Integer.parseInt(this.clientbuffer.split(" ")[3]);
					output.writeBytes("WELCOME " + userID + " " + load + System.lineSeparator());
					CPUtotal += os.getSystemLoadAverage();
					memUtil += Runtime.getRuntime().freeMemory() / (double)Runtime.getRuntime().totalMemory();
					totalRequests++;
				}
				long end = System.currentTimeMillis();
				long time = end - start;
				double throughput = (double)totalRequests / time;
				System.out.println("Throughput: " + throughput);
				System.out.println("Average Memory Utilization: " + ((double)memUtil / totalRequests));
				System.out.println("Average CPU Load: " + (CPUtotal / totalRequests));
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

	public static void main(String args[]) {
		try {
			@SuppressWarnings("resource")
			ServerSocket socket = new ServerSocket(80);
			System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());
			while (true) {
				Socket client = socket.accept();
				TCP_WORKER_SERVICE.submit(new TCPWorker(client));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
