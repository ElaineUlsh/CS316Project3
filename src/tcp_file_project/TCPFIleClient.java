package tcp_file_project;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class TCPFIleClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Please specify <serverIP> and <serverPort>");
            return;
        }

        int serverPort = Integer.parseInt(args[1]);
        String command;
        do{
            Scanner keyboard = new Scanner(System.in);
            command = keyboard.nextLine().toUpperCase();
            switch (command) {
                case "D": // delete command
                    System.out.println("Please enter the file name:");
                    String fileName = keyboard.nextLine();

                    ByteBuffer request = ByteBuffer.wrap((command + fileName).getBytes()); // byteBuffer that holds the command and the file name

                    SocketChannel channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(request);
                    channel.shutdownOutput();

                    //TODO: recieve server status code and tell user whether the file was successfully deleted.

                    break;

                case "L": // list command
                    break;
                case "R": // rename command
                    break;
                case "U": // upload command
                    break;
                case "G": // download command
                    break;
                default:
                    System.out.println("Invalid Command!");
            } // end of switch-case
        }while (!command.equals("Q")); // Q for quit, while the user doesn't want to quit
    }
}
