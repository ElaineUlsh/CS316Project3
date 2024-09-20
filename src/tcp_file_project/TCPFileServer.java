package tcp_file_project;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCPFileServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel listenChannel = ServerSocketChannel.open();
        listenChannel.bind(new InetSocketAddress(3000));

        while(true) { // currently there is no way for the server to gracefully shut down the server
            SocketChannel serveChannel = listenChannel.accept();

            ByteBuffer request = ByteBuffer.allocate(1024);
            int numBytes = serveChannel.read(request);
            request.flip();

            byte[] byteCommand = new byte[1]; // the number of bytes depends of the number of bytes that your command is
            request.get(byteCommand);
            String command = new String(byteCommand);
            System.out.println("\nReceived Command: " + command);

            switch (command) { // mirrors the client side
                case "D": // delete command
                    byte[] byteFileName = new byte[request.remaining()]; // request.remaining() returns the amount of remaining bytes in the buffer
                    String fileName = new String(byteFileName);

                    System.out.println("File to delete: " + fileName);

                    File file = new File("ServerFiles/" + fileName); // for on our computers, a folder inside your project folder
                    boolean success = false;
                    if (file.exists()) {
                        success = file.delete();
                    }

                    String replyCode;
                    if (success) {
                        replyCode = "S";
                    } else {
                        replyCode = "F";
                    }

                    ByteBuffer reply = ByteBuffer.wrap(replyCode.getBytes());
                    serveChannel.write(reply);
                    serveChannel.close();

                    break;

                case "L": // list command
                    System.out.println("Listing Files...");

                    break;

                case "R": // rename command
                    System.out.println("");
                    break;

                case "U": // upload command
                    break;

                case "G": // download command
                    break;

                default: // optional
                    System.out.println("Invalid command!");
                    // send a notification to the client that an invalid command was sent
                    break;
            } // end of switch-case
        } // end of while loop
    }
}
