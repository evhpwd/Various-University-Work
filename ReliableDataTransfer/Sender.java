/*************************************
 * Filename:  Sender.java
 *************************************/
import java.util.Random;
import java.util.Timer;

public class Sender extends NetworkHost {

    Window buffer;
    int curSeq = 0;

    static int timeout = 40;
    
    public Sender(int entityName,
                       EventList events,
                       double pLoss,
                       double pCorrupt,
                       int trace,
                       Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    // This routine will be called whenever the app layer at the sender
    // has a message to send.  The job of your protocol is to ensure that
    // the data in such a message is delivered in-order, and correctly, to
    // the receiving application layer.
    protected void Output(Message message) {
        var sndpacket = new Packet(curSeq, 0, 0, message.getData());
        sndpacket.setChecksum(calcChecksum(sndpacket));
        curSeq += 1;
        if (buffer.AddPacket(sndpacket)) {
            udtSend(sndpacket);
            if (buffer.CurrentWindowSize() == 1) {
                stopTimer();
                startTimer(timeout);
            }
        }
    }
    
    // This routine will be called whenever a packet sent from the receiver 
    // (i.e. as a result of a udtSend() being done by a receiver procedure)
    // arrives at the sender. "packet" is the (possibly corrupted) packet
    // sent from the receiver.
    protected void Input(Packet packet) {
        if (calcChecksum(packet) == packet.getChecksum()) {
            Packet oldest = buffer.OldestPacket();
            if (oldest != null && oldest.getSeqnum() == packet.getAcknum()) {
                buffer.RemoveOldestPacket();
                if (buffer.CurrentWindowSize() > 0) {
                    stopTimer();
                    startTimer(timeout);
                }
            }
        }
        Packet newpacket = buffer.AddBufferedPacketToWindow();
        if (newpacket != null) {
            udtSend(newpacket);
            if (buffer.CurrentWindowSize() == 1) {
                stopTimer();
                startTimer(timeout);
            }
        }
    }
    
    // This routine will be called when the sender's timer expires (thus
    // generating a timer interrupt). You'll probably want to use this routine 
    // to control the retransmission of packets. See startTimer() and 
    // stopTimer(), above, for how the timer is started and stopped. 
    protected void TimerInterrupt() {
        var window = buffer.PacketsInWindow();
        if (window.length > 0) {
            startTimer(timeout);
        }
        for (int i = 0; i < window.length; i++) {
            udtSend(window[i]);
        }
    }
    
    // This routine will be called once, before any of your other sender-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of the sender).
    protected void Init() {
        buffer = new Window(8, 100);
    }

    private static int calcChecksum(Packet packet) {
        int check = packet.getAcknum() + packet.getSeqnum();
        for (int i = 0; i < packet.getPayload().length(); i++) {
            check += packet.getPayload().charAt(i);
        }
        return check;
    }

    static class Window {
        Packet[] buffer;
        int windowSize;
        // Index of the oldest packet in the window, will exceed buffer.length
        int windowStart = 0;
        // One past the end of the window, will exceed buffer.length
        int windowEnd = 0;
        // Index the next packet should be added at, will not exceed buffer.length
        int nextPacket = 0;
        public Window(int windowSize, int bufferSize) {
            this.windowSize = windowSize;
            buffer = new Packet[bufferSize];
        }

        public Packet OldestPacket() {
            return buffer[windowStart % buffer.length];
        }

        // Add a new packet to the buffer. Returns true if it
        // fits within the current window and should be sent
        public boolean AddPacket(Packet packet) {
            if (buffer[nextPacket] != null) {
                throw new RuntimeException("Buffer is full of packets (erm what the flip!?)");
            }
            buffer[nextPacket] = packet;
            nextPacket += 1;
            if (nextPacket >= buffer.length) nextPacket = 0;
            if (CurrentWindowSize() < windowSize) {
                windowEnd += 1;
                return true;
            }
            return false;
        }

        public void RemoveOldestPacket() {
            Packet oldest = buffer[windowStart % buffer.length];
            buffer[windowStart % buffer.length] = null;
            windowStart += 1;
        }

        public int CurrentWindowSize() {
            return windowEnd - windowStart;
        }

        // If there is a buffered packet not yet in the window,
        // add it to the window and return it
        public Packet AddBufferedPacketToWindow() {
            if (CurrentWindowSize() == windowSize) return null;
            if (buffer[windowEnd % buffer.length] != null) {
                windowEnd += 1;
                return buffer[windowEnd % buffer.length];
            } else {
                return null;
            }
        }

        public Packet[] PacketsInWindow() {
            var packets = new Packet[CurrentWindowSize()];
            for (int i = 0; i < packets.length; i++) {
                packets[i] = buffer[(windowStart + i) % buffer.length];
            }
            return packets;
        }
    }
}
