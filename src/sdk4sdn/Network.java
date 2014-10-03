/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdk4sdn;

import org.zeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger log = LoggerFactory.getLogger(Network.class);
	
	public Network(String Publisher, String Subscriber){
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
}
