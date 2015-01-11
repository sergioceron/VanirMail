package net.underserver.mail.addons.RemoteView;

import net.underserver.mail.addons.AddonListener;
import net.underserver.mail.model.LocalMessage;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * User: sergio
 * Date: 17/08/12
 * Time: 03:39 PM
 */
public class RemoteView implements AddonListener {
	@Override
	public void onOpenMail(LocalMessage mail) {
		final Object content = mail.getContent();
		new Thread(){
			@Override
			public void run(){
				try {
					Socket server = new Socket("sxdellxps", 8790);
					OutputStream outs = server.getOutputStream();

					DataOutputStream dos = new DataOutputStream(outs);

					dos.writeUTF((String) content);
					//dos.flush();
					server.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
