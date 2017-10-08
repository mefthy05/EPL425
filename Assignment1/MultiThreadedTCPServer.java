import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {
	private static class TCPWorker implements Runnable {
		private Socket client;
		private String clientbuffer;
		private String lowload = null;
		private String highload = null;

		public TCPWorker(Socket client) {
			this.client = client;
			this.clientbuffer = "";
			StringBuilder s1 = new StringBuilder();
			StringBuilder s2 = new StringBuilder();
			if (lowload != null && highload != null)
				return;
			for (int i = 0; i < 300000; i++)
				s1.append('c');
			for (int i = 0; i < 2000000; i++)
				s2.append('c');
			this.lowload = s1.toString();
			this.highload = s2.toString();
		}

		@Override
		public void run() {
			try {
				int totalRequests = 0;
				long start = System.currentTimeMillis();
				Random ran = new Random();
				System.out.println("Client connected with: " + this.client.getInetAddress());
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
				while (client.isConnected() && !client.isClosed()){
					this.clientbuffer = reader.readLine();
					if (this.clientbuffer == null){
						break;
					}
					if (!this.clientbuffer.contains("HELLO"))
						continue;
					if (ran.nextInt(100) < 50)
						output.writeBytes("WELCOME" + this.highload + System.lineSeparator());
					else
						output.writeBytes("WELCOME" + this.highload + System.lineSeparator());
					totalRequests++;
				}
				long end = System.currentTimeMillis();
				long time = end - start;
				double throughput = (double)totalRequests / time;
				System.out.println("Throughput: " + throughput);
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
