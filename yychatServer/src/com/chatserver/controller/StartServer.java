package com.chatserver.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.yychat.model.Message;
import com.yychat.model.User;

public class StartServer {
	ServerSocket ss;
	Socket s;
	String passWord;
	Message mess;
	String userName;
	
	public static HashMap hmSocket=new HashMap<String,Socket>();//���ͣ�ͨ����
	
	public StartServer(){
		try {
			ss=new ServerSocket(3456);//�������˿ڼ���3456
			System.out.println("�������Ѿ�����������3456�˿�...");
			while(true){//?���߳�����
				s=ss.accept();//�ȴ��ͻ��˽�������
				System.out.println(s);//�������Socket����
				
				//�ֽ������� ��װ�� ����������
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				User user=(User)ois.readObject();//�����û���¼����user
				this.userName=user.getUserName();
				this.passWord=user.getPassWord();
				System.out.println(user.getUserName());
				System.out.println(user.getPassWord());
				//ע�����û�����7���ڷ�������������û���ע��
				if(user.getuserMessageType().equals("USER-REGISTER")){
					//ע�����û�����8����ע���û����в�ѯ
					//seekSuccessΪtrue����Ϊ����ͬ���û���Ϊfalse��������ͬ���û�
					boolean seekSuccess=YychatDbUtil.seekUser(userName);
					if(seekSuccess){
						//����ע��ʧ�ܵ�message
						Message mess=new Message();
						mess.setSender("Server");
						mess.setReceiver(userName);
						mess.setMessageType(Message.message_RegisterFailure);
					}else{
						//ע�����û�����9�����û��ͬ���û��������û�д�뵽user���У�������ע��ɹ���message����
						YychatDbUtil.addUser(userName,passWord);
						mess.setMessageType(Message.message_RegisterSuccess);	
					}
					sendMessage(s,mess);
					s.close();//ע�������Ӧ�ùرշ������˵�socket����
				}

				if(user.getuserMessageType().equals("USER-LOGIN")){
				
				boolean loginSuccess=YychatDbUtil.loginValidate(userName,passWord);
				
                //Server����֤�����Ƿ�123456��
				Message mess=new Message();
				mess.setSender("Server");
				mess.setReceiver(user.getUserName());
				//if(user.getPassWord().equals("123456")){//������"==",����Ƚ�
				if(loginSuccess){//������"==",����Ƚ�
					//��Ϣ���ݣ�����һ��Message����				
					mess.setMessageType(Message.message_LoginSuccess);//��֤ͨ��
					String friendString=YychatDbUtil.getFriendString(userName);
					
					mess.setContent(friendString);//���������Ϣ��mess�����content��Ա
					System.out.println(userName+"��ȫ�����ѣ�"+friendString);
				}
				else{				
					mess.setMessageType(Message.message_LoginFailure);//��֤��ͨ��	
				}				
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(mess);
				
				//if(user.getPassWord().equals("123456")){
				if(loginSuccess){
					mess.setMessageType(Message.message_NewOnlineFriend);
					mess.setSender("Server");
					mess.setContent(this.userName);
					Set friendSet=hmSocket.keySet();
					Iterator it=friendSet.iterator();
					String friendName;
					while(it.hasNext()){
						friendName=(String)it.next();
						mess.setReceiver(friendName);
						Socket s1=(Socket)hmSocket.get(friendName);
						oos=new ObjectOutputStream(s1.getOutputStream());
						oos.writeObject(mess);
					}
					//����ÿһ���û���Ӧ��Socket
					hmSocket.put(userName,s);
					System.out.println("�����û���Socket"+userName+s);
					//��ν��տͻ��˵�������Ϣ����һ���߳�������������Ϣ
					new ServerReceiverThread(s,hmSocket).start();//�����̣߳������߳̾���
					System.out.println("�����̳߳ɹ�");
				}	
			 }			
			}
			
		} catch (IOException | ClassNotFoundException e) {			
			e.printStackTrace();
		}
	}
	public void sendMessage(Socket s,Message mess) throws IOException{
	ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
	oos.writeObject(mess);
   }
}
