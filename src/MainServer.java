import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerThread svt = new ServerThread(4343);
		svt.start();
	}

}
class ServerThread extends Thread {
	private ServerSocket servsocket = null;
	Socket server=null;
	ArrayList<DataOutputStream> out_data=new ArrayList<DataOutputStream>();
	ArrayList<DataInputStream> in_data=new ArrayList<DataInputStream>();
	public ServerThread(int port) throws IOException {
		// TODO Auto-generated constructor stub
		servsocket = new ServerSocket(port);
		servsocket.setSoTimeout(30000);
		System.out.println("waiting for connection on " + servsocket.getLocalPort());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int count = 0;
		read();
		while (true) {
            try {
                server = servsocket.accept();
                System.out.println("Client"+(++count)+": connect to " + server.getRemoteSocketAddress());
                               
                DataInputStream in = new DataInputStream(server.getInputStream());
                
                in_data.add(in);
                System.out.println("in_list size = "+in_data.size());
                
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out_data.add(out);
                
                System.out.println("out_list size = "+in_data.size());
                out.flush();
                out.writeUTF("Client No. :"+count);
                
                //out.writeUTF("Thank you for connecting to "+ server.getLocalSocketAddress() + "\nGoodbye!");
                //server.close();
            } catch (Exception e) {
                System.out.println(e.toString());
                try {
                    servsocket.close();
                    System.out.println("ServerSocket Closed");
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
	}
	private void read() {
		// TODO Auto-generated method stub
		Thread t2=null;
        //System.out.println("test");
        t2 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.flush();
                    for (int i = 0; i < in_data.size(); i++) {
                        try {
                            if (in_data.get(i).available() > 0) {
                                String str = in_data.get(i).readUTF();
                                if (!str.equalsIgnoreCase("")) {
                                    System.out.println("Server Receive: "+str+" From Client"+(i+1));
                                    for (int j = 0; j < out_data.size(); j++) {
                                        out_data.get(j).flush();
                                        out_data.get(j).writeUTF("Client" + (i+1) + ": " + str);
                                    }
                                }
                            }
                        } catch (IOException ex) {
//                            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
        t2.start();
	}
}
