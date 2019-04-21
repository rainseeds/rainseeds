package com.chatserver.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;

import com.yychat.model.Message;

public class ServerReceiverThread extends Thread{//����Ҫ��run()����
	Socket s;
	HashMap hmSocket;
	Message mess;
	String sender;
	ObjectOutputStream oos;
	
	public ServerReceiverThread(Socket s,HashMap hmSocket){
		this.s=s;
		this.hmSocket=hmSocket;		
	}
	
	public void run(){		
		ObjectInputStream ois;
		while(true){
			try {
				//����Message��Ϣ
				ois=new ObjectInputStream(s.getInputStream());
				mess=(Message)ois.readObject();//�����û���������������Ϣ��������10���û���100����
				System.out.println("�ȴ��û�����������Ϣ");
				System.out.println(mess.getSender()+"��"+mess.getReceiver()+"˵:"+mess.getContent());
				sender=mess.getSender();
				
				//ת��Message��Ϣ
				if(mess.getMessageType().equals(Message.message_Common)){
					Socket s=(Socket)hmSocket.get(mess.getReceiver());
					sendMessage(s,mess);
					System.out.println("������ת������Ϣ"+mess.getSender()+"��"+mess.getReceiver()+"˵:"+mess.getContent());
				}
				
				//��2���裬�������ߺ�����Ϣ���ͻ���
				if(mess.getMessageType().equals(Message.message_RequestOnlineFriend)){
					//����Ҫ�õ����ߵĺ�����Ϣ
					Set friendSet=StartServer.hmSocket.keySet();//�õ��������Ƽ���
					Iterator it=friendSet.iterator();//Iterator��������Ŀ���Ƕ�friendset�����е�Ԫ�ؽ��б���
					String friendName;
					String friendString=" ";
					while(it.hasNext()){//�ж�������һ������
						friendName=(String)it.next();//��ȡ��һ�����ѵ�����
						if(!friendName.equals(mess.getSender()))//�ų��������ں�����������ʾ��
						       friendString=friendName+" "+friendString;//ΪʲôҪ�ӿո�
					}
					System.out.println("ȫ�����ѵ����֣�"+friendString);
					
					//��mess����ֵ
					mess.setContent(friendString);
					mess.setReceiver(sender);
					mess.setSender("Server");
					mess.setMessageType(Message.message_OnlineFriend);
					//�ٷ���
					Socket s1=(Socket)hmSocket.get(sender);
					sendMessage(s1,mess);
				}
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}	
	}

	private void sendMessage(Socket s,Message mess) throws IOException {
		ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
		oos.writeObject(mess);//ת��Message
	}	
}
