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
               
        // Obtém a configuração do arquivo
        ConfigFile config = new ConfigFile();
        config.importarConfiguracoes();
        
        // Cria uma fila de mensagens.
        MessageQueue queue = new MessageQueue();

        // Repassa a fila e as configurações ao controlador
        MessageController controller = new MessageController( 
                queue,
                config.getIp_port(),
                config.getT_token(),
                config.isToken(),
                config.getNickname());
        
        Thread thr_controller = new Thread(controller);
        Thread thr_receiver = new Thread(new MessageReceiver(
                queue, 
                config.getPort(), 
                controller));
        
        thr_controller.start();
        thr_receiver.start();
        
        /* Neste ponto, a thread principal deve ficar aguarando o usuário entrar com o destinatário
         * e a mensagem a ser enviada. Destinatário e mensagem devem ser adicionados na fila de mensagens pendentes.
         * MessageQueue()
         *
         */
        String mensagem = "TESTE sTESTE TESTE";
        queue.AddMessage(mensagem);
        
    }
    
}
