package ro.pirvulescusurdeanu.septica.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothService {
	private BluetoothServiceStatus status;
	private final BluetoothAdapter adapter;
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	
    private static final String NAME_INSECURE = "BluetoothChatInsecure"; 
	
    public BluetoothService() {
        adapter = BluetoothAdapter.getDefaultAdapter(); 
       	status = BluetoothServiceStatus.NONE;
    }
    
    private synchronized void setState(BluetoothServiceStatus status) {
    	Log.i("Test", status.name());
    	this.status = status;
    }
    
    public void waitUntilConnected() {
    	while (status != BluetoothServiceStatus.CONNECTED) {
    		try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
    
    public void write(byte[] buffer) { 
        connectedThread.write(buffer);
    }
    
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() { 
            BluetoothServerSocket temporary = null;  
 
            // Creaza un nou socket pe care se va asculta, pentru viitoare 
            // conexiuni.
            try {
            	// Initializeaza o conexiune nesecurizata. Este un simplu joc,
            	// nu avem de ce sa facem ceva sigur.
                temporary = adapter.listenUsingRfcommWithServiceRecord( 
                            NAME_INSECURE, UUID.randomUUID());
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
                    setState(BluetoothServiceStatus.CONNECTED);   
                } catch (IOException e) {
                    // TODO: Jurnalizare exceptie
                	e.printStackTrace();
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
            	Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            	// Canalul utilizat va fi cel cu numarul 10
            	socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(10));
            } catch (Exception e) {
            	
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
 
        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
 
            try {
            	inputStream = socket.getInputStream();
            	outputStream = socket.getOutputStream();
            } catch (IOException e) {
                // TODO: Jurnalizare exceptie
            	e.printStackTrace();
            }
        } 
 
        @Override
		public void run() {
        	byte[] buffer = new byte[1024];
        	int bytes;
        	
            while (true) {
                try {
                	if (inputStream.available() > 0) {
                		bytes = inputStream.read(buffer);
                		Log.i("Test", new String(buffer));
                	}
                } catch (IOException e) {
                	// Conexiunea a fost pierduta...
                	e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        } 
 
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
                Log.i("Test", "Test1");
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
