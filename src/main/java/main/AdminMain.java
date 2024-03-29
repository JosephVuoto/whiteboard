package main;

import rmi.ClientImpl;
import rmi.IClient;
import rmi.IServer;
import rmi.ServerImpl;
import ui.AdminMainFrame;
import ui.AdminStarterFrame;
import ui.BaseMainFrame;
import util.MD5Util;

import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Yangzhe Xie
 * @date 21/9/19
 */
public class AdminMain {

    /* Main program entry for admin */
    public static void main(String[] args) {
        AdminStarterFrame starterFrame = new AdminStarterFrame();
        starterFrame.setVisible(true);

        starterFrame.setOnAdminConnectClickListener((port, username, password) -> {
            try {
//                InetAddress inetAddress = InetAddress.getLocalHost();
                /* Create a registry on server side */
                LocateRegistry.createRegistry(port);
                Registry serverRegistry = LocateRegistry.getRegistry("127.0.0.1", port);
                IServer server = new ServerImpl(password);
                serverRegistry.bind("Server", server);
                System.out.println("Server is ready");

                /* Start a client on admin side and connect to that server above */
                startClient(port, username, password);
                starterFrame.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Start a client
     *
     * @param remotePort server port number
     * @param username   client username
     */
    private static void startClient(int remotePort, String username, String password) {
        try {
            username = username + " (admin)";
            /* This client is on admin side, so instantiate an AdminMainFrame */
            String windowName = String.format("%s-Server %s:%s", username, "127.0.0.1", remotePort);
            BaseMainFrame mainFrame = new AdminMainFrame(username, windowName);
            mainFrame.setResizable(false);

            /* Get a random available local port */
            ServerSocket s = new ServerSocket(0);
            int localPort = s.getLocalPort();
            s.close();

            /* Create a registry as a client and instantiate a IClient object then add it into that registry*/
            LocateRegistry.createRegistry(localPort);
            Registry clientRegistry = LocateRegistry.getRegistry("127.0.0.1", localPort);
            IClient client = new ClientImpl(mainFrame);
            clientRegistry.bind("Client", client);

            /* Connect to the server */
            Registry serverRegistry = LocateRegistry.getRegistry("127.0.0.1", remotePort);
            IServer server = (IServer) serverRegistry.lookup("Server");
            /* Send client information to server. The server can connect to the registry on the client
             * side with these information */
            String pass = MD5Util.md5(username + password);
            server.addUser(new String[]{username, "127.0.0.1", String.valueOf(localPort), "Client", pass});

            mainFrame.setServer(server);
            mainFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
