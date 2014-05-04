package ro.pirvulescusurdeanu.septica.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

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
    private static final UUID MY_UUID_INSECURE = 
        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"); 
	
    public BluetoothService() {
        adapter = BluetoothAdapter.getDefaultAdapter(); 
       	status = BluetoothServiceStatus.NONE;
    }
    
    private synchronized void setState(BluetoothServiceStatus status) {
    	this.status = status;
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
        // Opreste firul de executie pe care se asculta dupa alte conexiuni.
    	// Facem acest lucru pentru ca vom juca mereu in 2.
        if  (acceptThread != null) { 
        	acceptThread.cancel(); 
        	acceptThread = null;  
        }
        
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
    
    public void write(String out) { 
        ConnectedThread r; 
        synchronized (this) { 
            if (status != BluetoothServiceStatus.CONNECTED) {
            	return;
            }
            r = connectedThread; 
        } 
        r.write(out);
    }
    
    private class AcceptThread extends Thread {
        private  final  BluetoothServerSocket serverSocket;

        public AcceptThread() { 
            BluetoothServerSocket temporary = null;  
 
            // Creaza un nou socket pe care se va asculta, pentru viitoare 
            // conexiuni.
            try {
            	// Initializeaza o conexiune nesecurizata. Este un simplu joc,
            	// nu avem de ce sa facem ceva sigur.
                temporary = adapter.listenUsingInsecureRfcommWithServiceRecord( 
                            NAME_INSECURE, MY_UUID_INSECURE);
            } catch (IOException e) {
                // TODO: Jurnalizare eroare 
            }
            
            // Salveaza socketul pe care ascultam intern.
            serverSocket = temporary;
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
        
        /**
         * Intrerupe si anuleaza executia unui fir de executie.
         */
        public void cancel() {  
            try  {
                serverSocket.close();
            } catch  (IOException e) {
                // TODO: Jurnalizare exceptie
            }
        } 
    } // AcceptThread
    
    private class ConnectThread extends Thread {
        private BluetoothSocket socket; 
        private final BluetoothDevice device; 
 
        public ConnectThread(BluetoothDevice device) {
           	this.device = device;
           	
            try {
            	socket = device.createInsecureRfcommSocketToServiceRecord( 
                            MY_UUID_INSECURE);
            } catch (IOException e) {
                // TODO: Jurnalizare exceptie
            }
        } 
 
        @Override
		public  void  run() { 
            // Anuleaza procesul de discovery
            //adapter.cancelDiscovery(); 
 
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
        private BufferedReader inStream;
        private BufferedWriter outStream;
 
        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
 
            try {
            	inputStream = socket.getInputStream();
            	outputStream = socket.getOutputStream();
            	inStream = new BufferedReader(new InputStreamReader(inputStream));
            	outStream = new BufferedWriter(new OutputStreamWriter(outputStream));
            } catch (IOException e) {
                // TODO: Jurnalizare exceptie
            	e.printStackTrace();
            }
        } 
 
        @Override
		public void run() {
            while (true) {
                try  {
                	if (inputStream.available() > 0)
                		System.out.println(inStream.readLine());
                } catch (IOException e) {
                	// Conexiunea a fost pierduta...
                	e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        } 
 
        public void write(String line) {
            try {
                outStream.write(line + "\n");
                outputStream.flush();
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
