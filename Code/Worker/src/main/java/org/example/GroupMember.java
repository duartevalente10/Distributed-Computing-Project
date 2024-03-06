package org.example;

import spread.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class GroupMember {

    private SpreadConnection connection;

    private final Map<String, SpreadGroup> groupsBelonging=new HashMap<String,SpreadGroup>();
    private final Set<String> processedMessageIds = new HashSet<>();

    private static String CasaOuAlimentar;

    // sinalizador para parar de consumir o broker
    private volatile boolean continueConsuming = true;

    private boolean isLeader = false;
    private SpreadGroup[] members;

    private MessageHandling msgHandling;
    //private AdvancedMessageHandling advancedMsgHandling;
    public GroupMember(String user, String address, int port) {
        // Establish the spread connection.
        try  {
            connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(address), port, user, false, true);
            msgHandling = new MessageHandling(connection,this); connection.add(msgHandling);
            //advancedMsgHandling=new AdvancedMessageHandling(connection); connection.add(advancedMsgHandling);
        }
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

    public void JoinToGrupo(String groupName) throws SpreadException {
        SpreadGroup newGroup=new SpreadGroup();
        newGroup.join(connection, groupName);
        groupsBelonging.put(groupName,newGroup);
    }


    public void close() throws SpreadException {
        // remove listener
        connection.remove(msgHandling);
        //connection.remove(advancedMsgHandling);
        // Disconnect.
        connection.disconnect();
    }

    public void setContinueConsuming(boolean continueConsuming) {
        this.continueConsuming = continueConsuming;
    }

    public boolean shouldContinueConsuming() {
        return continueConsuming;
    }


    public SpreadGroup[] getMembers() {
        return members;
    }

    public SpreadGroup getMember() {
        return connection.getPrivateGroup();
    }

    public void setMembers(SpreadGroup[] members) {
        this.members = members;
    }

    public String getCasaOuAlimentar() {
        return CasaOuAlimentar;
    }

    public void setCasaOuAlimentar(String tipo) {
        this.CasaOuAlimentar = tipo;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    // Método para realizar a eleição
    public void performElection(SpreadGroup[] members) {
        // parar o consumo
        setContinueConsuming(false);

        // encontrar o leader
        SpreadGroup leader = findLeader(members);

        // verificar se este membro é lider
        if (leader.equals(connection.getPrivateGroup())) {
            setLeader(true);
            writeElectionMessage();
            // retomar a consumir
            restartConsumption();
        }else{
            setContinueConsuming(true);
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

    private SpreadGroup findLeader(SpreadGroup[] members) {
        // econtrar o leader que neste caso é o primeiro da lista
        SpreadGroup leader = members[0];
        for (SpreadGroup member : members) {
            if (member.toString().compareTo(leader.toString()) > 0) {
                leader = member;
            }
        }
        return leader;
    }

    // função para rescrever no ficheiro o elegido e o resumo final
    private void writeElectionMessage() {

        String nomeArquivoEleicao = "eleicao" + connection.getPrivateGroup() + ".txt";
        String nomeArquivoResumo = "resumo.txt";

        // buffers de resumo e eleicao
        try (BufferedWriter writerEleicao = new BufferedWriter(new FileWriter(nomeArquivoEleicao, true));
             BufferedWriter writerResumo = new BufferedWriter(new FileWriter(nomeArquivoResumo, true))) {

            // escrever a mensagem de eleição
            writerEleicao.write("Fui eleito como líder!");
            writerEleicao.newLine();
            System.out.println("Mensagem de eleição gravada em " + nomeArquivoEleicao);

            // juntar os resumos dos membros
            for (SpreadGroup member : members) {
                // ler do resumo do membro
                String memberResumo = readResumoFromFile(member);
                writerResumo.write(memberResumo);
                writerResumo.newLine();
            }

            System.out.println("Conteúdo consolidado dos resumos gravado em " + nomeArquivoResumo);

        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
        }
    }

    // funcao para retornar o conteudo do resumo de cada membro
    private String readResumoFromFile(SpreadGroup member) {

        String resumoFilePath = "resumo_" + member + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(resumoFilePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        } catch (IOException e) {
            System.err.println("Erro ao ler o resumo do arquivo: " + e.getMessage());
            return "";
        }
    }

    // função para voltar a trabalhar
    private void restartConsumption() {
        // remover o cargo de leader
        setLeader(false);
        // apagar os resumos ateriores
        clearPreviousResumos();
        // voltar a consumir
        setContinueConsuming(true);
    }

    // apagar os ficheiros de resumo
    private void clearPreviousResumos() {
        File resumoDir = new File(".");
        // para todos os ficheiros na diretoria que começem com resumo_ e sejam .txt
        File[] resumoFiles = resumoDir.listFiles((dir, name) -> name.startsWith("resumo_") && name.endsWith(".txt"));
        if (resumoFiles != null) {
            for (File resumoFile : resumoFiles) {
                if (!resumoFile.delete()) {
                    System.err.println("Erro ao excluir arquivo de resumo anterior: " + resumoFile.getName());
                }
            }
        }
    }
}
