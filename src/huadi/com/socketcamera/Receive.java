//package huadi.com.socketcamera;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//import android.util.Log;
//
//public class Receive extends Thread
//{
//	String serverIP;
//	int port;
//	
//	public Receive(String ServerIP, int Port)
//	{
//		serverIP = ServerIP;
//		port = Port;
//	}
//	
//	public void run()
//	{
//		try //��@ Socket
//		{
//			Socket socket = new Socket(serverIP, port);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			String line = reader.readLine();
//			Log.e("initSocket", "line:" + line);
//		}
//		catch (UnknownHostException e)
//		{
//			// TODO �۰ʲ��ͪ� catch �϶�
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			// TODO �۰ʲ��ͪ� catch �϶�
//			e.printStackTrace();
//		}
//	}
//}
