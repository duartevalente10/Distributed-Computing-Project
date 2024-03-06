package serverApp;

import spread.BasicMessageListener;
import spread.SpreadConnection;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class MessageHandling implements BasicMessageListener {
    private final SpreadConnection connection;
    private final GroupMember member;

    public MessageHandling(SpreadConnection connection, GroupMember member) {
        this.connection = connection;
        this.member = member;
    }

    @Override
    public void messageReceived(SpreadMessage spreadMessage) {
        try {
            System.out.println("Message Received ThreadID="+Thread.currentThread().getId()+":");
            PrintMessages.MessageDetails(spreadMessage);
            // enviar reply direto para o sender se a mensagem tiver conteudo "request"
            if (!spreadMessage.isMembership()) {
                SpreadGroup myPrivateGroup = connection.getPrivateGroup();
                //System.out.println("myPrivateGroup=" + myPrivateGroup.toString());
                SpreadGroup senderPrivateGroup = spreadMessage.getSender();
                String mensagem = new String(spreadMessage.getData());
                member.RedirectMessageToOverlappingGroups(senderPrivateGroup, mensagem);
                //System.out.println("senderPrivateGroup=" + senderPrivateGroup.toString());
                if (!myPrivateGroup.equals(senderPrivateGroup)) {
                    String txtMsg = new String(spreadMessage.getData());
                    if (txtMsg.equalsIgnoreCase("request")) {
                        SpreadMessage msg = new SpreadMessage();
                        msg.setSafe();
                        msg.addGroup(senderPrivateGroup.toString());
                        msg.setData(("Hello i am " + myPrivateGroup + ":I was received your group request").getBytes());
                        //System.out.println("enviar reply direto");
                        //member.RedirectMessageToOverlappingGroups(senderPrivateGroup.toString(), txtMsg);
                        connection.multicast(msg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

