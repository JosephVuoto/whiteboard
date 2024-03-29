package main;

import data.AllData;
import rmi.ClientImpl;
import rmi.IClient;
import rmi.IServer;
import ui.BaseMainFrame;
import ui.UserMainFrame;
import ui.UserStarterFrame;
import util.MD5Util;

import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Yangzhe Xie
 * @date 21/9/19
 */
public class UserMain {
    /* Main program entry for admin, the logic is similar to AdminMain.java */
    public static void main(String[] args) {
        UserStarterFrame starterFrame = new UserStarterFrame();
        starterFrame.setVisible(true);
        starterFrame.setOnClientConnectClickListener((ip, port, username, password) -> {
            try {
                String windowName = String.format("%s-Client %s:%s", username, ip, port);
                BaseMainFrame mainFrame = new UserMainFrame(username, windowName);
                mainFrame.setResizable(false);
                InetAddress inetAddress = InetAddress.getLocalHost();

                ServerSocket s = new ServerSocket(0);
                int localPort = s.getLocalPort();
                s.close();

                System.setProperty("java.rmi.server.hostname", inetAddress.getHostAddress());
                LocateRegistry.createRegistry(localPort);
                Registry clientRegistry = LocateRegistry.getRegistry(inetAddress.getHostAddress(), localPort);
                IClient client = new ClientImpl(mainFrame);
                clientRegistry.bind("Client", client);

                Registry serverRegistry = LocateRegistry.getRegistry(ip, port);
                IServer server = (IServer) serverRegistry.lookup("Server");
                String pass = MD5Util.md5(username + password);
                AllData allData = server.addUser(new String[]{username, inetAddress.getHostAddress(), String.valueOf(localPort), "Client", pass});

                if (allData.getCode() == -1) {
                    /* Username exists */
                    JOptionPane.showMessageDialog(null,
                            allData.getMsg(), "Message", JOptionPane.ERROR_MESSAGE);
                    mainFrame.dispose();
                } else {
                    mainFrame.setServer(server);
                    mainFrame.initShapes(allData.getShapeList());
                    mainFrame.initMessages(allData.getMessageList());
                    mainFrame.setUserList(allData.getUserList());

                    mainFrame.setVisible(true);
                    starterFrame.dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Server is not available.", "Message", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
