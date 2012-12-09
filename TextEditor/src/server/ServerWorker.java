package server;

import java.io.IOException;
import java.net.Socket;

import javax.swing.SwingWorker;

public class ServerWorker extends SwingWorker<Server, Server> {

	private final Server server;

	ServerWorker(Server server) {
		this.server = server;
	}

	@Override
	protected Server doInBackground() throws Exception {
		while (true) {
			final Socket socket = server.serverSocket.accept();
			server.sockets.add(socket);
			(new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					server.handleConnection(socket);
					return null;
				}

			}).execute();
		}
	}

}
