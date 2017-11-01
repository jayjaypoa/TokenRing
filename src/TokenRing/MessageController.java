package TokenRing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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
    private Boolean msgEnviadaComSucesso;
    private Boolean enviouMensagem;
    
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
        
        this.msgEnviadaComSucesso = false;
        
        this.enviouMensagem = false;
        
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
                            
                            // Envia o ACK a máquina de ORIGEM da mensagem
                            String msgACK = "4067;" + origem;
                            
                            // Informa ao usuário que recebeu a mensagem
                            System.out.println("     Sou o destinatário desta mensagem!");
                            System.out.println("     MENSAGEM RECEPCIONADA : " + conteudo);
                            System.out.println("     Irei retornar o ACK com a seguinte mensagem : " + msgACK);
                            
                            // Envia o ACK para a máquina de origem
                            this.enviaMensagem(msgACK);
                            
                        // Se a máquina de destino não existir na rede...
                        } else if (nickname.compareToIgnoreCase(destino) == 0){                                
                            
                            // Informa ao usuário que vai liberar o token
                            System.out.println("     Destinatário da mensagem não localizado nesta rede!");
                            System.out.println("     TOKEN será liberado...!");
                            
                            // libera o token
                            this.token = false;
                            this.enviaMensagem("4060");
                            
                        // Se o destinatário da mensagem não for a minha máquina,
                        // repassa a mensagem adiante
                        } else {
                            
                            // Informa o usuário que vai repassar a mensagem
                            System.out.println("     Mensagem NÃO é para mim! Repassando a mensagem adiante...");
                            
                            // Repassa a mensagem para a máquina da direita
                            this.enviaMensagem(msg);
                            
                        } // fim do if-else do destinatário
                        
                    } // fim do if da validação da mensagem (length == 3)
                    
                // 4067 - ACK
                } else if (comando[0].trim().compareToIgnoreCase("4067") == 0){
                    
                    // Se o destinatário da ACK for a minha máquina...
                    if (nickname.compareToIgnoreCase(comando[1].trim()) == 0){
                    
                        // Marca a flag indicando que recepcionou o retorno 
                        // da mensagem enviada com sucesso.
                        this.msgEnviadaComSucesso = true;
                        
                        // Informa o usuário que obteve o ACK e que irá repassar o TOKEN
                        System.out.println("     ACK recepcionado com sucesso!");
                        System.out.println("     Irei repassar o TOKEN...");
                        
                        // Repassa o Token para a máquina da direita
                        this.token = false;
                        this.enviaMensagem("4060");
                        
                    // Se o destinatário da ACK não for a minha máquina...
                    } else {
                        
                        // Informa o usuário que vai repassar a mensagem
                        System.out.println("     Mensagem NÃO é para mim! Repassando a mensagem adiante...");
                        
                        // Apenas repassa a mensagem original 
                        // para a máquina da direita
                        this.enviaMensagem(msg);
                        
                    }
                    
                }
                
            // Se não possuir ponto-e-vírgula, pode ser apenas 
            // mensagem do tipo 4060 (token liberado).
            } else {
                
                // Recepciona o token              
                token = true;
                
                // Informa ao usuário que recebeu o token
                System.out.println("Token recepcionado!");

                // Redefine os controladores
                this.enviouMensagem = false;
                this.msgEnviadaComSucesso = true;
                
            } // fim do if-else que verifica a mensagem recepcionada
            
        } // fim do if que verifica o tamanho da mensagem recepcionada
        
        try {

            // Espera time_token segundos para o envio do token. 
            // Isso é apenas para depuração, durante execução real faça time_token = 0
            Thread.sleep(time_token*1000);

        } catch (InterruptedException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Libera a thread para execução.
        WaitForMessage.release();
        
    }
    
    @Override
    public void run() {
        
        String ultimaMensagem = "";
        int nroTentativaEnvio = 0;        
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
                
                // Se a mensagem foi enviada e se o ReceivedMessage() ainda não recebeu a ACK,
                // gera uma nova tentativa de envio.
                if (this.enviouMensagem == true && this.msgEnviadaComSucesso == false){
                    nroTentativaEnvio++;
                }
                
                // Se a mensagem foi recepcionada pelo fluxo correto, zera os contadores. 
                // Fluxo correto é passando pelo ReceivedMessage().
                if (this.msgEnviadaComSucesso == true){                    
                    // zera o contador de tentativas de envio
                    nroTentativaEnvio = 0;
                }
                
                // System.out.println("nroTentativaEnvio ------> " + nroTentativaEnvio);
                
                if (nroTentativaEnvio <= 3 
                        && this.enviouMensagem == true
                        && this.msgEnviadaComSucesso == false){
                
                    // Informa ao usuário que nova tentativa de envio está sendo efetuada
                    System.out.println("Tentativa de envio " + nroTentativaEnvio);
                    System.out.println("Reenviando a mesma mensagem : " + ultimaMensagem);
                    
                    // reenvia a mesma mensagem
                    bloquear = false;
                    
                    // Envia a mensagem (primeira da fila) para a máquina da direita
                    try {

                        // repassa a mesma mensagem
                        sendData = ultimaMensagem.getBytes();

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
                    
                // Se já passou a quantidade de tentativa de envios,
                // informa ao usuário que a mensagem se perdeu e 
                // libera o TOKEN ao vizinho da direita.
                } else if (nroTentativaEnvio > 3 
                        && this.enviouMensagem == true
                        && this.msgEnviadaComSucesso == false){
                    
                    // redefine os controladores
                    nroTentativaEnvio = 0;
                    this.enviouMensagem = false;
                    this.msgEnviadaComSucesso = true;
                    
                    // Informa ao usuário que a mensagem se perdeu
                    // e que o TOKEN foi repassado a máquina da direita.
                    System.out.println("Mensagem se perdeu! Irei repassar o TOKEN...");
                        
                    // Repassa o Token para a máquina da direita
                    this.token = false;
                    this.enviaMensagem("4060");
                    
                // ENVIA A MENSAGEM PELA PRIMEIRA VEZ
                } else {
                
                    // ENVIA A MENSAGEM PELA PRIMEIRA VEZ
                    if (this.enviouMensagem == false){

                        // Verifica se possui alguma mensagem na fila de envio
                        if (queue.getTamanho() > 0) {  // Possui mensagem a ser enviada...

                            bloquear = false;

                            // Informa que há mensagens na fila
                            System.out.println("Possui mensagens na fila de envio! Quantidade de itens na fila: " + queue.getTamanho());

                            // Obtém a primeira mensagem da fila
                            String item = queue.RemoveMessage();
                            ultimaMensagem = item;
                            System.out.println("Enviando primeira mensagem da fila: " + item);

                            // Incrementa contador de controle de envio
                            nroTentativaEnvio++;
                            System.out.println("Tentativa de envio " + nroTentativaEnvio);

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
                            
                            // não bloqueia a thread, pois não enviou mensagem para a rede
                            bloquear = false; 
                            
                            // Informa ao usuário que não possui mensagens na fila
                            System.out.println("Não possui mensagens na fila de envio!");    
                            
                            // libera o token para o próximo
                            this.token = false;
                            enviaMensagem("4060");
                            
                        }

                    } // fim do if do enviouMensagem == false
                
                } // fim do if-else do reenvio da mensagem
                
  
            } // fim do if do token ativado...
            
            // Se houve um disparo de mensagem,
            // a estação fica aguardando a ação gerada pela função ReceivedMessage().
            if (bloquear){
                try {                  
                    
                    // Marca que enviou uma mensagem
                    this.enviouMensagem = true;
                    
                    // Deixa a flag de recebimento em false.
                    // Se passar pelo método de recebimento de retorno,
                    // altera esta flag para true.
                    this.msgEnviadaComSucesso = false;
                    
                    // Solicita a pausa da thread por 3 segundos.
                    // Este é o tempo que devemos esperar pelo ACK
                    WaitForMessage.tryAcquire(3, TimeUnit.SECONDS);
                    
                    // WaitForMessage.acquire(); 
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
           
            
            System.out.print("\n");
            
        } //fim do while true
    }
    
    private void enviaMensagem(String mensagem){
        
        DatagramSocket clientSocket = null;
        byte[] sendData = null;
        
        // Cria socket para envio de mensagem de retorno, caso necessário
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        // Envia o ACK pela rede a origem
        try {
            
            sendData = mensagem.getBytes();
            
            // monta o pacote de envio
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, IPAddress, port);
            
            // envia o pacote para a rede
            clientSocket.send(sendPacket);
            
        } catch (IOException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
