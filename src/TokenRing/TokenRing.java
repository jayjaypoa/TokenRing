package TokenRing;

import TokenRing.GUI.FrmFila;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class TokenRing {

    public static void main(String[] args) throws IOException, InterruptedException {
               
        System.out.println("INICIANDO APLICAÇÃO...\n");
        
        // Obtém a configuração do arquivo
        ConfigFile config = new ConfigFile();
        config.importarConfiguracoes();
        
        System.out.println("--> LEITURA DO ARQUIVO DE CONFIGURAÇÕES: ");
        System.out.println("IP = " + config.getIp_port());
        System.out.println("PORTA = " + config.getPort());        
        System.out.println("NICKNAME = " + config.getNickname());        
        if (config.isToken()){
            System.out.println("ESTADO DO TOKEN = TRUE");
        } else {
            System.out.println("ESTADO DO TOKEN = FALSO");
        }
        System.out.println("TEMPO DE ESPERA COM O TOKEN = " + config.getT_token());
        System.out.println("\n--> INICIANDO THREADS DE PROCESSAMENTO:");
        
        
        // Cria uma fila de mensagens.
        MessageQueue queue = new MessageQueue();

        // Repassa a fila e as configurações ao controlador
        MessageController controller = new MessageController( 
                queue,
                config.getIp_port(),
                config.getT_token(),
                config.isToken(),
                config.getNickname());        
        
        // Cria as threads de controle e recepção das mensagens
        Thread thr_controller = new Thread(controller);
        Thread thr_receiver = new Thread(
            new MessageReceiver ( queue, 
                                  config.getPort(),
                                  controller ));
        
        // Inicializa as threads
        thr_controller.start();
        thr_receiver.start();
        
        // Cria Frame para inserção de mensagens
        JFrame frmMensagens = new FrmFila(config.getNickname(), queue);
        frmMensagens.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frmMensagens.pack();
        frmMensagens.setVisible(true);
       
    }
    
}
