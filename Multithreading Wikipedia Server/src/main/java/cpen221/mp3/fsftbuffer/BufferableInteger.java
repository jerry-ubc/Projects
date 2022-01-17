package cpen221.mp3.fsftbuffer;

public class BufferableInteger implements Bufferable {

    private int integer;

    public BufferableInteger(int integer) {
        this.integer = integer;
    }

    @Override
    public String id() {
        String id = String.valueOf(integer);
        return id;
    }

    public boolean equals(BufferableInteger x) {
        return true;
    }
}
