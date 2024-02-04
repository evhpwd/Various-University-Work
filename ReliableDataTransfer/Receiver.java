/*************************************
 * Filename:  Receiver.java
 *************************************/
import java.util.Random;

public class Receiver extends NetworkHost { 

    static int lastSeq = -1;
    
    public Receiver(int entityName,
                       EventList events,
                       double pLoss,
                       double pCorrupt,
                       int trace,
                       Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    protected void Input(Packet packet) {
        int ackNum = packet.getSeqnum();
        if (calcChecksum(packet) == packet.getChecksum()) {
            if (packet.getSeqnum() == lastSeq + 1) {
                deliverData(packet.getPayload());
                lastSeq = packet.getSeqnum();
            }
            // discard out of order
        } else {
            ackNum = lastSeq;
        }
        if (lastSeq == -1) return;
        Packet ackPacket = new Packet(packet.getSeqnum()+1, ackNum, 0);
        ackPacket.setChecksum(calcChecksum(ackPacket));
        udtSend(ackPacket);
    }

    private static int calcChecksum(Packet packet) {
        int check = packet.getAcknum() + packet.getSeqnum();
        for (int i = 0; i < packet.getPayload().length(); i++) {
            check += packet.getPayload().charAt(i);
        }
        return check;
    }
    
    protected void Init() {
    }

}
