package frc.robot.commands;

import edu.wpi.first.cameraserver.CameraServer;

class Test {
    public static void main(String[] args) {
        System.out.println("Hello world");

        CameraServer server = CameraServer.getInstance();
        server.startAutomaticCapture();
       
    }
}