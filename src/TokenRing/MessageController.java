package TokenRing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageController implements Runnable {
    
    private MessageQueue queue;     // Tabela de roteamento
    private InetAddress IPAddress;
    private int port;
    private Semaphore WaitForMessage;
    private String nickname;
    private int time_token;
    private Boolean token;
    
    public MessageController( MessageQueue q, 
                              String ip_port, 
                              int t_token,
                              Boolean t,
                              String n) throws UnknownHostException {
        
        queue = q;
        
        String aux[] = ip_port.split(":");        
        IPAddress = InetAddress.getByName(aux[0]);
        port = Integer.parseInt(aux[1]);
        
        time_token = t_token;
        
        token = t;
        
        nickname = n;
        
        WaitForMessage = new Semaphore(0);
        
    }
    
    /** ReceiveMessage()
     *  Nesta função, vc deve decidir o que fazer com a mensagem recebida do vizinho da esquerda:
     *      Se for um token, é a sua chance de enviar uma mensagem de sua fila (queue);
     *      Se for uma mensagem de dados e se for para esta estação, apenas a exiba no console, senão, 
     * envie para seu vizinho da direita;
     *       Se for um ACK e se for para você, sua mensagem foi enviada com sucesso, passe o token para
     * o vizinho da direita, senão, repasse o ACK para o seu vizinho da direita.
     */
    public void ReceivedMessage(String msg){
        
        System.out.println("\n---> Mensagem recepcionada: " + msg);
        
        if (msg.trim().length() > 0){

            // Verifica se a mensagem possui ponto-e-vírgula.
            // Caso haja, pode ser mensagem tipo 4066 (mensagem) ou 4067 (ACK).
            if (msg.indexOf(";") > 0){

                String comando[] = msg.split(";");
                
                // System.out.println("Comando recepcionado - Posição 0 = " + comando[0]);
                // System.out.println("Comando recepcionado - Posição 1 = " + comando[1]);
                
                // 4066 - MENSAGEM
                if (comando[0].trim().compareToIgnoreCase("4066") == 0){
                    
                    String mensagem[] = comando[1].split(":");

                    // Se houver 3 campos (origem, destino e mensagem)...
                    if (mensagem.length == 3){

                        String origem = mensagem[0].trim();
                        String destino = mensagem[1].trim();
                        String conteudo = mensagem[2].trim();
                        
                        // System.out.println("Msg recepcionada - Origem = " + origem);
                        // System.out.println("Msg recepcionada - Destino = " + destino);
                        // System.out.println("Msg recepcionada - Conteudo = " + conteudo);
                        
                        // Se o destinatário da mensagem for a minha máquina...
                        if (nickname.compareToIgnoreCase(destino) == 0){
                            
                            System.out.println("     Sou o destinatário desta mensagem!");
                            
                        // Se o destinatário da mensagem não for a minha máquina,
                        // repassa a mensagem adiante
                        } else {
                            
                            System.out.println("     Mensagem NÃO é para mim! Repassando a mensagem adiante...");
                            
                        }
                        
                    }
                    
                // 4067 - ACK
                } else if (comando[0].trim().compareToIgnoreCase("4067") == 0){
                    
                    System.out.println("DESTINATARIO DO ACK ====> " + comando[1]);
                    
                }
                
            // Se não possuir ponto-e-vírgula, pode ser apenas 
            // mensagem do tipo 4060 (token liberado).
            } else {

                

                /*

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
              */

            } // fim do if-else que verifica a mensagem recepcionada
            
        } // fim do if que verifica o tamanho da mensagem recepcionada
        
        // Libera a thread para execução.
        WaitForMessage.release();
        
    }
    
    @Override
    public void run() {
        
        boolean bloquear = true;
        
        DatagramSocket clientSocket = null;
        byte[] sendData;
        
        // Cria socket para envio de mensagem
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        // System.out.println("Teste");
        
        while(true){

            try {
                
                // Espera time_token segundos para o envio do token. 
                // Isso é apenas para depuração, durante execução real faça time_token = 0
                Thread.sleep(time_token*1000);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Possui o TOKEN ativado...
            if(token == true){
                
                // Verifica se possui alguma mensagem na fila de envio
                if (queue.getTamanho() > 0) {  // Possui mensagem a ser enviada...
                    
                    bloquear = false;
                    
                    // Informa que há mensagens na fila
                    System.out.println("Possui mensagens na fila de envio! Quantidade de itens na fila: " + queue.getTamanho());
                    
                    // Obtém a primeira mensagem da fila
                    String item = queue.RemoveMessage();
                    System.out.println("Enviando primeira mensagem da fila: " + item);
                    
                    // Envia a mensagem (primeira da fila) para a máquina da direita
                    try {
                        
                        sendData = item.getBytes();
                        
                        // monta o pacote de envio
                        DatagramPacket sendPacket = new DatagramPacket(
                                sendData, sendData.length, IPAddress, port);         
                        
                        // envia o pacote para a rede
                        clientSocket.send(sendPacket);
                        
                        // Bloqueia a thread, visto que uma mensagem
                        // foi disparada na rede e é necessário aguardar
                        // o retorno deste envio.
                        bloquear = true;
                        
                    } catch (IOException ex) {
                        bloquear = false; // não bloqueia a thread, visto que deu erro no envio da mensagem
                        Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    
                // Caso NÃO possua mensagens a serem enviadas...
                } else {
                    bloquear = false; // não bloqueia a thread, pois não enviou mensagem para a rede
                    System.out.println("Não possui mensagens na fila de envio!");    
                }
  
            } // fim do if do token ativado...
            
            // Se houve um disparo de mensagem,
            // a estação fica aguardando a ação gerada pela função ReceivedMessage().
            if (bloquear){
                try {
                    WaitForMessage.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            System.out.print("\n");
            
        } //fim do while true
    }
}
