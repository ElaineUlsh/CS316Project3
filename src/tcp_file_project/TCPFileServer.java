package tcp_file_project;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.util.Arrays;

public class TCPFileServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel listenChannel = ServerSocketChannel.open();
        listenChannel.bind(new InetSocketAddress(3000));

        while(true) { // currently there is no way for the server to gracefully shut down the server
            SocketChannel serveChannel = listenChannel.accept();

            ByteBuffer request = ByteBuffer.allocate(1024);
            // int numBytes = serveChannel.read(request);
            request.flip();

            byte[] byteCommand = new byte[1]; // the number of bytes depends on the number of bytes that your command is
            request.get(byteCommand);
            String command = new String(byteCommand);
            System.out.println("\nReceived Command: " + command);

            byte semicolon = 0x3B; // hex code of semicolon

            switch (command) { // mirrors the client side
                case "D": // delete command
                    byte[] byteFileName = new byte[request.remaining()]; // request.remaining() returns the amount of remaining bytes in the buffer
                    request.get(byteFileName);
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

                    File folder = new File("ServerFiles/"); // folder object
                    File[] fileNames = folder.listFiles();

                    String listReplyCode = "";

                    if (fileNames.length >= 1) {
                        for (int i = 0; i < fileNames.length; i++) {
                            listReplyCode += fileNames[i].getName() + "\n";
                        }

                        listReplyCode += "S";
                    } else if (fileNames.length == 0) {
                        listReplyCode = "There are no files";
                    } else {
                        listReplyCode = "F";
                    }

                    System.out.println(listReplyCode);

                    ByteBuffer listReply = ByteBuffer.wrap(listReplyCode.getBytes());
                    serveChannel.write(listReply);
                    serveChannel.close();

                    break;

                case "R": // rename command
                    byte[] remainingBytes = new byte[request.remaining()];
                    request.get(remainingBytes);
                    byte[] originalFileNameBytes = new byte[1024];
                    byte[] newFileNameBytes = new byte[1024];

                    for (int i = 0; i < remainingBytes.length; i++) { // separating the new and original file names
                        if (remainingBytes[i] == semicolon) {
                            originalFileNameBytes = Arrays.copyOfRange(remainingBytes, 0, i);
                            newFileNameBytes = Arrays.copyOfRange(remainingBytes, i+1, remainingBytes.length);
                        }
                    }
                    String originalFileName = new String (originalFileNameBytes);
                    String newFileName = new String(newFileNameBytes);

                    System.out.println("File to Rename: " + originalFileName + "\nNew Name: " + newFileName);

                    File originalFile = new File("ServerFiles/" + originalFileName);
                    File newFile = new File("ServerFiles/" + newFileName);

                    boolean renamedSuccess = false;
                    if (originalFile.exists()) {
                        renamedSuccess = originalFile.renameTo(newFile);
                    }

                    String renameReplyCode;
                    if (renamedSuccess) {
                        renameReplyCode = "S";
                    } else {
                        renameReplyCode = "F";
                    }

                    ByteBuffer renameReply = ByteBuffer.wrap(renameReplyCode.getBytes());
                    serveChannel.write(renameReply);
                    serveChannel.close();

                    break;

                case "U": // upload command
                    byte[] uploadBytes = new byte[request.remaining()];
                    request.get(uploadBytes);

                    byte[] uploadFileNameBytes = new byte[1024];
                    byte[] newFileBytes = new byte[1024];

                    for (int i = 0; i < uploadBytes.length; i++) { // separating the new and original file names
                        if (uploadBytes[i] == semicolon) {
                            uploadFileNameBytes = Arrays.copyOfRange(uploadBytes, 0, i);
                            newFileBytes = Arrays.copyOfRange(uploadBytes, i+1, uploadBytes.length);
                        }
                    }
                    String uploadFileName = new String (uploadFileNameBytes);

                    System.out.println("File being uploaded: " + uploadFileName);

                    String path = "ServerFiles/" + uploadFileName;

                    FileOutputStream uploadFile = new FileOutputStream(path);
                    uploadFile.write(newFileBytes);

                    File uploadedFile = new File(path);

                    String uploadReplyCode = "F";
                    if (uploadedFile.exists()) {
                        uploadReplyCode = "S";
                    }

                    ByteBuffer uploadReply = ByteBuffer.wrap(uploadReplyCode.getBytes());
                    serveChannel.write(uploadReply);
                    serveChannel.close();

                    break;

                case "G": // download command
                    byte[] downloadByteFileName = new byte[request.remaining()]; // request.remaining() returns the amount of remaining bytes in the buffer
                    request.get(downloadByteFileName);
                    String downloadFileName = new String(downloadByteFileName);

                    System.out.println("File to delete: " + downloadFileName);

                    File downloadFile = new File("ServerFiles/" + downloadFileName); // for on our computers, a folder inside your project folder
                    byte[] downloadReplyCode = new byte[1024];
                    if (downloadFile.exists()) {
                        downloadReplyCode = Files.readAllBytes(downloadFile.toPath());
                    }

                    ByteBuffer downloadReply = ByteBuffer.wrap(downloadReplyCode);
                    serveChannel.write(downloadReply);
                    serveChannel.close();

                    break;

                default: // optional
                    String invalid = "Invalid Command!";

                    System.out.println(invalid);

                    ByteBuffer defaultReply = ByteBuffer.wrap(invalid.getBytes());
                    serveChannel.write(defaultReply);
                    serveChannel.close();

                    break;
            } // end of switch-case
        } // end of while loop
    }
}
