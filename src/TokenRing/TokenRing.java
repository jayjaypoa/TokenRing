package TokenRing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenRing {

    public static void main(String[] args) throws IOException {
               
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
        
        /* Neste ponto, a thread principal deve ficar aguarando o usuário entrar com o destinatário
         * e a mensagem a ser enviada. Destinatário e mensagem devem ser adicionados na fila de mensagens pendentes.
         * MessageQueue()
         *
         */        
        System.out.println("# INFORME MANSAGEM A SER RETRANSMITIDA (FORMATO ==> 4066;Origem:Destino:Mensagem a ser transmitida) : ");
        
        queue.AddMessage("MENSAGEM 1");
        queue.AddMessage("MENSAGEM 2");
        queue.AddMessage("MENSAGEM 3");
        queue.AddMessage("MENSAGEM 4");
        queue.AddMessage("MENSAGEM 5");
        
        String msg = queue.RemoveMessage();
        System.out.println("MENSAGEM -> " + msg + " - Fila com " + queue.getTamanho() + " posições ocupadas.");
        
        msg = queue.RemoveMessage();
        System.out.println("MENSAGEM -> " + msg + " - Fila com " + queue.getTamanho() + " posições ocupadas.");
        
        msg = queue.RemoveMessage();
        System.out.println("MENSAGEM -> " + msg + " - Fila com " + queue.getTamanho() + " posições ocupadas.");
        
    }
    
}
