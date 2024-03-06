package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class PointOfSale {
    private static final String EXCHANGE_NAME = "ExgSales";
    private static final String ROUTING_KEY_ALIMENTAR = "ALIMENTAR.#";
    private static final String ROUTING_KEY_CASA = "CASA.#";

    //private static String IP_BROKER="34.134.117.104";

    public static void main(String[] args) {

        // pedir o ip do broker
        System.out.print("IP do Broker: ");
        Scanner scanner = new Scanner(System.in);
        String rabbitMQHost = scanner.nextLine();

        // configda da conexao
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQHost);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // declaração do tipo de exchange
            //channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            boolean end = false;
            while (!end) {
                Menu();

                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        enviarVendasAlimentos(channel, ROUTING_KEY_ALIMENTAR);
                        break;
                    case 2:
                        enviarVendasCasa(channel, ROUTING_KEY_CASA);
                        break;
                    case 99:
                        end = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        scanner.close();
        }
    }

    private static void Menu() {
        System.out.println("      Menu");
        System.out.println("1 - Enviar 10 alimentos");
        System.out.println("2 - Enviar 10 produtos para casa");
        System.out.println("99 - Exit");
        System.out.print("Escolha uma opção: ");
    }

    // funcao para criar 10 vendas de alimentos
    private static void enviarVendasAlimentos(Channel channel, String routingKey) throws Exception {
            // criar e enviar mensagens
            Sale sale1 = new Sale();
            sale1.setCategory("ALIMENTAR");
            sale1.setDescription("Pao");
            sale1.setPrice(2);
            sale1.setAmount(5);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale1.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale2 = new Sale();
            sale2.setCategory("ALIMENTAR");
            sale2.setDescription("Carne");
            sale2.setPrice(8);
            sale2.setAmount(1);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale2.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale3 = new Sale();
            sale3.setCategory("ALIMENTAR");
            sale3.setDescription("Arroz");
            sale3.setPrice(15);
            sale3.setAmount(5);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale3.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale4 = new Sale();
            sale4.setCategory("ALIMENTAR");
            sale4.setDescription("Uabos");
            sale4.setPrice(2);
            sale4.setAmount(12);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale4.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale5 = new Sale();
            sale5.setCategory("ALIMENTAR");
            sale5.setDescription("Batatas");
            sale5.setPrice(5);
            sale5.setAmount(10);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale5.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale6 = new Sale();
            sale6.setCategory("ALIMENTAR");
            sale6.setDescription("Chocolate");
            sale6.setPrice(1);
            sale6.setAmount(2);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale6.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale7 = new Sale();
            sale7.setCategory("ALIMENTAR");
            sale7.setDescription("Sumo");
            sale7.setPrice(12);
            sale7.setAmount(6);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale7.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale8 = new Sale();
            sale8.setCategory("ALIMENTAR");
            sale8.setDescription("Sopa");
            sale8.setPrice(1);
            sale8.setAmount(5);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale8.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale9 = new Sale();
            sale9.setCategory("ALIMENTAR");
            sale9.setDescription("Espargete");
            sale9.setPrice(2);
            sale9.setAmount(2);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale9.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);
            Sale sale10 = new Sale();
            sale10.setCategory("ALIMENTAR");
            sale10.setDescription("Pudim");
            sale10.setPrice(8);
            sale10.setAmount(1);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale10.toString().getBytes());
            System.out.println(" Enviado o 'Sale' para " + routingKey);

    }

    // funcao para criar 10 vendas para a casa
    private static void enviarVendasCasa(Channel channel, String routingKey) throws Exception {
        // criar e enviar mensagens
        Sale sale1 = new Sale();
        sale1.setCategory("CASA");
        sale1.setDescription("Almofada");
        sale1.setPrice(10);
        sale1.setAmount(2);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale1.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale2 = new Sale();
        sale2.setCategory("CASA");
        sale2.setDescription("Tapete");
        sale2.setPrice(20);
        sale2.setAmount(1);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale2.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale3 = new Sale();
        sale3.setCategory("CASA");
        sale3.setDescription("Espelho");
        sale3.setPrice(12);
        sale3.setAmount(2);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale3.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale4 = new Sale();
        sale4.setCategory("CASA");
        sale4.setDescription("Pasta");
        sale4.setPrice(2);
        sale4.setAmount(1);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale4.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale5 = new Sale();
        sale5.setCategory("CASA");
        sale5.setDescription("Armario");
        sale5.setPrice(100);
        sale5.setAmount(1);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale5.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale6 = new Sale();
        sale6.setCategory("CASA");
        sale6.setDescription("Sofa");
        sale6.setPrice(1000);
        sale6.setAmount(1);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale6.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale7 = new Sale();
        sale7.setCategory("CASA");
        sale7.setDescription("Televisao");
        sale7.setPrice(100);
        sale7.setAmount(1);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale7.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale8 = new Sale();
        sale8.setCategory("CASA");
        sale8.setDescription("Cama");
        sale8.setPrice(500);
        sale8.setAmount(1);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale8.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale9 = new Sale();
        sale9.setCategory("CASA");
        sale9.setDescription("Prato");
        sale9.setPrice(30);
        sale9.setAmount(5);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale9.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
        Sale sale10 = new Sale();
        sale10.setCategory("CASA");
        sale10.setDescription("Candeeiro");
        sale10.setPrice(30);
        sale10.setAmount(2);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, sale10.toString().getBytes());
        System.out.println(" Enviado o 'Sale' para " + routingKey);
    }

}
