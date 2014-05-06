package huadi.com.socketcamera;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class CommandSend extends Thread //傳影像前, 先傳一小段字
{
	private String commandString;
	String serverIP;
	int port;

	public CommandSend(String Command, String ServerIP, int Port)
	{
		commandString = Command;
		serverIP = ServerIP;
		port = Port;
	}

	public void run()
	{
		try //實作 Socket
		{
			Socket socket = new Socket(serverIP, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(commandString);
			out.flush();
		}
		catch (UnknownHostException e)
		{
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		}
	}
	
	
}
