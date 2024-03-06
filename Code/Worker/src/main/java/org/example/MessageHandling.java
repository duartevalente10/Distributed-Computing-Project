package org.example;

import spread.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

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

            if (spreadMessage.isMembership()) {
                MembershipInfo info = spreadMessage.getMembershipInfo();
                SpreadGroup[] members = info.getMembers();
                member.setMembers(members);
                System.out.println(Arrays.toString(members));
                SpreadGroup[] teste = member.getMembers();
                System.out.println("Resultadooooo: " + Arrays.toString(members)+"----------");
            }

            if (!spreadMessage.isMembership()) {
                SpreadGroup myPrivateGroup = connection.getPrivateGroup();
                SpreadGroup senderPrivateGroup = spreadMessage.getSender();
                String mensagem = new String(spreadMessage.getData());

                if (!myPrivateGroup.equals(senderPrivateGroup)) {
                    // Verificar se a mensagem instrui os Workers a pararem de consumir
                    if (mensagem.equalsIgnoreCase("ALIMENTAR")) {
                        SpreadGroup[] members = member.getMembers();
                        if (members != null) {
                                member.performElection(members);
                                System.out.println("Recebido comando para parar de consumir mensagens do Tipo ALIMENTAR. Iniciando eleição.");
                        } else {
                            System.out.println("Não foi possível obter a lista de membros para iniciar a eleição.");
                        }
                        //member.setContinueConsuming(false);
                    }else if (mensagem.equalsIgnoreCase("CASA")){
                        SpreadGroup[] members = member.getMembers();
                        if (members != null) {
                                member.performElection(members);
                                System.out.println("Recebido comando para parar de consumir mensagens do Tipo CASA. Iniciando eleição.");
                        } else {
                            System.out.println("Não foi possível obter a lista de membros para iniciar a eleição.");
                        }
                    }else if (mensagem.equalsIgnoreCase("request")) {
                        SpreadMessage msg = new SpreadMessage();
                        msg.setSafe();
                        msg.addGroup(senderPrivateGroup.toString());
                        msg.setData(("Foi recebido um pedido").getBytes());
                        connection.multicast(msg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


