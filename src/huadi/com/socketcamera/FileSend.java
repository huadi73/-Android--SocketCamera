package huadi.com.socketcamera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileSend extends Thread
{
	String userName;
	String id;
	String serverIP;
	int port;
	
	byte byteBuffer[] = new byte[1024];
	private OutputStream outputStream;
	ByteArrayOutputStream bOutputStream;
	
	public FileSend(ByteArrayOutputStream OutStream, String UserName, String Id, String ServerIP, int Port)
	{
		bOutputStream = OutStream;
		userName = UserName;
		id = Id;
		serverIP = ServerIP;
		port = Port;
		
		try
		{
			OutStream.close();
		}
		catch (IOException e)
		{
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			Socket socket = new Socket(serverIP, port);
			outputStream = socket.getOutputStream();
			
			String msg = java.net.URLEncoder.encode("VIDEO|" + userName	+ "|" + id + "|" + System.currentTimeMillis()+ "|", "utf-8"); //傳影像前, 先傳一小段字
			byte[] buffer = msg.getBytes();
			outputStream.write(buffer);
			
			ByteArrayInputStream inputstream = new ByteArrayInputStream(bOutputStream.toByteArray());
			int count;
			while ((count = inputstream.read(byteBuffer)) != -1)
			{
				outputStream.write(byteBuffer, 0, count);
			}
			bOutputStream.flush(); //將 stream 送出
			bOutputStream.close();
			socket.close();
			
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
