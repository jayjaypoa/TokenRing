package TokenRing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigFile {
    
    private String ip_port;
    private int port;
    private int t_token = 0;
    private boolean token = false;
    private String nickname;

    public void importarConfiguracoes() {

        // Le arquivo de configuração.
        try { 

            BufferedReader inputFile = new BufferedReader(new FileReader("ring.cfg"));

            // Lê IP e Porta
            ip_port = inputFile.readLine();
            String aux[] = ip_port.split(":");
            port = Integer.parseInt(aux[1]);

            // Lê apelido
            nickname = inputFile.readLine();

            // Lê tempo de espera com o token. Usado para fins de depuração.
            // Em caso de execução normal use valor 0.
            t_token = Integer.parseInt(inputFile.readLine());

            // Lê se a estação possui o token inicial.
            token = Boolean.parseBoolean(inputFile.readLine());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TokenRing.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ioex){
            Logger.getLogger(TokenRing.class.getName()).log(Level.SEVERE, null, ioex);
            return;
        }
        
    } // fim do importarConfiguracoes()

    public String getIp_port() {
        return ip_port;
    }

    public void setIp_port(String ip_port) {
        this.ip_port = ip_port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getT_token() {
        return t_token;
    }

    public void setT_token(int t_token) {
        this.t_token = t_token;
    }

    public boolean isToken() {
        return token;
    }

    public void setToken(boolean token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }    
    
}
