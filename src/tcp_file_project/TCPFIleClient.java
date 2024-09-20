package tcp_file_project;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
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
                    System.out.println("Please enter the name of the file you'd like to delete:");
                    String fileName = keyboard.nextLine();

                    ByteBuffer deleteRequest = ByteBuffer.wrap((command + fileName).getBytes()); // byteBuffer that holds the command and the file name

                    SocketChannel deleteChannel = SocketChannel.open();
                    deleteChannel.connect(new InetSocketAddress(args[0], serverPort));
                    deleteChannel.write(deleteRequest);
                    deleteChannel.shutdownOutput();

                    //TODO: receive server status code and tell user whether the file was successfully deleted.

                    break;

                case "L": // list command
                    ByteBuffer listRequest = ByteBuffer.wrap((command).getBytes());

                    SocketChannel listChannel = SocketChannel.open();
                    listChannel.connect(new InetSocketAddress(args[0], serverPort));
                    listChannel.write(listRequest);
                    listChannel.shutdownOutput();

                    //TODO: receive server status code and tell user whether the file was successfully listed or list itself.

                    break;

                case "R": // rename command
                    System.out.println("Please enter the name of the file you'd like to rename: ");
                    String originalFileName = keyboard.nextLine();

                    System.out.println("Please enter the new name of the file: ");
                    String newFileName = keyboard.nextLine();

                    ByteBuffer renameRequest = ByteBuffer.wrap((command + originalFileName + ";" + newFileName).getBytes());

                    SocketChannel renameChannel = SocketChannel.open();
                    renameChannel.connect(new InetSocketAddress(args[0], serverPort));
                    renameChannel.write(renameRequest);
                    renameChannel.shutdownOutput();

                    //TODO: receive server status code and tell user whether the file was successfully renamed.

                    break;

                case "U": // upload command
                    System.out.println("Enter the name of the file you want to upload to the sever:");
                    String uploadFileName = keyboard.nextLine();

                    File uploadFile = new File("ClientFiles/" + uploadFileName);

                    byte[] uploadFileBytes = Files.readAllBytes(uploadFile.toPath());

                    ByteBuffer uploadRequest = ByteBuffer.wrap(uploadFileBytes);

                    SocketChannel uploadChannel = SocketChannel.open();
                    uploadChannel.connect(new InetSocketAddress(args[0], serverPort));
                    uploadChannel.write(uploadRequest);
                    uploadChannel.shutdownOutput();

                    //TODO: receive server status code and tell user whether the file was successfully uploaded.

                    break;

                case "G": // download command
                    System.out.println("What file would you like to download?");
                    String downloadFileName = keyboard.nextLine();

                    ByteBuffer downloadRequest = ByteBuffer.wrap((command + downloadFileName).getBytes()); // byteBuffer that holds the command and the file name

                    SocketChannel downloadChannel = SocketChannel.open();
                    downloadChannel.connect(new InetSocketAddress(args[0], serverPort));
                    downloadChannel.write(downloadRequest);
                    downloadChannel.shutdownOutput();

                    //TODO: receive server status code and tell user whether the file was successfully downloaded.

                    break;

                default:
                    System.out.println("Invalid Command!");
            } // end of switch-case
        }while (!command.equals("Q")); // Q for quit, while the user doesn't want to quit
    }
}
