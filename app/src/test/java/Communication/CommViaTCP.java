package Communication;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import DataPackage.IPackage;
import DataPackage.PackageDimmer;
import DataPackage.PackageElectricRelay;
import DataPackage.PackageRoomStatus;
import Property.INewDataEvent;
import Property.Room;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by xiaodongtao on 17/3/28.
 */

public class CommViaTCP {
    //Properties
    private InputStream reader;
    private OutputStream writer;
    private Socket socket;
    private ConcurrentLinkedQueue<byte[]> unhandled;
    private Object objLock = new Object();
    INewDataEvent Caller;

    public CommViaTCP(INewDataEvent caller) {
        this.Caller = caller;
    }

    public int Connect2(String ipaddress, int port) {
        int rv = -1;
        try {
            this.socket = new Socket(ipaddress, port);
            this.writer = this.socket.getOutputStream();
            this.reader = this.socket.getInputStream();
            // start threads for read and write data.
            ThreadPreprocess tpp = new ThreadPreprocess();
            new Thread(tpp).start();
            ThreadReading tr = new ThreadReading();
            new Thread(tr).start();
            rv = 0;
        } catch (IOException e) {
            e.printStackTrace();
            rv = -1;
        }
        return rv;
    }

    public int Disconnect() {
        int rv = -1;
        try {
            this.socket.close();
            rv = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rv;
    }

    public void Send(byte[] data) throws IOException {
        this.writer.write(data);
    }

    public boolean IsConnected() {
        if (this.socket == null) {
            return false;
        } else {
            return this.socket.isConnected();
        }
    }

    public class ThreadPreprocess implements Runnable {
        public void run() {
            //byte[] buffer = new byte[8192];
            //int pTail = 0;
            while (true) {
                synchronized (objLock) {
                    try {
                        objLock.wait();
                        while (unhandled.size() > 0) {
                            byte[] buffer = unhandled.peek();
                            if (buffer != null) {
                                // 拼接到专用数组
                                //for (int i = 0; i < tmp.length; i++, pTail ++) {
                                //   buffer[pTail] = tmp[i];
                                // 分析内容有效性：1，头正确与否；2，内容是否完整（Data2+3=Total length.）
                                if (buffer[2] > 10) // replace 10 with min length in protocols.
                                {
                                    IPackage pkg;
                                    pkg = PackageDimmer.isKindof(buffer);
                                    if (pkg == null) {
                                        pkg = PackageElectricRelay.isKindof(buffer);
                                        if (pkg == null) {
                                            pkg = PackageRoomStatus.isKindof(buffer);
                                        }
                                    }
                                    if (pkg == null) {
                                        // skip top 1st array from queue
                                    } else {
                                        // how to update datapackage to room???
                                        Caller.newDataPackage(pkg);
                                    }
                                }
                            }
                            unhandled.poll();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class ThreadReading implements Runnable {
        public void run() {
            byte[] buffer = new byte[4096];
            do {
                try {
                    int len = reader.read(buffer);
                    if (len > 0) {
                        byte[] result = new byte[len];
                        for (int i = 0; i < len; i++) {
                            result[i] = buffer[i];
                        }
                        unhandled.add(result);
                        synchronized (objLock) {
                            objLock.notify();
                        }
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            } while (true);
        }
    }
}
