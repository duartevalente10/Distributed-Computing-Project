package mangerApp;


import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import managerckSUB.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class mangerApp {
    private static String svcIP; // ip a connectar
    private static int svcPort = 7500; // porta do servidor
    private static ManagedChannel channel;
    private static ManagerServeGrpc.ManagerServeBlockingStub blockingStub;
    private static ManagerServeGrpc.ManagerServeStub noBlockStub;
    private static Map<String, ByteString> Enviados = new HashMap<>();


    // funcao principal do maneger
    public static void main(String[] args) {

        // pedir o ip do servidor
        Scanner scanner1 = new Scanner(System.in);
        System.out.print("Introduza o IP do ManegerServer: ");
        svcIP = scanner1.nextLine();

        // conectar ao servidor
        try (Scanner scanner = new Scanner(System.in)) {
            if (args.length == 2) {
                svcIP = args[0];
                svcPort = Integer.parseInt(args[1]);
            }
            System.out.println("Conectado " + svcIP + ":" + svcPort);
            // criar o canal de comunicação
            channel = ManagedChannelBuilder.forAddress(svcIP, svcPort)
                    .usePlaintext()
                    .build();
            blockingStub = ManagerServeGrpc.newBlockingStub(channel);
            noBlockStub = ManagerServeGrpc.newStub(channel);

            // cases para cada função
            int Menu;
            do {
                Menu = menu(scanner);

                // ir mostrando o menu entre cada case
                switch (Menu) {
                    case 1: // enviar mensagem para resumir vendas alimentares
                        ResumoVendas("ALIMENTAR");
                        break;
                    case 2: // enviar mensagem para resumir vendas da casa
                        ResumoVendas("CASA");
                        break;
                    case 3: // pedir para fazer download do ficheiro de resumo
                        DownloadResumo(scanner);
                        break;
                    case 99: // fechar a aplicação
                        channel.shutdown();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opção Invalido");
                        break;
                }
            } while (Menu != 99);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // menu da aplicacao dos managers
    private static int menu(Scanner scanner) {
        System.out.println("\n=== MENU ===");
        System.out.println("1 - Resumir vendas Alimentares");
        System.out.println("2 - Resumir vendas Casa");
        System.out.println("3 - Download de do resumo");
        System.out.println("99 - Sair");
        System.out.print("Escolha uma opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // função para enviar a mensagem do tipo requerido ao servidor
    private static void ResumoVendas(String tipo) {
        try {
            // cria a mensagem a enviar ao servidor
            ResumeVenda request = ResumeVenda.newBuilder()
                    .setResume(tipo)
                    .build();
            // recebe a mensagem do servidor à resposta pedida
            NotificacaoResumo response = blockingStub.resumoVendas(request);
            // mostra a resposta do servidor
            System.out.println("Resposta do servidor: " + response.getMensagem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // funcao que faz o download do ficheiro de resumo para o manager
    private static void DownloadResumo(Scanner scanner) {
        try {
            String nomeArquivo = "resumo.txt";

            DownloadRequest request = DownloadRequest.newBuilder()
                    .setDownloadResumo(nomeArquivo)
                    .build();
            //  obter a resposta do servidor
            Iterator<ResumoResponse> responseIterator = blockingStub.downloadVendas(request);

            if (responseIterator.hasNext()) {
                ResumoResponse response = responseIterator.next();

                if (response.getConteudo() != null) {
                    // gurdar o conteúdo em um arquivo
                    ByteString conteudoArquivo = response.getConteudo();
                    String caminhoDestino = obterCaminhoDestino(scanner);
                    salvarArquivo(conteudoArquivo.toByteArray(), caminhoDestino + "resumo.txt");
                } else {
                    System.out.println("O resumo ainda não está processado.");
                }
            } else {
                System.out.println("Nenhuma resposta do servidor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // predir diretoria onde se pretende armazenar o resumo
    private static String obterCaminhoDestino(Scanner scanner) {
        System.out.print("Introduza onde pertende salvar o seu arquivo: ");
        return scanner.nextLine();
    }

    // salva um arquivo em uma diretoria especificada
    private static void salvarArquivo(byte[] bytes, String caminhoDestino) {
        try (FileOutputStream fos = new FileOutputStream(caminhoDestino)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}









