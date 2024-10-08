package tcp_file_project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

                    ByteBuffer deleteReplyBuffer = ByteBuffer.allocate(1024);
                    int bytesReadForDeleteReply = deleteChannel.read(deleteReplyBuffer);
                    deleteChannel.close();
                    deleteReplyBuffer.flip();
                    byte[] deleteReply = new byte[bytesReadForDeleteReply];
                    deleteReplyBuffer.get(deleteReply);
                    String deleteServerMessage = new String(deleteReply);
                    System.out.println(deleteServerMessage);

                    break;

                case "L": // list command
                    ByteBuffer listRequest = ByteBuffer.wrap((command).getBytes());

                    SocketChannel listChannel = SocketChannel.open();
                    listChannel.connect(new InetSocketAddress(args[0], serverPort));
                    listChannel.write(listRequest);
                    listChannel.shutdownOutput();

                    String listServerMessage = "List of Files: \n";

                    ByteBuffer listReplyBuffer = ByteBuffer.allocate(1024);
                    int bytesReadForListReply = listReplyBuffer.array().length;
                    while(bytesReadForListReply != -1) {
                        bytesReadForListReply = listChannel.read(listReplyBuffer);
                        listReplyBuffer.flip();

                        while (listReplyBuffer.hasRemaining()) {
                            byte[] listReply = new byte[bytesReadForListReply];
                            listReplyBuffer.get(listReply);

                            String runningList = new String(listReply);
                            listServerMessage += runningList;
                        }

                        listReplyBuffer.clear();
                    }

                    listChannel.close();

                    System.out.println(listServerMessage);

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

                    ByteBuffer renameReplyBuffer = ByteBuffer.allocate(1024);
                    int bytesReadForRenameReply = renameChannel.read(renameReplyBuffer);
                    renameChannel.close();
                    renameReplyBuffer.flip();
                    byte[] renameReply = new byte[bytesReadForRenameReply];
                    renameReplyBuffer.get(renameReply);
                    String renameServerMessage = new String(renameReply);
                    System.out.println(renameServerMessage);

                    break;

                case "U": // upload command
                    System.out.println("Enter the name of the file you want to upload to the sever:");
                    String uploadFileName = keyboard.nextLine();

                    File uploadFile = new File("ClientFiles/" + uploadFileName);
                    String uploadBytes = command + uploadFileName + ";"; //+ uploadFileString;
                    byte[] commandAndStringBytes = uploadBytes.getBytes();
                    byte[] justFileBytes = Files.readAllBytes(uploadFile.toPath());

                    SocketChannel uploadChannel = SocketChannel.open();
                    uploadChannel.connect(new InetSocketAddress(args[0], serverPort));
                    uploadChannel.write(ByteBuffer.wrap(commandAndStringBytes));

                    FileInputStream fis = new FileInputStream("ClientFiles/" + uploadFileName);
                    byte[] data = new byte[1024];
                    int bytesRead = 0;
                    while((bytesRead=fis.read(data)) != -1) {
                        ByteBuffer buffer = ByteBuffer.wrap(data, 0, bytesRead);
                        uploadChannel.write(buffer);
                    }
                    fis.close();

                    uploadChannel.shutdownOutput();

                    ByteBuffer uploadReplyBuffer = ByteBuffer.allocate(1024);
                    int bytesReadForUploadReply = uploadChannel.read(uploadReplyBuffer);
                    uploadChannel.close();
                    uploadReplyBuffer.flip();
                    byte[] uploadReply = new byte[bytesReadForUploadReply];
                    uploadReplyBuffer.get(uploadReply);
                    String uploadServerMessage = new String(uploadReply);
                    System.out.println(uploadServerMessage);

                    break;

                case "G": // download command
                    System.out.println("What file would you like to download?");
                    String downloadFileName = keyboard.nextLine();

                    ByteBuffer downloadRequest = ByteBuffer.wrap((command + downloadFileName).getBytes()); // byteBuffer that holds the command and the file name

                    SocketChannel downloadChannel = SocketChannel.open();
                    downloadChannel.connect(new InetSocketAddress(args[0], serverPort));
                    downloadChannel.write(downloadRequest);
                    downloadChannel.shutdownOutput();

                    ByteBuffer downloadReplyBuffer = ByteBuffer.allocate(1);
                    int bytesReadForDownloadReply = downloadChannel.read(downloadReplyBuffer);
                    downloadReplyBuffer.flip();
                    byte[] downloadReply = new byte[bytesReadForDownloadReply];
                    downloadReplyBuffer.get(downloadReply);
                    String code = new String(downloadReply);

                    if (code.equals("S")) {
                        String path = "ClientFiles/" + downloadFileName;

                        FileOutputStream fos = new FileOutputStream(path);
                        ByteBuffer downloadData = ByteBuffer.allocate(1024);

                        int downloadBytesRead;
                        while((downloadBytesRead = downloadChannel.read(downloadData)) != -1) {
                            downloadData.flip();
                            byte[] a = new byte[downloadBytesRead];
                            downloadData.get(a);
                            fos.write(a);
                            downloadData.clear();
                        }
                        fos.close();
                    }
                    downloadChannel.close();

                    System.out.println(code);

                    break;

                default:
                    if (!command.equals("Q")) {
                        System.out.println("Invalid Command!");
                    }
            } // end of switch-case
        }while (!command.equals("Q")); // Q for quit, while the user doesn't want to quit
    }
}
