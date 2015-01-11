package net.underserver.mail.controller;

import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import org.apache.log4j.Logger;

import javax.mail.Message;
import java.io.*;

/**
 * User: sergio
 * Date: 3/08/12
 * Time: 12:54 PM
 */
public class MessageController {
	private static final Logger logger = Logger.getLogger("main");
	private static FolderController folderController = FolderController.getInstance();
	private static MessageController instance;
	
	private MessageController(){ }
	
	public static MessageController getInstance(){
		instance = instance != null ? instance : new MessageController();
		return instance;
	}
	
	public String getEML (LocalFolder localFolder, LocalMessage localMessage){
		Message remoteMessage;
		try {
			remoteMessage = folderController.getRemoteMessage(localFolder, localMessage);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			remoteMessage.writeTo(outStream);
			return new String(outStream.toByteArray());
		} catch (Exception e) {
			logger.error("Error generating EML file", e);
		}
		return "";
	}
}
