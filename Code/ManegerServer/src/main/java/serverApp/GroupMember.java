package serverApp;

import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

// class para um membro de um grupo spread
public class GroupMember {

    private SpreadConnection connection; // canal de conexao
    private final Map<String, SpreadGroup> groupsBelonging=new HashMap<String,SpreadGroup>(); // grupos a que pretence
    private final Set<String> processedMessageIds = new HashSet<>(); // ids de mensagens
    private MessageHandling msgHandling; // como tratar as mensagens recebidas

    // criar um menbro de um grupo
    public GroupMember(String user, String address, int port) {
        // efetuar a conexao
        try  {
            connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(address), port, user, false, true);
            msgHandling = new MessageHandling(connection,this); connection.add(msgHandling);
        }
        // em caso de erro
        catch(SpreadException e)  {
            System.err.println("There was an error connecting to the daemon.");
            e.printStackTrace();
            System.exit(1);
        }
        catch(UnknownHostException e) {
            System.err.println("Can't find the daemon " + address);
            System.exit(1);
        }
    }

    // função para enviar uma mensagem a um determinado grupo
    public void SendMessage(String groupToSend, String txtMessage) throws SpreadException {
        // criar a mensagem
        SpreadMessage msg = new SpreadMessage();
        msg.setSafe();
        msg.addGroup(groupToSend);
        // enviar a mensagem ao grupo
        msg.setData(txtMessage.getBytes());
        connection.multicast(msg);
    }

    // funcao para enviar mensagem para grupos que o membro possui
    public void RedirectMessageToOverlappingGroups(SpreadGroup senderGroup, String txtMessage) throws SpreadException {
        String messageId = generateMessageId(txtMessage);
        if (!processedMessageIds.contains(messageId)) {
            processedMessageIds.add(messageId);
            for (SpreadGroup group : groupsBelonging.values()) {
                if (!group.equals(senderGroup)) {
                    SpreadMessage msg = new SpreadMessage();
                    msg.setSafe();
                    msg.addGroup(group.toString());
                    msg.setData(txtMessage.getBytes());
                    connection.multicast(msg);
                }
            }
        }
    }

    // gerar um id de mensagem
    private String generateMessageId(String message) {
        // You can implement your own logic to generate a unique identifier based on the message content
        return message.hashCode() + "";
    }

}
