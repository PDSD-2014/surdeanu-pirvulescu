package ro.pirvulescusurdeanu.septica.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothService {
	private BluetoothServiceStatus status;
	private final BluetoothAdapter adapter;
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	
    private static final String NAME_INSECURE = "BluetoothChatInsecure"; 
    
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
    public BluetoothService() {
        adapter = BluetoothAdapter.getDefaultAdapter(); 
       	status = BluetoothServiceStatus.NONE;
    }
    
    private synchronized void setState(BluetoothServiceStatus status) {
    	this.status = status;
    }
    
    public void waitUntilConnected() {
    	while (status != BluetoothServiceStatus.CONNECTED) {
    		try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public synchronized void start() {
        setState(BluetoothServiceStatus.LISTEN);
        
        // Acceptarea clientilor se va face pe un nou fir de executie.
        // De asemenea, ascultarea se va realiza utilizand un BluetoothServerSocket.
        if (acceptThread == null) {
        	acceptThread = new AcceptThread();
        	acceptThread.start();
        }
    }
    
    public synchronized void connect(BluetoothDevice device) {
        if (status == BluetoothServiceStatus.CONNECTING && connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
 
        if (connectedThread != null) {
        	connectedThread.cancel();
        	connectedThread = null;
        } 
 
        connectThread = new ConnectThread(device);
        connectThread.start();
        
        setState(BluetoothServiceStatus.CONNECTING);
    }
    
    public synchronized void connected(BluetoothSocket socket,
    								   BluetoothDevice device) {      
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
 
        setState(BluetoothServiceStatus.CONNECTED);
    }
    
    private void connectionLost() {
        start(); 
    }
    
    private void connectionFailed() {
        start();
    }
    
    /**
     * Metoda utilitara prin care se realizeaza trimiterea unui mesaj prin
     * Bluetooth. Mesajul nu trebuie sa se termine cu linie noua. Aceasta din
     * urma este adaugata de aceasta functie.
     * 
     * @param message
     * 		Mesajul ce urmeaza sa fie trimis.
     */
    public void write(String message) {
    	// Inainte de a trimite un mesaj ne asiguram ca suntem conectati.
    	// Altfel asteptam pana vom fi conectati.
    	waitUntilConnected();
    	// In acest moment putem trimite mesajul.
        connectedThread.write(message + "\n");
    }
    
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;

        public AcceptThread() { 
            // Creaza un nou socket pe care se va asculta, pentru viitoare 
            // conexiuni.
            try {
            	// Initializeaza o conexiune nesecurizata. Este un simplu joc,
            	// nu avem de ce sa facem ceva sigur.
            	serverSocket = adapter.listenUsingRfcommWithServiceRecord(NAME_INSECURE, MY_UUID);
            } catch (IOException e) {
                // TODO: Jurnalizare eroare
            }
        } 
 
        @Override
		public  void  run() { 
            BluetoothSocket socket = null;  
 
            // Ascultam pana cand am realizat o conexiune cu un alt dispozitiv
            while (status != BluetoothServiceStatus.CONNECTED) {
                try  { 
                	// Apel blocant care se va intoarce numai dupa ce o conexiune
                	// la nivelul transport s-a realizat sau o exceptie a fost
                	// generata.
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    // TODO: Jurnalizare exceptie
                	// Iesim din loop pentru a nu ramane inghetati cu firul de
                	// executie.
                    break;
                }
 
                // Daca avem un socket valid pentru client
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (status) { 
                        	case LISTEN:
                        	case CONNECTING:
                        		// Se porneste un alt fir de executie
                        		connected(socket, socket.getRemoteDevice()); 
                        		break;  
                        	case NONE:
                        	case CONNECTED: 
                            	// S-a intamplat ceva ciudat. Termina conexiunea.
                        		try {
                        			socket.close();
                        		} catch (IOException e) {
                        			// TODO: Jurnalizare exceptie
                        		}
                        		break;  
                        }
                    }
                }
            }
        }
    } // AcceptThread
    
    private class ConnectThread extends Thread {
        private BluetoothSocket socket; 
        private final BluetoothDevice device; 
 
        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                // TODO: Jurnalizare exceptie
            }
        } 
 
        @Override
		public void run() { 
            try {
                socket.connect(); 
            } catch (IOException e) { 
                try {
                	socket.close(); 
                } catch (IOException e1) {
                    // TODO: Jurnalizare exceptie
                }
                connectionFailed();
                return;
            }
            synchronized (BluetoothService.this) {
                connectThread = null;
            }
            connected(socket, device);
        } 
 
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            	// TODO: Jurnalizare exceptie 
            }
        }
    } // ConnectThread
    
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private BufferedReader inputReader;
        private BufferedWriter outputWriter;
 
        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
 
            try {
            	inputStream = socket.getInputStream();
            	outputStream = socket.getOutputStream();
            	inputReader = new BufferedReader(new InputStreamReader(inputStream));
            	outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            } catch (IOException e) {
                // TODO: Jurnalizare exceptie
            	e.printStackTrace();
            }
        } 
 
        @Override
		public void run() {
            while (true) {
                try {
                	// Avem ceva disponibil prin Bluetooth?
                	if (inputStream.available() > 0) {
                		BluetoothController.getInstance().addMessage(inputReader.readLine());
                	}
                } catch (IOException e) {
                	// Conexiunea a fost pierduta...
                	e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        } 
 
        public void write(String message) {
            try {
                outputWriter.write(message);
                outputWriter.flush();
            } catch (IOException e) {
                // TODO: Jurnalizare exceptie.
            	e.printStackTrace();
            } 
        } 
 
        public void cancel() {
            try  { 
                socket.close();
            } catch (IOException e) {
            	// TODO: Jurnalizare exceptie.
            }
        }
    } // ConnectedThread
}
