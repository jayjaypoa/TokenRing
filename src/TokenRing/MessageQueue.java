package TokenRing;

import java.net.InetAddress;
import java.util.ArrayList;

/* Esta classe deve implementar uma fila de mensagens. Observe que esta fila será
 * acessada por um consumidor (MessageSender) e um produtor (Classe principal, TokenRing).
 * Portanto, implemente controle de acesso (sincronização), para acesso a fila. 
 */

public class MessageQueue {
    /*Implemente uma estrutura de dados para manter uma lista de mensagens em formato string. 
     * Você pode, por exemplo, usar um ArrayList(). 
     * Não se esqueça que em uma fila, o primeiro elemente a entrar será o primeiro
     * a ser removido.
    */
    ArrayList<String> queue;
    
    public MessageQueue(){
        this.queue = new ArrayList<>();
    }
    
    
    // Adiciona a mensagem na fila
    public void AddMessage(String message){
        
        // Adicione a mensagem no final da fila. 
        // Não se esqueça de garantir que apenas uma thread faça isso por vez.
        
        this.queue.add(message);
        
    
    }
    
    public String RemoveMessage(){
        String msg = "Bob:hello world";  // Exemplo de mensagem armazenada na fila.
        
        // Retire uma mensagem do inicio da fila. 
        // Não se esqueça de garantir que apenas uma thread faça isso por vez.

        // Retira o primeiro elemento da lista. Lista é diminuida.
        msg = this.queue.remove(0);
       
        return msg;
    }
    

    
}
