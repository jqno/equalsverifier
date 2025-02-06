package nl.jqno.equalsverifier.jpms.model;

public final class Records {
    public record RecordPoint(int x, int y) {}

    public record RecordPointContainer(RecordPoint rp) {}
}
