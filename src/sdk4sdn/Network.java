/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdk4sdn;

import com.google.gson.Gson;
import java.nio.charset.Charset;
import java.util.List;
import org.zeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.PluginManager;
import sdk4sdn.openflow13.*;
/**
 *
 * @author aepp
 */
public class Network {
	
	ZMQ.Context PubContext;
	
	ZMQ.Context SubContext;
	
	ZMQ.Socket Publisher;
			
	ZMQ.Socket Subscriber;
	
	String PubDescriptor;
	
	String SubDescriptor;
	
	PluginManager pluginManager;
	
	private static final Logger log = LoggerFactory.getLogger(Network.class);
	
	public Network(String Publisher, String Subscriber, PluginManager pluginManager){
		this.pluginManager = pluginManager;
		this.PubDescriptor = Publisher;
		this.SubDescriptor = Subscriber;
	}
	
	public void CreatePublisher(){
		try {
			this.PubContext = ZMQ.context(1);
			this.Publisher = this.PubContext.socket(ZMQ.PUB);
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
	}
	
	public void CreateSubscriber(){
		try {
			this.SubContext = ZMQ.context(1);
			this.Subscriber = this.SubContext.socket(ZMQ.SUB);
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void Connect(){
		this.RunSubscriber();
	}
	
	private void RunSubscriber(){
		//  Subscribe to zipcode, default is NYC, 10001
		this.Subscriber.connect("ipc:///tmp/controller.ipc");
		this.Subscriber.subscribe("controller".getBytes());
		log.info("Connecting to: ipc:///tmp/"+this.SubDescriptor+".ipc");
		
		while (!Thread.currentThread ().isInterrupted ()) {
			//FIXME: Do something usefull with the topic
			String topic = this.Subscriber.recvStr(Charset.defaultCharset());
			String msg = this.Subscriber.recvStr(Charset.defaultCharset());
			
			Gson gson = new Gson();
			OpenFlow OFPMessage = gson.fromJson(msg, OpenFlow.class);
			
			//Execute all packet in listners 
			//FIXME: move this code somewhere 
			//else and only do this, if it is a packet_in
			List<OFPEventPacketIn> OFPEventPacketIns = pluginManager.getExtensions(OFPEventPacketIn.class);
			for (OFPEventPacketIn PacketIn : OFPEventPacketIns) {
				PacketIn.packetIn(OFPMessage);
			}
		}
	}
		

}
