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

//서버
public class GUIChattingServer extends JFrame implements ActionListener {
	TextArea txt_list; //텍스트(채팅내용)리스트
	JButton btn_exit; //종료버튼
	ServerSocket ss = null; // 서버소켓
	Vector user; //인원수 카운트 벡터
	
	
	public GUIChattingServer() {
		super("Chatting - Server");
		
		txt_list = new TextArea();
		btn_exit = new JButton("서버종료");
		
		txt_list.setBackground(new Color(23, 51, 70));
		btn_exit.setBackground(new Color(34, 34, 34)); 
		btn_exit.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		btn_exit.setForeground(Color.WHITE);
		btn_exit.setBorder(BorderFactory.createLineBorder(new Color(34, 34, 34)));
		btn_exit.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		//컴포넌트 만들기
		add(btn_exit, "South");
		add(txt_list, "Center");
		setSize(450, 550);
		setVisible(true); //표시여부
		
		//이벤트처리
		super.setDefaultCloseOperation(EXIT_ON_CLOSE); //종료버튼 연결
		btn_exit.addActionListener(this);
		user = new Vector(); //유저카운트 저장
		serverStart();
		
	}//생성자 end

	@Override 
	public void actionPerformed(ActionEvent e) { 
		// btn_exit 종료이벤트 연결
		if(e.getSource() == btn_exit) { System.exit(0); }
	}//end

	public void serverStart() { //소켓설정 해주기
		final int PORT = 7500; //포트번호 상수로 선언
		try {
			ss = new ServerSocket(PORT); //수신한 포트번호로 서버소켓 객체
			LocalDateTime localDateTime = LocalDateTime.now();
			DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm E");
			String TodayFormat =" ServerSocket start ... "+localDateTime.format(dateformat)+"요일\n";
			txt_list.append(TodayFormat);
			txt_list.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
			txt_list.setForeground(Color.WHITE);
			while(true) {
				Socket sock = ss.accept(); //클라이언트의 연결을 기다림
				String str = sock.getInetAddress().getHostAddress(); //ip 주소 가져오기
				txt_list.append(str);  //ip 주소 표시
				//병행처리를 하기위한 클라이언트 객체 생성(사용자정의 클래스)
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

	}//ChatHandle 생성자
	
	public void run() {
		String nickname = null;
		
		try {
			nickname = br.readLine();
			server.setMsg("["+nickname+"]님이 입장 하셨습니다.\n");
			broadcast("["+nickname+"]님이 입장 하셨습니다.\n");
			//대화시작 ----------------------------------------
			while(true) {
				try {
					String text = br.readLine();
					server.setMsg(nickname+" : "+ text + "\n"); //text_list에 append() = textarea 에 표시
					broadcast(nickname+" : "+ text + "\n"); //전체표시
				}catch (Exception e) { 
					e.printStackTrace();
				}
			}//while end
			//대화 끝 ----------------------------------------
		} catch (Exception e) { 
			e.printStackTrace();
		}finally {
			//synchronized (server.inwon) {
			//server.inwon.remove(this);
			//server.setMsg("["+nickname+"]님이 퇴장 하셨습니다\n");
			//broadcast("["+nickname+"]님이 퇴장 하셨습니다\n");
			//}
		}
	}//end
	
	// 전체알림 -> 모든접속자에게 메세지 출력
	private void broadcast(String str) {
		//!나중에 한번찾아보기 synchronized (server.inwon){동기화처리 블럭}
		int s = server.user.size();
		for(int i=0; i<s; i++) {
			ChatHandle ch = (ChatHandle) server.user.elementAt(i);
			ch.pw.println(str);
			ch.pw.flush();
		}
	}//end
}//ChatHandle class END
