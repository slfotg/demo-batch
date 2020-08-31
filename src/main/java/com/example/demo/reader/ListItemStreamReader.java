package com.example.demo.reader;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.ListItemReader;

/**
 * Extends {@link ListItemReader} with empty implementations for
 * {@link ItemStream} to be used as a {@link ItemStreamReader}.
 *
 */
public class ListItemStreamReader<T> extends ListItemReader<T> implements ItemStreamReader<T> {

    public ListItemStreamReader(List<T> list) {
        super(list);
    }

    /**
     * No-op.
     * 
     * @see org.springframework.batch.item.ItemStream#open(ExecutionContext)
     */
    @Override
    public void open(ExecutionContext executionContext) {
        // do nothing
    }

    /**
     * No-op.
     * 
     * @see org.springframework.batch.item.ItemStream#update(ExecutionContext)
     */
    @Override
    public void update(ExecutionContext executionContext) {
        // do nothing
    }

    /**
     * No-op.
     * 
     * @see org.springframework.batch.item.ItemStream#close()
     */
    @Override
    public void close() {
        // do nothing
    }

}
