package net.viperfish.ticketClient;

import java.awt.EventQueue;
import java.io.IOException;

public class TicketClient {
	private Window currentWindow;
	private ClientWorker w;
	private Thread worker;
	private Thread updater;
	private Thread currentUpdater;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new TicketClient();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TicketClient() {
		currentWindow = new MainWindow(this);
		currentWindow.show();

		w = new ClientWorker();
		worker = new Thread(w);

		updater = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					synchronized (w) {
						try {
							w.wait();
						} catch (InterruptedException e) {
							return;
						}
						currentWindow.updateDisplay();
					}
				}

			}

		});
	}

	public synchronized void setCurrentWindow(Window currentWindow) {
		this.currentWindow = currentWindow;
	}

	public void connect(String ip, String name) throws TicketException {
		try {
			w.connect(ip);
			if (name.length() != 0) {
				w.setName(name);
			}
		} catch (IOException e) {
			throw new TicketException(e.getMessage(), e);
		}
		currentUpdater = new Thread(new CurrentUpdater(ip, w));
		worker.start();
		updater.start();
		currentUpdater.start();
	}

	public void getTicket() throws TicketException {
		try {
			w.getTicket();
		} catch (IOException e) {
			throw new TicketException(e.getMessage(), e);
		}
	}

	public void done() throws TicketException {
		try {
			w.done();
		} catch (IOException e) {
			throw new TicketException(e.getMessage(), e);
		}
	}

}
