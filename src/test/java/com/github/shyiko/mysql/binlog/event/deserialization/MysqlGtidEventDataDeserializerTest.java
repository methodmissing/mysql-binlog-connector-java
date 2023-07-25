package com.github.shyiko.mysql.binlog.event.deserialization;

import com.github.shyiko.mysql.binlog.event.GtidEventData;
import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public class MysqlGtidEventDataDeserializerTest {

    private GtidEventDataDeserializer deserializer = new GtidEventDataDeserializer();

    @Test
    public void testDeserialize() throws IOException {
        GtidEventData data = deserializer.deserialize(new ByteArrayInputStream(
            new byte[]{
                0x03, //flags
                0x24, (byte) 0xbc, 0x78, 0x50, 0x2c, 0x16, 0x11, (byte) 0xe6, // sourceId mostSignificantBits big endian
                (byte) 0xa0, 0x73, 0x02, 0x42, (byte) 0xac, 0x11, 0x00, 0x02, // sourceId leastSignificantBits big endian
                (byte) 0x0b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // sequence little endian
            }
        ));
        assertEquals(data.getFlags(), 0x03);
        assertEquals(data.getMySqlGtid().toString(), "24bc7850-2c16-11e6-a073-0242ac110002:11");
        assertEquals(data.getLastCommitted(), 0);
        assertEquals(data.getSequenceNumber(), 0);
        assertEquals(data.toString(), "GtidEventData{flags=3, gtid='24bc7850-2c16-11e6-a073-0242ac110002:11', last_committed='0', sequence_number='0'}");
    }

    @Test
    public void testDeserializeMts() throws IOException {
        GtidEventData data = deserializer.deserialize(new ByteArrayInputStream(
            new byte[]{
                0x03, //flags
                0x24, (byte) 0xbc, 0x78, 0x50, 0x2c, 0x16, 0x11, (byte) 0xe6, // sourceId mostSignificantBits big endian
                (byte) 0xa0, 0x73, 0x02, 0x42, (byte) 0xac, 0x11, 0x00, 0x02, // sourceId leastSignificantBits big endian
                (byte) 0x0b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // sequence little endian
                0x02, //MTS
                (byte) 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // last committed little endian
                (byte) 0x0d, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // sequence number little endian
            }
        ));
        assertEquals(data.getFlags(), 0x03);
        assertEquals(data.getMySqlGtid().toString(), "24bc7850-2c16-11e6-a073-0242ac110002:11");
        assertEquals(data.getLastCommitted(), 12);
        assertEquals(data.getSequenceNumber(), 13);
        assertEquals(data.toString(), "GtidEventData{flags=3, gtid='24bc7850-2c16-11e6-a073-0242ac110002:11', last_committed='12', sequence_number='13'}");
    }
}
