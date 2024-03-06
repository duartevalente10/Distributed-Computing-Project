package org.example;

import com.rabbitmq.client.*;
import spread.SpreadException;
import spread.SpreadMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Worker {
    private static final String QUEUE_NAME_ALIMENTAR = "Q_ALIMENTAR";
    private static final String QUEUE_NAME_CASA = "Q_CASA";
    private static int daemonPort=4803;
    private static GroupMember memberName;
    private static final String EXCHANGE_NAME = "ExgResumo";

    //private static String IP_BROKER="34.134.117.104";

    public static void main(String[] args) {

        // pedir o ip do broker
        System.out.print("IP do Broker: ");
        Scanner scanner = new Scanner(System.in);
        String rabbitMQHost = scanner.nextLine();

        // pedir o ip do daemon
        System.out.print("Introduza o IP da VM do daemon: ");
        String daemonIP = scanner.nextLine();

        // entrar no grupo Spread
        try {
            if (args.length > 0 ) {
                daemonIP=args[0];
            }
            Scanner scaninput = new Scanner(System.in);
            String userName = read("MemberApp name? ", scaninput);
            System.out.println("connected to "+daemonIP);
            GroupMember member = new GroupMember(userName, daemonIP, daemonPort);

            //System.out.println("Main ThreadID="+Thread.currentThread().getId());

            String groupName = read("Join to group named? ", scaninput);
            member.JoinToGrupo(groupName);

            memberName = member;

            // config da conexao
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitMQHost);

            // connectar ao broker
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                // criar o consumidor para a fila de produtos alimentares
                //Consumer consumerAlimentar = criarConsumidor(channel, QUEUE_NAME_ALIMENTAR);
                //channel.basicConsume(QUEUE_NAME_ALIMENTAR, true, consumerAlimentar);

                // criar o consumidor para a fila de produtos para casa
                //Consumer consumerCasa = criarConsumidor(channel, QUEUE_NAME_CASA);
                //channel.basicConsume(QUEUE_NAME_CASA, true, consumerCasa);

                boolean end = false;
                while (!end) {
                    Menu();

                    int opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            member.setContinueConsuming(true);
                            consumirFila(channel, QUEUE_NAME_ALIMENTAR, member);
                            break;
                        case 2:
                            member.setContinueConsuming(true);
                            consumirFila(channel, QUEUE_NAME_CASA, member);
                            break;
                        case 99:
                            end = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                scanner.close();
            }

            member.close();
            System.exit(0);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    // menu
    private static void Menu() {
        System.out.println("      Menu");
        System.out.println("1 - Consumir da Queue Alimentos");
        System.out.println("2 - Consumir da Queue Casa");
        System.out.println("99 - Exit");
        System.out.print("Escolha uma opção: ");
    }

    // funcao para criar novos consumidores
    private static Consumer criarConsumidor(Channel channel, String queueName, GroupMember member) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (member.shouldContinueConsuming()) {
                    String message = new String(body, "UTF-8");
                    System.out.println("Mensagem recebida: '" + message + "' da fila: '" + queueName + "'");
                    escreverNoArquivo(message);
                } else {
                    System.out.println("O Worker parou de consumir da fila '" + queueName + "'");
                }
            }

        };
    }

    // funcao para consumir as mensagens de uma queue passada por parametro
    private static void consumirFila(Channel channel, String queueName, GroupMember member) throws Exception {
        while (member.shouldContinueConsuming()) {
            Consumer consumer = criarConsumidor(channel, queueName, member);
            String consumerTag = channel.basicConsume(queueName, true, consumer);
            enviarResumo(channel);
            // defenir o tipo de queue que o worker esta a consumir
            member.setCasaOuAlimentar(queueName);
            System.out.println("----------"+member.getCasaOuAlimentar());

            System.out.println(" À espera de mensagens na fila '" + queueName + "'");

            while (member.shouldContinueConsuming()) {
                // Esperar
                Thread.sleep(1000);
            }
            // Cancelar o consumidor após a condição de parada
            channel.basicCancel(consumerTag);
        }
    }

    // escrever a mensagem recebida
    private static void escreverNoArquivo(String content) {
        String nomeArquivo = "resumo_"+ memberName.getMember() +".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            writer.write(content);
            writer.newLine();
            System.out.println("Conteúdo da mensagem gravado em " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
        }
    }

    private static String read(String msg, Scanner scaninput) {
        System.out.println(msg);
        String aux=scaninput.nextLine();
        return aux;
    }

    // funcao para criar 10 vendas para a casa
    private static void enviarResumo(Channel channel) throws Exception {
        // criar e enviar mensagens
        Resumo resumo = new Resumo();
        resumo.setResumo("resumo.txt");
        // enviar o resumo
        channel.basicPublish(EXCHANGE_NAME, "", null, resumo.getResumo().getBytes());
        System.out.println(" Enviado o 'Resumo' para a Queue Q_Resumo");
    }
}