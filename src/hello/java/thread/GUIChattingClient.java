package hello.java.thread;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//Ŭ���̾�Ʈ
public class GUIChattingClient extends JFrame implements ActionListener, Runnable{

	JPanel mainPan, firstPan, twoPan;  

	JLabel  info;
	JButton btn_connect, btn_send, btn_exit;
	JTextField txt_server_ip, txt_name, txt_input;
	TextArea txt_list;
	CardLayout cardlayout; 
	
	String ip_txt;                            
	Socket sock;
	
	final int PORT=7500;
	PrintWriter pw=null;                
	BufferedReader br=null;            
	OutputStream os=null;
	
	Color CUSTOM_BLACK = new Color(34,34,34);
	
	public GUIChattingClient() { //������ mainPan
		this.setTitle("Chatting Client(ver 2.0)");
		serverConnect();
		chatPanel();
		
		mainPan = new JPanel();
		cardlayout = new CardLayout(); 
		mainPan.setLayout(cardlayout);
		
		mainPan.add(firstPan, "����â");
		mainPan.add(twoPan, "ä��â");
		cardlayout.show(mainPan, "����â");
		add(mainPan);
		setBounds(200, 200, 450, 350); //x,y��ġ , ����,���� ����
		setVisible(true);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		btn_connect.addActionListener(this);
		btn_send.addActionListener(this);
		btn_exit.addActionListener(this);
		txt_input.addActionListener(this);
		
	}//end
	
	public void serverConnect() { //���������г� firstPan
		firstPan = new JPanel();
		JPanel pn=new JPanel();
		JPanel pn0 = new JPanel();
		JPanel pn1 = new JPanel();
		JPanel pn2 = new JPanel();
		
		info = new JLabel("��ȭ���� �Է��� �ּ���");
		info.setFont(new Font("����", Font.BOLD, 15));
		info.setForeground(Color.magenta);
		pn0.add(info);
		
		JLabel lb1 = new JLabel("���� I P : ");
		lb1.setFont(new Font("����", Font.BOLD, 15));
		
		txt_server_ip = new JTextField("127.0.0.1", 15);
		txt_server_ip.setEnabled(false);//��Ȱ��ȭ + ����ϰ� ó��
		pn1.add(lb1);    pn1.add(txt_server_ip);
		
		JLabel lb2 = new JLabel("�� ȭ �� : "); 
		lb2.setFont(new Font("����", Font.BOLD, 15));
		txt_name = new JTextField("",15);	
		
		pn2.add(lb2);    pn2.add(txt_name);
		pn.add(pn1);     pn.add(pn2);    pn.add(info);
		
		btn_connect = new JButton("��������");
		
		firstPan.setBorder(BorderFactory.createTitledBorder("����ä��ȭ��"));
		firstPan.setLayout(new BorderLayout());
		firstPan.add(pn,"Center");
		firstPan.add(btn_connect,"South");
		
	}//end
	
	public void chatPanel() { //ä��â(��ȭâ) �г� twoPan
		 twoPan = new JPanel();
		 JPanel pn = new JPanel();
		 txt_list = new  TextArea();
		 txt_input = new JTextField("", 20);
		 btn_send = new JButton("����");
		 btn_exit= new JButton("����(q,quit)");
		 
		 pn.setBorder(BorderFactory.createTitledBorder("��ȭ�ϱ�"));
		 twoPan.setBorder(BorderFactory.createTitledBorder("ä�ó���"));
		 pn.add(txt_input); pn.add(btn_send); pn.add(btn_exit);
		 
		 twoPan.setLayout(new BorderLayout());
		 twoPan.add(txt_list, "Center");
		 twoPan.add(pn, "South");

	}//end
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_connect) {
			cardlayout.show(mainPan, "ä��â");
			this.setTitle("�������̸� : "+ txt_name.getText());
			ip_txt = txt_server_ip.getText();
			Thread th = new Thread(this);
			th.start();
		}
		if(e.getSource() == btn_send) {send();}
		if(e.getSource() == txt_input) {send();}
		if(e.getSource() == btn_exit) {
			pw.println(txt_name.getText()+"�� �����մϴ�\n");
			pw.flush();
			System.exit(1);
		}
	}
	public void send() {
		String text = txt_input.getText();
		if(text.equals("q")||text.equals("quit")) {
			pw.println(txt_name.getText()+"�� �����մϴ�\n");
			pw.flush();
			System.out.println("ä��Ŭ���̾�Ʈ ���� ���α׷��� ����˴ϴ�");
			System.exit(1);
		}
		txt_input.setText("");
		txt_input.requestFocus();
		
		pw.println(text);
		pw.flush();
	}//end
	
	
	
	@Override
	public void run() {
		try {
			sock = new Socket(ip_txt, PORT);
			String nickname = txt_name.getText();
			os = sock.getOutputStream();
			pw = new PrintWriter(new OutputStreamWriter(os));
			pw.println(nickname);
			pw.flush();
			InputStream is = sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			String str;
			while(true) {
				str = br.readLine();
				txt_list.append(str + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new GUIChattingClient();
	}//end

	
	
	
	
	
}//GUIChattingClient class END=============================================
