import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

import java.io.*;
/**
 * Created by renan on 7/24/16.
 */
public class ChatWithReplicatedHashMap extends ReceiverAdapter {

    private JChannel channel;

    private ReplicatedHashMap<String, Integer> wordCounter;

    @Override
    public void viewAccepted(View view) {
        System.out.println("New view :: " + view.toString());
    }

    public void runClient() throws Exception {
        channel = new JChannel("udp.xml");
        /* instancia o hasmap usando o canal, e logo apos conecta o canal ao chat */
        wordCounter = new ReplicatedHashMap<String, Integer>(channel);
        channel.connect("Hash");
        wordCounter.start(10_000);

        /* para ler do teclado */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        while(true){

            line = bufferedReader.readLine();
            switch (line){
                case "count":
                    System.out.println("Word Counter: " + wordCounter.toString());
                    break;
                default:
                    printOut(line);
                    break;
            }

        }

    }

    public static void main(String[] args) throws Exception {
        new ChatWithReplicatedHashMap().runClient();
    }


    public void printOut(String text){
        System.out.println("Nova Mensagem: " + text);
        Integer valueFromTheCache = wordCounter.get(text);
        int currentCount = valueFromTheCache == null ? 0 : valueFromTheCache;
        wordCounter.put(text, ++currentCount);
    }

}