package com.edu.ulab.app.utility;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final long START_SEQUENCE = 100_000;
    private static AtomicLong USER_ID = new AtomicLong(START_SEQUENCE);
    private static AtomicLong BOOK_ID = new AtomicLong(START_SEQUENCE);
    private static AtomicLong USER_BOOK_ID = new AtomicLong(START_SEQUENCE);

    public static Long nextUserId() {
        return USER_ID.getAndIncrement();
    }

    public static Long nextBookId() {
        return BOOK_ID.getAndIncrement();
    }

    public static Long nextUserBookId() {
        return USER_BOOK_ID.getAndIncrement();
    }
}
