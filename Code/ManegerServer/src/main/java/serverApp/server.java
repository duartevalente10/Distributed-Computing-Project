package serverApp;

import com.google.protobuf.ByteString;
import com.rabbitmq.client.*;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import managerckSUB.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeoutException;


public class server extends ManagerServeGrpc.ManagerServeImplBase {

    private static int daemonPort = 4803; // porto do daemon
    private static String ipDaemon; // ip do deamon
    private static String userName; //  userName do server para propositos do Spread
    private static int svcPort = 7500; // porto do servidor
    private static GroupMember member;
    static String IP_BROKER; // ip do broker
    private static final String EXCHANGE_NAME = "ExgResumo";
    private static final String QUEUE_NAME = "Q_RESUMO";
    private static String diretoria = "resumo.txt";

    // main
    public static void main(String[] args) throws IOException, TimeoutException {

        // pedir o ip do deamon do spread
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduza o IP da VM do daemon: ");
        ipDaemon = scanner.nextLine();
        System.out.print("Introduza o IP do Broker: ");
        IP_BROKER = scanner.nextLine();
        // pedir um nome para ser reconhecido pelo grupo spread
        Scanner scaninput = new Scanner(System.in);
        userName = read("MemberApp name? ", scaninput);
        System.out.println("connected to "+ ipDaemon);

        // criar um grupo spread para poder comunicar com o outro grupo
        member = new GroupMember(userName, ipDaemon, daemonPort);

        // connexao com o rabbit
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_BROKER);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Set up a consumer to consume messages from the queue
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                storeMessage(message);
            };

            // consumir da queue
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

            // ... (existing code)
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // iniciar o servidor
        try {
            if (args.length > 0) svcPort = Integer.parseInt(args[0]);
            io.grpc.Server svc = ServerBuilder
                    .forPort(svcPort)
                    .addService(new server())
                    .build();
            svc.start();
            System.out.println("Servidor iniciado na porta " + svcPort);
            ;
            svc.awaitTermination();
            svc.shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // funcao receber a mensagem do manger, enviar uma resposta e enviar a mensagem para o grupo spread
    @Override
    public void resumoVendas(ResumeVenda request, StreamObserver<NotificacaoResumo> responseObserver) {
        try {
            // mensagem recebida
            String mensagem = request.getResume();

            // enviar mensagem para o spreadGroup "SG"
            member.SendMessage("SG",mensagem);

            // cria a notificação de resposta ao manager
            NotificacaoResumo notificacaoResumo = NotificacaoResumo.newBuilder()
                    .setMensagem("Mensagem de resumo de vendas do tipo " + mensagem + " recebida.")
                    .build();
            // enviar a notificação de resposta
            responseObserver.onNext(notificacaoResumo);
            responseObserver.onCompleted();

        } catch (Exception e) {
            // em caso de erro
            responseObserver.onError(e);
        }
    }

    // funcao que recebe um nome de um arquivo e retorna o conteudo desse arquivo
    @Override
    public void downloadVendas(DownloadRequest request, StreamObserver<ResumoResponse> responseObserver) {
        // obter diretoria atual
        Path caminhoArquivo = Paths.get(System.getProperty("user.dir"), diretoria);

        // procurar o arquivo na diretoria atual e enviar como resposta ao maneger
        if (Files.exists(caminhoArquivo) && !Files.isDirectory(caminhoArquivo)) {
            try {
                // ler o conteudo do arquivo
                byte[] conteudoArquivo = Files.readAllBytes(caminhoArquivo);
                // construir a mensagem de resposta com o conteudo a enviar
                ResumoResponse resposta = ResumoResponse.newBuilder().setConteudo(ByteString.copyFrom(conteudoArquivo)).build();
                // enviar a mensagem de resposta
                responseObserver.onNext(resposta);
                responseObserver.onCompleted();
            } catch (IOException e) {
                responseObserver.onError(e);
            }
        } else {
            // caso o arquivo nao esteja em memoria
            responseObserver.onError(new RuntimeException("Arquivo não encontrado"));
        }
    }

    // funcao utilizada para ler os inputs
    private static String read(String msg, Scanner scaninput) {
        System.out.println(msg);
        String aux=scaninput.nextLine();
        return aux;
    }

    // guardar valor recebido
    private static void storeMessage(String message) {
        diretoria = message;
        System.out.println("Mensagem recebida do Broker: " + diretoria);
    }
}
