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

//클라이언트
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
	
	public GUIChattingClient() { //생성자 mainPan
		this.setTitle("Chatting Client(ver 2.0)");
		serverConnect();
		chatPanel();
		
		mainPan = new JPanel();
		cardlayout = new CardLayout(); 
		mainPan.setLayout(cardlayout);
		
		mainPan.add(firstPan, "접속창");
		mainPan.add(twoPan, "채팅창");
		cardlayout.show(mainPan, "접속창");
		add(mainPan);
		setBounds(200, 200, 450, 350); //x,y위치 , 가로,세로 길이
		setVisible(true);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		btn_connect.addActionListener(this);
		btn_send.addActionListener(this);
		btn_exit.addActionListener(this);
		txt_input.addActionListener(this);
		
	}//end
	
	public void serverConnect() { //서버접속패널 firstPan
		firstPan = new JPanel();
		JPanel pn=new JPanel();
		JPanel pn0 = new JPanel();
		JPanel pn1 = new JPanel();
		JPanel pn2 = new JPanel();
		
		info = new JLabel("대화명을 입력해 주세요");
		info.setFont(new Font("굴림", Font.BOLD, 15));
		info.setForeground(Color.magenta);
		pn0.add(info);
		
		JLabel lb1 = new JLabel("서버 I P : ");
		lb1.setFont(new Font("굴림", Font.BOLD, 15));
		
		txt_server_ip = new JTextField("127.0.0.1", 15);
		txt_server_ip.setEnabled(false);//비활성화 + 희미하게 처리
		pn1.add(lb1);    pn1.add(txt_server_ip);
		
		JLabel lb2 = new JLabel("대 화 명 : "); 
		lb2.setFont(new Font("굴림", Font.BOLD, 15));
		txt_name = new JTextField("",15);	
		
		pn2.add(lb2);    pn2.add(txt_name);
		pn.add(pn1);     pn.add(pn2);    pn.add(info);
		
		btn_connect = new JButton("서버접속");
		
		firstPan.setBorder(BorderFactory.createTitledBorder("다중채팅화면"));
		firstPan.setLayout(new BorderLayout());
		firstPan.add(pn,"Center");
		firstPan.add(btn_connect,"South");
		
	}//end
	
	public void chatPanel() { //채팅창(대화창) 패널 twoPan
		 twoPan = new JPanel();
		 JPanel pn = new JPanel();
		 txt_list = new  TextArea();
		 txt_input = new JTextField("", 20);
		 btn_send = new JButton("전송");
		 btn_exit= new JButton("종료(q,quit)");
		 
		 pn.setBorder(BorderFactory.createTitledBorder("대화하기"));
		 twoPan.setBorder(BorderFactory.createTitledBorder("채팅내용"));
		 pn.add(txt_input); pn.add(btn_send); pn.add(btn_exit);
		 
		 twoPan.setLayout(new BorderLayout());
		 twoPan.add(txt_list, "Center");
		 twoPan.add(pn, "South");

	}//end
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_connect) {
			cardlayout.show(mainPan, "채팅창");
			this.setTitle("접속자이름 : "+ txt_name.getText());
			ip_txt = txt_server_ip.getText();
			Thread th = new Thread(this);
			th.start();
		}
		if(e.getSource() == btn_send) {send();}
		if(e.getSource() == txt_input) {send();}
		if(e.getSource() == btn_exit) {
			pw.println(txt_name.getText()+"님 퇴장합니다\n");
			pw.flush();
			System.exit(1);
		}
	}
	public void send() {
		String text = txt_input.getText();
		if(text.equals("q")||text.equals("quit")) {
			pw.println(txt_name.getText()+"님 퇴장합니다\n");
			pw.flush();
			System.out.println("채팅클라이언트 유저 프로그램이 종료됩니다");
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
