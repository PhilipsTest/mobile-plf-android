package com.philips.pins.shinelib.dicommsupport;

import android.support.annotation.NonNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

class DiCommMessage {

    private static final byte FIRST_START_BYTE = (byte) 0xFE;
    private static final byte SECOND_START_BYTE = (byte) 0xFF;
    private static final int HEADER_SIZE = 5;

    private final MessageType messageTypeIdentifier;
    private final byte[] payload;

    public DiCommMessage(@NonNull byte[] data) throws InvalidParameterException {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);

            if (byteBuffer.get() != FIRST_START_BYTE || byteBuffer.get() != SECOND_START_BYTE) {
                throw new InvalidParameterException();
            }

            this.messageTypeIdentifier = MessageType.fromByte(byteBuffer.get());
            int length = byteBuffer.getShort();

            this.payload = new byte[length];
            byteBuffer.get(this.payload);
        } catch (BufferUnderflowException e) {
            throw new InvalidParameterException(e.getMessage());
        }
    }

    public DiCommMessage(@NonNull MessageType messageTypeIdentifier, @NonNull byte[] payload) {
        this.messageTypeIdentifier = messageTypeIdentifier;
        this.payload = payload;
    }

    public byte[] toData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_SIZE + payload.length);
        byteBuffer.put(FIRST_START_BYTE);
        byteBuffer.put(SECOND_START_BYTE);
        byteBuffer.put(messageTypeIdentifier.getByte());
        byteBuffer.putShort((short) payload.length);
        byteBuffer.put(payload);

        return byteBuffer.array();
    }

    public MessageType getMessageTypeIdentifier() {
        return messageTypeIdentifier;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getTotalDataSize() {
        return HEADER_SIZE + payload.length;
    }
}
