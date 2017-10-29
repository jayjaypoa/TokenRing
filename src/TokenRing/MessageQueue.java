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
        
        // Verifica o tamanho da mensagem
        if (message.trim().length() > 0){

            // Valida se a mensagem cadastrada está no formato correto
            if (message.indexOf(";") < 0 && message.split(";").length != 2){

                System.out.println("\nERRO >> Mensagem não inserida! Formato da mensagem inválido!\n");

            // Se possuir ao menos 1 ponto-e-vírgula
            } else {

                String comando[] = message.split(";");

                // System.out.println("Comando Posição 0 = " + comando[0]);
                // System.out.println("Comando Posição 1 = " + comando[1]);

                // Se for o comando de envio de mensagem...
                if (comando[0].trim().compareToIgnoreCase("4066") == 0){

                    String mensagem[] = comando[1].split(":");

                    // Se houver 3 campos (origem, destino e mensagem)...
                    if (mensagem.length == 3){

                        // System.out.println("Msg Posição 0 = " + mensagem[0]);
                        // System.out.println("Msg Posição 1 = " + mensagem[1]);
                        // System.out.println("Msg Posição 2 = " + mensagem[2]);

                        // Adiciona a mensagem ao final da fila
                        this.queue.add(message);
                        
                        // Exibe mensagem de inclusão efetuada com sucesso
                        System.out.println("\nMensagem inserida na fila! Tamanho atual da fila : " + this.getTamanho() + " - "
                                + "Mensagem adicionada : " + message + "\n");

                    } else {
                        System.out.println("\nERRO >> Mensagem não inserida! Formato da mensagem inválido!\n");
                    }

                } else {
                    System.out.println("\nERRO >> Mensagem não inserida! Código inválido!\n");
                }

            }
            
        }
        
    }
    
    // Retorna a primeira mensagem da fila
    public String RemoveMessage(){
        
//:: String msg = "Bob:hello world";  // Exemplo de mensagem armazenada na fila.        
        
        // Retire uma mensagem do inicio da fila. 
        // Não se esqueça de garantir que apenas uma thread faça isso por vez.

        // Retira o primeiro elemento da lista. Lista é diminuida.
        String msg = this.queue.remove(0);
       
        return msg;
    }
    
    public Integer getTamanho(){
        return this.queue.size();
    }
    
}
