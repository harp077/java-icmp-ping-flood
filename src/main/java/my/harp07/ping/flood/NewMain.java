package my.harp07.ping.flood;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JOptionPane;
import org.apache.commons.validator.routines.InetAddressValidator;

public class NewMain {

    private static boolean pingFloodEnabled = false;
    private static String ip;
    private static long N = 0;
    private static long M = 0;
    private static int time = 100;
    //private static ConcurrentHashMap<Integer, String> chm = new ConcurrentHashMap<>();
    private static long run;
    private static long end;
    //private static long k = 0;
    public static InetAddressValidator validIP = InetAddressValidator.getInstance();
    private static int smp;

    // ВИСНЕТ КОГДА НЕ РАБОТАЕТ DNS И БЛОКИ CATCH ПУСТЫЕ !!!
    public static Boolean pingIp(String ipad, int timeout) {
        try {
            return InetAddress.getByName(ipad).isReachable(timeout);
        } catch (UnknownHostException ex) {
            System.out.println("//_ping_DNS-error_UnknownHostException=" + ex.getMessage());
            //pingEnabled = false;
        } catch (IOException | NullPointerException ex) {
            System.out.println("//_ping_IOException/NullPointerException=" + ex.getMessage());
            //pingEnabled = false;
        }
        return false;
    }
    
    public static void runPingFlood(String ip, int timeout) {
        ConcurrentHashMap<Integer, String> chm = new ConcurrentHashMap<>();
        for (int j = 1; j < 999; j++) {
            chm.put(j, ip);
        }    
        new Thread(() -> {
            while (pingFloodEnabled) {
                chm.values().parallelStream()
                        .forEach(x -> {
                            if (pingIp(x, timeout)) {
                                N++;
                            } else {
                                M++;
                            }
                        });
                //end = System.currentTimeMillis();
                //System.out.println("> "+1000 * (M + N) / (end - run) + " pps");
            }
        }).start();        
    }
    
    public static void ipCheck(String ip) {
        if (!validIP.isValid(ip)) {
            JOptionPane.showMessageDialog(null, "Wrong input IP !", "Input Error !", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("ONLY FOR CHECK NETWORK NODES DEFENCE ! \nNOT FOR ATTACK !");
        smp=Runtime.getRuntime().availableProcessors();
        if (smp > 1) {
            System.out.println("Use parallel calculation: Detected CPU's = "+smp);
        }
        System.out.println("Use small packets - for example: linux=32 byte, win=64 byte, \nFlood-ping running.  Enter 'stop' to stop:");        
    }

    public synchronized static void main(String[] args) {
        ip = JOptionPane.showInputDialog("Input target IP:");
        ipCheck(ip);
        pingFloodEnabled = true;
        Scanner sc = new Scanner(System.in);
        run = System.currentTimeMillis();
        runPingFlood(ip, time);
        while (pingFloodEnabled) {
            if (sc.next().toLowerCase().trim().equals("stop")) {
                pingFloodEnabled = false;
                end = System.currentTimeMillis();
                System.out.println("all ping = " + (N + M));
                System.out.println("all time = " + (end - run) + " msec");
                System.out.println("ping with response = " + N);
                System.out.println("ping with not response = " + M);
                System.out.println("Average Packets Per Second = " + 1000 * (M + N) / (end - run) + " pps");
            }
        }
    }

}
