package huadi.com.socketcamera;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class CommandSend extends Thread //�Ǽv���e, ���Ǥ@�p�q�r
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
		try //��@ Socket
		{
			Socket socket = new Socket(serverIP, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(commandString);
			out.flush();
		}
		catch (UnknownHostException e)
		{
			// TODO �۰ʲ��ͪ� catch �϶�
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO �۰ʲ��ͪ� catch �϶�
			e.printStackTrace();
		}
	}
	
	
}
