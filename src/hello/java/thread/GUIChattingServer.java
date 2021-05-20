package hello.java.thread;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;

//����
public class GUIChattingServer extends JFrame implements ActionListener {
	TextArea txt_list; //�ؽ�Ʈ(ä�ó���)����Ʈ
	JButton btn_exit; //�����ư
	ServerSocket ss = null; // ��������
	Vector user; //�ο��� ī��Ʈ ����
	
	
	public GUIChattingServer() {
		super("Chatting - Server");
		
		txt_list = new TextArea();
		btn_exit = new JButton("��������");
		
		txt_list.setBackground(new Color(23, 51, 70));
		btn_exit.setBackground(new Color(34, 34, 34)); 
		btn_exit.setFont(new Font("���� ���", Font.BOLD, 14));
		btn_exit.setForeground(Color.WHITE);
		btn_exit.setBorder(BorderFactory.createLineBorder(new Color(34, 34, 34)));
		btn_exit.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		//������Ʈ �����
		add(btn_exit, "South");
		add(txt_list, "Center");
		setSize(450, 550);
		setVisible(true); //ǥ�ÿ���
		
		//�̺�Ʈó��
		super.setDefaultCloseOperation(EXIT_ON_CLOSE); //�����ư ����
		btn_exit.addActionListener(this);
		user = new Vector(); //����ī��Ʈ ����
		serverStart();
		
	}//������ end

	@Override 
	public void actionPerformed(ActionEvent e) { 
		// btn_exit �����̺�Ʈ ����
		if(e.getSource() == btn_exit) { System.exit(0); }
	}//end

	public void serverStart() { //���ϼ��� ���ֱ�
		final int PORT = 7500; //��Ʈ��ȣ ����� ����
		try {
			ss = new ServerSocket(PORT); //������ ��Ʈ��ȣ�� �������� ��ü
			LocalDateTime localDateTime = LocalDateTime.now();
			DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm E");
			String TodayFormat =" ServerSocket start ... "+localDateTime.format(dateformat)+"����\n";
			txt_list.append(TodayFormat);
			txt_list.setFont(new Font("���� ���", Font.PLAIN, 15));
			txt_list.setForeground(Color.WHITE);
			while(true) {
				Socket sock = ss.accept(); //Ŭ���̾�Ʈ�� ������ ��ٸ�
				String str = sock.getInetAddress().getHostAddress(); //ip �ּ� ��������
				txt_list.append(str);  //ip �ּ� ǥ��
				//����ó���� �ϱ����� Ŭ���̾�Ʈ ��ü ����(��������� Ŭ����)
				ChatHandle ch = new ChatHandle(this, sock);	
				user.addElement(ch);
				ch.start();
			}//while end	
		} catch (Exception e) {
			e.printStackTrace();  
		}
	}//end
	
	public static void main(String[] args) {
		new GUIChattingServer();
	}//end
	
	public void setMsg(String str) {
		txt_list.append(str);
	}//end
	
}//GUIChattingServer class END=================================================

class ChatHandle extends Thread {
	GUIChattingServer server = null;
	Socket sock = null;
	BufferedReader br = null;
	PrintWriter pw = null;
	
	
	public ChatHandle(GUIChattingServer server, Socket sock) {
		this.server = server;
		this.sock = sock;
		try {
			InputStream is = sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			
			OutputStream os = sock.getOutputStream();
			pw = new PrintWriter(new OutputStreamWriter(os));
		} catch (Exception e) { 
			e.printStackTrace(); 
		}//end

	}//ChatHandle ������
	
	public void run() {
		String nickname = null;
		
		try {
			nickname = br.readLine();
			server.setMsg("["+nickname+"]���� ���� �ϼ̽��ϴ�.\n");
			broadcast("["+nickname+"]���� ���� �ϼ̽��ϴ�.\n");
			//��ȭ���� ----------------------------------------
			while(true) {
				try {
					String text = br.readLine();
					server.setMsg(nickname+" : "+ text + "\n"); //text_list�� append() = textarea �� ǥ��
					broadcast(nickname+" : "+ text + "\n"); //��üǥ��
				}catch (Exception e) { 
					e.printStackTrace();
				}
			}//while end
			//��ȭ �� ----------------------------------------
		} catch (Exception e) { 
			e.printStackTrace();
		}finally {
			//synchronized (server.inwon) {
			//server.inwon.remove(this);
			//server.setMsg("["+nickname+"]���� ���� �ϼ̽��ϴ�\n");
			//broadcast("["+nickname+"]���� ���� �ϼ̽��ϴ�\n");
			//}
		}
	}//end
	
	// ��ü�˸� -> ��������ڿ��� �޼��� ���
	private void broadcast(String str) {
		//!���߿� �ѹ�ã�ƺ��� synchronized (server.inwon){����ȭó�� ��}
		int s = server.user.size();
		for(int i=0; i<s; i++) {
			ChatHandle ch = (ChatHandle) server.user.elementAt(i);
			ch.pw.println(str);
			ch.pw.flush();
		}
	}//end
}//ChatHandle class END
